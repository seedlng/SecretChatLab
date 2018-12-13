package ank.com.secretchatlab.ui.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.message.MessageManager;
import ank.com.secretchatlab.data.roster.RosterManager;

public class ChatHistoryClearDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String ARGUMENT_ACCOUNT = ChatHistoryClearDialog.class.getName() + "ARGUMENT_ACCOUNT";
    public static final String ARGUMENT_USER = ChatHistoryClearDialog.class.getName() + "ARGUMENT_USER";

    AccountJid account;
    UserJid user;

    public static ChatHistoryClearDialog newInstance(AccountJid account, UserJid user) {
        ChatHistoryClearDialog fragment = new ChatHistoryClearDialog();

        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_ACCOUNT, account);
        arguments.putParcelable(ARGUMENT_USER, user);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        account = args.getParcelable(ARGUMENT_ACCOUNT);
        user = args.getParcelable(ARGUMENT_USER);

        String name = RosterManager.getInstance().getBestContact(account, user).getName();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.clear_history)
                .setMessage(getString(R.string.clear_chat_history_dialog_message, name))
                .setPositiveButton(R.string.clear_chat_history_dialog_button, this)
                .setNegativeButton(android.R.string.cancel, this).create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            MessageManager.getInstance().clearHistory(account, user);
        }
    }

}
