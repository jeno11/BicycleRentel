package com.example.bicyclerentel;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDetails {
    public String image;
    public String fullName;
    public String email;
    public String password;
    public String idNumber;
    public String phone;

    public UserDetails(String image, String fullName, String email, String password, String idNumber, String phone){
        this.image = image;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.idNumber = idNumber;
        this.phone = phone;
    }

    public String getImage(){ return image;}

    public void setImage(String image){this.image = image;}

    public String getFullName(){ return fullName; }

    public void setFullName(String fullName){this.fullName = fullName;}

    public String getEmail(){ return email; }

    public void setEmail(String email){this.email = email;}

    public String getPassword(){ return password; }

    public void setPassword(String password){this.password = password;}

    public String getIdNumber(){ return idNumber; }

    public void setIdNumber(String idNumber){this.idNumber = idNumber;}

    public String getPhone(){ return phone; }

    public void setPhone(String phone){this.phone = phone;}

    @NonNull
    @Override
    public String toString() {
        return "UserDetails{" +
                "image='" + image + '\'' +
                ", fullname='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", phoneNumber='" + phone + '\'' + '}';
    }
}
