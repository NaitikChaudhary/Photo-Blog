package com.example.photoblog;

public class Comments {

    private String comment;
    private String commentId;
    private String fromUser;
    private String time;
    private String imageId;

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getTime() {
        return time;
    }

    public Comments(String comment, String commentId, String fromUser, String time, String imageId) {
        this.comment = comment;
        this.commentId = commentId;
        this.fromUser = fromUser;
        this.time = time;
        this.imageId = imageId;
    }

    public Comments() {
    }
}
