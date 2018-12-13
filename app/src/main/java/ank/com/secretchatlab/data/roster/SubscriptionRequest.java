/**
 * Copyright (c) 2013, Redsolution LTD. All rights reserved.
 *
 * This file is part of Xabber project; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.
 *
 * Xabber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package ank.com.secretchatlab.data.roster;

import android.content.Intent;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.BaseEntity;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.notification.EntityNotificationItem;
import ank.com.secretchatlab.ui.activity.ContactListActivity;

public class SubscriptionRequest extends BaseEntity implements EntityNotificationItem {

    public SubscriptionRequest(AccountJid account, UserJid user) {
        super(account, user);
    }

    @Override
    public Intent getIntent() {
        return ContactListActivity.createContactSubscriptionIntent(Application.getInstance(), account, user);
    }

    @Override
    public String getText() {
        return Application.getInstance().getString(
                R.string.subscription_request_message);
    }

    @Override
    public String getTitle() {
        return user.toString();
    }

    public String getConfirmation() {
        String accountName = AccountManager.getInstance().getVerboseName(account);
        return Application.getInstance().getString(R.string.contact_subscribe_confirm, accountName);
    }

}
