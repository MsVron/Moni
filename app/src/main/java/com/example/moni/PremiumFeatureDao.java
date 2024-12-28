package com.example.moni;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PremiumFeatureDao {
    @Insert
    void insert(PremiumFeature feature);

    @Query("SELECT * FROM premium_features WHERE isActive = 1")
    List<PremiumFeature> getActiveFeatures();
}