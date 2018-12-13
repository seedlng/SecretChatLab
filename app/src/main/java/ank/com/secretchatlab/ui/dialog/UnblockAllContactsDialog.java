package ank.com.secretchatlab.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.extension.blocking.BlockingManager;

public class UnblockAllContactsDialog extends DialogFragment implements DialogInterface.OnClickListener, BlockingManager.BlockContactListener, BlockingManager.UnblockContactListener {

    public static final String ARGUMENT_ACCOUNT = "ank.com.secretchatlab.ui.dialog.UnblockAllContactsDialog.ARGUMENT_ACCOUNT";

    private AccountJid account;

    public static UnblockAllContactsDialog newInstance(AccountJid account) {
        UnblockAllContactsDialog fragment = new UnblockAllContactsDialog();

        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_ACCOUNT, account);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        account = args.getParcelable(ARGUMENT_ACCOUNT);

        return new AlertDialog.Builder(getActivity())
                .setMessage(String.format(getActivity().getString(R.string.unblock_all_contacts_confirm),
                        AccountManager.getInstance().getVerboseName(account)))
                .setPositiveButton(R.string.unblock_all, this)
                .setNegativeButton(android.R.string.cancel, this).create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            BlockingManager.getInstance().unblockAll(account, this);
        }
    }

    @Override
    public void onSuccess() {
        Toast.makeText(Application.getInstance(),
                Application.getInstance().getString(R.string.contacts_unblocked_successfully),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(Application.getInstance(),
                Application.getInstance().getString(R.string.error_unblocking_contacts),
                Toast.LENGTH_SHORT).show();
    }
}
