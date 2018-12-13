package ank.com.secretchatlab.ui.adapter;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.log.LogManager;
import ank.com.secretchatlab.data.account.AccountItem;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.connection.ConnectionState;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.extension.avatar.AvatarManager;
import ank.com.secretchatlab.data.roster.RosterManager;
import ank.com.secretchatlab.data.xaccount.XMPPAccountSettings;
import ank.com.secretchatlab.data.xaccount.XabberAccountManager;
import ank.com.secretchatlab.ui.color.ColorManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class NavigationDrawerAccountAdapter extends BaseListEditorAdapter<AccountJid> {

    public NavigationDrawerAccountAdapter(Activity activity) {
        super(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        AccountManager accountManager = AccountManager.getInstance();
        if (convertView == null) {
            view = getActivity().getLayoutInflater().inflate(R.layout.contact_list_drawer_account_item, parent, false);
        } else {
            view = convertView;
        }
        AccountJid account = getItem(position);

        ((ImageView) view.findViewById(R.id.color)).setImageDrawable(new ColorDrawable((ColorManager.getInstance().getAccountPainter().getAccountMainColor(account))));
        ((ImageView) view.findViewById(R.id.avatar)).setImageDrawable(AvatarManager.getInstance().getAccountAvatar(account));

        TextView accountName = (TextView) view.findViewById(R.id.name);

        try {
            accountName.setText(RosterManager.getInstance().getBestContact(account, UserJid.from(accountManager.getVerboseName(account))).getName());
        } catch (UserJid.UserJidCreateException e) {
            LogManager.exception(this, e);
        }
        accountName.setTextColor(ColorManager.getInstance().getAccountPainter().getAccountTextColor(account));

        ((TextView) view.findViewById(R.id.account_jid)).setText(accountManager.getVerboseName(account));

        AccountItem accountItem = accountManager.getAccount(account);
        ConnectionState state;
        if (accountItem == null) {
            state = ConnectionState.offline;
        } else {
            state = accountItem.getState();
        }
        ((TextView) view.findViewById(R.id.status)).setText(getActivity().getString(state.getStringId()));
        return view;
    }

    @Override
    protected Collection<AccountJid> getTags() {
        List<AccountJid> list = new ArrayList<>();
        list.addAll(AccountManager.getInstance().getEnabledAccounts());
        Collections.sort(list);
        return list;
    }

}