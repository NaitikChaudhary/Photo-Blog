package com.example.photoblog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePostsAdapter extends RecyclerView.Adapter<HomePostsAdapter.PostsViewHolder> {

    public List<Posts> mPostsList;

    public HomePostsAdapter(List<Posts> mPostsList) {
        this.mPostsList = mPostsList;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_post_user ,parent, false);
        
        return new PostsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostsViewHolder holder, int position) {

        final Posts p = mPostsList.get(position);
        FirebaseDatabase.getInstance().getReference().child("Users").child(p.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    if(dataSnapshot.hasChild("name")) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        holder.mUserName.setText(name);
                    }
                    if(dataSnapshot.hasChild("imageCompressed")) {
                        String image = dataSnapshot.child("imageCompressed").getValue().toString();
                        Glide.with(holder.mUserImage.getContext())
                                .load(image)
                                .into(holder.mUserImage);
                    }

                    FirebaseDatabase.getInstance().getReference().child("Posts").child(p.getId()).child(p.getImageId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot != null) {
                                if(dataSnapshot.hasChild("imageCompressed")) {
                                    String image = dataSnapshot.child("imageCompressed").getValue().toString();
                                    Glide.with(holder.mPostImage.getContext())
                                            .load(image)
                                            .into(holder.mPostImage);
                                }
                                if(dataSnapshot.hasChild("time")) {
                                    String timeEpoch = dataSnapshot.child("time").getValue().toString();
                                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                                    long lastSeen = Long.parseLong(timeEpoch);
                                    String time = getTimeAgo.getTimeAgo(lastSeen, holder.timeAgo.getContext());
                                    holder.timeAgo.setText(time);
                                }
                                if(dataSnapshot.hasChild("likes")) {
                                    if(dataSnapshot.child("likes").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        holder.mLikeButton.setBackgroundResource(R.drawable.heart_full);
                                    } else {
                                        holder.mLikeButton.setBackgroundResource(R.drawable.heart);
                                    }
                                }
                                if(dataSnapshot.hasChild("caption")) {
                                    holder.postCaption.setVisibility(View.VISIBLE);
                                    String caption = dataSnapshot.child("caption").getValue().toString();
                                    holder.postCaption.setText(caption);
                                } else {
                                    holder.postCaption.setVisibility(View.GONE);
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

        holder.mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToProfile(p.getId(), holder.mUserImage.getContext());
            }
        });

        holder.mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToProfile(p.getId(), holder.mUserName.getContext());
            }
        });

        holder.mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewPostActivity(p.getId(), p.getImageId(), holder.mPostImage.getContext());
            }
        });

        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("Posts").child(p.getId()).child(p.getImageId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null) {
                            setNoLikes(holder, p);
                            if(dataSnapshot.hasChild("likes")) {
                                if(dataSnapshot.child("likes").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    FirebaseDatabase.getInstance().getReference().child("Posts")
                                            .child(p.getId()).child(p.getImageId()).child("likes")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    holder.mLikeButton.setBackgroundResource(R.drawable.heart);
                                } else {
                                    FirebaseDatabase.getInstance().getReference().child("Posts")
                                            .child(p.getId()).child(p.getImageId()).child("likes")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(System.currentTimeMillis());
                                    holder.mLikeButton.setBackgroundResource(R.drawable.heart_full);
                                }
                            } else {
                                FirebaseDatabase.getInstance().getReference().child("Posts")
                                        .child(p.getId()).child(p.getImageId()).child("likes")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(System.currentTimeMillis());
                                holder.mLikeButton.setBackgroundResource(R.drawable.heart_full);
                            }
                            setNoLikes(holder, p);
                        }
                    }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        setNoLikes(holder, p);

    }

    private void openViewPostActivity(String id, String imageId, Context context) {

        Intent profileIntent = new Intent(context, VIewPostActivity.class);
        profileIntent.putExtra("user_id", id);
        profileIntent.putExtra("image_id", imageId);
        context.startActivity(profileIntent);

    }

    private void sendToProfile(String id, Context context) {

        Intent profileIntent = new Intent(context, ProfileActivity.class);
        profileIntent.putExtra("user_id", id);
        context.startActivity(profileIntent);

    }

    public void setNoLikes(final PostsViewHolder holder, Posts p) {

        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(p.getId()).child(p.getImageId());
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("likes")) {
                    if(dataSnapshot.child("likes").getChildrenCount() == 1) {
                        holder.noOfLikes.setText("1 like");
                    } else if(dataSnapshot.child("likes").getChildrenCount() > 1) {
                        holder.noOfLikes.setText((int) dataSnapshot.child("likes").getChildrenCount() + " likes");
                    }
                } else {
                    holder.noOfLikes.setText("0 likes");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPostsList.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView mUserImage;
        public TextView mUserName, postCaption;
        public ImageView mPostImage;
        public ImageButton mLikeButton;
        public TextView noOfLikes, timeAgo;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserImage = itemView.findViewById(R.id.userImage);
            mUserName = itemView.findViewById(R.id.userName);
            mPostImage = itemView.findViewById(R.id.userPostImage);
            postCaption = itemView.findViewById(R.id.userPostCaption);
            mLikeButton = itemView.findViewById(R.id.likeButton);
            noOfLikes = itemView.findViewById(R.id.noOfLikes);
            timeAgo = itemView.findViewById(R.id.timeAgo);
        }
    }



}
