package ank.com.secretchatlab.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.SettingsManager;

public class TranslationDialog extends DialogFragment implements DialogInterface.OnClickListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.translation_unavailable)
                .setMessage(R.string.translation_unavailable_message)
                .setPositiveButton(R.string.help_translate_xabber, this)
                .setNegativeButton(android.R.string.cancel, this)
                .setCancelable(false)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        SettingsManager.setTranslationSuggested();

        if (which == Dialog.BUTTON_POSITIVE) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.translation_url)));
            startActivity(i);
        }
    }
}
