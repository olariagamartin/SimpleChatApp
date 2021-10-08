package com.themarto.mychatapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ContactEntity.class}, version = 1, exportSchema = false)
public abstract class ChatAppDatabase extends RoomDatabase {

    public abstract ContactDao contactDao();

    private static volatile ChatAppDatabase INSTANCE;
    private final static int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static ChatAppDatabase getDatabase (final Context context) {
        if (INSTANCE == null) {
            synchronized (ChatAppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context,
                        ChatAppDatabase.class, "chat_app_database")
                        .build();
            }
        }
        return INSTANCE;
    }
}
