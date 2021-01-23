package com.example.doanjava.data.model;

public class LoanCatecoryModel {
    public String Id;
    public String Name;

    public LoanCatecoryModel() {
    }

    public LoanCatecoryModel(String id, String name) {
        Id = id;
        Name = name;
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
