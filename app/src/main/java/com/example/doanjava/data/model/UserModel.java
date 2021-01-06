package com.example.doanjava.data.model;

import android.net.Uri;

public class UserModel{
    public String fullName;
    public String phoneNumber;
    public String email;
    public String password;
    public Double balance;
    public boolean isInputBalance;
    public String photoUrl;


    public UserModel() {

    }

    public UserModel(String fullName, String phoneNumber, String email, String password,String photoUrl) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.balance = 0.0;
        this.isInputBalance = false;
        this.photoUrl = photoUrl;
    }
}