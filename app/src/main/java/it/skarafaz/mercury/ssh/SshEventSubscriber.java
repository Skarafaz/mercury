/*
 * Mercury-SSH
 * Copyright (C) 2018 Skarafaz
 *
 * This file is part of Mercury-SSH.
 *
 * Mercury-SSH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Mercury-SSH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mercury-SSH.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.skarafaz.mercury.ssh;

import android.support.annotation.NonNull;
import android.text.InputType;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.activity.MercuryActivity;
import it.skarafaz.mercury.event.*;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressWarnings("unused")
public class SshEventSubscriber {
    private MercuryActivity activity;

    public SshEventSubscriber(MercuryActivity activity) {
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
        MercuryApplication.showProgressDialog(activity.getSupportFragmentManager(), activity.getString(R.string.sending_command));

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSshCommandEnd(SshCommandEnd event) {
        Toast.makeText(activity, activity.getString(event.getStatus().message()), Toast.LENGTH_SHORT).show();
        MercuryApplication.dismissProgressDialog(activity.getSupportFragmentManager());

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
