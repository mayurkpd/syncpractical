package webpract.com.practical.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import webpract.com.practical.Util;

public class MyStartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Util.scheduleJob(context);
    }
}