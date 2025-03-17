package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    @JsonProperty("budget")
    private double budget;

    @JsonProperty("expenses")
    private List<Expense> expenses;

    // Bo'sh konstruktor (Jackson uchun kerak)
    public UserData() {
        this.budget = 1000000.00;
        this.expenses = new ArrayList<>();
    }

    // Getter va Setter metodlari
    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public void addExpense(Expense expense) {
        this.expenses.add(expense);
    }
}
