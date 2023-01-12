package com.devtaghreed.mystore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.devtaghreed.mystore.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    private Uri mainUri;
    //where i want to store my data
    StorageReference storageReference;
    ProgressDialog progressDialog;
    public FirebaseStorage firebaseStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();


        binding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select image"), 111);
            }
        });

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainUri.getPath() != null){
                    uploadImage();
                }
            }
        });

    }

    private void uploadImage() {

        showProgress();
        //StorageReference : i want to do path like i want to create file for (images or files...) inner this file ill store my data
        storageReference = firebaseStorage.getReference("images/" + currentUser.getUid() + "/" + mainUri.getLastPathSegment());
        //UploadTask : to do Upload , the images we can upload it like (bytes , files , stream...)<- fits with what we need
        StorageTask<UploadTask.TaskSnapshot> uploadTask = storageReference.putFile(mainUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "image uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, "error : " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        //getBytesTransferred : how many bytes loaded
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        progressDialog.setProgress((int) progress);
                        Log.d("uploadImage", "Uploaded " + (int) progress + "% done");

                    }
                });
    }

    public void showProgress() {
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setTitle("Uploading");
        //progressDialog.setMessage("Uploading....");
        progressDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //return image as uri
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mainUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mainUri);
                binding.image.setImageBitmap(bitmap);
//                if (mainUri.getPath() != null){
//                    uploadImage();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}