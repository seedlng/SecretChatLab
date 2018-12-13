package ank.com.secretchatlab.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.xaccount.XabberAccountManager;
import ank.com.secretchatlab.ui.preferences.PreferenceEditor;


public class AccountDeleteDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String ARGUMENT_ACCOUNT = "ank.com.secretchatlab.ui.dialog.AccountDeleteDialog.ARGUMENT_ACCOUNT";

    private AccountJid account;
    private CheckBox chbDeleteSettings;
    private String jid;

    public static DialogFragment newInstance(AccountJid account) {
        AccountDeleteDialog fragment = new AccountDeleteDialog();

        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_ACCOUNT, account);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        account = args.getParcelable(ARGUMENT_ACCOUNT);
        if (account != null)
            jid = account.getFullJid().asBareJid().toString();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_account, null);

        chbDeleteSettings = (CheckBox) view.findViewById(R.id.chbDeleteSettings);
        chbDeleteSettings.setChecked(XabberAccountManager.getInstance().isAccountSynchronize(jid));
        if (XabberAccountManager.getInstance().getAccountSyncState(jid) == null)
            chbDeleteSettings.setVisibility(View.GONE);

        return new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.account_delete_confirm,
                        AccountManager.getInstance().getVerboseName(account)))
                .setView(view)
                .setPositiveButton(R.string.account_delete, this)
                .setNegativeButton(android.R.string.cancel, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which != Dialog.BUTTON_POSITIVE) {
            return;
        }

        AccountManager.getInstance().removeAccount(account);

        if (chbDeleteSettings != null && chbDeleteSettings.isChecked()) {
            Activity activity = getActivity();
            if (activity instanceof PreferenceEditor)
                ((PreferenceEditor)activity).onDeleteAccountSettings(jid);
        }
    }
}
