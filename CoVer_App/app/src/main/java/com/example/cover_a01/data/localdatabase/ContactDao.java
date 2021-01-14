package com.example.cover_a01.data.localdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cover_a01.data.model.Contact;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contact")
    List<Contact> getAll();

    @Query("SELECT COUNT(*) from contact")
    int countUsers();

    @Query("SELECT COUNT(*) from contact where `key` = :key")
    int contactWithKey(String key);

//    @Query("DELETE FROM contact WHERE contactDate < :date")
//    void deleteOlderThan(long date);

    @Query("DELETE FROM contact")
    void nukeTable();

    @Insert
    void insert(Contact users);

    @Delete
    void delete(Contact user);
}
