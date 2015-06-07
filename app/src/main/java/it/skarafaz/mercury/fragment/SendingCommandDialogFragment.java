package it.skarafaz.mercury.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.skarafaz.mercury.R;

public class SendingCommandDialogFragment extends DialogFragment {
    public static final String TAG = "SENDING_COMMAND_DIALOG";

    public static SendingCommandDialogFragment newInstance() {
        return new SendingCommandDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.DialogTransparent);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_progress, container, false);
    }
}
