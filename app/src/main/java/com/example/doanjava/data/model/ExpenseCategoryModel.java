package com.example.doanjava.data.model;

public class ExpenseCategoryModel {
    public String Id;
    public String Name;

    public ExpenseCategoryModel() {
    }

    public ExpenseCategoryModel(String id, String name) {
        Id = id;
        Name = name;
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
