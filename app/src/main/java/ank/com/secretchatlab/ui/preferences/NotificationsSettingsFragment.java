package ank.com.secretchatlab.ui.preferences;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.ui.activity.PreferenceSummaryHelperActivity;

public class NotificationsSettingsFragment extends android.preference.PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_notifications);

        PreferenceSummaryHelperActivity.updateSummary(getPreferenceScreen());

        Preference resetPreference = (Preference) getPreferenceScreen().findPreference(getString(R.string.events_reset_key));
        resetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.events_reset_alert)
                        .setPositiveButton(R.string.category_reset, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), R.string.events_reset_toast, Toast.LENGTH_SHORT).show();
                                PreferenceManager
                                        .getDefaultSharedPreferences(getActivity())
                                        .edit()
                                        .clear()
                                        .apply();
                                PreferenceManager.setDefaultValues(getActivity(), R.xml.preference_notifications, true);
                                ((NotificationsSettings) getActivity()).restartFragment();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                return true;
            }
        });
    }
}
