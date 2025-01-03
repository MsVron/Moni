package com.example.moni;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {
    // Insert a new expense
    @Insert
    void insert(Expense expense);

    // Retrieve all expenses for a user, ordered by date in descending order
    @Query("SELECT * FROM expense WHERE userId = :userId ORDER BY date DESC")
    List<Expense> getAllExpenses(int userId);

    // Calculate the total expense amount for a user
    @Query("SELECT SUM(amount) FROM expense WHERE userId = :userId")
    double getTotalExpense(int userId);

    // Retrieve expenses by category for a user
    @Query("SELECT * FROM expense WHERE userId = :userId AND category = :category")
    List<Expense> getExpensesByCategory(int userId, String category);

    // Retrieve the total expenses grouped by category for a user
    @Query("SELECT category, SUM(amount) as total FROM expense " +
            "WHERE userId = :userId GROUP BY category")
    List<CategoryTotal> getExpenseTotalsByCategory(int userId);

    // Retrieve recurring expenses for a user
    @Query("SELECT * FROM expense WHERE userId = :userId AND isRecurring = 1")
    List<Expense> getRecurringExpenses(int userId);

    // Calculate the total expense for a specific category for a user
    @Query("SELECT SUM(amount) FROM expense " +
            "WHERE userId = :userId AND category = :category")
    double getTotalExpenseByCategory(int userId, String category);

    // Retrieve expenses within a specific date range for a user
    @Query("SELECT * FROM expense WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    List<Expense> getExpensesByDateRange(int userId, String startDate, String endDate);

    // Retrieve the highest expense recorded for a user
    @Query("SELECT * FROM expense WHERE userId = :userId ORDER BY amount DESC LIMIT 1")
    Expense getHighestExpense(int userId);

    // Retrieve the lowest expense recorded for a user
    @Query("SELECT * FROM expense WHERE userId = :userId ORDER BY amount ASC LIMIT 1")
    Expense getLowestExpense(int userId);
}