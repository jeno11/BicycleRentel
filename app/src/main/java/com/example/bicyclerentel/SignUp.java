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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SignUp extends AppCompatActivity {
    public static final String FB_STORAGE_PATH = "users/";
    public static final String FB_DATABASE_PATH = "UserDetails";
    public static final int REQUEST_CODE = 1234;
    private Uri imgUrl;
    private ImageButton iB;
    private EditText fn, eAddress, uPass, uIdNumber, uPhone;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference myRef;
    private StorageReference storageReference;
    private String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialize firebase object
        storageReference = FirebaseStorage.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH).child("ID_number");
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid(); //generate unique id from firebase database

        iB = (ImageButton) findViewById(R.id.imageButton);
        fn = (EditText) findViewById(R.id.ed_fname);
        eAddress = (EditText) findViewById(R.id.ed_email);
        uPass = (EditText) findViewById(R.id.ed_pass);
        uIdNumber = (EditText) findViewById(R.id.ed_id);
        uPhone = (EditText) findViewById(R.id.ed_phone);
    }

    public void TakeImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image"),REQUEST_CODE);
    }

    public void userRegister(View v) {

        if (imgUrl != null){
            final ProgressDialog dialog = new ProgressDialog(SignUp.this);
            dialog.setTitle("Saving Data");
            dialog.show();

            //get the storage reference
            StorageReference ref = storageReference.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUrl));

            //add file to reference
            ref.putFile(imgUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //dismiss dialog when succeed
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Details successfully uploaded", Toast.LENGTH_SHORT).show();

                    //get the user input
                    UserDetails userDetails = new UserDetails(
                            taskSnapshot.getDownloadUrl().toString(),
                            fn.getText().toString(),
                            eAddress.getText().toString(),
                            uPass.getText().toString(),
                            uIdNumber.getText().toString(),
                            uPhone.getText().toString()
                    );

                    //save information into firebase database
                    myRef.child(UserId).child(uIdNumber.getText().toString()).setValue(userDetails);

                    //start activity page
                    finish();
                    Intent intent = new Intent(SignUp.this,PageMain.class);
                    startActivity(intent);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            //show upload progress
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded" + (int) progress + "%");
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(),"Please Check Details",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgUrl = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUrl);
                iB.setImageBitmap(bm);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void CancelBtn(View view) {
        finish();
        firebaseAuth.signOut();
        startActivity(new Intent(SignUp.this,MainActivity.class));
    }
}