package com.devtaghreed.mystore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.devtaghreed.mystore.databinding.ActivityAddBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    ActivityAddBinding binding;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    private Uri mainUri;
    //where i want to store my data
    StorageReference storageReference;
    ProgressDialog progressDialog;
    public FirebaseStorage firebaseStorage;
    FirebaseFirestore db;
    Products products;
    Uri profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select image"), 111);
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etName.getText().toString().trim();
                int price = Integer.parseInt(binding.etPrice.getText().toString().trim());
                String details = binding.etDetails.getText().toString().trim();

                if (name.isEmpty()) {
                    binding.etName.setError("Enter your Name");
                    binding.etName.requestFocus();
                } else if (binding.etPrice.getText().toString().trim().isEmpty()) {
                    binding.etPrice.setError("Enter the price");
                    binding.etPrice.requestFocus();
                    return;
                }else {
                    binding.etName.setText("");
                    binding.etPrice.setText("");
                    binding.etDetails.setText("");
                    saveSingleItem(name,price,details,profileImageUrl.toString());
                    finish();
                  //  startActivity(new Intent(getApplicationContext(),ShowAllActivity.class));
                }}
        });

    }

    private void saveSingleItem(String name, int price, String details , String image) {
        Products products = new Products(name,price,details,image);
        db.collection("products").add(products).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    String id = task.getResult().getId();
                    Log.d("TAG", "DocumentReference" + id);
                }
            }
        });
    }


    private void uploadImage() {
        showProgress();
        storageReference = firebaseStorage.getReference("image/"+ mainUri.getLastPathSegment());
        StorageTask<UploadTask.TaskSnapshot> uploadTask = storageReference.putFile(mainUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(AddActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                //this is the new way to do it
                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        profileImageUrl = task.getResult();
                        Log.i("UploadActivity", profileImageUrl.toString());

                        //   saveSingleItem(name, email, country, birthdate, gender,profileImageUrl.toString());
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(AddActivity.this, "Image Uploaded Failed!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        progressDialog.setProgress((int) progress);
                        Log.d("UploadActivity", "Upload is " + progress + "% done");
                    }
                });
    }



    public void showProgress() {
        progressDialog = new ProgressDialog(AddActivity.this);
        progressDialog.setTitle("Uploading");
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
                binding.imgProduct.setImageBitmap(bitmap);
               // if (mainUri.getPath() != null) {
                    uploadImage();
             //   }
            } catch (IOException e) {
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

}













