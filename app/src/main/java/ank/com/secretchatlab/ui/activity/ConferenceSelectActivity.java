package ank.com.secretchatlab.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.intent.EntityIntentBuilder;
import ank.com.secretchatlab.ui.color.BarPainter;
import ank.com.secretchatlab.ui.fragment.ConferenceSelectFragment;

public class ConferenceSelectActivity extends ManagedActivity implements ConferenceSelectFragment.Listener {

    private BarPainter barPainter;

    public static Intent createIntent(Context context) {
        return new EntityIntentBuilder(context, ConferenceSelectActivity.class).build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_with_toolbar_and_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_default);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ConferenceSelectActivity.this);
            }
        });
        toolbar.setTitle(getString(R.string.muc_choose_conference));

        barPainter = new BarPainter(this, toolbar);
        barPainter.setDefaultColor();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_container, new ConferenceSelectFragment()).commit();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onAccountSelected(AccountJid account) {
        barPainter.updateWithAccountName(account);
    }
}
