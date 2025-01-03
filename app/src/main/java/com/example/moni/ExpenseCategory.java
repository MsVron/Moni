package com.example.moni;

public enum ExpenseCategory {
    HOUSING("Housing", new String[] {
            "Rent", "Mortgage", "Utilities", "Maintenance", "Insurance", "Property Tax"
    }),
    TRANSPORTATION("Transportation", new String[] {
            "Car Payment", "Fuel", "Public Transit", "Maintenance", "Insurance", "Parking"
    }),
    FOOD("Food", new String[] {
            "Groceries", "Restaurants", "Take-out", "Coffee Shops", "Snacks"
    }),
    HEALTHCARE("Healthcare", new String[] {
            "Insurance", "Medications", "Doctor Visits", "Dental", "Vision", "Lab Tests"
    }),
    ENTERTAINMENT("Entertainment", new String[] {
            "Movies", "Games", "Sports", "Hobbies", "Subscriptions", "Events"
    }),
    SHOPPING("Shopping", new String[] {
            "Clothing", "Electronics", "Home Goods", "Personal Care", "Gifts"
    }),
    EDUCATION("Education", new String[] {
            "Tuition", "Books", "Supplies", "Courses", "Training"
    }),
    DEBT("Debt Payments", new String[] {
            "Credit Card", "Personal Loan", "Student Loan", "Other Loans"
    }),
    SAVINGS("Savings", new String[] {
            "Emergency Fund", "Retirement", "Investments", "Goals"
    }),
    OTHER("Other", new String[] {
            "Miscellaneous", "Unplanned Expenses"
    });

    private final String categoryName;
    private final String[] subcategories;

    ExpenseCategory(String categoryName, String[] subcategories) {
        this.categoryName = categoryName;
        this.subcategories = subcategories;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String[] getSubcategories() {
        return subcategories;
    }
}