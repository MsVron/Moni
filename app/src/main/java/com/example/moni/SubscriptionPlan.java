package com.example.moni;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.List;

@Entity(tableName = "subscription_plans")
public class SubscriptionPlan {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private double price;
    private double discountedPrice;
    private String description;
    private boolean isDiscounted;
    private boolean isActive;
    private List<String> features;

    public SubscriptionPlan(String title, double price, String description) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.isActive = true;
        this.isDiscounted = false;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public double getDiscountedPrice() { return discountedPrice; }
    public String getDescription() { return description; }
    public boolean isDiscounted() { return isDiscounted; }
    public boolean isActive() { return isActive; }
    public List<String> getFeatures() { return features; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(double price) { this.price = price; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscounted(boolean discounted) { isDiscounted = discounted; }
    public void setActive(boolean active) { isActive = active; }
    public void setFeatures(List<String> features) { this.features = features; }
}