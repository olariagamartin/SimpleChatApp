package com.themarto.mychatapp.data.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "contacts_table")
public class ContactEntity {
    @PrimaryKey
    @NonNull
    public String id;
    @ColumnInfo
    public String name;
    @ColumnInfo
    public byte[] profileImage;
    @ColumnInfo
    public boolean online;
}
