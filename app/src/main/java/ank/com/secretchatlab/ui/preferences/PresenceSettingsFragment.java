package ank.com.secretchatlab.ui.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.ui.activity.PreferenceSummaryHelperActivity;

/**
 * Created by valer on 28.08.2017.
 */

public class PresenceSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_presence);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceSummaryHelperActivity.updateSummary(preferenceScreen);
    }
}
