package ank.com.secretchatlab.data.message.chat;

import android.content.Intent;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.BaseEntity;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.notification.EntityNotificationItem;
import ank.com.secretchatlab.data.roster.RosterManager;
import ank.com.secretchatlab.ui.activity.ContactListActivity;

public class MucPrivateChatNotification extends BaseEntity implements EntityNotificationItem {

    public MucPrivateChatNotification(AccountJid account, UserJid user) {
        super(account, user);
    }

    @Override
    public Intent getIntent() {
        return ContactListActivity.createMucPrivateChatInviteIntent(Application.getInstance(), account, user);
    }

    @Override
    public String getTitle() {
        return RosterManager.getInstance().getBestContact(account, user).getName();
    }

    @Override
    public String getText() {
        return Application.getInstance().getString(R.string.conference_private_chat);
    }

}
