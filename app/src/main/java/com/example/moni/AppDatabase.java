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
        Offer.class,
        SubscriptionPlan.class,
        PremiumFeature.class
}, version = 6, exportSchema = false)
@TypeConverters({Converters.class})  // Add this line
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract IncomeDao incomeDao();
    public abstract ExpenseDao expenseDao();
    public abstract OfferDao offerDao();
    public abstract SubscriptionPlanDao subscriptionPlanDao();
    public abstract PremiumFeatureDao premiumFeatureDao();

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
                            MIGRATION_4_5,
                            MIGRATION_5_6
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

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

    // Migration 4 to 5
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS subscription_plans ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "title TEXT NOT NULL,"
                    + "price REAL NOT NULL,"
                    + "discountedPrice REAL,"
                    + "description TEXT,"
                    + "isDiscounted INTEGER NOT NULL DEFAULT 0,"
                    + "isActive INTEGER NOT NULL DEFAULT 1)");
        }
    };

    // Migration 5 to 6
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS premium_features ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "name TEXT NOT NULL,"
                    + "description TEXT,"
                    + "isActive INTEGER NOT NULL DEFAULT 1)");
        }
    };
}
