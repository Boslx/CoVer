package com.example.cover_a01.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PowerButtonBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //The screen off position
            Toast.makeText(context,"Screen is off", Toast.LENGTH_LONG).show();
            Log.d("test_bles","screen off");
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //The screen on position
            Toast.makeText(context,"Screen is on", Toast.LENGTH_LONG).show();
            Log.d("test_bles","screen on");
        }
    }
}
