package com.example.bicyclerentel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class BookBicycle extends AppCompatActivity {

    public static final String DATABASE_PATH_FB = "Customer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_bicycle);
    }
}