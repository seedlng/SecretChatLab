package ank.com.secretchatlab.ui.helper;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.extension.avatar.AvatarManager;
import ank.com.secretchatlab.data.extension.cs.ChatStateManager;
import ank.com.secretchatlab.data.message.NotificationState;
import ank.com.secretchatlab.data.roster.AbstractContact;
import ank.com.secretchatlab.data.roster.RosterContact;

import org.jivesoftware.smackx.chatstates.ChatState;

/**
 * Created by valery.miller on 26.10.17.
 */

public class NewContactTitleInflater {

    public static void updateTitle(View titleView, final Context context, AbstractContact abstractContact,
                                   NotificationState.NotificationMode mode) {
        final TextView nameView = (TextView) titleView.findViewById(R.id.name);
        final ImageView avatarView = (ImageView) titleView.findViewById(R.id.ivAvatar);

        nameView.setText(abstractContact.getName());

        // notification mute
        Resources resources = context.getResources();
        switch (mode) {
            case enabled:
                nameView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        resources.getDrawable(R.drawable.ic_unmute_large), null);
                break;
            case disabled:
                nameView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        resources.getDrawable(R.drawable.ic_mute_large), null);
                break;
            default:
                nameView.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null);
        }

        // if it is account, not simple user contact
        if (abstractContact.getUser().getJid().asBareJid().equals(abstractContact.getAccount().getFullJid().asBareJid())) {
            avatarView.setImageDrawable(AvatarManager.getInstance().getAccountAvatar(abstractContact.getAccount()));
        } else {
            avatarView.setImageDrawable(abstractContact.getAvatar());
        }
        setStatus(context, titleView, abstractContact);
    }

    private static void setStatus(Context context, View titleView, AbstractContact abstractContact) {
        final ImageView statusModeView = (ImageView) titleView.findViewById(R.id.ivStatus);
        //final ImageView ivStatusBackground = (ImageView) titleView.findViewById(R.id.ivStatusBackground);

        int statusLevel = abstractContact.getStatusMode().getStatusLevel();
        statusModeView.setVisibility(View.GONE);
        if (isContactOffline(statusLevel)) {
            statusModeView.setVisibility(View.GONE);
            //ivStatusBackground.setVisibility(View.GONE);
        } else {
            statusModeView.setVisibility(View.VISIBLE);
            statusModeView.setImageLevel(statusLevel);
            //ivStatusBackground.setVisibility(View.VISIBLE);
            //ivStatusBackground.setImageLevel(AccountPainter.getAccountColorLevel(abstractContact.getAccount()));
        }

        final TextView statusTextView = (TextView) titleView.findViewById(R.id.status_text);


        ChatState chatState = ChatStateManager.getInstance().getChatState(
                abstractContact.getAccount(), abstractContact.getUser());

        CharSequence statusText;
        if (chatState == ChatState.composing) {
            statusText = context.getString(R.string.chat_state_composing);
        } else if (chatState == ChatState.paused) {
            statusText = context.getString(R.string.chat_state_paused);
        } else {
            statusText = abstractContact.getStatusText().trim();
            if (statusText.toString().isEmpty()) {
                statusText = getLastActivity(abstractContact);
                if (statusText.length() <= 0)
                    statusText = context.getString(abstractContact.getStatusMode().getStringID());
            }
        }
        statusTextView.setText(statusText);
    }

    private static boolean isContactOffline(int statusLevel) {
        return statusLevel == 6;
    }

    private static String getLastActivity(AbstractContact contact) {
        if (contact instanceof RosterContact) {
            RosterContact rosterContact = (RosterContact) contact;
            if (!rosterContact.getStatusMode().isOnline())
                return rosterContact.getLastActivity();
        }
        return "";
    }

}
