package com.example.cover_a01.data.localdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cover_a01.data.model.SecretKey;

import java.util.List;

@Dao
public interface SecretKeyDao {

    @Query("SELECT * FROM secretKey")
    List<SecretKey> getAll();

    @Query("SELECT COUNT(*) from secretKey")
    int countUsers();

    @Insert
    void insert(SecretKey users);

    @Delete
    void delete(SecretKey user);

    @Query("DELETE FROM secretKey")
    void nukeTable();
}
