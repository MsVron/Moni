package com.example.moni;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface IncomeDao {
    @Insert
    void insert(Income income);

    @Query("SELECT * FROM income WHERE userId = :userId ORDER BY date DESC")
    List<Income> getAllIncome(int userId);

    @Query("SELECT SUM(amount) FROM income WHERE userId = :userId")
    double getTotalIncome(int userId);

    @Query("SELECT * FROM income WHERE userId = :userId AND category = :category")
    List<Income> getIncomeByCategory(int userId, String category);

    @Query("SELECT category, SUM(amount) as total FROM income " +
            "WHERE userId = :userId GROUP BY category")
    List<CategoryTotal> getIncomeTotalsByCategory(int userId);

    @Query("SELECT * FROM income WHERE userId = :userId AND isRecurring = 1")
    List<Income> getRecurringIncome(int userId);
}