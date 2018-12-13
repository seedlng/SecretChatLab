package ank.com.secretchatlab.data.extension.vcard;


import ank.com.secretchatlab.data.BaseUIListener;
import ank.com.secretchatlab.data.entity.AccountJid;

public interface OnVCardSaveListener extends BaseUIListener {
    void onVCardSaveSuccess(AccountJid account);
    void onVCardSaveFailed(AccountJid account);
}
