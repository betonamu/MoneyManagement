package com.example.doanjava.data.model;

import java.util.Date;

public class ExpenseModel {
    public String Id;
    public Double Value;
    public String CategoryId;
    public String Description;
    public Date CreateAt;

    public String UserId;

    public ExpenseModel() {
    }

    public ExpenseModel(String id, Double value, String categoryId,String description, Date createAt, String userId) {
        Id = id;
        Value = value;
        CategoryId = categoryId;
        Description = description;
        CreateAt = createAt;
        UserId = userId;
    }
}
