package ank.com.secretchatlab.ui.dialog;

import android.app.DialogFragment;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.log.LogManager;
import ank.com.secretchatlab.data.message.MessageManager;
import ank.com.secretchatlab.ui.activity.ChatActivity;

public class MucPrivateChatInvitationDialog extends BaseContactDialog {

    public static DialogFragment newInstance(AccountJid account, UserJid contact) {
        DialogFragment fragment = new MucPrivateChatInvitationDialog();
        setArguments(account, contact, fragment);
        return fragment;
    }

    @Override
    protected int getDialogTitleTextResource() {
        return R.string.conference_private_chat;
    }

    @Override
    protected String getMessage() {
        return String.format(getString(R.string.conference_private_chat_invitation),
                getContact().getJid().getResourceOrNull().toString(), getContact().getJid().asBareJid().toString());
    }

    @Override
    protected int getNegativeButtonTextResource() {
        return R.string.decline_contact;
    }

    @Override
    protected int getPositiveButtonTextResource() {
        return R.string.accept_muc_private_chat;
    }

    @Override
    protected Integer getNeutralButtonTextResourceOrNull() {
        return null;
    }

    @Override
    protected void onPositiveButtonClick() {
        try {
            MessageManager.getInstance().acceptMucPrivateChat(getAccount(), getContact());
            startActivity(ChatActivity.createSpecificChatIntent(Application.getInstance(), getAccount(), getContact()));
        } catch (UserJid.UserJidCreateException e) {
            LogManager.exception(this, e);
        }

    }

    @Override
    protected void onNegativeButtonClick() {
        MessageManager.getInstance().discardMucPrivateChat(getAccount(), getContact());
    }

    @Override
    protected void onNeutralButtonClick() {

    }
}
