package com.example.doanjava.data.model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class ExpenseModel {
    public String Id;
    public Double Value;
    public String CategoryId;
    public String Description;
    public Timestamp CreateAt;

    public String UserId;

    public ExpenseModel() {
    }

    public ExpenseModel(String id, Double value, String categoryId,String description, Timestamp createAt, String userId) {
        Id = id;
        Value = value;
        CategoryId = categoryId;
        Description = description;
        CreateAt = createAt;
        UserId = userId;
    }
}
