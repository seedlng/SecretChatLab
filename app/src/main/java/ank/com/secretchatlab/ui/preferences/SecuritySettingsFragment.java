package ank.com.secretchatlab.ui.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.account.AccountItem;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.log.LogManager;
import ank.com.secretchatlab.ui.activity.PreferenceSummaryHelperActivity;

import java.util.Collection;

public class SecuritySettingsFragment extends android.preference.PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = SecuritySettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_security);

        PreferenceSummaryHelperActivity.updateSummary(getPreferenceScreen());
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.security_check_certificate_key))) {
            boolean checkCertificate = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.security_check_certificate_default));

            LogManager.i(LOG_TAG, "Check certificate preference changed. new value " + checkCertificate);

            // reconnect all enabled account to apply and check changes
            Collection<AccountJid> enabledAccounts = AccountManager.getInstance().getAllAccounts();
            for (AccountJid accountJid : enabledAccounts) {
                AccountItem accountItem = AccountManager.getInstance().getAccount(accountJid);
                if (accountItem != null) {
                    accountItem.recreateConnection();
                }
            }
        }
    }
}
