package ank.com.secretchatlab.ui.preferences;

import android.os.Bundle;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.ui.activity.PreferenceSummaryHelperActivity;

public class ChatGlobalSettingsFragment extends android.preference.PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_chat_global);

        PreferenceSummaryHelperActivity.updateSummary(getPreferenceScreen());
    }
}
