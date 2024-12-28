package com.example.moni;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface SubscriptionPlanDao {
    @Insert
    void insert(SubscriptionPlan plan);

    @Update
    void update(SubscriptionPlan plan);

    @Query("SELECT * FROM subscription_plans WHERE isActive = 1")
    List<SubscriptionPlan> getActivePlans();

    @Query("SELECT * FROM subscription_plans WHERE id = :planId")
    SubscriptionPlan getPlanById(int planId);
}