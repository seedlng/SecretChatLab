package ank.com.secretchatlab.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.NetworkException;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.intent.EntityIntentBuilder;
import ank.com.secretchatlab.data.roster.PresenceManager;
import ank.com.secretchatlab.data.roster.RosterContact;
import ank.com.secretchatlab.data.roster.RosterManager;
import ank.com.secretchatlab.ui.dialog.BlockContactDialog;
import ank.com.secretchatlab.ui.dialog.ChatExportDialogFragment;
import ank.com.secretchatlab.ui.dialog.ChatHistoryClearDialog;
import ank.com.secretchatlab.ui.dialog.ContactDeleteDialogFragment;
import ank.com.secretchatlab.ui.helper.PermissionsRequester;

public class ContactEditActivity extends ContactActivity implements Toolbar.OnMenuItemClickListener {

    private static final int PERMISSIONS_REQUEST_EXPORT_CHAT = 27;

    public static Intent createIntent(Context context, AccountJid account, UserJid user) {
        return new EntityIntentBuilder(context, ContactEditActivity.class)
                .setAccount(account).setUser(user).build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = getToolbar();

        RosterContact rosterContact = RosterManager.getInstance().getRosterContact(getAccount(), getUser());
        if (rosterContact != null) {
            toolbar.inflateMenu(R.menu.toolbar_contact);
            toolbar.setOnMenuItemClickListener(this);
            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_overflow_menu_white_24dp));
            onCreateOptionsMenu(toolbar.getMenu());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        RosterContact rosterContact = RosterManager.getInstance().getRosterContact(getAccount(), getUser());
        if (rosterContact != null) {
            menu.clear();
            getMenuInflater().inflate(R.menu.toolbar_contact, menu);

            // request subscription
            menu.findItem(R.id.action_request_subscription).setVisible(!rosterContact.isSubscribed());
        }

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return onOptionsItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_request_subscription:
                try {
                    PresenceManager.getInstance().requestSubscription(getAccount(), getUser());
                } catch (NetworkException e) {
                    Application.getInstance().onError(e);
                }
                return true;

            case R.id.action_send_contact:
                sendContact();
                return true;

            case R.id.action_clear_history:
                ChatHistoryClearDialog.newInstance(getAccount(), getUser()).show(getFragmentManager(), ChatHistoryClearDialog.class.getSimpleName());
                return true;

            case R.id.action_export_chat:
                if (PermissionsRequester.requestFileReadPermissionIfNeeded(this, PERMISSIONS_REQUEST_EXPORT_CHAT)) {
                    ChatExportDialogFragment.newInstance(getAccount(), getUser()).show(getFragmentManager(), "CHAT_EXPORT");
                }
                return true;

            case R.id.action_block_contact:
                BlockContactDialog.newInstance(getAccount(), getUser()).show(getFragmentManager(), BlockContactDialog.class.getName());
                return true;

            case R.id.action_edit_alias:
                editAlias();
                return true;

            case R.id.action_edit_groups:
                startActivity(GroupEditActivity.createIntent(this, getAccount(), getUser()));
                return true;

            case R.id.action_remove_contact:
                ContactDeleteDialogFragment.newInstance(getAccount(), getUser())
                        .show(getFragmentManager(), "CONTACT_DELETE");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editAlias() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_alias);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        RosterContact rosterContact = RosterManager.getInstance().getRosterContact(getAccount(), getUser());
        input.setText(rosterContact.getName());
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RosterManager.getInstance().setName(getAccount(), getUser(), input.getText().toString());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void sendContact() {
        RosterContact rosterContact = RosterManager.getInstance().getRosterContact(getAccount(), getUser());
        String text = rosterContact != null ? rosterContact.getName() + "\nxmpp:" + getUser().toString() : "xmpp:" + getUser().toString();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }
}
