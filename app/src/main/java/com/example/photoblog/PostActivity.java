package com.example.photoblog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Uri imagePath;
    private ImageView mPostImage;
    private EditText postCaption;
    private Button PostButton;
    private Bitmap selectedImage;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private DatabaseReference mRootRef;

    HashMap<String, String> postMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mPostImage = findViewById(R.id.postImage);
        imagePath = getIntent().getParcelableExtra("filePath");
        PostButton = findViewById(R.id.postButton);
        postCaption = findViewById(R.id.postDescription);

        mPostImage.setImageURI(imagePath);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        PostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compressAndUploadImage();
            }
        });

    }

    private void compressAndUploadImage() {

        final StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference().child("Images").child(mAuth.getCurrentUser().getUid()).child("Posts");
        final StorageReference compressedImageRef = imageStorageRef.child("/" + System.currentTimeMillis() + " - Compressed Pic.jpg");
        final StorageReference imageRef = imageStorageRef.child("/" + System.currentTimeMillis() + " - Pic.jpg");


        try {
            final InputStream imageStream = getContentResolver().openInputStream(imagePath);
            selectedImage = BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(PostActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        Bitmap bmpCompressed = selectedImage;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmpCompressed.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();


        //uploading the image
        UploadTask compressedUploadTask = compressedImageRef.putBytes(data);
        compressedUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                compressedImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        postMap.put("imageCompressed",uri.toString());

                        UploadTask uploadTask = imageRef.putFile(imagePath);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if(postCaption.getText() != null) {
                                            postMap.put("caption", postCaption.getText().toString());
                                        }
                                        String pushId = mRootRef.child("Posts").child(currentUserId).push().getKey();
                                        postMap.put("imageId", pushId);
                                        postMap.put("image",uri.toString());
                                        postMap.put("id", currentUserId);
                                        postMap.put("time", String.valueOf(System.currentTimeMillis()));
                                        mRootRef.child("Posts").child(currentUserId).child(pushId).setValue(postMap);
                                        openMainActivity();
                                    }
                                });
                                Toast.makeText(PostActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PostActivity.this, "Upload Failed -> " + e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostActivity.this, "Upload Failed -> " + e, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void openMainActivity() {
        Intent intent = new Intent(PostActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
