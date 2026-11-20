package co.edu.uptc.model;

import co.edu.uptc.enums.ErrorMessages;

public class Category {
    private String name;
    private double budget;
    private double spent;

    public Category(String name, double budget) {
        this.name = name;
        this.budget = budget;
        this.spent = 0;
    }

    public String getName() {
        return name;
    }

    public double getBudget() {
        return budget;
    }

    public double getSpent() {
        return spent;
    }

    public boolean canAfford(double amount) {
        return amount <= (budget - spent);
    }

    public void addSpent(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(ErrorMessages.AMOUNT_GREATER_THAN_ZERO.getText());
        }
        if (!canAfford(amount)) {
            throw new IllegalStateException(ErrorMessages.CATEGORY_WITHOUT_BUDGET.getText());
        }
        this.spent += amount;
    }
}
