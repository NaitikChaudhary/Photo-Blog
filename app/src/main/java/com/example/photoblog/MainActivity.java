package com.example.photoblog;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends FragmentActivity {

    static final int NUM_ITEMS = 5;

    PagerAdapter mAdapter;

    private FirebaseUser mCurrentUser;

    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAdapter = new PagerAdapter(getSupportFragmentManager());

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navView.getMenu().getItem(0).setChecked(false);
                navView.getMenu().getItem(1).setChecked(false);
                navView.getMenu().getItem(2).setChecked(false);
                navView.getMenu().getItem(3).setChecked(false);
                navView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mCurrentUser == null) {
            openStartActivity();
        }

    }

    private void openStartActivity() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_search:
                    mPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_profile:
                    mPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_settings:
                    mPager.setCurrentItem(3);
                    return true;
                case R.id.navigation_notifications:
                    mPager.setCurrentItem(4);
                    return true;
            }
            return false;
        }
    };


}
