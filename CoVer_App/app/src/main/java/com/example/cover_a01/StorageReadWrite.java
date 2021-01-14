package com.example.cover_a01;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.UUID;

/**
 * Read + Write Ids on the phones storage
 * deprecated since the introduction of DAO database
 */

public class StorageReadWrite {
    private static final String TAG = "fileReadWrite  class";

    private JSONObject closeProximityUUIDs = new JSONObject();

    /**
     * Writes given variable data to file at filename in androids storage.
     */
    static void writeToFile(String data, String filename, Context context) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(context.getFilesDir() + "/" + filename));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Loads data from given filename.
     */
    static String loadFromFile(Context context, String filename) {
        String userData = null;

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString =  bufferedReader.readLine();
                inputStream.close();

                userData = receiveString;
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Cannot read file: " + e.toString());
        }

        return userData;
    }

    /**
     * Loads in close proximity UUIDs and adds the new UUID to the JSON unless the UUID is
     * already in the file.
     */
    private void writeUuidsToFile(UUID uuid, Context context) throws JSONException, InterruptedException {
        //Read in proximity UUIDs file
        try {
            closeProximityUUIDs = new JSONObject(StorageReadWrite.loadFromFile(context.getApplicationContext(), "proximityUuids.json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //If the UUID is already in ArrayList, don't write the value again
        assert uuid != null;
        if (!closeProximityUUIDs.toString().contains(uuid.toString())) {
            closeProximityUUIDs.put(uuid.toString(), new Date().getTime());

            //Write proximity uuids back to file
            StorageReadWrite.writeToFile(closeProximityUUIDs.toString(), "proximityUuids.json", context.getApplicationContext());
            Log.i(TAG, "Adding UUID to file : " + uuid);
        }
    }

    /**
     * sample method to POST to API
     */
    private void postUUID(){
        //put the API access code here
    }
}
