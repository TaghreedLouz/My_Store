package com.devtaghreed.mystore;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devtaghreed.mystore.databinding.ItemRvBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.UserViewHolder> {

    ArrayList<Products>productsArrayList;
    Context context;
    onClick listener;

    public MyAdapter(ArrayList<Products> productsArrayList, Context context, onClick listener) {
        this.productsArrayList = productsArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvBinding binding = ItemRvBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (productsArrayList == null)
            return;

        Products products = productsArrayList.get(holder.getAdapterPosition());
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycle_view);
        holder.itemView.startAnimation(animation);

        Glide.with(context).load(products.getImage()).into(holder.image);
        holder.name.setText(products.getName());
        holder.details.setText(products.getDetails());
        holder.price.setText(String.valueOf(products.getPrice()));
        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.like(products);
                holder.imgLike.setBackgroundResource(R.drawable.ic_like);

            }
        });
        holder.imgBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               listener.buy(products);
                holder.imgBuy.setBackgroundResource(R.drawable.ic_cart_add);

            }
        });
        }

    @Override
    public int getItemCount() {
        return productsArrayList != null ? productsArrayList.size() : 0;
    }



    class UserViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        AppCompatButton imgLike , imgBuy;
        TextView name,price,details;

        public UserViewHolder(@NonNull ItemRvBinding binding) {
            super(binding.getRoot());
            image = binding.imgProduct;
            name = binding.tvName;
            price = binding.tvPrice;
            details = binding.tvDetails;
            imgLike = binding.imgLike;
            imgBuy = binding.imgBuy;


        }
    }
}
