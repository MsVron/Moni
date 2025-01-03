package com.example.moni;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {
        User.class,
        Income.class,
        Expense.class,
        Offer.class
}, version = 7, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract IncomeDao incomeDao();
    public abstract ExpenseDao expenseDao();
    public abstract OfferDao offerDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "moni_db"
                    )
                    .addMigrations(
                            MIGRATION_1_2,
                            MIGRATION_2_3,
                            MIGRATION_3_4,
                            MIGRATION_6_7
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    // Migrations 1-2, 2-3, and 3-4 remain unchanged since they're for core functionality

    // Migration 1 to 2
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

    // Migration 2 to 3
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

    // Migration 3 to 4
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS offers ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "title TEXT NOT NULL,"
                    + "description TEXT,"
                    + "endTime INTEGER NOT NULL,"
                    + "imageUrl TEXT,"
                    + "isActive INTEGER NOT NULL DEFAULT 1)");
        }
    };

    // Migration 6 to 7 (expense table update)
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create new table with updated schema
            database.execSQL("CREATE TABLE IF NOT EXISTS expense_new ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "userId INTEGER NOT NULL, "
                    + "amount REAL NOT NULL, "
                    + "category TEXT NOT NULL, "
                    + "subcategory TEXT NOT NULL, "
                    + "date TEXT NOT NULL, "
                    + "description TEXT, "
                    + "color TEXT, "
                    + "currency TEXT NOT NULL, "
                    + "isRecurring INTEGER NOT NULL DEFAULT 0, "
                    + "recurringPeriod TEXT)");

            // Copy data from old table to new table
            database.execSQL(
                    "INSERT INTO expense_new (id, userId, amount, category, subcategory, date, description, color, currency) "
                            + "SELECT id, userId, amount, type, 'General', date, description, color, currency FROM expense"
            );

            // Drop the old table
            database.execSQL("DROP TABLE expense");

            // Rename new table
            database.execSQL("ALTER TABLE expense_new RENAME TO expense");
        }
    };
}