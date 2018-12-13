package ank.com.secretchatlab.presentation.mvp.contactlist;

import android.view.ContextMenu;
import android.view.View;

import ank.com.secretchatlab.data.account.CommonState;
import ank.com.secretchatlab.data.roster.AbstractContact;
import ank.com.secretchatlab.presentation.ui.contactlist.viewobjects.ButtonVO;

import java.util.List;

import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Created by valery.miller on 02.02.18.
 */

public interface ContactListView {

    void updateItems(List<IFlexible> items);
    void onContactClick(AbstractContact contact);
    void onItemContextMenu(int adapterPosition, ContextMenu menu);
    void onContactAvatarClick(int adapterPosition);
    void onAccountAvatarClick(int adapterPosition);
    void onAccountMenuClick(int adapterPosition, View view);
    void onButtonItemClick(ButtonVO buttonVO);
    void closeSnackbar();
    void closeSearch();
    void showPlaceholder(String message);
    void hidePlaceholder();
    void onContactListChanged(CommonState commonState, boolean hasContacts,
                              boolean hasVisibleContacts, boolean isFilterEnabled);
    void startAddContactActivity();
    void startJoinConferenceActivity();
    void startSetStatusActivity();
}
