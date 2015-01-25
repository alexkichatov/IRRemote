package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;
import org.twinone.irremote.components.Remote;

public class RenameRemoteDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

    private static final String ARG_REMOTE = "org.twinone.irremote.arg.menu_main";

    private String mOriginalRemoteName;
    private EditText mNewRemoteName;
    private OnRemoteRenamedListener mListener;

    public static void showFor(Activity a, String remoteName) {
        RenameRemoteDialog.newInstance(remoteName).show(a.getFragmentManager(),
                "rename_remote_dialog");
    }

    public static RenameRemoteDialog newInstance(String remoteName) {
        RenameRemoteDialog f = new RenameRemoteDialog();
        Bundle b = new Bundle();
        b.putString(ARG_REMOTE, remoteName);
        f.setArguments(b);
        return f;
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "save_remote_dialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOriginalRemoteName = getArguments().getString(ARG_REMOTE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit_text, null, false);
        mNewRemoteName = (EditText) view
                .findViewById(R.id.dialog_edittext_input);
        mNewRemoteName.setText(mOriginalRemoteName);

        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.customView(view, false);

        mb.title(R.string.rename_remote_title);

        mb.content(getString(R.string.rename_remote_message,
                mOriginalRemoteName));
        mb.positiveText(R.string.rename_remote_save);
        mb.negativeText(android.R.string.cancel);
        return mb.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                final String newName = mNewRemoteName.getText().toString();

                Remote.rename(getActivity(), mOriginalRemoteName, newName);

                if (mListener != null)
                    mListener.onRemoteRenamed(newName);
                break;
        }
    }

    public void setOnRemoteRenamedListener(OnRemoteRenamedListener listener) {
        mListener = listener;
    }

    public interface OnRemoteRenamedListener {
        public void onRemoteRenamed(String newName);
    }

}
