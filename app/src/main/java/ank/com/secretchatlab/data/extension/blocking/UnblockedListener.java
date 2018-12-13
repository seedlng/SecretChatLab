package ank.com.secretchatlab.data.extension.blocking;

import ank.com.secretchatlab.data.entity.AccountJid;

import org.jivesoftware.smackx.blocking.JidsUnblockedListener;
import org.jxmpp.jid.Jid;

import java.util.List;

public class UnblockedListener implements JidsUnblockedListener {
    private AccountJid account;

    public UnblockedListener(AccountJid account) {
        this.account = account;
    }

    @Override
    public void onJidsUnblocked(List<Jid> unblockedJids) {
        BlockingManager.notify(account);
    }
}
