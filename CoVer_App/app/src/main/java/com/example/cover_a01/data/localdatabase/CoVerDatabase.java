package com.example.cover_a01.data.localdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cover_a01.data.model.InfectionStatus;
import com.example.cover_a01.data.model.Contact;
import com.example.cover_a01.data.model.Exposee;
import com.example.cover_a01.data.model.SecretKey;
import com.example.cover_a01.data.model.Settings;


@Database(entities = {Exposee.class, Contact.class, SecretKey.class, Settings.class}, version = 1)
public abstract class CoVerDatabase extends RoomDatabase {

    private static CoVerDatabase INSTANCE;

    public abstract ExposeesDao exposeesDao();
    public abstract ContactDao contactDao();
    public abstract SecretKeyDao secretKeyDao();
    public abstract SettingsDao settingsDao();

    public static void setupAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), CoVerDatabase.class, "cover-db_3")
                            .allowMainThreadQueries()
                            .build();

            if(INSTANCE.settingsDao().getAll().isEmpty()){
                Settings firstSettings = new Settings();
                firstSettings.setLastInfectionStatus(InfectionStatus.calculating.getValue());
                INSTANCE.settingsDao().insert(new Settings());
            }
        }
    }

    public static CoVerDatabase getAppDatabase(){
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
