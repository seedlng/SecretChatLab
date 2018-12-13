package ank.com.secretchatlab.ui.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.extension.muc.MUCManager;
import ank.com.secretchatlab.ui.fragment.ChatFragment;
import ank.com.secretchatlab.ui.fragment.ContactVcardViewerFragment;
import ank.com.secretchatlab.ui.fragment.OccupantListFragment;
import ank.com.secretchatlab.ui.fragment.RecentChatFragment;

public class ChatViewerAdapter extends FragmentStatePagerAdapter {

    private static final String LOG_TAG = ChatViewerAdapter.class.getSimpleName();
    public static final int PAGE_POSITION_RECENT_CHATS = 0;
    public static final int PAGE_POSITION_CHAT = 1;
    public static final int PAGE_POSITION_CHAT_INFO = 2;

    public interface FinishUpdateListener {
        void onChatViewAdapterFinishUpdate();
    }

    private AccountJid accountJid;
    private UserJid userJid;

    private int itemCount;

    private FinishUpdateListener finishUpdateListener;

    public ChatViewerAdapter(FragmentManager fragmentManager,
                             FinishUpdateListener finishUpdateListener) {
        super(fragmentManager);
        this.finishUpdateListener = finishUpdateListener;
        itemCount = 1;
    }

    public ChatViewerAdapter(FragmentManager fragmentManager,
                             FinishUpdateListener finishUpdateListener,
                             @NonNull AccountJid accountJid,
                             @NonNull UserJid userJid) {
        super(fragmentManager);
        this.finishUpdateListener = finishUpdateListener;
        setChat(accountJid, userJid);
    }

    public void selectChat(@NonNull AccountJid accountJid, @NonNull UserJid userJid) {
        if (accountJid.equals(this.accountJid) && userJid.equals(this.userJid)) {
            return;
        }

        setChat(accountJid, userJid);
        notifyDataSetChanged();
    }

    private void setChat(@NonNull AccountJid accountJid, @NonNull UserJid userJid) {
        itemCount = 3;
        this.accountJid = accountJid;
        this.userJid = userJid;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == PAGE_POSITION_RECENT_CHATS) {
            return RecentChatFragment.newInstance();
        } else if (position == PAGE_POSITION_CHAT) {
            return ChatFragment.newInstance(accountJid, userJid);
        } else if (position == 2) {
            if (MUCManager.getInstance().hasRoom(accountJid, userJid))
                return OccupantListFragment.newInstance(accountJid, userJid.getBareUserJid());
            else return ContactVcardViewerFragment.newInstance(accountJid, userJid);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return itemCount;
    }


    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);

        finishUpdateListener.onChatViewAdapterFinishUpdate();
    }

    @Override
    public float getPageWidth(int position) {
        if (position == 0 || position == 2) return 0.85f;
        else return 1;
    }

    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }
}