package ank.com.secretchatlab.ui.widget.bottomnavigation;

import android.graphics.drawable.Drawable;

import ank.com.secretchatlab.data.account.AccountItem;
import ank.com.secretchatlab.data.account.AccountManager;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.extension.avatar.AvatarManager;
import ank.com.secretchatlab.ui.color.ColorManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by valery.miller on 12.10.17.
 */

public class AccountShortcutVO {

    private Drawable avatar;
    private int statusLevel;
    private AccountJid accountJid;
    private int accountColorIndicator;

    public AccountShortcutVO(Drawable avatar, int statusLevel, AccountJid accountJid, int accountColorIndicator) {
        this.avatar = avatar;
        this.statusLevel = statusLevel;
        this.accountJid = accountJid;
        this.accountColorIndicator = accountColorIndicator;
    }

    public static AccountShortcutVO convert(AccountJid accountJid) {
        Drawable avatar = AvatarManager.getInstance().getAccountAvatar(accountJid);
        int statusLevel = 0;

        AccountItem accountItem = AccountManager.getInstance().getAccount(accountJid);
        if (accountItem != null) {
            statusLevel = accountItem.getDisplayStatusMode().getStatusLevel();
        }

        int accountColorIndicator = ColorManager.getInstance().getAccountPainter().getAccountMainColor(accountJid);
        return new AccountShortcutVO(avatar, statusLevel, accountJid, accountColorIndicator);
    }

    public static ArrayList<AccountShortcutVO> convert(Collection<AccountJid> accounts) {
        ArrayList<AccountShortcutVO> items = new ArrayList<>();
        for (AccountJid account : accounts) {
            items.add(convert(account));
        }
        return items;
    }

    public Drawable getAvatar() {
        return avatar;
    }

    public int getStatusLevel() {
        return statusLevel;
    }

    public AccountJid getAccountJid() {
        return accountJid;
    }

    public int getAccountColorIndicator() {
        return accountColorIndicator;
    }
}
