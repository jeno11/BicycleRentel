package com.example.bicyclerentel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
        txtV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(PageMain.this, MainActivity.class));
            }
        });

        txtV2 = (TextView) findViewById(R.id.textView13);
        txtV2 = (TextView) findViewById(R.id.textView15);
        txtV2 = (TextView) findViewById(R.id.textView16);
        txtV2 = (TextView) findViewById(R.id.textView17);
        img1 = (ImageView) findViewById(R.id.img2);

        DatabaseReference ref = db.getReference("UserDetails").child("NIC").child(UserID);
        Query query = ref.orderByChild(UserID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    UserDetails uD = ds.getValue(UserDetails.class); //call data model
                    //Picasso.get().load(uD.getImage()).resize(350,350).into(img1);
                    txtV2.setText(uD.getFullName());
                    txtV3.setText(uD.getEmail());
                    txtV4.setText(uD.getIdNumber());
                    txtV5.setText(uD.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        customerList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.ls1);

        DatabaseReference reff = db.getReference(BookBicycle.DATABASE_PATH_FB);

        //displaying data into list view on app
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //fetch data from firebase database
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    //call default constructor from data model to retrieve data from firebase database
                    Customer i = snapshot1.getValue(Customer.class);
                    customerList.add(i);
                }
                adapter = new CustomerAdapter(PageMain.this,R.layout.customlistbookabicycle,customerList);

                //set adapter for list view
                listView.setAdapter((ListAdapter) adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set list view when get selected item
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               view.setSelected(true);
           }
       });
    }

    public void rent_bike(View view) {
        finish();
        Intent intent = new Intent(this, rentmybicycle.class);
        startActivity(intent);
    }


    public void book_bike(View view) {
        finish();
        Intent intent = new Intent(this, selectbicycle.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}