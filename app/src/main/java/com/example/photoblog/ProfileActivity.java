package com.example.photoblog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Button Follow;
    private TextView mNameUser, mBioUser;
    private CircleImageView imageUser;
    private String userId;

    private RecyclerView mPostsListView;
    private UserPostsAdapter mAdapter;

    private List<Posts> mPostsList = new ArrayList<>();

    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userId = getIntent().getStringExtra("user_id");

        Follow = findViewById(R.id.profile_follow);
        mNameUser = findViewById(R.id.profile_name);
        mBioUser = findViewById(R.id.profile_bio);
        imageUser = findViewById(R.id.profile_image);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mPostsListView = findViewById(R.id.profile_user_posts);

        LinearLayoutManager mLinearLayout = new LinearLayoutManager(ProfileActivity.this);

        mAdapter = new UserPostsAdapter(mPostsList);

        mPostsListView.setHasFixedSize(true);
        mPostsListView.setLayoutManager(mLinearLayout);
        mPostsListView.setAdapter(mAdapter);

        setUserDetails();

        loadPosts();

        Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRootRef.child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if( dataSnapshot != null) {
                            if(dataSnapshot.hasChild(userId)) {
                                mRootRef.child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userId).removeValue();
                                mRootRef.child("Followers").child(userId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                Follow.setText("Follow");
                            } else {
                                mRootRef.child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userId).setValue(System.currentTimeMillis());
                                mRootRef.child("Followers").child(userId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(System.currentTimeMillis());
                                Follow.setText("Unfollow");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void loadPosts() {

        final DatabaseReference posts = FirebaseDatabase.getInstance().getReference().child("Posts").child(userId);
        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPostsList.clear();
                for (final DataSnapshot userSnap: dataSnapshot.getChildren()) {

                    Posts p = userSnap.getValue(Posts.class);
                    mPostsList.add(p);

                    Collections.sort(mPostsList);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUserDetails() {
        mRootRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("name")) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    mNameUser.setText(name);
                }
                if(dataSnapshot.hasChild("Bio")) {
                    String bio = dataSnapshot.child("Bio").getValue().toString();
                    mBioUser.setText(bio);
                }
                if(dataSnapshot.hasChild("imageCompressed")) {
                    String image = dataSnapshot.child("imageCompressed").getValue().toString();
                    Glide.with(imageUser.getContext()).load(image).into(imageUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( dataSnapshot != null) {
                    if(dataSnapshot.hasChild(userId)) {
                        Follow.setText("Unfollow");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
