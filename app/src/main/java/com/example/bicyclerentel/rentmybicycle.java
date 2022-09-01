package com.example.bicyclerentel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class rentmybicycle extends AppCompatActivity {
    public static final String STORAGE_PATH = "bicycleowner/";
    public static final String DATABASE_PATH = "BicycleOwner";
    public static final int REQUEST_CODE = 12;
    private Uri uri;
    private ImageView img2;
    private EditText ed1, ed2, ed3;
    private TextView txt1;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference myReference;
    private StorageReference myStoreRef;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rentmybicycle);

        myStoreRef = FirebaseStorage.getInstance().getReference();
        myReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //check whether user already login
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user==null){
                    finish();
                    firebaseAuth.signOut();
                    startActivity(new Intent(rentmybicycle.this,MainActivity.class));
                }
            }
        };

        img2 = (ImageView) findViewById(R.id.imageView2);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"), REQUEST_CODE);
            }
        });

        txt1 = (TextView) findViewById(R.id.sgnout_txt);
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                firebaseAuth.signOut();
            }
        });

        ed1 = (EditText) findViewById(R.id.editText);
        ed2 = (EditText) findViewById(R.id.editText2);
        ed3 = (EditText) findViewById(R.id.editText3);
    }

    @SuppressWarnings("Test")
    public void submitRent(View v) {
        if (uri != null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Saving Data");
            dialog.show();

            //get the storage reference
            StorageReference ref = myStoreRef.child(STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(uri));

            //add file to reference
            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //dismiss dialog when success
                    dialog.dismiss();

                    //display success toast message
                    Toast.makeText(getApplicationContext(), "Details Uploaded",Toast.LENGTH_SHORT).show();

                    //get input data
                    bicycleOwner bicycleOwner = new bicycleOwner(
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(),
                            ed1.getText().toString(),
                            ed2.getText().toString(),
                            ed3.getText().toString());

                    //save information into firebase database
                    String iD = myReference.push().getKey();
                    myReference.child(iD).setValue(bicycleOwner);

                    //start PageMain activity after finish this activity
                    finish();
                    Intent intent = new Intent(rentmybicycle.this,PageMain.class);
                    startActivity(intent);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //dismiss dialog when error occurs
                            dialog.dismiss();
                            //display error toast message
                            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            //show upload progress
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(),"Please Check Details", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode ==RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                img2.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void cancelRent(View view) {
        finish();
        Intent intent = new Intent(rentmybicycle.this, PageMain.class);
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