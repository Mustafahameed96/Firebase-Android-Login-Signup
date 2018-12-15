package com.example.mustafa.assignment3mse;

import android.net.Uri;

public class User {

    public String FirstName,LastName,Email,ContactNumber,Cnic;
    public String ProfileImageUri;

    public User(){


    }

    public User(String firstName, String lastName, String email, String contactNumber, String cnic , String profileImageUri) {
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        ContactNumber = contactNumber;
        Cnic = cnic;
        ProfileImageUri = profileImageUri;
    }
}
