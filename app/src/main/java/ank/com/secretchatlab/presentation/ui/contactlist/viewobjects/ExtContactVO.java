package ank.com.secretchatlab.presentation.ui.contactlist.viewobjects;

/**
 * Created by valery.miller on 06.02.18.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;

import ank.com.secretchatlab.R;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.extension.otr.OTRManager;
import ank.com.secretchatlab.data.message.NotificationState;
import ank.com.secretchatlab.data.roster.AbstractContact;
import ank.com.secretchatlab.utils.StringUtils;

import java.util.Date;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class ExtContactVO extends ContactVO {

    public ExtContactVO(int accountColorIndicator, int accountColorIndicatorBack,
                        boolean showOfflineShadow, String name,
                        String status, int statusId, int statusLevel, Drawable avatar,
                        int mucIndicatorLevel, UserJid userJid, AccountJid accountJid, int unreadCount,
                        boolean mute, NotificationState.NotificationMode notificationMode, String messageText,
                        boolean isOutgoing, Date time, int messageStatus, String messageOwner,
                        boolean archived, String lastActivity, ContactClickListener listener) {

        super(accountColorIndicator, accountColorIndicatorBack, showOfflineShadow, name, status,
                statusId, statusLevel, avatar,
                mucIndicatorLevel, userJid, accountJid, unreadCount, mute, notificationMode, messageText,
                isOutgoing, time, messageStatus, messageOwner, archived, lastActivity, listener);
    }

    public static ExtContactVO convert(AbstractContact contact, ContactClickListener listener) {
        ContactVO contactVO = ContactVO.convert(contact, listener);
        return new ExtContactVO(
                contactVO.getAccountColorIndicator(), contactVO.getAccountColorIndicatorBack(),
                contactVO.isShowOfflineShadow(),
                contactVO.getName(), contactVO.getStatus(), contactVO.getStatusId(),
                contactVO.getStatusLevel(), contactVO.getAvatar(), contactVO.getMucIndicatorLevel(),
                contactVO.getUserJid(), contactVO.getAccountJid(), contactVO.getUnreadCount(),
                contactVO.isMute(), contactVO.getNotificationMode(), contactVO.getMessageText(),
                contactVO.isOutgoing(), contactVO.getTime(), contactVO.getMessageStatus(),
                contactVO.getMessageOwner(), contactVO.isArchived(), contactVO.getLastActivity(),
                contactVO.listener);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_ext_contact_in_contact_list;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder viewHolder, int position, List<Object> payloads) {
        super.bindViewHolder(adapter, viewHolder, position, payloads);
        Context context = viewHolder.itemView.getContext();

        /** set up TIME of last message */
        viewHolder.tvTime.setText(StringUtils
                .getSmartTimeTextForRoster(context, getTime()));
        viewHolder.tvTime.setVisibility(View.VISIBLE);

        /** set up SENDER NAME */
        viewHolder.tvOutgoingMessage.setVisibility(View.GONE);
        if (isOutgoing()) {
            viewHolder.tvOutgoingMessage.setText(context.getString(R.string.sender_is_you) + ": ");
            viewHolder.tvOutgoingMessage.setVisibility(View.VISIBLE);
            //viewHolder.tvOutgoingMessage.setTextColor(getAccountColorIndicator());
        }

        if (getMessageOwner() != null && !getMessageOwner().trim().isEmpty()) {
            viewHolder.tvOutgoingMessage.setText(getMessageOwner() + ": ");
            viewHolder.tvOutgoingMessage.setVisibility(View.VISIBLE);
            //viewHolder.tvOutgoingMessage.setTextColor(getAccountColorIndicator());
        }

        /** set up MESSAGE TEXT */
        String text = getMessageText();
        if (text.isEmpty()) {
            viewHolder.tvMessageText.setText(R.string.no_messages);
            viewHolder.tvMessageText.
                    setTypeface(viewHolder.tvMessageText.getTypeface(), Typeface.ITALIC);
        } else {
            viewHolder.tvMessageText.setTypeface(Typeface.DEFAULT);
            viewHolder.tvMessageText.setVisibility(View.VISIBLE);
            if (OTRManager.getInstance().isEncrypted(text)) {
                viewHolder.tvMessageText.setText(R.string.otr_not_decrypted_message);
                viewHolder.tvMessageText.
                        setTypeface(viewHolder.tvMessageText.getTypeface(), Typeface.ITALIC);
            } else {
                viewHolder.tvMessageText.setText(Html.fromHtml(text));
                viewHolder.tvMessageText.setTypeface(Typeface.DEFAULT);
            }
        }

        /** set up MESSAGE STATUS */
        viewHolder.ivMessageStatus.setVisibility(text.isEmpty() ? View.INVISIBLE : View.VISIBLE);

        switch (getMessageStatus()) {
            case 0:
                viewHolder.ivMessageStatus.setImageResource(R.drawable.ic_message_delivered_14dp);
                break;
            case 3:
                viewHolder.ivMessageStatus.setImageResource(R.drawable.ic_message_has_error_14dp);
                break;
            case 4:
                viewHolder.ivMessageStatus.setImageResource(R.drawable.ic_message_acknowledged_14dp);
                break;
            default:
                viewHolder.ivMessageStatus.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
