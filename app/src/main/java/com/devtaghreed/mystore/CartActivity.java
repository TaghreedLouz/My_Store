package com.devtaghreed.mystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.devtaghreed.mystore.databinding.ActivityCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public FirebaseStorage firebaseStorage;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        setTitle("your product order");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();


        db.collection("buy").document(currentUser.getUid()).collection("buyUser")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  List<Products> products= task.getResult().toObjects(Products.class);

                            if (task.isSuccessful()) {
                                ArrayList<Products> productsArrayList = new ArrayList<>();

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                    Products p = documentSnapshot.toObject(Products.class);
                                    p.setDocumentId(documentSnapshot.getId());

                                    productsArrayList.add(p);

                                    CartAdapter adapter = new CartAdapter(productsArrayList, CartActivity.this, new onClick() {
                                        @Override
                                        public void like(Products products) {


                                            db.collection("like")
                                                    .document(currentUser.getUid())
                                                    .collection("likeUser")
                                                    .document(products.getDocumentId())
                                                    .set(products).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CartActivity.this, "add to ur Favourite", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(CartActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });



                                        }

                                        @Override
                                        public void buy(Products products) {

                                            db.collection("buy")
                                                    .document(currentUser.getUid())
                                                    .collection("buyUser")
                                                    .document(products.getDocumentId())
                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CartActivity.this, "remove Cart", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(CartActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    });

                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    binding.rv.setLayoutManager(linearLayoutManager);
                                    binding.rv.setAdapter(adapter);
                                }
                            }


                        } else {
                            Toast.makeText(CartActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

}