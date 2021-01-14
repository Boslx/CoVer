package com.example.cover_a01.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "exposee")
public class Exposee {
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @Expose
    private Integer id;
    @ColumnInfo
    @SerializedName("key")
    @Expose
    private String key;

    public Exposee(String key) {
        this.key = key;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
