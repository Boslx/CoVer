package com.example.cover_a01.bluetooth;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.cover_a01.MainActivity;

/**
 * starts the Bluetooth Communication, when the service is started.
 */

public class BackgroundServiceReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")

    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context, MainActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
