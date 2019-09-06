package com.example.photoblog;

public class Posts implements Comparable<Posts>{

    private String imageCompressed, image, id, time, caption, imageId;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setImageCompressed(String imageCompressed) {
        this.imageCompressed = imageCompressed;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageCompressed() {
        return imageCompressed;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getCaption() {
        return caption;
    }

    public Posts(String imageCompressed, String image, String id, String time, String caption, String imageId) {
        this.imageCompressed = imageCompressed;
        this.image = image;
        this.id = id;
        this.time = time;
        this.caption = caption;
        this.imageId = imageId;
    }

    public Posts() {
    }


    @Override
    public int compareTo(Posts posts) {
        return posts.getTime().compareTo(this.getTime());
    }
}
