package ank.com.secretchatlab.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.account.AccountItem;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.xaccount.XMPPAccountSettings;
import ank.com.secretchatlab.data.xaccount.XabberAccount;
import ank.com.secretchatlab.data.xaccount.XabberAccountManager;
import ank.com.secretchatlab.ui.color.ColorManager;

import java.util.List;

/**
 * Created by valery.miller on 20.07.17.
 */

public class XMPPAccountAdapter extends RecyclerView.Adapter {

    private List<XMPPAccountSettings> items;
    private boolean isAllChecked;
    private Context context;

    public XMPPAccountAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<XMPPAccountSettings> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean checked) {
        isAllChecked = checked;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new XMPPAccountVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_xmpp_account, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final XMPPAccountSettings account = items.get(position);
        XMPPAccountVH viewHolder = (XMPPAccountVH) holder;

        viewHolder.chkAccountSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                account.setSynchronization(isChecked);
            }
        });

        viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account.isSynchronization() || isAllChecked) {
                    if (account.getStatus() == XMPPAccountSettings.SyncStatus.localNewer) {
                        account.setStatus(XMPPAccountSettings.SyncStatus.remoteNewer);
                        account.setTimestamp(0);

                        // change settings
                        XMPPAccountSettings set = XabberAccountManager.getInstance().getAccountSettingsForSync(account.getJid());
                        if (set != null) {
                            account.setColor(set.getColor());
                        }
                    } else if (account.getStatus() == XMPPAccountSettings.SyncStatus.remoteNewer) {
                        account.setStatus(XMPPAccountSettings.SyncStatus.localNewer);
                        account.setTimestamp((int) (System.currentTimeMillis() / 1000L));

                        // change settings
                        AccountItem item = AccountManager.getInstance().getAccount(XabberAccountManager.getInstance().getExistingAccount(account.getJid()));
                        if (item != null) {
                            account.setColor(ColorManager.getInstance().convertIndexToColorName(item.getColorIndex()));
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });

        // set colors
        int colorId = ColorManager.getInstance().convertColorNameToId(account.getColor());
        viewHolder.color.setColorFilter(colorId);
        viewHolder.username.setTextColor(colorId);

        // set username
        if (account.getUsername() != null && !account.getUsername().isEmpty())
            viewHolder.username.setText(account.getUsername());
        else viewHolder.username.setText(account.getJid());

        // set jid
        //viewHolder.jid.setText(account.getJid());

        if (account.isSynchronization() || isAllChecked) {
            if (account.getStatus() != null) {
                switch (account.getStatus()) {
                    case local:
                        viewHolder.jid.setText(R.string.sync_status_local);
                        viewHolder.avatar.setImageResource(R.drawable.ic_sync_upload);
                        break;
                    case remote:
                        viewHolder.jid.setText(R.string.sync_status_remote);
                        viewHolder.avatar.setImageResource(R.drawable.ic_sync_download);
                        break;
                    case localNewer:
                        viewHolder.jid.setText(R.string.sync_status_local);
                        viewHolder.avatar.setImageResource(R.drawable.ic_sync_upload);
                        break;
                    case remoteNewer:
                        if (account.isDeleted()) {
                            viewHolder.jid.setText(R.string.sync_status_deleted);
                            viewHolder.avatar.setImageResource(R.drawable.ic_delete_grey);
                        } else {
                            viewHolder.jid.setText(R.string.sync_status_remote);
                            viewHolder.avatar.setImageResource(R.drawable.ic_sync_download);
                        }
                        break;
                    case localEqualsRemote:
                        viewHolder.jid.setText(R.string.sync_status_ok);
                        viewHolder.avatar.setImageResource(R.drawable.ic_sync_done);
                        break;
                    default:
                        break;
                }
            }
        } else {
            viewHolder.jid.setText(R.string.sync_status_no);
            viewHolder.avatar.setImageResource(R.drawable.ic_sync_disable);
            viewHolder.username.setTextColor(context.getResources().getColor(R.color.grey_500));
        }

        // set sync checkbox
        if (account.isSyncNotAllowed()) {
            viewHolder.jid.setText(R.string.sync_status_not_allowed);
            viewHolder.chkAccountSync.setEnabled(false);
        } else if (isAllChecked) {
            viewHolder.chkAccountSync.setEnabled(false);
            viewHolder.chkAccountSync.setChecked(true);
        } else {
            viewHolder.chkAccountSync.setEnabled(true);
            viewHolder.chkAccountSync.setChecked(account.isSynchronization());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class XMPPAccountVH extends RecyclerView.ViewHolder {

        ImageView color;
        ImageView avatar;
        TextView username;
        TextView jid;
        CheckBox chkAccountSync;


        XMPPAccountVH(View itemView) {
            super(itemView);
            color = (ImageView) itemView.findViewById(R.id.color);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.tvAccountName);
            jid = (TextView) itemView.findViewById(R.id.tvAccountJid);
            chkAccountSync = (CheckBox) itemView.findViewById(R.id.chkAccountSync);
            chkAccountSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyDataSetChanged();
                }
            });
        }

    }
}
