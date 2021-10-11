package com.themarto.mychatapp.data.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "messages_table")
public class MessageEntity {

    @ColumnInfo
    public String message;
    @ColumnInfo
    public String senderId;
    @ColumnInfo
    public String receiverId;
    @ColumnInfo
    @NonNull
    public String chatRoom;
    @ColumnInfo
    public long timestamp;
}
