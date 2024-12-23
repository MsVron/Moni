package com.example.moni;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {User.class}, version = 2, exportSchema = false)  // Updated version to 2
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract UserDao userDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "moni_db"
                    ).addMigrations(MIGRATION_1_2)  // Added migration logic
                    .build();
        }
        return instance;
    }

    // Migration from version 1 to version 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users RENAME TO users_old");
            database.execSQL("CREATE TABLE users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "email TEXT,"
                    + "password TEXT,"
                    + "name TEXT)");
            database.execSQL("INSERT INTO users (id, email, password, name) SELECT id, email, password, name FROM users_old");
            database.execSQL("DROP TABLE users_old");
        }
    };
}
