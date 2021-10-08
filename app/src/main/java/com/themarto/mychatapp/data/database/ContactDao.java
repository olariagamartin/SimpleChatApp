package com.themarto.mychatapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao {

    @Insert
    void insertContacts(List<ContactEntity> contactList);

    @Query("SELECT * FROM contacts_table")
    LiveData<List<ContactEntity>> getContactList();
}
