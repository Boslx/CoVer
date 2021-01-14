package com.example.cover_a01.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact")
public class Contact {
    public Contact(String key, long contactDate) {
        this.key = key;
        this.contactDate = contactDate;
        this.knownExposee = false;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public long getContactDate() {
        return contactDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isKnownExposee() { return knownExposee; }

    public void setKnownExposee(boolean knownExposee) { this.knownExposee = knownExposee; }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String key;

    @ColumnInfo
    private long contactDate;

    @ColumnInfo
    private boolean knownExposee;
}
