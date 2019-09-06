package com.example.photoblog;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchFragment extends Fragment {

    private EditText mSearch;
    private ImageButton searchBtn;
    private RecyclerView mSearchResultList;

    private DatabaseReference mRootRef;

    public SearchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mSearch = view.findViewById(R.id.search_Fragment);
        searchBtn = view.findViewById(R.id.searchButtonFragment);
        mSearchResultList = view.findViewById(R.id.searchListFragment);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mSearchResultList.setLayoutManager(linearLayoutManager);

        mSearchResultList.setHasFixedSize(true);

        mSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    SearchUsers(s.toString());
                } else {
                    SearchUsers(".12gjnc6p[]..cl3ol,v");
                }
            }
        });

        return view;
    }

    private void SearchUsers(String name) {

        Query query = mRootRef.child("Users").orderByChild("name").startAt(name).endAt(name + "\uf8ff");

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();

        FirebaseRecyclerAdapter<Users, SearchViewHolder> adapter = new FirebaseRecyclerAdapter<Users, SearchViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SearchViewHolder searchViewHolder, int i, @NonNull final Users users) {
                searchViewHolder.setName(users.getName());
                searchViewHolder.setImage(users.getImageCompressed());
                searchViewHolder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
                        profileIntent.putExtra("user_id", users.getId());
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout, parent,false);
                SearchViewHolder searchViewHolder = new SearchViewHolder(view);

                return searchViewHolder;
            }
        };

        mSearchResultList.setAdapter(adapter);
        adapter.startListening();

    }


    @Override
    public void onStart() {

        super.onStart();

    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout root;

        public CircleImageView image;

        public TextView name;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.singleLayout);

            name = itemView.findViewById(R.id.singleName);

            image = itemView.findViewById(R.id.singleImage);
        }

        public void setName(String string) {

            name.setText(string);

        }

        public void setImage(String string) {

            Glide.with(image.getContext()).load(string).into(image);

        }
    }

}
