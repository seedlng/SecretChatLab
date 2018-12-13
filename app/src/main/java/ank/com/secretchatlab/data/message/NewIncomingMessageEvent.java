package ank.com.secretchatlab.data.message;

import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;

public class NewIncomingMessageEvent {
    private final AccountJid account;
    private final UserJid user;

    public NewIncomingMessageEvent(AccountJid account, UserJid user) {
        this.account = account;
        this.user = user;
    }

    public AccountJid getAccount() {
        return account;
    }

    public UserJid getUser() {
        return user;
    }
}
