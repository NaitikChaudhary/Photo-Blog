package com.example.photoblog;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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


public class ProfileFragment extends Fragment {

    private Button mEditProfile;
    private String name;
    private String bio;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    private CircleImageView profilePic;
    private TextView mName, mBio;

    private RecyclerView mAllPostsList;

    private List<Posts> mPostsList = new ArrayList<>();

    private MyPostsAdapter mAdapter;

    private DatabaseReference users;
    private DatabaseReference posts;

    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mAllPostsList = view.findViewById(R.id.profileFragmentList);

        profilePic = view.findViewById(R.id.profileFragmentImage);
        mName = view.findViewById(R.id.profileFragmentName);
        mBio = view.findViewById(R.id.profileFragmentBio);

        LinearLayoutManager mLinearLayout = new LinearLayoutManager(getActivity().getApplicationContext());

        mAdapter = new MyPostsAdapter(mPostsList);

        mAllPostsList.setHasFixedSize(true);
        mAllPostsList.setLayoutManager(mLinearLayout);
        mAllPostsList.setAdapter(mAdapter);

        users = FirebaseDatabase.getInstance().getReference().child("Follow");
        posts = FirebaseDatabase.getInstance().getReference().child("Posts");
        users.keepSynced(true);
        posts.keepSynced(true);

        mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    if(dataSnapshot.hasChild("name")) {
                        name = dataSnapshot.child("name").getValue().toString();
                        mName.setText(name);
                    }

                    if(dataSnapshot.hasChild("Bio")) {
                        bio = dataSnapshot.child("Bio").getValue().toString();
                        mBio.setText(bio);
                    }

                    if(dataSnapshot.hasChild("imageCompressed")) {
                        String imageUri = dataSnapshot.child("imageCompressed").getValue().toString();
                        Glide.with(profilePic.getContext())
                                .load(imageUri)
                                .into(profilePic);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadPosts();

        mEditProfile = view.findViewById(R.id.editProfile);
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileActivity(name);
            }
        });

        return view;
    }

    private void loadPosts() {

        final DatabaseReference posts = FirebaseDatabase.getInstance().getReference().child("Posts").child(currentUserId);
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

    private void openProfileActivity(String name) {

        Intent profileIntent = new Intent(getActivity().getApplicationContext(), AccountActivity.class);
        profileIntent.putExtra("name", name);
        startActivity(profileIntent);

    }

}
