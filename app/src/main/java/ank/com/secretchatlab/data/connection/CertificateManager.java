package ank.com.secretchatlab.data.connection;


import android.app.Activity;
import android.support.annotation.NonNull;

import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.account.AccountItem;
import ank.com.secretchatlab.data.account.listeners.OnAccountRemovedListener;
import ank.com.secretchatlab.data.entity.AccountJid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.duenndns.ssl.MemorizingTrustManager;

public class CertificateManager implements OnAccountRemovedListener {

    private static CertificateManager instance;

    public static CertificateManager getInstance() {
        if (instance == null) {
            instance = new CertificateManager();
        }

        return instance;
    }

    private Map<AccountJid, MemorizingTrustManager> memorizingTrustManagerMap;
    private Map<AccountJid, MemorizingTrustManager> fileUploadMap;

    private CertificateManager() {
        this.memorizingTrustManagerMap = new ConcurrentHashMap<>();
        this.fileUploadMap = new ConcurrentHashMap<>();
    }

    @NonNull
    MemorizingTrustManager getNewMemorizingTrustManager(@NonNull final AccountJid accountJid) {
        MemorizingTrustManager mtm = new MemorizingTrustManager(Application.getInstance());
        memorizingTrustManagerMap.put(accountJid, mtm);
        return mtm;
    }

    @NonNull
    public MemorizingTrustManager getNewFileUploadManager(@NonNull final AccountJid accountJid) {
        MemorizingTrustManager mtm = new MemorizingTrustManager(Application.getInstance());
        fileUploadMap.put(accountJid, mtm);
        return mtm;
    }

    public void registerActivity(Activity activity) {
        for (MemorizingTrustManager memorizingTrustManager : memorizingTrustManagerMap.values()) {
            memorizingTrustManager.bindDisplayActivity(activity);
        }

        for (MemorizingTrustManager memorizingTrustManager : fileUploadMap.values()) {
            memorizingTrustManager.bindDisplayActivity(activity);
        }
    }

    public void unregisterActivity(Activity activity) {
        for (MemorizingTrustManager memorizingTrustManager : memorizingTrustManagerMap.values()) {
            memorizingTrustManager.unbindDisplayActivity(activity);
        }

        for (MemorizingTrustManager memorizingTrustManager : fileUploadMap.values()) {
            memorizingTrustManager.unbindDisplayActivity(activity);
        }
    }

    @Override
    public void onAccountRemoved(AccountItem accountItem) {
        memorizingTrustManagerMap.remove(accountItem.getAccount());
        fileUploadMap.remove(accountItem.getAccount());
    }
}
