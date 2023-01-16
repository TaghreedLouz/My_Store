package com.devtaghreed.mystore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.devtaghreed.mystore.databinding.ActivityShowAllBinding;
import com.devtaghreed.mystore.databinding.ItemRvBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {

    ActivityShowAllBinding binding;
    MyAdapter adapter;
    public FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    private Uri mainUri;
    Uri profileImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAllBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Toolbar
        setSupportActionBar(binding.toolbar);
        setTitle("Products");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.AddFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddActivity.class));
            }
        });

        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Products> productsArrayList = new ArrayList<>();

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                Products p = documentSnapshot.toObject(Products.class);
                                p.setDocumentId(documentSnapshot.getId());

                                productsArrayList.add(p);

                            }


                            adapter = new MyAdapter(productsArrayList, ShowAllActivity.this, new onClick() {
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
                                                Toast.makeText(ShowAllActivity.this, "add to ur Favourite", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                                            .set(products).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(ShowAllActivity.this, "add to ur Cart", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                });



    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
          db = FirebaseFirestore.getInstance();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                db.collection("products")
                        .whereArrayContainsAny("name", Collections.singletonList(query))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Products> productsArrayList = new ArrayList<>();

                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                        Products p = documentSnapshot.toObject(Products.class);
                                        p.setDocumentId(documentSnapshot.getId());

                                        productsArrayList.add(p);

                                    }


                                    adapter = new MyAdapter(productsArrayList, ShowAllActivity.this, new onClick() {
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
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Favourite", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    .set(products).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Cart", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                        });


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                db.collection("products")
                        .whereEqualTo("name",newText)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Products> productsArrayList = new ArrayList<>();

                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                        Products p = documentSnapshot.toObject(Products.class);
                                        p.setDocumentId(documentSnapshot.getId());

                                        productsArrayList.add(p);

                                    }


                                    adapter = new MyAdapter(productsArrayList, ShowAllActivity.this, new onClick() {
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
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Favourite", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    .set(products).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Cart", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                        });


                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sittings:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                return true;
            case R.id.Favourite:
                startActivity(new Intent(getApplicationContext(), FavouriteActivity.class));
                return true;
            case R.id.cart:
                Intent intentCart = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intentCart);
                return true;
            case R.id.filterName:
                db.collection("products")
                        .orderBy("name", Query.Direction.ASCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Products> productsArrayList = new ArrayList<>();

                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                        Products p = documentSnapshot.toObject(Products.class);
                                        p.setDocumentId(documentSnapshot.getId());

                                        productsArrayList.add(p);

                                    }


                                    adapter = new MyAdapter(productsArrayList, ShowAllActivity.this, new onClick() {
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
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Favourite", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    .set(products).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Cart", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                        });
                return true;
            case R.id.filterPriceBid:

                db.collection("products")
                        .orderBy("price", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Products> productsArrayList = new ArrayList<>();

                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                        Products p = documentSnapshot.toObject(Products.class);
                                        p.setDocumentId(documentSnapshot.getId());

                                        productsArrayList.add(p);

                                    }


                                    adapter = new MyAdapter(productsArrayList, ShowAllActivity.this, new onClick() {
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
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Favourite", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    .set(products).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Cart", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                        });
                return true;
            case R.id.filterPriceCheapest:

                db.collection("products")
                        .orderBy("price", Query.Direction.ASCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<Products> productsArrayList = new ArrayList<>();

                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                        Products p = documentSnapshot.toObject(Products.class);
                                        p.setDocumentId(documentSnapshot.getId());

                                        productsArrayList.add(p);

                                    }


                                    adapter = new MyAdapter(productsArrayList, ShowAllActivity.this, new onClick() {
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
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Favourite", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    .set(products).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ShowAllActivity.this, "add to ur Cart", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ShowAllActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                        });
                return true;
        }
        return false;
    }

    private void uploadImage() {
        showProgress();
        storageReference = firebaseStorage.getReference("image/" + currentUser.getUid() + "/" + mainUri.getLastPathSegment());
        StorageTask<UploadTask.TaskSnapshot> uploadTask = storageReference.putFile(mainUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(ShowAllActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ShowAllActivity.this,
                                "Image Uploaded Failed!!",
                                Toast.LENGTH_SHORT)
                                .show();
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
        progressDialog.setMessage("Uploading");
        progressDialog.show();
    }


}