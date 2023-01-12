package com.devtaghreed.mystore;

import android.net.Uri;

public class Products {
String name;
double price;
String details;
Uri image;

    public Products(String name, double price, String details, Uri image) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
