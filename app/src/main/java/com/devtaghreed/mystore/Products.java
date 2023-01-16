package com.devtaghreed.mystore;

public class Products {
String name;
int price;
String details;
String image;
String documentId;

    public Products() {
    }

    public Products(String name, int price, String details, String image) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.image = image;
    }

    public Products(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Products(String name, int price, String details) {
        this.name = name;
        this.price = price;
        this.details = details;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String  getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Products{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", details='" + details + '\'' +
                ", image=" + image +
                '}';
    }
}
