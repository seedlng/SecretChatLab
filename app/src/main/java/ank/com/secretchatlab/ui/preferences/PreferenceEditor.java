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
package ank.com.secretchatlab.ui.preferences;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.ActivityManager;
import ank.com.secretchatlab.data.SettingsManager;
import ank.com.secretchatlab.data.account.AccountItem;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.log.LogManager;
import ank.com.secretchatlab.data.xaccount.AuthManager;
import ank.com.secretchatlab.data.xaccount.XMPPAccountSettings;
import ank.com.secretchatlab.data.xaccount.XabberAccountManager;
import ank.com.secretchatlab.ui.activity.AccountActivity;
import ank.com.secretchatlab.ui.activity.AccountAddActivity;
import ank.com.secretchatlab.ui.activity.AccountListActivity;
import ank.com.secretchatlab.ui.activity.ContactListActivity;
import ank.com.secretchatlab.ui.activity.ManagedActivity;
import ank.com.secretchatlab.ui.activity.StatusEditActivity;
import ank.com.secretchatlab.ui.adapter.AccountListPreferenceAdapter;
import ank.com.secretchatlab.ui.color.BarPainter;
import ank.com.secretchatlab.ui.dialog.AccountDeleteDialog;
import ank.com.secretchatlab.ui.helper.ToolbarHelper;
import ank.com.secretchatlab.ui.widget.XMPPListPreference;
import ank.com.secretchatlab.utils.RetrofitErrorConverter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PreferenceEditor extends ManagedActivity
        implements PreferencesFragment.OnPreferencesFragmentInteractionListener, AccountListPreferenceAdapter.Listener {

    private BarPainter barPainter;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private ProgressDialog progressDialog;
    private final static String LOG_TAG = PreferenceEditor.class.getSimpleName();

    public static Intent createIntent(Context context) {
        return new Intent(context, PreferenceEditor.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFinishing())
            return;

        setContentView(R.layout.activity_with_toolbar_and_container);

        Toolbar toolbar = ToolbarHelper.setUpDefaultToolbar(this);
        barPainter = new BarPainter(this, toolbar);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new PreferencesFragment()).commit();
        }

        // Force request sound. This will set default value if not specified.
        SettingsManager.eventsSound();
        SettingsManager.chatsAttentionSound();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barPainter.setDefaultColor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
    }

    protected void showProgress(String title) {
        if (!isFinishing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(title);
            progressDialog.setMessage(getResources().getString(R.string.progress_message));
            progressDialog.show();
        }
    }

    protected void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogManager.exception(this, e);
        }
        return "";
    }

    @Override
    public void onThemeChanged() {
        ActivityManager.getInstance().clearStack(true);
        startActivity(ContactListActivity.createIntent(this));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddAccountClick(XMPPListPreference.AddAccountClickEvent event) {
        startActivity(AccountAddActivity.createIntent(this));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReorderClick(XMPPListPreference.ReorderClickEvent event) {
        startActivity(AccountListActivity.createIntent(this));
    }

    @Override
    public void onAccountClick(AccountJid account) {
        startActivity(AccountActivity.createIntent(this, account));
    }

    @Override
    public void onEditAccountStatus(AccountItem accountItem) {
        startActivity(StatusEditActivity.createIntent(this, accountItem.getAccount()));
    }

    @Override
    public void onEditAccount(AccountItem accountItem) {
        startActivity(AccountActivity.createIntent(this, accountItem.getAccount()));
    }

    @Override
    public void onDeleteAccount(AccountItem accountItem) {
        AccountDeleteDialog.newInstance(accountItem.getAccount()).show(getSupportFragmentManager(),
                AccountDeleteDialog.class.getName());
    }

    public void onDeleteAccountSettings(String jid) {
        showProgress(getResources().getString(R.string.progress_title_delete_settings));

        if (XabberAccountManager.getInstance().getAccountSyncState(jid) != null) {

            Subscription deleteSubscription = AuthManager.deleteClientSettings(jid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<XMPPAccountSettings>>() {
                        @Override
                        public void call(List<XMPPAccountSettings> settings) {
                            handleSuccessDelete(settings);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            handleErrorDelete(throwable);
                        }
                    });
            compositeSubscription.add(deleteSubscription);
        }
    }

    private void handleSuccessDelete(List<XMPPAccountSettings> settings) {
        hideProgress();
        Toast.makeText(this, R.string.settings_delete_success, Toast.LENGTH_SHORT).show();
    }

    private void handleErrorDelete(Throwable throwable) {
        String message = RetrofitErrorConverter.throwableToHttpError(throwable);
        if (message != null) {
            if (message.equals("Invalid token")) {
                XabberAccountManager.getInstance().onInvalidToken();
            } else {
                Log.d(LOG_TAG, "Error while deleting settings: " + message);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(LOG_TAG, "Error while deleting settings: " + throwable.toString());
            Toast.makeText(this, "Error while deleting settings: " + throwable.toString(), Toast.LENGTH_LONG).show();
        }
        hideProgress();
    }
}
