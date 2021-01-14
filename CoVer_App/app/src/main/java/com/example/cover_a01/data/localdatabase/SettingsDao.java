package com.example.cover_a01.data.localdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cover_a01.data.model.Settings;

import java.util.List;

@Dao
public interface SettingsDao {
    @Query("SELECT * FROM settings")
    List<Settings> getAll();

    @Query("SELECT COUNT(*) from settings")
    int countUsers();

    @Insert
    void insert(Settings settings);

    @Delete
    void delete(Settings settings);

    @Query("DELETE FROM settings")
    void nukeTable();
}
