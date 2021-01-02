package com.example.doanjava.data.model;

public class UserModel{
    public String fullName;
    public String phoneNumber;
    public String email;
    public String password;

    public UserModel() {

    }

    public UserModel(String fullName, String phoneNumber, String email, String password) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }
}