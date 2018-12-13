package ank.com.secretchatlab.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.account.AccountItem;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.database.messagerealm.MessageItem;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.extension.muc.MUCManager;
import ank.com.secretchatlab.data.message.AbstractChat;
import ank.com.secretchatlab.data.message.MessageManager;
import ank.com.secretchatlab.data.roster.AbstractContact;
import ank.com.secretchatlab.data.roster.RosterManager;
import ank.com.secretchatlab.presentation.ui.contactlist.viewobjects.ChatVO;
import ank.com.secretchatlab.presentation.ui.contactlist.viewobjects.ContactVO;
import ank.com.secretchatlab.ui.activity.ChatActivity;
import ank.com.secretchatlab.ui.activity.ContactActivity;
import ank.com.secretchatlab.ui.activity.ContactEditActivity;
import ank.com.secretchatlab.ui.adapter.ChatComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

public class RecentChatFragment extends Fragment implements Toolbar.OnMenuItemClickListener,
        ContactVO.ContactClickListener, FlexibleAdapter.OnItemClickListener,
        ChatVO.IsCurrentChatListener, FlexibleAdapter.OnItemSwipeListener {

    private FlexibleAdapter<IFlexible> adapter;
    private List<IFlexible> items;
    CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;

    @Nullable
    private Listener listener;

    public interface Listener {
        void onChatSelected(AccountJid accountJid, UserJid userJid);
        void registerRecentChatFragment(RecentChatFragment recentChatFragment);
        void unregisterRecentChatFragment();
        boolean isCurrentChat(String account, String user);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecentChatFragment() {
    }

    public static RecentChatFragment newInstance() {
        RecentChatFragment fragment = new RecentChatFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (Listener) activity;
        listener.registerRecentChatFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_recent_chats, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recent_chats_recycler_view);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);

        items = new ArrayList<>();
        adapter = new FlexibleAdapter<>(items, null, false);
        recyclerView.setAdapter(adapter);
        adapter.setSwipeEnabled(true);
        adapter.addListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateChats();
        return rootView;
    }

    @Override
    public void onDetach() {
        if (listener != null) {
            listener.unregisterRecentChatFragment();
            listener = null;
        }
        super.onDetach();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_close_chats) {
            MessageManager.closeActiveChats();
            updateChats();
        }

        return false;
    }

    @Override
    public void onContactAvatarClick(int adapterPosition) {
        IFlexible item = adapter.getItem(adapterPosition);
        if (item != null && item instanceof ContactVO) {
            Intent intent;
            AccountJid accountJid = ((ContactVO) item).getAccountJid();
            UserJid userJid = ((ContactVO) item).getUserJid();
            if (MUCManager.getInstance().hasRoom(accountJid, userJid)) {
                intent = ContactActivity.createIntent(getActivity(), accountJid, userJid);
            } else {
                intent = ContactEditActivity.createIntent(getActivity(), accountJid, userJid);
            }
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onContactCreateContextMenu(int adapterPosition, ContextMenu menu) {}

    @Override
    public void onContactButtonClick(int adapterPosition) {}

    public void updateItems(List<AbstractContact> items) {
        this.items.clear();
        this.items.addAll(ChatVO.convert(items, this, this));
        adapter.updateDataSet(this.items);
    }

    public void updateChats() {

        Application.getInstance().runInBackgroundUserRequest(new Runnable() {
            @Override
            public void run() {
                Collection<AbstractChat> chats = MessageManager.getInstance().getChats();

                List<AbstractChat> recentChats = new ArrayList<>();

                for (AbstractChat abstractChat : chats) {
                    MessageItem lastMessage = abstractChat.getLastMessage();

                    if (lastMessage != null && !TextUtils.isEmpty(lastMessage.getText())) {
                        AccountItem accountItem = AccountManager.getInstance().getAccount(abstractChat.getAccount());
                        if (accountItem != null && accountItem.isEnabled()) {
                            recentChats.add(abstractChat);
                        }
                    }
                }

                Collections.sort(recentChats, ChatComparator.CHAT_COMPARATOR);


                final List<AbstractContact> newContacts = new ArrayList<>();

                for (AbstractChat chat : recentChats) {
                    if (!chat.isArchived() || ((ChatActivity)getActivity()).isShowArchived())
                        newContacts.add(RosterManager.getInstance()
                                .getBestContact(chat.getAccount(), chat.getUser()));
                }

                Application.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateItems(newContacts);
                    }
                });
            }
        });
    }

    @Override
    public boolean onItemClick(int position) {
        ChatVO chat = (ChatVO) adapter.getItem(position);
        if (listener != null && chat != null)
            listener.onChatSelected(chat.getAccountJid(), chat.getUserJid());
        return true;
    }

    @Override
    public void onItemSwipe(int position, int direction) {
        Object itemAtPosition = adapter.getItem(position);
        if (itemAtPosition != null && itemAtPosition instanceof ChatVO) {

            // backup of removed item for undo purpose
            final ChatVO deletedItem = (ChatVO) itemAtPosition;

            // update value
            setChatArchived(deletedItem, !(deletedItem).isArchived());
            deletedItem.setArchived(!(deletedItem).isArchived());


            // remove the item from recycler view
            adapter.removeItem(position);
            if (((ChatActivity)getActivity()).isShowArchived()) adapter.addItem(position, deletedItem);

            // showing snackbar with Undo option
            showSnackbar(deletedItem, position);
        }
    }

    @Override
    public void onActionStateChanged(RecyclerView.ViewHolder viewHolder, int actionState) {}

    @Override
    public boolean isCurrentChat(String account, String user) {
        return listener != null && listener.isCurrentChat(account, user);
    }

    public void showSnackbar(final ChatVO deletedItem, final int deletedIndex) {
        if (snackbar != null) snackbar.dismiss();
        final boolean archived = deletedItem.isArchived();
        snackbar = Snackbar.make(coordinatorLayout, !archived ? R.string.chat_was_unarchived
                : R.string.chat_was_archived, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // update value
                setChatArchived(deletedItem, !archived);
                deletedItem.setArchived(!archived);

                // update item in recycler view
                if (((ChatActivity)getActivity()).isShowArchived())
                    adapter.removeItem(deletedIndex);
                adapter.addItem(deletedIndex, deletedItem);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    public void closeSnackbar() {
        if (snackbar != null) snackbar.dismiss();
    }

    public void setChatArchived(ChatVO chatVO, boolean archived) {
        AbstractChat chat = MessageManager.getInstance().getChat(chatVO.getAccountJid(), chatVO.getUserJid());
        if (chat != null) chat.setArchived(archived, true);
    }
}
