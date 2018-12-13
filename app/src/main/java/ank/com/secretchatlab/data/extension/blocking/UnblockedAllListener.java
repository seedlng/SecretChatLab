package ank.com.secretchatlab.data.extension.blocking;

import ank.com.secretchatlab.data.entity.AccountJid;

import org.jivesoftware.smackx.blocking.AllJidsUnblockedListener;

public class UnblockedAllListener implements AllJidsUnblockedListener {
    private AccountJid account;

    public UnblockedAllListener(AccountJid account) {
        this.account = account;
    }

    @Override
    public void onAllJidsUnblocked() {
        BlockingManager.notify(account);
    }
}
