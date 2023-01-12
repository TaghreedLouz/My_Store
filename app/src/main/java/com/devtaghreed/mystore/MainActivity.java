package com.devtaghreed.mystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.devtaghreed.mystore.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();


        //FirebaseApp.initializeApp(getApplicationContext());
        //Instance
       // mAuth.getCurrentUser();

//        String email = binding.emailEt.getText().toString().trim();
//        String password = binding.passwordEt.getText().toString().trim();
//        if (email.isEmpty() || password.isEmpty()) {
//            binding.registerBtn.setEnabled(false);
//        }


        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailEt.getText().toString().trim();
                String password = binding.passwordEt.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    register(email, password);
                }
            }
        });


        binding.loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailEt.getText().toString().trim();
                String password = binding.passwordEt.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    login(email, password);
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
                    FirebaseUser user = mAuth.getCurrentUser();

                    Toast.makeText(MainActivity.this, "Welcome : " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    Log.d("errorTask", "onComplete: " + user.getEmail());

                } else {
                    //take exception and know wheres the thing make it failure
                    Toast.makeText(MainActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("errorTask", "onComplete: " + task.getException().getMessage());

                }

            }
        });

    }

    public void login(String email, String password) {
        //register with Email & Password , return if the operation done or...
        //complete mean the operation done chick if it successful oe failure
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //when the operation successful that mean there's a user , have we can send this data
                    FirebaseUser user = mAuth.getCurrentUser();

                    Toast.makeText(MainActivity.this, "Welcome : " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    Log.d("errorTask", "onComplete: " + user.getEmail());
                    finish();

                } else {
                    //take exception and know wheres the thing make it failure
                    Toast.makeText(MainActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("errorTask", "onComplete: " + task.getException().getMessage());

                }

            }
        });

    }

}