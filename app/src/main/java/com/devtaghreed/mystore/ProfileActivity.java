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

import com.bumptech.glide.Glide;
import com.devtaghreed.mystore.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    private Uri mainUri;
    //where i want to store my data
    StorageReference storageReference;
    ProgressDialog progressDialog;
    public FirebaseStorage firebaseStorage;
    FirebaseFirestore db;
    User userEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Profile");
        editUnAble();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();


        db.collection("users").document(currentUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            User user = task.getResult().toObject(User.class);

                            if (document.exists() && user != null) {
                                binding.tvName.setText(user.getName());
                                binding.etEmail.setText(user.getEmail());
                                binding.spCountry.setSelection(user.getCountry());
                                binding.etBirthdate.setText(user.getBirthdate());
                                Glide.with(ProfileActivity.this).load(user.getImage()).into(binding.image);

                                if (user.getGender().equals("Female")) {
                                    binding.registarRbFemale.setChecked(true);
                                } else if (user.getGender().equals("Male")) {
                                    binding.registarRbMale.setChecked(true);

                                }
                                Toast.makeText(ProfileActivity.this, "DocumentSnapshot data: " + document.getData(), Toast.LENGTH_SHORT).show();
                                Log.d("getUidTest", "DocumentSnapshot if: " + currentUser.getUid());
                                //       Log.d("getUidTest", "email "+user.getEmail());
//
                            } else {
                                Toast.makeText(ProfileActivity.this, "No such document" + task.getException(), Toast.LENGTH_SHORT).show();
                                //  Log.d("getUidTest", "No such document");
                                Log.d("getUidTest", "DocumentSnapshot else: " + currentUser.getUid());
//                                Log.d("getUidTest", "email "+user.getEmail());
//                                Log.d("getUidTest", "name "+document.getString("name"));
                            }

                            Log.d("getUidTest", "DocumentSnapshot out: " + currentUser.getUid());

                        } else {
                            Toast.makeText(ProfileActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        binding.btnSettings.setVisibility(View.VISIBLE);
        binding.btnUpdateEmail.setVisibility(View.GONE);
        binding.layUpdateEmail.setVisibility(View.GONE);
        binding.btnUpdatePassword.setVisibility(View.GONE);
        binding.layUpdatePassword.setVisibility(View.GONE);
        binding.btnLogout.setVisibility(View.GONE);
        binding.btnCancel.setVisibility(View.GONE);
        binding.btnSaveApp.setVisibility(View.GONE);

        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnSettings.setVisibility(View.GONE);
                binding.btnUpdateEmail.setVisibility(View.VISIBLE);
                binding.layUpdateEmail.setVisibility(View.GONE);
                binding.btnUpdatePassword.setVisibility(View.VISIBLE);
                binding.layUpdatePassword.setVisibility(View.GONE);
                binding.btnLogout.setVisibility(View.VISIBLE);
                binding.btnCancel.setVisibility(View.VISIBLE);
            }
        });



        //date picker
        binding.etBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();

                com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        int Age = now.get(Calendar.YEAR) - year;
                        if (Age < 10 || Age > 45) {
                            Toast.makeText(ProfileActivity.this, "your not allow you to play this game", Toast.LENGTH_SHORT).show();
                        } else {
                            binding.etBirthdate.setText(String.valueOf(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + "    ur Age \"" + Age + "\""));
                        }
                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                dpd.show(getSupportFragmentManager(), " null ");
            }
        });


        //edit
        binding.btnEditApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editAble();
                binding.btnSaveApp.setVisibility(View.VISIBLE);
                binding.btnEditApp.setVisibility(View.GONE);

            }
        });


        binding.btnSaveApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameEdit = binding.tvName.getText().toString().trim();
                String emailEdit = binding.etEmail.getText().toString().trim();
                String birthdateEdit = binding.etBirthdate.getText().toString().trim();
                int countryEdit = binding.spCountry.getSelectedItemPosition();
                String imageEdit = String.valueOf(mainUri);

                String genderEdit = "";
                if (binding.registarRbFemale.isChecked()) {
                    genderEdit = "Female";
                } else if (binding.registarRbMale.isChecked()) {
                    genderEdit = "Male";
                }

                if (nameEdit.isEmpty()) {
                    binding.tvName.setError("Please Enter your Name");
                    binding.tvName.requestFocus();
                } else if (emailEdit.isEmpty()) {
                    binding.etEmail.setError("Please Enter your Email Address");
                    binding.etEmail.requestFocus();
                    return;
                } else if (!(emailEdit.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    binding.etEmail.setError("Invalid Email Address");
                    binding.etEmail.requestFocus();
                    return;
                } else if (birthdateEdit.isEmpty()) {
                    binding.etBirthdate.setError("Select your Birthdate");
                    binding.etBirthdate.requestFocus();
                    return;
                } else {
                    User userEdit = new User(nameEdit, emailEdit, countryEdit, birthdateEdit,genderEdit , imageEdit);
                   // currentUser.updateEmail(emailEdit);
                    db.collection("users").document(currentUser.getUid()).set(userEdit);
                  //  currentUser.reload();


                    Toast.makeText(ProfileActivity.this, "Edit Successful", Toast.LENGTH_SHORT).show();

                    //actions
                    editUnAble();
                    binding.btnEditApp.setVisibility(View.VISIBLE);
                    binding.btnSaveApp.setVisibility(View.GONE);

                }

            }
        });


        binding.btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnUpdateEmail.setVisibility(View.GONE);
                binding.layUpdateEmail.setVisibility(View.VISIBLE);
                binding.btnUpdatePassword.setVisibility(View.GONE);
                binding.layUpdatePassword.setVisibility(View.GONE);
                binding.btnLogout.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            }
        });

        binding.btnCancelEmailUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnUpdateEmail.setVisibility(View.VISIBLE);
                binding.layUpdateEmail.setVisibility(View.GONE);
                binding.btnUpdatePassword.setVisibility(View.VISIBLE);
                binding.layUpdatePassword.setVisibility(View.GONE);
                binding.btnLogout.setVisibility(View.VISIBLE);
                binding.btnCancel.setVisibility(View.VISIBLE);
            }
        });

        binding.btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
                binding.btnUpdateEmail.setVisibility(View.GONE);
                binding.layUpdateEmail.setVisibility(View.GONE);
                binding.btnUpdatePassword.setVisibility(View.GONE);
                binding.layUpdatePassword.setVisibility(View.VISIBLE);
                binding.btnLogout.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            }
        });

        binding.btnCancelPasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnUpdateEmail.setVisibility(View.VISIBLE);
                binding.layUpdateEmail.setVisibility(View.GONE);
                binding.btnUpdatePassword.setVisibility(View.VISIBLE);
                binding.layUpdatePassword.setVisibility(View.GONE);
                binding.btnLogout.setVisibility(View.VISIBLE);
                binding.btnCancel.setVisibility(View.VISIBLE);
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnSettings.setVisibility(View.VISIBLE);
                binding.btnUpdateEmail.setVisibility(View.GONE);
                binding.layUpdateEmail.setVisibility(View.GONE);
                binding.btnUpdatePassword.setVisibility(View.GONE);
                binding.layUpdatePassword.setVisibility(View.GONE);
                binding.btnLogout.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            }
        });


        binding.btnSaveEmailUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateEmail();
                binding.btnSettings.setVisibility(View.VISIBLE);
                binding.btnUpdateEmail.setVisibility(View.GONE);
                binding.layUpdateEmail.setVisibility(View.GONE);
                binding.btnUpdatePassword.setVisibility(View.GONE);
                binding.layUpdatePassword.setVisibility(View.GONE);
                binding.btnLogout.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            }
        });

        binding.btnSavePasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
                binding.btnSettings.setVisibility(View.VISIBLE);
                binding.btnUpdateEmail.setVisibility(View.GONE);
                binding.layUpdateEmail.setVisibility(View.GONE);
                binding.btnUpdatePassword.setVisibility(View.GONE);
                binding.layUpdatePassword.setVisibility(View.GONE);
                binding.btnLogout.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });

        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select image"), 1000);
            }
        });
    }


    public void updateEmail() {
        //Re-authenticate a user like login but its not login

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        //we can use currentUser.getEmail but to be ture about the email
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), binding.etPassword.getText().toString().trim());

        // Prompt the user to re-provide their sign-in credentials
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            currentUser.updateEmail(binding.etEmailEdit.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //mach sure that the data updated & do reload and update for data
                                                currentUser.reload();
                                                Toast.makeText(ProfileActivity.this, "Email updated for " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                task.getException().printStackTrace();
                                                Toast.makeText(ProfileActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    })
                            ;
                        } else {
                            task.getException().printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }});
    }

    public void updatePassword() {

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), binding.etOldPass.getText().toString().trim());
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            currentUser.updatePassword(binding.etNewPass.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                currentUser.reload();
                                                Toast.makeText(ProfileActivity.this, "Password updated for " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                task.getException().printStackTrace();
                                                Toast.makeText(ProfileActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        } else {
                            task.getException().printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                })
        ;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //return image as uri
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mainUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mainUri);
                binding.image.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }


    //editUnAble
    public void editUnAble() {
        binding.tvName.setEnabled(false);
        binding.etEmail.setEnabled(false);
        binding.etBirthdate.setEnabled(false);
        binding.registarRbMale.setEnabled(false);
        binding.registarRbFemale.setEnabled(false);
        binding.spCountry.setEnabled(false);
        binding.image.setEnabled(false);
    }

    //editAble
    public void editAble() {
        binding.tvName.setEnabled(true);
        binding.etEmail.setEnabled(true);
        binding.etBirthdate.setEnabled(true);
        binding.registarRbMale.setEnabled(true);
        binding.registarRbFemale.setEnabled(true);
        binding.spCountry.setEnabled(true);
        binding.image.setEnabled(true);
    }
}













