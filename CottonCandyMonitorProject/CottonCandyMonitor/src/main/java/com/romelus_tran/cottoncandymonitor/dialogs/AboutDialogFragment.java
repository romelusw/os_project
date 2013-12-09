package com.romelus_tran.cottoncandymonitor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.romelus_tran.cottoncandymonitor.R;

/**
 * Pops up a dialog that shows the about information.
 * Created by Brian on 12/9/13.
 */
public class AboutDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.about_title));
        builder.setMessage(getResources().getString(R.string.about_text));
        builder.setNegativeButton("Cool", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user cancelled the dialog
                // do nothing...
            }
        });

        return builder.create();
    }
}
