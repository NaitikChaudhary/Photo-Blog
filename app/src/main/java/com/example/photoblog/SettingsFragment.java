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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SettingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView textView;
    private Button mLogoutBtn;
    private DatabaseReference mRootRef;
    private String User_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference();


        textView = view.findViewById(R.id.mainText);
        mLogoutBtn = view.findViewById(R.id.logoutBtnMain);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null) {
            openStartActivity();
        } else {
            mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        User_name = dataSnapshot.getValue().toString();
                        textView.setText("Hello " + User_name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logoutUser();

            }
        });

        return view;
    }

    private void logoutUser() {
        if(mAuth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            openStartActivity();
        }
    }

    private void openStartActivity() {
        Intent startIntent = new Intent(getActivity().getApplicationContext(), StartActivity.class);
        startActivity(startIntent);
        getActivity().finish();
    }

}
