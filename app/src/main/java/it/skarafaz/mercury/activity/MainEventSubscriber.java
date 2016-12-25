package it.skarafaz.mercury.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.InputType;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.event.SftpDownloadEnd;
import it.skarafaz.mercury.event.SftpDownloadProgress;
import it.skarafaz.mercury.event.SftpDownloadStart;
import it.skarafaz.mercury.event.SshCommandConfirm;
import it.skarafaz.mercury.event.SshCommandEnd;
import it.skarafaz.mercury.event.SshCommandMessage;
import it.skarafaz.mercury.event.SshCommandPassword;
import it.skarafaz.mercury.event.SshCommandPubKeyInput;
import it.skarafaz.mercury.event.SshCommandRulerUpdate;
import it.skarafaz.mercury.event.SshCommandStart;
import it.skarafaz.mercury.event.SshCommandYesNo;
import it.skarafaz.mercury.ssh.SshCommandStatus;

public class MainEventSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(MainEventSubscriber.class);
    private MainActivity activity;
    private NotificationManager notificationManager;
    private MimeTypeMap mimeTypeMap;
    private HashMap<Integer, NotificationCompat.Builder> notificationBuilders;

    public MainEventSubscriber(MainActivity activity) {
        this.activity = activity;
        notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        mimeTypeMap = MimeTypeMap.getSingleton();
        notificationBuilders = new HashMap<Integer, NotificationCompat.Builder>();
    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSftpDownloadEnd(final SftpDownloadEnd event) {
        NotificationCompat.Builder notificationBuilder = notificationBuilders.get(event.getId());
        Intent viewIntent = null;
        if (event.getStatus() == SshCommandStatus.COMMAND_SUCCESSFUL) {
            notificationBuilder
                    .setOngoing(true)
                    .setTicker(activity.getString(R.string.download_succeeded))
                    .setContentTitle(activity.getString(R.string.download_succeeded))
                    .setContentText(String.format(activity.getString(R.string.download_succeeded_file),
                        event.getFile().getName()));
            // Include a pending intent in the notification viewing the file or start the intent directly
            viewIntent = new Intent(Intent.ACTION_VIEW);
            String mimeType = mimeTypeMap.getMimeTypeFromExtension(fileExt(event.getFile().toString()));
            logger.debug(String.format("view %s (extension %s, mime type %s)", fileExt(event.getFile().toString()),
                    event.getFile(), mimeType));
            viewIntent.setDataAndType(Uri.fromFile(event.getFile()), mimeType);
            viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationBuilder.setContentIntent(PendingIntent.getActivity(activity, (int)
                    System.currentTimeMillis(), viewIntent, 0));
        } else {
            notificationBuilder
                    .setTicker(activity.getString(R.string.download_failed))
                    .setContentTitle(activity.getString(R.string.download_failed))
                    .setContentText(String.format(activity.getString(R.string.download_failed_file),
                        event.getFile().getName()));
        }
        if (event.getStatus() == SshCommandStatus.COMMAND_SUCCESSFUL && event.getView()) {
            try {
                activity.startActivity(viewIntent);
                notificationManager.cancel(event.getId());
            } catch (ActivityNotFoundException e) {
                logger.debug("Could not start view activity: ", e);
                Toast.makeText(activity, R.string.no_handler, Toast.LENGTH_LONG).show();
            }
        } else {
            notificationBuilder
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setProgress(0, 0, false);
            notificationManager.notify(event.getId(), notificationBuilder.build());
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSftpDownloadProgress(final SftpDownloadProgress event) {
        long progress = event.getProgress();
        long max = event.getMax();
        while (max > (1L << 31)) {
            max >>= 1;
            progress >>= 1;
        }
        NotificationCompat.Builder notificationBuilder = notificationBuilders.get(event.getId());
        notificationBuilder
                .setProgress((int) max, (int) progress, false)
                .setContentText(String.format(activity.getString(R.string.downloading_percent), 100 * progress /
                        max, event.getFile().getName(), Formatter.formatFileSize(activity, progress), Formatter
                        .formatFileSize(activity, max)));
        notificationManager.notify(event.getId(), notificationBuilder.build());

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSftpDownloadStart(final SftpDownloadStart event) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity);
        notificationBuilders.put(event.getId(), new NotificationCompat.Builder(activity));
        notificationBuilder = notificationBuilders.get(event.getId());
        notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(activity.getString(R.string.downloading))
                .setContentTitle(activity.getString(R.string.downloading))
                .setOngoing(true)
                .setProgress(0, 0, true)
                .setContentText(String.format(activity.getString(R.string.downloading_starting), event.getFile()
                        .getName()));
        notificationManager.notify(event.getId(), notificationBuilder.build());

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandConfirm(final SshCommandConfirm event) {
        new MaterialDialog.Builder(activity)
                .title(R.string.confirm_exec)
                .content(event.getCmd())
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        event.getDrop().put(true);
                    }

                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        event.getDrop().put(false);
                    }
                })
                .show();

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandStart(SshCommandStart event) {
        if (event.getBackground()) {
            activity.onCommandListChanged();
        } else {
            activity.showProgressDialog(activity.getString(R.string.sending_command));
        }

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandEnd(SshCommandEnd event) {
        if (!event.getSilent() || (event.getStatus() != SshCommandStatus.COMMAND_SUCCESSFUL &&
                event.getStatus() != SshCommandStatus.COMMAND_SENT)) {
            Toast.makeText(activity, activity.getString(event.getStatus().message()), Toast
                    .LENGTH_SHORT).show();
        }
        if (event.getBackground()) {
            activity.onCommandListChanged();
        } else {
            activity.dismissProgressDialog();
        }

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandPassword(final SshCommandPassword event) {
        new MaterialDialog.Builder(activity)
                .title(R.string.password)
                .content(event.getMessage())
                .positiveText(R.string.submit)
                .negativeText(R.string.cancel)
                .cancelable(false)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(null, null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        event.getDrop().put(input.toString());
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        event.getDrop().put(null);
                    }
                })
                .show();

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandYesNo(final SshCommandYesNo event) {
        new MaterialDialog.Builder(activity)
                .content(event.getMessage())
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        event.getDrop().put(true);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        event.getDrop().put(false);
                    }
                })
                .show();

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandMessage(final SshCommandMessage event) {
        new MaterialDialog.Builder(activity)
                .content(event.getMessage())
                .positiveText(R.string.ok)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        event.getDrop().put(true);
                    }
                })
                .show();

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandPubKeyInput(final SshCommandPubKeyInput event) {
        new MaterialDialog.Builder(activity)
                .title(R.string.send_publick_key)
                .content(R.string.connection_string_message)
                .positiveText(R.string.submit)
                .negativeText(R.string.cancel)
                .cancelable(false)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .alwaysCallInputCallback()
                .input(R.string.connection_string_hint, 0, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (isConnectionStringValid(input.toString())) {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        } else {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        }
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (dialog.getInputEditText() != null) {
                            String input = StringUtils.trimToNull(dialog.getInputEditText().getText().toString());
                            event.getDrop().put(input);
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        event.getDrop().put(null);
                    }
                })
                .show();

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandRulerUpdate(SshCommandRulerUpdate event) {
        activity.onCommandListChanged();

        EventBus.getDefault().removeStickyEvent(event);
    }

    private boolean isConnectionStringValid(String input) {
        return input.matches("^.+@.+$");
    }
}
