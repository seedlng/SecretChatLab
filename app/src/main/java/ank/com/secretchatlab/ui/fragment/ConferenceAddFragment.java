package ank.com.secretchatlab.ui.fragment;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.extension.avatar.AvatarManager;
import ank.com.secretchatlab.data.extension.bookmarks.BookmarksManager;
import ank.com.secretchatlab.data.extension.muc.MUCManager;
import ank.com.secretchatlab.data.extension.muc.RoomInvite;
import ank.com.secretchatlab.data.log.LogManager;
import ank.com.secretchatlab.ui.activity.ChatActivity;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.util.XmppStringUtils;

public class ConferenceAddFragment extends Fragment {

    protected static final String ARG_ACCOUNT = "ank.com.secretchatlab.ui.fragment.ConferenceAddFragment.ARG_ACCOUNT";
    protected static final String ARG_CONFERENCE_JID = "ank.com.secretchatlab.ui.fragment.ConferenceAddFragment.ARG_CONFERENCE_NAME";

    private EditText nickView;
    private EditText passwordView;

    private AccountJid account = null;
    private EntityBareJid conferenceJid = null;

    public static ConferenceAddFragment newInstance(AccountJid account, EntityBareJid conference) {
        ConferenceAddFragment fragment = new ConferenceAddFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ACCOUNT, account);
        args.putSerializable(ARG_CONFERENCE_JID, conference);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            account = getArguments().getParcelable(ARG_ACCOUNT);
            conferenceJid = (EntityBareJid) getArguments().getSerializable(ARG_CONFERENCE_JID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conference_add, container, false);

        ((TextView) view.findViewById(R.id.muc_conference_jid)).setText(conferenceJid);
        ((TextView) view.findViewById(R.id.muc_account_jid)).setText(account.getFullJid().asBareJid().toString());

        Drawable accountAvatar = AvatarManager.getInstance().getAccountAvatar(account);
        int h = accountAvatar.getIntrinsicHeight();
        int w = accountAvatar.getIntrinsicWidth();
        accountAvatar.setBounds( 0, 0, w, h );
        ((ImageView) view.findViewById(R.id.ivAvatar)).setImageDrawable(accountAvatar);


        nickView = (EditText) view.findViewById(R.id.muc_nick);
        nickView.setText(MUCManager.getInstance().getNickname(account, conferenceJid));
        if ("".equals(nickView.getText().toString())) {
            nickView.setText(getNickname(account));
        }

        passwordView = (EditText) view.findViewById(R.id.muc_password);
        String password;
        RoomInvite roomInvite = MUCManager.getInstance().getInvite(account, conferenceJid);
        if (roomInvite != null) {
            password = roomInvite.getPassword();
        } else {
            password = MUCManager.getInstance().getPassword(account, conferenceJid);
        }
        passwordView.setText(password);

        MUCManager.getInstance().removeAuthorizationError(account, conferenceJid);

        setHasOptionsMenu(true);

        return view;
    }


    /**
     * @return Suggested nickname in the conferenceJid.
     */
    private String getNickname(AccountJid account) {
        if (account == null) {
            return "";
        }
        String nickname = AccountManager.getInstance().getNickName(account);
        String name = XmppStringUtils.parseLocalpart(nickname);
        if ("".equals(name)) {
            return nickname;
        } else {
            return name;
        }
    }

    public void addConference() {
        Resourcepart nick;
        try {
            nick = Resourcepart.from(nickView.getText().toString().trim());
        } catch (XmppStringprepException e) {
            LogManager.exception(this, e);
            Toast.makeText(getActivity(), getString(R.string.EMPTY_NICK_NAME), Toast.LENGTH_LONG).show();
            return;
        }

        String password = passwordView.getText().toString();
        final boolean join = true;
        MUCManager.getInstance().createRoom(account, conferenceJid, nick, password, join);

        // add conference to bookmarks
        BookmarksManager.getInstance().addConferenceToBookmarks(account,
                conferenceJid.getLocalpart().toString(), conferenceJid, nick);

        try {
            startActivity(ChatActivity.createSpecificChatIntent(getActivity(), account, UserJid.from(conferenceJid)));
        } catch (UserJid.UserJidCreateException e) {
            LogManager.exception(this, e);
        }
    }
}
