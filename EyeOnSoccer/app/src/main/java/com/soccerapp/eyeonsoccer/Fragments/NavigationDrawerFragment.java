package com.soccerapp.eyeonsoccer.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.soccerapp.eyeonsoccer.Classes.Constants;
import com.soccerapp.eyeonsoccer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean mUserAwareOfDrawer;
    private boolean mStartingFirstTime;
    private View mView;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserAwareOfDrawer = Boolean.valueOf(readFromPreferences(getActivity(),
                Constants.KEY_USER_AWARE_OF_DRAWER, "false"));

            mStartingFirstTime = savedInstanceState != null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }


    public void setup(int fragmentId, DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
        mView = getActivity().findViewById(fragmentId);

        //Setup drawer toggle
        setupDrawerToggle();

        // Check if app is running first time and draweer has not been opened
        // before during life cycle of app
        if (!mUserAwareOfDrawer && !mStartingFirstTime) {
            mDrawerLayout.openDrawer(mView);
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
        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserAwareOfDrawer) {
                    mUserAwareOfDrawer = true;
                    saveToPreferences(getActivity(),
                            Constants.KEY_USER_AWARE_OF_DRAWER, mUserAwareOfDrawer + "");
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
    }

    public static void saveToPreferences(Context context, String preferenceKey, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (Constants.DRAWER_PREF_FILE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceKey, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceKey, String defauletValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (Constants.DRAWER_PREF_FILE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(preferenceKey, defauletValue);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mActionBarDrawerToggle.onOptionsItemSelected(item);
    }
}
