package com.example.photoblog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    public List<Comments> mCommentsList;

    public CommentsAdapter(List<Comments> mCommentsList) {
        this.mCommentsList = mCommentsList;
    }

    @NonNull
    @Override
    public CommentsAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_comment_layout ,parent, false);
        return new CommentsAdapter.CommentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsAdapter.CommentsViewHolder holder, int position) {

        final Comments c = mCommentsList.get(position);
        FirebaseDatabase.getInstance().getReference().child("Users").child(c.getFromUser()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    holder.commentTextView.setText(c.getComment());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView mUserImage;
        public TextView mUserName, commentTextView;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserImage = itemView.findViewById(R.id.userImageSingleComment);
            mUserName = itemView.findViewById(R.id.userNameSingleComment);
            commentTextView = itemView.findViewById(R.id.singleComment);
        }
    }

}
