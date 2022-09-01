package com.example.bicyclerentel;

import androidx.annotation.NonNull;

public class bicycleOwner {
    public String image;
    public String name;
    public String type;
    public String contact;

    public bicycleOwner(){
    }

    public bicycleOwner(String image, String name, String type, String contact){
        this.image = image;
        this.name = name;
        this.type = type;
        this.contact = contact;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getContact(){
        return contact;
    }

    public void setContact(String contact){
        this.contact = contact;
    }

    @NonNull
    @Override
    public String toString() {
        return "CarsOwner{" +
                "image='" + image + '\'' +
                "name='" + name + '\'' +
                "type='" + type + '\'' +
                "contact='" + contact + '\'' +
                '}';
    }
}
