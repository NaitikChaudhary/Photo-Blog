package com.example.photoblog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VIewPostActivity extends AppCompatActivity {

    private CircleImageView userImage;
    private CardView cardViewImage;
    private ImageView postImage;
    private TextView name, caption, noOfLikes, timeAgoText;
    private EditText commentText;
    private ImageButton likeButton;
    private Button commentButton;
    private RecyclerView allCommentsList;

    private String userId, imageId;

    private DatabaseReference mRootRef;

    List<Comments> mCommentsList = new ArrayList<>();
    CommentsAdapter mAdapter = new CommentsAdapter(mCommentsList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);


//    ------------------initialising views-------------------------------

        userImage = findViewById(R.id.userImageComment);
        cardViewImage = findViewById(R.id.imageCardViewComment);
        postImage = findViewById(R.id.userPostImageComment);
        name = findViewById(R.id.userNameComment);
        caption = findViewById(R.id.userPostCaptionComment);
        noOfLikes = findViewById(R.id.noOfLikesComment);
        timeAgoText = findViewById(R.id.timeAgoComment);
        commentText = findViewById(R.id.commentText);
        likeButton = findViewById(R.id.likeButtonComment);
        commentButton = findViewById(R.id.commentButton);
        allCommentsList = findViewById(R.id.allCommentsListView);


//    -------------------getting String extras----------------------------

        userId = getIntent().getStringExtra("user_id");
        imageId = getIntent().getStringExtra("image_id");

//    -------------------initialising recyclerView------------------------

        LinearLayoutManager mLinearLayout = new LinearLayoutManager(getApplicationContext());

        allCommentsList.setHasFixedSize(true);
        allCommentsList.setLayoutManager(mLinearLayout);
        allCommentsList.setAdapter(mAdapter);
        mRootRef = FirebaseDatabase.getInstance().getReference();

        loadComments();

        mRootRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dataSnapshot.hasChild("name")){
                        String userName = dataSnapshot.child("name").getValue().toString();
                        name.setText(userName);
                    }
                    if(dataSnapshot.hasChild("imageCompressed")){
                        String image = dataSnapshot.child("imageCompressed").getValue().toString();
                        Glide.with(userImage.getContext()).load(image).into(userImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("Posts").child(userId).child(imageId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null) {

                    if(dataSnapshot != null) {
                        if(dataSnapshot.hasChild("imageCompressed")) {
                            String image = dataSnapshot.child("imageCompressed").getValue().toString();
                            Glide.with(getApplicationContext())
                                    .load(image)
                                    .into(postImage);
                        }
                        if(dataSnapshot.hasChild("time")) {
                            String timeEpoch = dataSnapshot.child("time").getValue().toString();
                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            long lastSeen = Long.parseLong(timeEpoch);
                            String time = getTimeAgo.getTimeAgo(lastSeen, timeAgoText.getContext());
                            timeAgoText.setText(time);
                        }
                        if(dataSnapshot.hasChild("likes")) {
                            if(dataSnapshot.child("likes").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                likeButton.setBackgroundResource(R.drawable.heart_full);
                            }
                        }
                        if(dataSnapshot.hasChild("caption")) {
                            caption.setVisibility(View.VISIBLE);
                            String userCaption = dataSnapshot.child("caption").getValue().toString();
                            caption.setText(userCaption);
                        } else {
                            caption.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setNoOfLikes();

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commentText.getText().toString().trim().length() > 0) {
                    String pushId = mRootRef.child("Posts").child(userId).child(imageId).child("comments").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                    HashMap<String, String> commentMap = new HashMap<>();
                    commentMap.put("fromUser",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    commentMap.put("comment", commentText.getEditableText().toString());
                    commentMap.put("time", String.valueOf(System.currentTimeMillis()));
                    commentMap.put("commentId", pushId);
                    commentMap.put("imageId", imageId);
                    mRootRef.child("Posts").child(userId).child(imageId).child("comments").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pushId).setValue(commentMap);
                    commentText.setText("");
                }
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Posts").child(userId).child(imageId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null) {

                            if(dataSnapshot.hasChild("likes")) {
                                if(dataSnapshot.child("likes").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    FirebaseDatabase.getInstance().getReference().child("Posts")
                                            .child(userId).child(imageId).child("likes")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    likeButton.setBackgroundResource(R.drawable.heart);
                                } else {
                                    FirebaseDatabase.getInstance().getReference().child("Posts")
                                            .child(userId).child(imageId).child("likes")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(System.currentTimeMillis());
                                    likeButton.setBackgroundResource(R.drawable.heart_full);
                                }
                            } else {
                                FirebaseDatabase.getInstance().getReference().child("Posts")
                                        .child(userId).child(imageId).child("likes")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(System.currentTimeMillis());
                                likeButton.setBackgroundResource(R.drawable.heart_full);
                            }
                            setNoOfLikes();
                        }
                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void loadComments() {
        mRootRef.child("Posts")
                .child(userId).child(imageId).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCommentsList.clear();
                for (final DataSnapshot commentUsersSnap: dataSnapshot.getChildren()) {
                    for (DataSnapshot commentSnap : commentUsersSnap.getChildren()) {
                        Comments c = commentSnap.getValue(Comments.class);
                        mCommentsList.add(c);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setNoOfLikes() {

        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(userId).child(imageId);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("likes")) {
                    if(dataSnapshot.child("likes").getChildrenCount() == 1) {
                        noOfLikes.setText("1 like");
                    } else if(dataSnapshot.child("likes").getChildrenCount() > 1) {
                        noOfLikes.setText((int) dataSnapshot.child("likes").getChildrenCount() + " likes");
                    }
                } else {
                    noOfLikes.setText("0 likes");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent mainIntent = new Intent(VIewPostActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
