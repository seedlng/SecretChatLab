package ank.com.secretchatlab.data.extension.blocking;

import ank.com.secretchatlab.data.BaseUIListener;
import ank.com.secretchatlab.data.entity.AccountJid;

public interface OnBlockedListChangedListener extends BaseUIListener{
    void onBlockedListChanged(AccountJid account);
}
