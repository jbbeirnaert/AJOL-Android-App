package com.ajol.ajolpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class RefreshAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            //Owen: for testing, emit beep sound
            Uri beepUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone beep = RingtoneManager.getRingtone(context, beepUri);
            beep.play();
        }
        catch (Exception exception) {
            //do nothing
        }
    }
}
