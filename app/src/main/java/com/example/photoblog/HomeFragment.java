package com.example.photoblog;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {

    private FloatingActionButton AddPostButton;
    private int PICK_IMAGE = 1;
    private Uri filePath;
    private RecyclerView mAllPostsList;

    private HomePostsAdapter mAdapter;

    private List<Posts> mPostsList = new ArrayList<>();

    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private DatabaseReference users;
    private DatabaseReference posts;


    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_home, container, false);

        AddPostButton = view.findViewById(R.id.floatingActionButton);
        mAllPostsList = view.findViewById(R.id.allPostsHome);

        users = FirebaseDatabase.getInstance().getReference().child("Follow");
        posts = FirebaseDatabase.getInstance().getReference().child("Posts");
        users.keepSynced(true);
        posts.keepSynced(true);

        AddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery();
            }
        });

        mAdapter = new HomePostsAdapter(mPostsList);

        LinearLayoutManager mLinearLayout = new LinearLayoutManager(getActivity().getApplicationContext());

        mAllPostsList.setHasFixedSize(true);
        mAllPostsList.setLayoutManager(mLinearLayout);
        mAllPostsList.setAdapter(mAdapter);

        mPostsList.clear();
        loadPosts();

        return view;
    }

    private void loadPosts() {

        final DatabaseReference posts = FirebaseDatabase.getInstance().getReference().child("Posts");
        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                mPostsList.clear();
//                mAdapter.notifyDataSetChanged();

                for (final DataSnapshot userSnap: dataSnapshot.getChildren()) {
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot followSnap) {
                            if (followSnap.child(currentUserId).hasChild(userSnap.getKey()) || (userSnap.getKey().equals(currentUserId))) {
                                for (DataSnapshot postSnap: userSnap.getChildren()) {
                                    Posts p = postSnap.getValue(Posts.class);

                                    if(!hasPost(p)) {
                                        mPostsList.add(p);
                                        Collections.sort(mPostsList);
                                        mAdapter.notifyDataSetChanged();
                                    }

//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                        if(!(mPostsList.stream().anyMatch(ti -> ti.getImageId() == p.getImageId()))) {
//
//                                        }
//                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private boolean hasPost(Posts p) {
        for(Posts po : mPostsList) {
            if(po.getImageId() == p.getImageId()) {
                return true;
            }
        }
        return false;
    }

    private void pickImageFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            if(data.getData() != null) {
                filePath = data.getData();
                Intent i = new Intent(getActivity().getApplicationContext(), PostActivity.class);
                i.putExtra("filePath", filePath);
                startActivity(i);
            }

        }else {
            Toast.makeText(getActivity().getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

}
