package com.devtaghreed.mystore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devtaghreed.mystore.databinding.ItemRvBinding;
import com.devtaghreed.mystore.databinding.ItemRvCartBinding;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<Products> productsArrayList;
    Context context;
    onClick listener;

    public CartAdapter(ArrayList<Products> productsArrayList, Context context, onClick listener) {
        this.productsArrayList = productsArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvCartBinding binding = ItemRvCartBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new CartAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (productsArrayList == null)
            return;

        Products products = productsArrayList.get(holder.getAdapterPosition());

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
                holder.imgBuy.setBackgroundResource(R.drawable.ic_cart);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsArrayList != null ? productsArrayList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        AppCompatButton imgLike , imgBuy;
        TextView name,price,details;

        public ViewHolder(@NonNull ItemRvCartBinding binding) {
            super(binding.getRoot());
            image = binding.imgProduct;
            name = binding.tvName;
            price = binding.tvPrice;
            details = binding.tvDetails;
            imgLike = binding.imgLike;
            imgBuy = binding.imgBuy;
            binding.imgBuy.setBackgroundResource(R.drawable.ic_cart_add);

        }
    }
}
