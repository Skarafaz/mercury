package it.skarafaz.mercury.activity;

import android.support.annotation.NonNull;
import android.text.InputType;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.event.SshCommandConfirm;
import it.skarafaz.mercury.event.SshCommandEnd;
import it.skarafaz.mercury.event.SshCommandMessage;
import it.skarafaz.mercury.event.SshCommandPassword;
import it.skarafaz.mercury.event.SshCommandPubKeyInput;
import it.skarafaz.mercury.event.SshCommandStart;
import it.skarafaz.mercury.event.SshCommandYesNo;

public class MainEventSubscriber {
    private MainActivity activity;

    public MainEventSubscriber(MainActivity activity) {
        this.activity = activity;
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
        activity.showProgressDialog(activity.getString(R.string.sending_command));

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandEnd(SshCommandEnd event) {
        Toast.makeText(activity, activity.getString(event.getStatus().message()), Toast.LENGTH_SHORT).show();
        activity.dismissProgressDialog();

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

    private boolean isConnectionStringValid(String input) {
        return input.matches("^.+@.+$");
    }
}
