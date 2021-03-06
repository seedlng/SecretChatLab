package ank.com.secretchatlab.ui.dialog;

import android.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.extension.muc.MUCManager;
import ank.com.secretchatlab.data.extension.muc.RoomInvite;
import ank.com.secretchatlab.data.roster.AbstractContact;
import ank.com.secretchatlab.data.roster.RosterManager;
import ank.com.secretchatlab.ui.activity.ConferenceAddActivity;


public class MucInviteDialog extends BaseContactDialog {

    private RoomInvite roomInvite;

    public static DialogFragment newInstance(AccountJid account, UserJid contact) {
        DialogFragment fragment = new MucInviteDialog();
        setArguments(account, contact, fragment);
        return fragment;
    }

    @Override
    protected int getDialogTitleTextResource() {
        return R.string.conference_invitation;
    }

    @Override
    protected String getMessage() {
        return roomInvite.getConfirmation();
    }

    @Override
    protected int getNegativeButtonTextResource() {
        return android.R.string.cancel;
    }

    @Override
    protected int getPositiveButtonTextResource() {
        return R.string.muc_join;
    }

    @Override
    protected Integer getNeutralButtonTextResourceOrNull() {
        return null;
    }

    @Override
    protected void onPositiveButtonClick() {
        startActivity(ConferenceAddActivity.createIntent(getActivity(), getAccount(), getContact().getBareUserJid()));
    }

    @Override
    protected void onNegativeButtonClick() {
        MUCManager.getInstance().removeInvite(roomInvite);
    }

    @Override
    protected void onNeutralButtonClick() {

    }

    @Override
    protected void setUpContactTitleView(View view) {
        roomInvite = MUCManager.getInstance().getInvite(getAccount(), getContact().getJid().asEntityBareJidIfPossible());
        final UserJid inviter = roomInvite.getInviter().getBareUserJid();

        final AbstractContact bestContact = RosterManager.getInstance().getBestContact(getAccount(), inviter);

        ((ImageView)view.findViewById(R.id.avatar)).setImageDrawable(bestContact.getAvatar());
        ((TextView)view.findViewById(R.id.name)).setText(bestContact.getName());
        ((TextView)view.findViewById(R.id.status_text)).setText(inviter.toString());
    }

}
