package it.skarafaz.mercury.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.skarafaz.mercury.R;

public class ProgressDialogFragment extends DialogFragment {
    public static final String TAG = "SENDING_COMMAND_DIALOG";
    public static final String CONTENT_ARG = "CONTENT_ARG";
    @Bind(R.id.message)
    protected TextView message;
    public String content;

    public static ProgressDialogFragment newInstance(String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(CONTENT_ARG, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = getArguments() != null ? getArguments().getString(CONTENT_ARG) : null;
        setStyle(STYLE_NO_FRAME, R.style.DialogTransparent);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_progress, container, false);
        ButterKnife.bind(this, view);
        message.setText(content);
        return view;
    }
}
