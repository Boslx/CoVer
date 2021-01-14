package com.example.cover_a01.bluetooth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cover_a01.App;
import com.example.cover_a01.R;
import com.example.cover_a01.data.localdatabase.CoVerDatabase;
import com.example.cover_a01.data.model.Contact;
import com.example.cover_a01.data.model.SecretKey;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

public class BluetoothFragment extends Fragment implements BeaconConsumer, View.OnClickListener {

    private View root;

    //defines the refresh ID interval

    private int seconds = App.getApp().getResources().getInteger(R.integer.RefreshIDIntervallSeconds);
    private Handler taskHandler = new android.os.Handler();

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private TextView myIdView;
    private EditText customID;
    private TextView externalList;

    private Button button_generateNewID;
    private Button button_generateNewID_custom;

    public static final String CHANNEL_ID = "exampleForegroundServiceChannel";
    public static final String sysIdKey = "sharedPrefKey";
    private BeaconManager beaconManager;
    Set<String> remote_beacon_ids_set;

    private CoVerDatabase coVerDatabase;

    private Context contextStored;

    private Timer timer;
    private static BluetoothFragment bluetoothFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        preferences = this.getActivity().getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE);
        editor = preferences.edit();
        contextStored = getApplicationContext();

        /**
         * Bluetooth Tracing Initiate
         */
        myIdView = root.findViewById(R.id.myid);
        customID = root.findViewById(R.id.customUUID);
        externalList = root.findViewById(R.id.externalList);

        beaconManager = BeaconManager.getInstanceForApplication(this.getContext());
        verifyBluetooth();

        String uid = getSystemID();
        remote_beacon_ids_set.add("Internal Phone ID : "+ uid);
        myIdView.setText(uid);
        startServiceBroadcast(uid);

        //new key gets added to the Database
        coVerDatabase.secretKeyDao().insert(new SecretKey(uid));

        //initialize onClick listeners ( dev UI )
        button_generateNewID = (Button) root.findViewById(R.id.btn_generateNewID);
        button_generateNewID.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                generateNewID();
            }
        });
        button_generateNewID_custom = (Button) root.findViewById(R.id.btn_generateNewID_custom);
        button_generateNewID_custom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                generateNewID_custom();
            }
        });
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coVerDatabase = CoVerDatabase.getAppDatabase();
        remote_beacon_ids_set = new HashSet<String>();
        bluetoothFragment = this;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
                                      @Override
                                      public void run(){
                                          generateNewID();
                                      }
                                  },
                5000,
                seconds*1000);
         */
        startHandlerRepeatNewID();
    }

    @Override
    public void onResume() {
        super.onResume();
        App application = ((App) this.getActivity().getApplicationContext());
        beaconManager.bind(this);
        application.setMonitoringActivity(this);
        updateLog(application.getLog());
        contextStored = getApplicationContext();
    }

    @Override
    public void onPause(){
        super.onPause();
        contextStored = getApplicationContext();
    }


    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(getApplicationContext()).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please restart this App with Bluetooth enabled.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //finish();
                        //System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("This device does not support Bluetooth Low Energy.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    //finish();
                    //System.exit(0);
                }

            });
            builder.show();

        }

    }

    /**
     * generates the UUID for the Internal ID
     */
    private String getSystemID(){
        String systemID = Utilities.generateUidNamespace();
        editor.putString(sysIdKey, systemID);
        editor.commit();
        return systemID;
    }

    private Intent serviceIntent;
    private void startServiceBroadcast(String uid) {
        if(serviceIntent!=null){
            contextStored.stopService(serviceIntent);
        }
        serviceIntent = new Intent(contextStored, BackgroundServiceTransmitter.class);
        serviceIntent.putExtra(sysIdKey, uid);
        ContextCompat.startForegroundService(contextStored, serviceIntent);
    }

    /**
     * to be called by a button (testing) and automatically on a timer / loading homescreen
     */
    public void generateNewID(){
        //only generate a new key when tracking is enabled
        if(preferences.getBoolean("trackingOn",true)) {
            button_generateNewID.setBackgroundColor(getResources().getColor(R.color.blue_NICE));
            button_generateNewID.setText("GENERATE RANDOM ID");
            String uid = getSystemID();
            startServiceBroadcast(uid);
            myIdView.setText(uid);
            coVerDatabase.secretKeyDao().insert(new SecretKey(uid));
        }else{
            button_generateNewID.setBackgroundColor(getResources().getColor(R.color.red_NICE));
            button_generateNewID.setText("tracking disabled");
        }
    }

    /**
     * same as generateNewID but with a custom value from a textfield CAN ONLY BE HEX VALUE 0-9,a-f
     */
    public void generateNewID_custom(){
        //only generate a new key when tracking is enabled
        if(preferences.getBoolean("trackingOn",true)) {
            button_generateNewID_custom.setBackgroundColor(getResources().getColor(R.color.blue_NICE));
            button_generateNewID_custom.setText("GENERATE FROM CUSTOM HEX");
            String uid = customID.getText().toString();
            startServiceBroadcast(uid);
            myIdView.setText(uid);
            coVerDatabase.secretKeyDao().insert(new SecretKey(uid));
        }else{
            button_generateNewID_custom.setBackgroundColor(getResources().getColor(R.color.red_NICE));
            button_generateNewID_custom.setText("tracking disabled");
        }
    }



    public void updateLog(final String log) {
        Log.i("Log Adapter: ",log);
    }

    @Override
    public void onBeaconServiceConnect() {

        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.d("test_ble", "didRangeBeaconsInRegion called with beacon count:  "+beacons.size());
                    Beacon firstBeacon = beacons.iterator().next();
                    String beaconLog = "The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.";
                    Log.d("test_ble_Id1",String.valueOf(firstBeacon.getId1()));
                    String beaconID = String.valueOf(firstBeacon.getId1());
                    String externalID = beaconID.substring(2,beaconID.length());

                    externalList.setText("External Phone ID : "+ externalID + "\nEstimated distance "+firstBeacon.getDistance()+"\n"+externalList.getText());

                    //add recieved Key to the Database, only if within 5meter distance and tracking is enabled
                    if(firstBeacon.getDistance()<=(float)App.getApp().getResources().getInteger(R.integer.maxDistanceFromPhoneMeters)
                            && preferences.getBoolean("trackingOn", true)){
                        if (coVerDatabase.contactDao().contactWithKey(externalID)==0) {
                            coVerDatabase.contactDao().insert(new Contact(externalID, Calendar.getInstance().getTime().getTime()));

                            List<Contact> test = coVerDatabase.contactDao().getAll(); }
                    }
                    Log.d("test_ble",beaconLog);
                }
            }
        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {   }
    }

    @Override
    public Context getApplicationContext() {
        return this.getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }

    @Override
    public void onClick(View view) {
    }


    /**
     * runnable to generate a new ID every $seconds
     */
    private Runnable repeatNewID = new Runnable() {
        public void run() {
            //only generate a new key when tracking is enabled
            generateNewID();
            taskHandler.postDelayed(repeatNewID, 1000 * seconds);
        }
    };

    void startHandlerRepeatNewID() {
        taskHandler.postDelayed(repeatNewID, 1000 * seconds);
    }

    void stopHandlerRepeatNewID() {
        taskHandler.removeCallbacks(repeatNewID);
    }

    public static BluetoothFragment getBluetoothFragment(){
        return bluetoothFragment;
    }

}