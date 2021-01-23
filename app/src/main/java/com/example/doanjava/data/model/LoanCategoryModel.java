package com.example.doanjava.data.model;

public class LoanCategoryModel {
    public String Id;
    public String Name;

    public LoanCategoryModel() {
    }

    public LoanCategoryModel(String id, String name) {
        Id = id;
        Name = name;
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
