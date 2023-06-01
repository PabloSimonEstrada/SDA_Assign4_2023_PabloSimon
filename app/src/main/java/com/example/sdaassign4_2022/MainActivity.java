package com.example.sdaassign4_2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

/*
 * @author Chris Coughlan 2019
 */
public class MainActivity extends AppCompatActivity {
    // This constant is used to manage fragments lifecycle
    public static final int BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT = 1;

    // This is the ViewPager object for managing pages of fragments
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_main);

        // Set the toolbar
        // The toolbar is a generalization of action bars for use within application layouts
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup ViewPager and its adapter to handle fragment transactions
        viewPager = findViewById(R.id.pager);
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, getApplicationContext());
        viewPager.setAdapter(adapter);

        // Set up TabLayout with the ViewPager for tab functionalities
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}