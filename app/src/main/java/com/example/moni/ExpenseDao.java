package com.example.moni;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insert(Expense expense);

    @Query("SELECT * FROM expense WHERE userId = :userId ORDER BY date DESC")
    List<Expense> getAllExpenses(int userId);

    @Query("SELECT SUM(amount) FROM expense WHERE userId = :userId")
    double getTotalExpense(int userId);
}