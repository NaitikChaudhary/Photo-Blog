package com.example.photoblog;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import static com.example.photoblog.MainActivity.NUM_ITEMS;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                HomeFragment fragmentHome = new HomeFragment();
                return fragmentHome;
            case 1:
                SearchFragment searchFragment = new SearchFragment();
                return searchFragment;
            case 2:
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            case 3:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            case 4:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            default:
                return null;

        }
    }

}
