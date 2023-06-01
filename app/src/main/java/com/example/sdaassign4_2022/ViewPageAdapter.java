package com.example.sdaassign4_2022;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/*
 * viewPager adapter.
 * @author Chris Coughlan 2019
 */
// This class provides the pages to display for a ViewPager widget.
public class ViewPageAdapter extends FragmentPagerAdapter {

    private Context context;

    // Constructor for this class.
    // Receives a FragmentManager, an int for the behavior of the ViewPager, and the current Context
    ViewPageAdapter(FragmentManager fm, int behavior, Context nContext) {
        super(fm, behavior);
        context = nContext;
    }

    // This method returns the Fragment to display for a page
    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new Fragment();

        // Adjust the position because array starts at 0
        position = position+1;

        // Find and return the fragment corresponding to the selected tab position
        switch (position)
        {
            case 1:
                fragment = new Welcome();
                break;
            case 2:
                fragment = new BookList();
                break;
            case 3:
                fragment = new Settings();
                break;
        }

        return fragment;
    }

    // This method returns the number of pages (tabs) to display
    @Override
    public int getCount() {
        return 3;
    }

    // This method returns the title to display for a page
    @Override
    public CharSequence getPageTitle(int position) {
        // Adjust the position because array starts at 0
        position = position+1;

        CharSequence tabTitle = "";

        // Find and return the title corresponding to the selected tab position
        switch (position)
        {
            case 1:
                tabTitle = "HOME";
                break;
            case 2:
                tabTitle = "BOOKS";
                break;
            case 3:
                tabTitle = "SETTINGS";
                break;
        }

        return tabTitle;
    }
}
