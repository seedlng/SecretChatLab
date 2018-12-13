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
package ank.com.secretchatlab.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.account.AccountItem;
import ank.com.secretchatlab.data.log.LogManager;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.account.listeners.OnAccountChangedListener;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.BaseEntity;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.extension.muc.MUCManager;
import ank.com.secretchatlab.data.extension.vcard.VCardManager;
import ank.com.secretchatlab.data.intent.AccountIntentBuilder;
import ank.com.secretchatlab.data.intent.EntityIntentBuilder;
import ank.com.secretchatlab.data.roster.AbstractContact;
import ank.com.secretchatlab.data.roster.OnContactChangedListener;
import ank.com.secretchatlab.data.roster.RosterContact;
import ank.com.secretchatlab.data.roster.RosterManager;
import ank.com.secretchatlab.ui.color.ColorManager;
import ank.com.secretchatlab.ui.color.StatusBarPainter;
import ank.com.secretchatlab.ui.fragment.ConferenceInfoFragment;
import ank.com.secretchatlab.ui.fragment.ContactVcardViewerFragment;
import ank.com.secretchatlab.ui.helper.ContactTitleInflater;

import java.util.Collection;

public class ContactActivity extends ManagedActivity implements
        OnContactChangedListener, OnAccountChangedListener, ContactVcardViewerFragment.Listener {

    private static final String LOG_TAG = ContactActivity.class.getSimpleName();
    private AccountJid account;
    private UserJid user;
    private Toolbar toolbar;
    private View contactTitleView;
    private AbstractContact bestContact;
    private CollapsingToolbarLayout collapsingToolbar;

    public static Intent createIntent(Context context, AccountJid account, UserJid user) {
        return new EntityIntentBuilder(context, ContactActivity.class)
                .setAccount(account).setUser(user).build();
    }

    private static AccountJid getAccount(Intent intent) {
        return AccountIntentBuilder.getAccount(intent);
    }

    private static UserJid getUser(Intent intent) {
        return EntityIntentBuilder.getUser(intent);
    }

    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        account = getAccount(getIntent());
        user = getUser(getIntent());

        AccountItem accountItem = AccountManager.getInstance().getAccount(this.account);
        if (accountItem == null) {
            LogManager.e(LOG_TAG, "Account item is null " + account);
            finish();
            return;
        }

        if (user != null && user.getBareJid().equals(account.getFullJid().asBareJid())) {
            try {
                user = UserJid.from(accountItem.getRealJid().asBareJid());
            } catch (UserJid.UserJidCreateException e) {
                LogManager.exception(this, e);
            }
        }

        if (account == null || user == null) {
            Application.getInstance().onError(R.string.ENTRY_IS_NOT_FOUND);
            finish();
            return;
        }

        setContentView(R.layout.activity_contact);

        if (savedInstanceState == null) {

            Fragment fragment;
            if (MUCManager.getInstance().hasRoom(account, user)) {
                fragment = ConferenceInfoFragment.newInstance(account, user.getJid().asEntityBareJidIfPossible());
            } else {
                fragment = ContactVcardViewerFragment.newInstance(account, user);
            }

            getFragmentManager().beginTransaction().add(R.id.scrollable_container, fragment).commit();


        }

        bestContact = RosterManager.getInstance().getBestContact(account, user);

        toolbar = (Toolbar) findViewById(R.id.toolbar_default);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        StatusBarPainter statusBarPainter = new StatusBarPainter(this);
        statusBarPainter.updateWithAccountName(account);

        final int accountMainColor = ColorManager.getInstance().getAccountPainter().getAccountMainColor(account);

        contactTitleView = findViewById(R.id.contact_title_expanded);
        findViewById(R.id.ivStatus).setVisibility(View.GONE);
        contactTitleView.setBackgroundColor(accountMainColor);
        TextView contactNameView = (TextView) findViewById(R.id.name);
        contactNameView.setVisibility(View.INVISIBLE);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(bestContact.getName());

        collapsingToolbar.setBackgroundColor(accountMainColor);
        collapsingToolbar.setContentScrimColor(accountMainColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.getInstance().addUIListener(OnContactChangedListener.class, this);
        Application.getInstance().addUIListener(OnAccountChangedListener.class, this);
        ContactTitleInflater.updateTitle(contactTitleView, this, bestContact);
        updateName();
    }

    @Override
    public void onPause() {
        super.onPause();
        Application.getInstance().removeUIListener(OnContactChangedListener.class, this);
        Application.getInstance().removeUIListener(OnAccountChangedListener.class, this);
    }

    @Override
    public void onContactsChanged(Collection<RosterContact> entities) {
        for (BaseEntity entity : entities) {
            if (entity.equals(account, user)) {
                updateName();
                break;
            }
        }
    }

    private void updateName() {
        if (MUCManager.getInstance().isMucPrivateChat(account, user)) {
            String vCardName = VCardManager.getInstance().getName(user.getJid());
            if (!"".equals(vCardName)) {
                collapsingToolbar.setTitle(vCardName);
            } else {
                collapsingToolbar.setTitle(user.getJid().getResourceOrNull().toString());
            }

        } else {
            collapsingToolbar.setTitle(RosterManager.getInstance().getBestContact(account, user).getName());
        }
    }

    @Override
    public void onAccountsChanged(Collection<AccountJid> accounts) {
        if (accounts.contains(account)) {
            updateName();
        }
    }

    protected AccountJid getAccount() {
        return account;
    }

    protected UserJid getUser() {
        return user;
    }

    @Override
    public void onVCardReceived() {
        ContactTitleInflater.updateTitle(contactTitleView, this, bestContact);
    }

    @Override
    public void registerVCardFragment(ContactVcardViewerFragment fragment) {}
}
