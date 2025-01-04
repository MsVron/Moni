package com.example.moni;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IncomeDao {
    @Insert
    void insert(Income income);

    // Retrieve all income records for a user, ordered by date in descending order
    @Query("SELECT * FROM income WHERE userId = :userId ORDER BY strftime('%Y-%m-%d', date) DESC")
    List<Income> getAllIncome(int userId);

    // Calculate the total income amount for a user
    @Query("SELECT SUM(amount) FROM income WHERE userId = :userId")
    double getTotalIncome(int userId);

    // Retrieve income records by category for a user, ordered by date in descending order
    @Query("SELECT * FROM income WHERE userId = :userId AND category = :category ORDER BY strftime('%Y-%m-%d', date) DESC")
    List<Income> getIncomeByCategory(int userId, String category);

    // Retrieve the total income grouped by category for a user
    @Query("SELECT category, SUM(amount) as total FROM income " +
            "WHERE userId = :userId GROUP BY category")
    List<CategoryTotal> getIncomeTotalsByCategory(int userId);

    // Retrieve recurring income records for a user
    @Query("SELECT * FROM income WHERE userId = :userId AND isRecurring = 1")
    List<Income> getRecurringIncome(int userId);

    @Delete
    void delete(Income income);

    // Delete an income record by ID
    @Query("DELETE FROM income WHERE id = :incomeId")
    void deleteById(long incomeId);

    @Update
    void update(Income income);
}