package com.example.photoblog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private CircleImageView profilePic;
    private EditText mName, mBio;
    private Button nextButton;

    private Bitmap selectedImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    private String currentUserId;

    private boolean picSelected = false;

    private Uri filePath;

    private int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        profilePic = findViewById(R.id.profPic_profile);
        mName = findViewById(R.id.name_profile);
        mBio = findViewById(R.id.bio_profile);
        nextButton = findViewById(R.id.next_profile);

        final String name = getIntent().getStringExtra("name");

        mName.setText(name);

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mRootRef.child("Users").child(currentUserId).child("imageCompressed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String imageUri = dataSnapshot.getValue().toString();
                    Glide.with(profilePic.getContext())
                            .load(imageUri)
                            .into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRootRef.child("Users").child(currentUserId).child("Bio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String bio = dataSnapshot.getValue().toString();
                    mBio.setText(bio);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picSelected = true;
                pickImageFromGallery();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String changedName = mName.getEditableText().toString();
                String bio = mBio.getEditableText().toString();

                addBio(bio);

                if(changedName != null) {
                    addName(changedName);
                }

                if(picSelected) {
                    compressAndUploadImage();
                } else {
                    openMainActivity();
                }

            }
        });

    }

    private void addName(String changedName) {
        mRootRef.child("Users").child(currentUserId).child("name").setValue(changedName);
    }

    private void addBio(String bio) {
        mRootRef.child("Users").child(currentUserId).child("Bio").setValue(bio);
    }

    private void compressAndUploadImage() {

        final StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference().child("Images").child(mAuth.getCurrentUser().getUid());
        final StorageReference compressedImageRef = imageStorageRef.child("/" + System.currentTimeMillis() + " - Compressed Profile Pic.jpg");
        final StorageReference imageRef = imageStorageRef.child("/" + System.currentTimeMillis() + " - Profile Pic.jpg");
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
                        mRootRef.child("Users").child(currentUserId).child("imageCompressed").setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountActivity.this, "Upload Failed -> " + e, Toast.LENGTH_LONG).show();
            }
        });

        UploadTask uploadTask = imageRef.putFile(filePath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mRootRef.child("Users").child(currentUserId).child("image").setValue(uri.toString());
                        openMainActivity();
                    }
                });
                Toast.makeText(AccountActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountActivity.this, "Upload Failed -> " + e, Toast.LENGTH_LONG).show();
            }
        });

    }

    void pickImageFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            if(data.getData() != null) {
                filePath = data.getData();
            }

            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                profilePic.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(AccountActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(AccountActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
