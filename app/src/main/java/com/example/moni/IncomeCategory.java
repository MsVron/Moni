package com.example.moni;

public enum IncomeCategory {
    SALARY("Salary", new String[] {
            "Regular Salary", "Bonus", "Overtime", "Commission", "Tips"
    }),
    INVESTMENTS("Investments", new String[] {
            "Stocks", "Bonds", "Real Estate", "Dividends", "Crypto", "Interest"
    }),
    BUSINESS("Business", new String[] {
            "Sales", "Services", "Rental Income", "Royalties", "Consulting"
    }),
    FREELANCE("Freelance", new String[] {
            "Project Work", "Consulting", "Contract Work", "Gig Work", "Commissions"
    }),
    PASSIVE("Passive Income", new String[] {
            "Affiliate Marketing", "Online Content", "Digital Products", "Subscriptions"
    }),
    BENEFITS("Benefits", new String[] {
            "Government Benefits", "Insurance Payout", "Social Security", "Pension"
    }),
    OTHER("Other", new String[] {
            "Gifts", "Inheritance", "Sale of Assets", "Miscellaneous"
    });

    private final String categoryName;
    private final String[] subcategories;

    IncomeCategory(String categoryName, String[] subcategories) {
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