package com.example.moni;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class, Income.class, Expense.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract IncomeDao incomeDao();
    public abstract ExpenseDao expenseDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "moni_db"
                    )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
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

    // Migration from version 2 to version 3 (adding expense table)
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS expense ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "userId INTEGER NOT NULL,"
                    + "amount REAL NOT NULL,"
                    + "type TEXT,"
                    + "date TEXT,"
                    + "description TEXT,"
                    + "color TEXT,"
                    + "currency TEXT)");
        }
    };
}