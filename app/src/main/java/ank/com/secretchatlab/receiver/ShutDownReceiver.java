package ank.com.secretchatlab.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ank.com.secretchatlab.data.Application;

public class ShutDownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Application.getInstance().requestToClose();
    }
}
