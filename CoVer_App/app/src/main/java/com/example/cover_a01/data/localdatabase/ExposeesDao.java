package com.example.cover_a01.data.localdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cover_a01.data.model.Exposee;

import java.util.List;

@Dao
public interface ExposeesDao {
    @Query("SELECT * FROM exposee")
    List<Exposee> getAll();

    @Query("SELECT COUNT(*) from exposee")
    int countUsers();

    @Insert
    void insert(Exposee users);

    @Delete
    void delete(Exposee user);

    @Query("DELETE FROM exposee")
    void nukeTable();
}
