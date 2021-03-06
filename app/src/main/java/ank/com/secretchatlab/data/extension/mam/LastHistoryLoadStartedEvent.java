package ank.com.secretchatlab.data.extension.mam;

import ank.com.secretchatlab.data.BaseChatEvent;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.BaseEntity;
import ank.com.secretchatlab.data.entity.UserJid;

public class LastHistoryLoadStartedEvent extends BaseChatEvent {
    public LastHistoryLoadStartedEvent(BaseEntity entity) {
        super(entity);
    }

    public AccountJid getAccount() {
        return getEntity().getAccount();
    }

    public UserJid getUser() {
        return getEntity().getUser();
    }
}
