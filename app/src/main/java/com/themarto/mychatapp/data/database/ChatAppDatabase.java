package com.themarto.mychatapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ContactEntity.class, MessageEntity.class}, version = 2, exportSchema = false)
public abstract class ChatAppDatabase extends RoomDatabase {

    public abstract ContactDao contactDao();
    public abstract MessageDao messageDao();

    private static volatile ChatAppDatabase INSTANCE;
    private final static int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ChatAppDatabase getDatabase (final Context context) {
        if (INSTANCE == null) {
            synchronized (ChatAppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context,
                        ChatAppDatabase.class, "chat_app_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }
}
