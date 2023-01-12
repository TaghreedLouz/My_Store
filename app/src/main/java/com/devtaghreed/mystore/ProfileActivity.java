package com.devtaghreed.mystore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Gallery;
import android.widget.Toast;

import com.devtaghreed.mystore.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    private Uri mainUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        binding.btnImge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select image"), 1000);
            }
        });

        binding.btnUpdateImageAndName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateProfile();

            }
        });


        binding.btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateEmail();

            }
        });

        binding.btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });
    }

    private void updateProfile() {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(binding.etName.getText().toString().trim())
                .setPhotoUri(mainUri)
                .build();
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //mach sure that the data updated & do reload and update for data
                            currentUser.reload();
                            Toast.makeText(ProfileActivity.this, "profile updated for " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                        } else {
                            task.getException().printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                })
        ;
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
                            currentUser.updateEmail(binding.etEmail.getText().toString())
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
                    }
                })
        ;

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
        if (requestCode == 1000 && resultCode == RESULT_OK && data.getData() != null) {
            mainUri = data.getData();
        }
    }
}