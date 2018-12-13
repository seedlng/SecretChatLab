package ank.com.secretchatlab.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.account.listeners.OnAccountChangedListener;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.log.LogManager;
import ank.com.secretchatlab.data.roster.OnContactChangedListener;
import ank.com.secretchatlab.data.roster.RosterContact;
import ank.com.secretchatlab.data.roster.RosterManager;
import ank.com.secretchatlab.ui.adapter.OccupantListAdapter;

import org.jxmpp.jid.EntityBareJid;

import java.util.Collection;

/**
 * Created by valery.miller on 26.10.17.
 */

public class OccupantListFragment extends Fragment implements AdapterView.OnItemClickListener,
        OnAccountChangedListener, OnContactChangedListener {

    public static final String ARGUMENT_ACCOUNT = "ARGUMENT_ACCOUNT";
    public static final String ARGUMENT_ROOM = "ARGUMENT_ROOM";

    private AccountJid account;
    private EntityBareJid room;
    private OccupantListAdapter listAdapter;

    @Nullable
    private Listener listener;

    public interface Listener {
        void onOccupantClick(String username);
    }

    public static OccupantListFragment newInstance(AccountJid account, UserJid room) {
        OccupantListFragment fragment = new OccupantListFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_ACCOUNT, account);
        arguments.putParcelable(ARGUMENT_ROOM, room);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        this.account = args.getParcelable(ARGUMENT_ACCOUNT);
        UserJid user = args.getParcelable(ARGUMENT_ROOM);
        if (user != null)
            this.room = user.getJid().asEntityBareJidIfPossible();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list);
        listAdapter = new OccupantListAdapter(getActivity(), account, room);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        listView.setDividerHeight(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Application.getInstance().addUIListener(OnAccountChangedListener.class, this);
        Application.getInstance().addUIListener(OnContactChangedListener.class, this);
        listAdapter.onChange();
    }

    @Override
    public void onPause() {
        super.onPause();
        Application.getInstance().removeUIListener(OnAccountChangedListener.class, this);
        Application.getInstance().removeUIListener(OnContactChangedListener.class, this);
    }

    @Override
    public void onContactsChanged(Collection<RosterContact> entities) {
        try {
            if (entities.contains(RosterManager.getInstance().getAbstractContact(account, UserJid.from(room)))) {
                listAdapter.onChange();
            }
        } catch (UserJid.UserJidCreateException e) {
            LogManager.exception(this, e);
        }
    }

    @Override
    public void onAccountsChanged(Collection<AccountJid> accounts) {
        if (accounts.contains(account)) {
            listAdapter.onChange();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ank.com.secretchatlab.data.extension.muc.Occupant occupant
                = (ank.com.secretchatlab.data.extension.muc.Occupant) listAdapter.getItem(position);
        LogManager.i(this, occupant.getNickname().toString());
        if (listener != null) listener.onOccupantClick(occupant.getNickname().toString());

//        UserJid occupantFullJid = null;
//        try {
//            occupantFullJid = UserJid.from(JidCreate.entityFullFrom(room, occupant.getNickname()));
//        } catch (UserJid.UserJidCreateException e) {
//            LogManager.exception(this, e);
//            return;
//        }
//
//        final AbstractChat mucPrivateChat;
//        try {
//            mucPrivateChat = MessageManager.getInstance()
//                    .getOrCreatePrivateMucChat(account, occupantFullJid.getJid().asFullJidIfPossible());
//        } catch (UserJid.UserJidCreateException e) {
//            LogManager.exception(this, e);
//            return;
//        }
//        mucPrivateChat.setIsPrivateMucChatAccepted(true);
//
//        startActivity(ChatActivity.createSpecificChatIntent(getActivity(), account, occupantFullJid));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (Listener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
