package com.example.cover_a01.bluetooth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.cover_a01.MainActivity;
import com.example.cover_a01.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;


public class BackgroundServiceTransmitter extends Service {

    private BeaconTransmitter beaconTransmitter;

    @Override
    public void onCreate() {
        super.onCreate();
        PowerButtonBroadcastReceiver broadcastReceiver = new PowerButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(); intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        if (broadcastReceiver != null) {
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String inString = intent.getStringExtra("inputExtra");
        String sysId = intent.getStringExtra(BluetoothFragment.sysIdKey);
        Log.i("myid","system id recieved "+ sysId);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String NOTIFICATION_CHANNEL_ID = "cover_channel_id_01";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, "CoVer notification", NotificationManager.IMPORTANCE_LOW
            );

            notificationChannel.setDescription("");
            notificationChannel.setSound(null,null);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("ID: "+sysId)
                .setContentText(inString)
                .setSmallIcon(R.drawable.zwerghamster_logo_pushnotification_blackwhite)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(Notification.FLAG_ONGOING_EVENT, notification);
        startBroadcast(sysId);
        return START_STICKY;
    }

    public  void startBroadcast(String sysid){
        Log.d("test_ble","starting broadcast function");
        Beacon beacon = new Beacon.Builder()
                .setId1(sysid)
                .setId2("1")
                .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[] {0l})) // Remove this for beacon layouts without d: fields
                .build();
// Change the layout below for other beacon types
        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout(  BeaconParser.EDDYSTONE_UID_LAYOUT);

        beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {

            @Override
            public void onStartFailure(int errorCode) {
                Log.e("test_ble", "Advertisement start failed with code: "+errorCode);
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i("test_ble", "Advertisement start succeeded.");
                Log.d("test_ble", settingsInEffect.toString());
            }
        });
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconTransmitter.stopAdvertising();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}