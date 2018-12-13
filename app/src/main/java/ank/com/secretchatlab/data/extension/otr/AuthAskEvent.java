package ank.com.secretchatlab.data.extension.otr;

import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;

/**
 * Created by valery.miller on 04.07.17.
 */

public class AuthAskEvent {

    private AccountJid account;
    private UserJid user;

    public AuthAskEvent(AccountJid account, UserJid user) {
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
