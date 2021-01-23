package com.example.doanjava.data.model;

import com.google.firebase.Timestamp;

public class LoanModel {
    public String Id;
    public Double Value;
    public String CategoryId;
    public String Dobtor;
    public Timestamp PayDay;
    public Timestamp Borrowing;
    public String UserId;
    public String PhotoUri;

    public LoanModel() {
    }

    public LoanModel(String id, Double value, String categoryId, String dobter,
                        Timestamp payday,Timestamp borrowing, String userId, String photoUri) {
        Id = id;
        Value = value;
        CategoryId = categoryId;
        Dobtor = dobter;
        PayDay = payday;
        Borrowing = borrowing;
        UserId = userId;
        PhotoUri = photoUri;
    }
}
