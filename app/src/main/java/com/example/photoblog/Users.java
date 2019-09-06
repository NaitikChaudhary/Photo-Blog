package com.example.photoblog;

public class Users {

    private String name;
    private String Bio;
    private String imageCompressed;
    private String id;

    public Users() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBio(String Bio) {
        this.Bio = Bio;
    }

    public void setImageCompressed(String imageCompressed) {
        this.imageCompressed = imageCompressed;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return this.Bio;
    }

    public String getImageCompressed() {
        return imageCompressed;
    }

    public Users(String id, String name, String Bio, String imageCompressed) {
        this.id = id;
        this.name = name;
        this.Bio = Bio;
        this.imageCompressed = imageCompressed;
    }
}
