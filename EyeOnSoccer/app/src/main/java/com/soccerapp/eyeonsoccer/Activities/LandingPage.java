package com.soccerapp.eyeonsoccer.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.soccerapp.eyeonsoccer.Adapters.FragmentsPagerAdapter;
import com.soccerapp.eyeonsoccer.Classes.SlidingTabLayout;
import com.soccerapp.eyeonsoccer.Fragments.NavigationDrawerFragment;
import com.soccerapp.eyeonsoccer.R;

public class LandingPage extends AppCompatActivity {

    private ViewPager mViewPager;
    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SlidingTabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        mNavigationDrawerFragment.setup(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        //Sync action bar with drawer
        //Set home button to launch drawer;
        setHomeButton();

        mViewPager = (ViewPager) findViewById(R.id.landing_activity_pager);

        mFragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentsPagerAdapter(mFragmentManager));

        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setViewPager(mViewPager);
        mTabs.setDistributeEvenly(true);

        //setup actionbar tabs
        //setupTabs();
    }

    private void setHomeButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mNavigationDrawerFragment.onOptionsItemSelected(item)) {
                return true;
            }
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(LandingPage.this, Preferences.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
