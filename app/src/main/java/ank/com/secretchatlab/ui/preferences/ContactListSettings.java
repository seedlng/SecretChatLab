package ank.com.secretchatlab.ui.preferences;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.ui.activity.ManagedActivity;
import ank.com.secretchatlab.ui.activity.PreferenceSummaryHelperActivity;
import ank.com.secretchatlab.ui.color.BarPainter;
import ank.com.secretchatlab.ui.helper.ToolbarHelper;

public class ContactListSettings extends ManagedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFinishing())
            return;

        setContentView(R.layout.activity_with_toolbar_and_container);

        String title = PreferenceSummaryHelperActivity.getPreferenceTitle(getString(R.string.preference_contacts));
        Toolbar toolbar = ToolbarHelper.setUpDefaultToolbar(this, title);

        BarPainter barPainter = new BarPainter(this, toolbar);
        barPainter.setDefaultColor();
        
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new ContactListSettingsFragment()).commit();
        }
    }
}
