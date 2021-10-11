package com.themarto.mychatapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(List<MessageEntity> messageList);

    @Query("SELECT * FROM messages_table WHERE chatRoom = :chatRoom")
    LiveData<List<MessageEntity>> getMessages(String chatRoom);
}
