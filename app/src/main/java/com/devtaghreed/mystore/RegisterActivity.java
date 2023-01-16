package com.devtaghreed.mystore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.devtaghreed.mystore.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;

    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    private Uri mainUri;
    String Birthdate , Full_name , Re_password , Password ,Email ,Gender ;
    int Country;
    //where i want to store my data
    StorageReference storageReference;
    ProgressDialog progressDialog;
    public FirebaseStorage firebaseStorage;
    FirebaseFirestore db;

    Uri profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();


        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select file"), 1111);

            }
        });

        //date picker
        binding.etBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        int Age = now.get(Calendar.YEAR) - year;
                        if (Age < 10 || Age > 70) {
                            Toast.makeText(RegisterActivity.this, "your not allow you to register", Toast.LENGTH_SHORT).show();
                        } else {
                            binding.etBirthdate.setText(String.valueOf(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + "    ur Age \"" + Age + "\""));
                        }
                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                dpd.show(getSupportFragmentManager(), " null ");
            }
        });

        //Register info
        binding.registarBtnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View view) {
                 Email = binding.registarEtEmail.getText().toString().trim();
                 Password = binding.registarEtPassword.getText().toString().trim();
                 Re_password = binding.registarEtRepassword.getText().toString().trim();
                 Full_name = binding.registarEtFullname.getText().toString().trim();
                 Birthdate = binding.etBirthdate.getText().toString().trim();
                 Country = binding.registarSpCountry.getSelectedItemPosition();
                 Gender = "";
                if (binding.registarRbFemale.isChecked()) {
                    Gender = "Female";
                } else if (binding.registarRbMale.isChecked()) {
                    Gender = "Male";
                }


                //EditText Validation
                if (Full_name.isEmpty()) {
                    binding.registarEtFullname.setError("Please Enter your Name");
                    binding.registarEtFullname.requestFocus();
                } else if (Email.isEmpty()) {
                    binding.registarEtEmail.setError("Please Enter your Email Address");
                    binding.registarEtEmail.requestFocus();
                    return;
                } else if (!(Email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    binding.registarEtEmail.setError("Invalid Email Address");
                    binding.registarEtEmail.requestFocus();
                    return;
                } else if (Password.isEmpty()) {
                    binding.registarEtPassword.setError("Please Enter Password");
                    binding.registarEtPassword.requestFocus();
                    return;
                } else if (Password.length() < 6) {
                    binding.registarEtPassword.setError("Please Enter Password Minimum 6 Char");
                    binding.registarEtPassword.requestFocus();
                    return;
                } else if (!Re_password.equals(Password)) {
                    binding.registarEtRepassword.setError("Make sure the password matches");
                    binding.registarEtRepassword.requestFocus();
                } else if (Birthdate.isEmpty()) {
                    binding.etBirthdate.setError("Select your Birthdate");
                    binding.etBirthdate.requestFocus();
                    return;
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("Email", Email);
                    intent.putExtra("Password", Password);
                    setResult(RESULT_OK, intent);
                    register(Email, Password);

                }
            }

        });

    }

    public void register(String email, String password) {
        //register with Email & Password , return if the operation done or...
        //complete mean the operation done chick if it successful oe failure
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //when the operation successful that mean there's a user , have we can send this data
                    currentUser = mAuth.getCurrentUser();
                    Toast.makeText(RegisterActivity.this, "Welcome : " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();

                    saveSingleItem(Full_name, Email, Country, Birthdate, Gender , profileImageUrl.toString());

                    Log.d("errorTask", "onComplete: " + currentUser.getEmail());

                } else {
                    //take exception and know wheres the thing make it failure
                    Toast.makeText(RegisterActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("errorTask", "onComplete: " + task.getException().getMessage());

                }

            }
        });
    }

    private void saveSingleItem(String name, String email, int country, String birthdate, String gender , String image) {
        User user = new User(name, email, country, birthdate, gender ,image);
        db.collection("users").document(currentUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                 //   String id = task.getResult().toString();
                    Log.d("getUidTest", "DocumentReference");
                    startActivity(new Intent(getApplicationContext(), ShowAllActivity.class));
                    finish();

                }else {
                    Log.d("getUidTest", "DocumentReference" + task.getException().getMessage());
                }
            }
        });
    }

    private void uploadImage() {
        showProgress();
        storageReference = firebaseStorage.getReference("userimage/"  + mainUri.getLastPathSegment());
        StorageTask<UploadTask.TaskSnapshot> uploadTask = storageReference.putFile(mainUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RegisterActivity.this, "Image Uploaded Failed!!", Toast.LENGTH_SHORT).show();
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mainUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mainUri);
                binding.image.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
