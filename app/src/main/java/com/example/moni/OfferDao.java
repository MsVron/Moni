package com.example.moni;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface OfferDao {
    @Insert
    void insert(Offer offer);

    @Query("SELECT * FROM offers WHERE isActive = 1 AND endTime > :currentTime ORDER BY endTime DESC")
    List<Offer> getActiveOffers(long currentTime); // This correctly returns a List<Offer>

    @Update
    void update(Offer offer);

    @Query("DELETE FROM offers WHERE id = :offerId")
    void deleteOffer(int offerId);
}
