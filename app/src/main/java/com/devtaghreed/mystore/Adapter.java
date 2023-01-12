package com.devtaghreed.mystore;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devtaghreed.mystore.databinding.ItemRvBinding;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.UserViewHolder> {

    ArrayList<Products>productsArrayList;

    public Adapter(ArrayList<Products> productsArrayList) {
        this.productsArrayList = productsArrayList;
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

        Products products = productsArrayList.get(position);
        holder.name.setText(products.getName());
        holder.price.setText(String.valueOf(products.getPrice()));
        holder.details.setText(products.getDetails());
    }

    @Override
    public int getItemCount() {
        return productsArrayList != null ? productsArrayList.size() : 0;
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name,price,details;

        public UserViewHolder(@NonNull ItemRvBinding binding) {
            super(binding.getRoot());
            image = binding.imgProduct;
            name = binding.tvName;
            price = binding.tvPrice;
            details = binding.tvDetails;

        }
    }
}
