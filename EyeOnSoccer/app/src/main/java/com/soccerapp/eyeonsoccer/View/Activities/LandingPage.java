package com.soccerapp.eyeonsoccer.View.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.soccerapp.eyeonsoccer.GlobalClasses.Constants;
import com.soccerapp.eyeonsoccer.GlobalClasses.Global;
import com.soccerapp.eyeonsoccer.GlobalClasses.SlidingTabLayout;
import com.soccerapp.eyeonsoccer.View.Fragments.NavigationDrawerFragment;
import com.soccerapp.eyeonsoccer.R;
import com.soccerapp.eyeonsoccer.View.Fragments.NewsFragment;
import com.soccerapp.eyeonsoccer.View.Fragments.ScheduleFragment;
import com.soccerapp.eyeonsoccer.View.Fragments.TableFragment;
import com.soccerapp.eyeonsoccer.View.Fragments.WatchFragment;

public class LandingPage extends AppCompatActivity {

    private ViewPager mViewPager;
    private FragmentManager mFragmentManager;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SlidingTabLayout mTabs;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    // Fields to check whether app is being launched for first time
    private boolean mUserAwareOfDrawer;
    private boolean mStartingFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        // Setup side navigation
        setupNavigationDrawer(savedInstanceState);

        // Setup navigation fragment
        mNavigationDrawerFragment.setup(mDrawerLayout);

        //Set home button to launch drawer;
        setHomeButton();

        // Setup view pager to manage tabs
        mViewPager = (ViewPager) findViewById(R.id.landing_activity_pager);
        mFragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentsPagerAdapter(mFragmentManager));

        // Set view pager to tabs
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setViewPager(mViewPager);

        // Set tabs to be displayed evenly
        mTabs.setDistributeEvenly(true);
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

        // Let the drawer toggle to manage home button click
        if (id == android.R.id.home) {
            if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(LandingPage.this, Preferences.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupNavigationDrawer(Bundle savedInstanceState) {

        // Check if user is running app for very first time
        mUserAwareOfDrawer = Boolean.valueOf(readFromPreferences(getApplicationContext(),
                Constants.KEY_USER_AWARE_OF_DRAWER, "false"));

        // Check if activity is being created first time
        mStartingFirstTime = savedInstanceState != null;

        //Setup drawer toggle
        setupDrawerToggle();

        // Check if app is running first time and drawer has not been opened
        // before during life cycle of app
        if (!mUserAwareOfDrawer && !mStartingFirstTime) {
            mDrawerLayout.openDrawer(findViewById(R.id.fragment_navigation_drawer));
        }

        // Set listener to drawer layout
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        // Sync action bar with drawer
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });
    }

    private void setupDrawerToggle() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(LandingPage.this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserAwareOfDrawer) {
                    mUserAwareOfDrawer = true;
                    saveToPreferences(getApplicationContext(),
                            Constants.KEY_USER_AWARE_OF_DRAWER, mUserAwareOfDrawer + "");
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (Global.selectedLeagueName == null) return;

                String currentActionBarTitle = getSupportActionBar().getTitle().toString();

                if (currentActionBarTitle.equals(Global.selectedLeagueName)) return;
                else getSupportActionBar().setTitle(Global.selectedLeagueName);
            }
        };
    }

    private void saveToPreferences(Context context, String preferenceKey, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (Constants.DRAWER_PREF_FILE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceKey, preferenceValue);
        editor.apply();
    }

    private String readFromPreferences(Context context, String preferenceKey, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (Constants.DRAWER_PREF_FILE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(preferenceKey, defaultValue);
    }

    /*******************************************************************************************
     * Pager Adapter
     *****************************************************************************************/

    private class FragmentsPagerAdapter extends FragmentStatePagerAdapter {

        public FragmentsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case Constants.TABLE_FRAGMENT_INDEX:
                    return new TableFragment();
                case Constants.SCHEDULE_FRAGMENT_INDEX:
                    return new ScheduleFragment();
                case Constants.NEWS_FRAGMENT_INDEX:
                    return new NewsFragment();
                case Constants.WATCH_FRAGMENT_INDEX:
                    return new WatchFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return Constants.NUMBER_OF_FRAGMENTS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Constants.TAB_NAMES[position];
        }
    }
}
