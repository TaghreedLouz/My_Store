package com.devtaghreed.mystore;

public class User {
    String name;
    String email;
    int country;
    String Birthdate;
    String gender;
    String image;

    public User() {
    }

    public User(String name, String email, int country, String birthdate, String gender , String image) {
        this.name = name;
        this.email = email;
        this.country = country;
        Birthdate = birthdate;
        this.gender = gender;
        this.image = image;
    }

    public User(String name, String email, int country, String birthdate, String gender) {
        this.name = name;
        this.email = email;
        this.country = country;
        Birthdate = birthdate;
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(String birthdate) {
        Birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
