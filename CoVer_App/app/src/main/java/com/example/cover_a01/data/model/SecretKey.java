package com.example.cover_a01.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "secretKey")
public class SecretKey {
    public SecretKey(String key){
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String key;
}
