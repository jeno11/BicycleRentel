package com.example.bicyclerentel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PageMain extends AppCompatActivity {
    private TextView txtV1, txtV2, txtV3, txtV4, txtV5;
    private ImageView img1;
    private ListView listView;
    private List<Customer> customerList;
    private CustomerAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressDialog progressDialog;
    private FirebaseDatabase db;
    private DatabaseReference dR;
    private FirebaseUser user;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_main);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        UserID = user.getUid();
        db = FirebaseDatabase.getInstance();
        dR = db.getReference();

        //check whether user already logged in or not
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null){
                    finish();
                    firebaseAuth.signOut();
                    startActivity(new Intent(PageMain.this,MainActivity.class));
                }
            }
        };

        txtV1 = (TextView) findViewById(R.id.logoutBtn);

    }

    public void rent_bike(View view) {
    }
}