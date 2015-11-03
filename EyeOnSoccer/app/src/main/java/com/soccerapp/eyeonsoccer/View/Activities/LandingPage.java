package com.soccerapp.eyeonsoccer.View.Activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.soccerapp.eyeonsoccer.GlobalClasses.Constants;
import com.soccerapp.eyeonsoccer.GlobalClasses.Global;
import com.soccerapp.eyeonsoccer.GlobalClasses.SlidingTabLayout;
import com.soccerapp.eyeonsoccer.Model.Team;
import com.soccerapp.eyeonsoccer.View.Fragments.NavigationDrawerFragment;
import com.soccerapp.eyeonsoccer.R;
import com.soccerapp.eyeonsoccer.View.Fragments.NewsFragment;
import com.soccerapp.eyeonsoccer.View.Fragments.ScheduleFragment;
import com.soccerapp.eyeonsoccer.View.Fragments.TableFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents main activity that hosts all fragments
 */
public class LandingPage extends AppCompatActivity {

    private static final String HREF_ATTR = "href";
    private static final String CLEAR_MESSAGE = "teamlist cleared";
    private static final String FALSE = "false";
    private static final String PREMIER_LEAGUE = "Barclays Premier League";
    private static final String LOG_KEY = "test";
    private ViewPager mViewPager;
    private FragmentManager mFragmentManager;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SlidingTabLayout mTabs; //Layout to manage tabs
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private final String FIRST_ENTRY = "All";
    private final int ZERO_INDEX = 0;
    private final int INDEX_ONE = 1;
    private final int INDEX_THREE = 3;
    private final int INDEX_FOUR = 4;
    private final int INDEX_FIVE = 5;
    private final int INDEX_SIX = 6;
    private final int INDEX_TWENTY_TWO = 22;
    private final int INDEX_TWENTY_THREE = 23;

    // To manage broadcast messages
    private LocalBroadcastManager mLocalBroadcastManager;

    // Fields to check whether app is being launched for first time
    private boolean mUserAwareOfDrawer;
    private boolean mStartingFirstTime;

    /**
     * Cteates instance of this landing page
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            getSupportActionBar().setTitle(savedInstanceState.getString(Constants.KEY_ACTION_BAR_TITLE));
        else getSupportActionBar().setTitle(PREMIER_LEAGUE);

        setContentView(R.layout.landing_page);

        //Set action bar
        Toolbar actionBarToolbar = (Toolbar) findViewById(R.id.action_bar);
        actionBarToolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange)));
        actionBarToolbar.setTitleTextColor(Color.WHITE);

        // Set broadcast manager
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        // Set drawer layout
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

    /**
     * Sets home button to options button
     */
    private void setHomeButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * On resume, fetchs table data
     */
    @Override
    protected void onResume() {
        super.onResume();

        new TableDataAsync().execute(getLeagueIndex(getSupportActionBar().getTitle().toString()));
    }

    /**
     * Save objects to bundle
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.KEY_ACTION_BAR_TITLE, getSupportActionBar().getTitle().toString());

        super.onSaveInstanceState(outState);
    }

    /**
     * Create options menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_landing_page, menu);
        return true;
    }

    /**
     * Setup oprions item select
     *
     * @param item
     * @return
     */
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

    /**
     * Setup navigation drawer
     *
     * @param savedInstanceState
     */
    public void setupNavigationDrawer(Bundle savedInstanceState) {

        // Check if user is running app for very first time
        mUserAwareOfDrawer = Boolean.valueOf(readFromPreferences(getApplicationContext(),
                Constants.KEY_USER_AWARE_OF_DRAWER, FALSE));

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

    /**
     * Setup drawer toggle
     */
    private void setupDrawerToggle() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(LandingPage.this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            /**Manage drawer open even
             *
             * @param drawerView
             */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserAwareOfDrawer) { //Checks if student is launchig app for first time
                    mUserAwareOfDrawer = true;
                    saveToPreferences(getApplicationContext(),
                            Constants.KEY_USER_AWARE_OF_DRAWER, mUserAwareOfDrawer + "");
                }
            }

            /**Manage drawer close event
             *
             * @param drawerView
             */
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (Global.selectedLeagueName == null) return;

                String currentActionBarTitle = getSupportActionBar().getTitle().toString();

                // If same league that is displayed, is tapped, don't do anything
                if (currentActionBarTitle.equals(Global.selectedLeagueName)) return;
                else {
                    getSupportActionBar().setTitle(Global.selectedLeagueName);

                    //Reset global team list
                    Global.teamList.clear();
                    Log.d(LOG_KEY, CLEAR_MESSAGE);
                    Global.teamNameList.clear();
                    Global.sortedTeamNameList.clear();

                    //Fetch new data
                    new TableDataAsync().execute(getLeagueIndex(Global.selectedLeagueName));

                    // Send broadcast to update data in fragments
                    mLocalBroadcastManager.sendBroadcast
                            (new Intent(Constants.LEAGUE_CHANGED_FILTER));
                }
            }
        };
    }

    /**
     * Given the league name, return index position of it in list of leagues
     *
     * @param currentActionBarTitle
     * @return
     */
    private int getLeagueIndex(String currentActionBarTitle) {
        return Arrays.asList(Constants.LEAGUE_NAMES).indexOf(currentActionBarTitle);
    }

    /**
     * Saves information about whether app is being run for first time
     *
     * @param context
     * @param preferenceKey
     * @param preferenceValue
     */
    private void saveToPreferences(Context context, String preferenceKey, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (Constants.DRAWER_PREF_FILE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceKey, preferenceValue);
        editor.apply();
    }

    /**
     * Reads information about whether app is being run for first time
     *
     * @param context
     * @param preferenceKey
     * @param defaultValue
     * @return
     */
    private String readFromPreferences(Context context, String preferenceKey, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (Constants.DRAWER_PREF_FILE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getString(preferenceKey, defaultValue);
    }

    /*******************************************************************************************
     * Pager Adapter
     *****************************************************************************************/

    /**
     * Adapter to manage tabs
     */
    private class FragmentsPagerAdapter extends FragmentStatePagerAdapter {

        /**
         * Constructor
         *
         * @param fm
         */
        public FragmentsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        /**
         * Given the tab position, return fragment at that position
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case Constants.TABLE_FRAGMENT_INDEX:
                    return new TableFragment();
                case Constants.SCHEDULE_FRAGMENT_INDEX:
                    return new ScheduleFragment();
                case Constants.NEWS_FRAGMENT_INDEX:
                    return new NewsFragment();
            }
            return null;
        }

        /**
         * Get number of tabs
         *
         * @return
         */
        @Override
        public int getCount() {
            return Constants.NUMBER_OF_FRAGMENTS;
        }

        /**
         * Given the position of tab, get tab name
         *
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return Constants.TAB_NAMES[position];
        }
    }

    /********************************************************************************
     * Async task
     ********************************************************************************/

    /**
     * Async task to get table data
     */
    private class TableDataAsync extends AsyncTask<Object, Void, Void> {

        /**
         * On pre execute
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Get data in background from web
         *
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Object... params) {
            int itemPosition = (int) params[ZERO_INDEX]; //Position of league in pre-defined list

            try {
                Document doc = Jsoup.connect(getTableLink(itemPosition))
                        .userAgent(Constants.USER_AGENT).get();
                Log.d("clear", doc.html());

                // Select tables that contains team rows
                Elements teams = doc.select("table").first().children().get(INDEX_ONE).children().select("tr");

                getTeamsData(teams);

                Log.d("clear", doc.html());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Given the postion, return league table link
         *
         * @param itemPosition
         * @return
         */
        private String getTableLink(int itemPosition) {
            return String.format(Constants.TABLE_WEBLINK,
                    Constants.LEAGUE_NAMES[itemPosition].replaceAll(" ", "-").toLowerCase(), Constants.TEAM_CODES[itemPosition]);
        }

        /**
         * Extracts data from retrieved html document
         *
         * @param teams
         */
        private void getTeamsData(Elements teams) {

            int numberOfTeams = teams.size();
            List<String> teamNames = new ArrayList<String>(); //Stores team names only
            List<Team> teamList = new ArrayList<Team>(); //Stores team objects

            // INDEX_ONE to ignore table header
            for (int index = INDEX_ONE; index < numberOfTeams; index++) {

                /**********HTML parsing to get data************/

                Element team = teams.get(index);

                String rank = team.child(ZERO_INDEX).text();
                String teamName = team.child(INDEX_ONE).text();
                String teamHomeLink = team.child(INDEX_ONE).child(ZERO_INDEX).attr(HREF_ATTR);

                String matchesPlayed = team.child(INDEX_THREE).text();
                String wins = team.child(INDEX_FOUR).text();
                String draws = team.child(INDEX_FIVE).text();
                String loss = team.child(INDEX_SIX).text();
                String goalDiff = team.child(INDEX_TWENTY_TWO).text();
                String points = team.child(INDEX_TWENTY_THREE).text();

                /**********HTML parsing to get data************/

                teamList.add(new Team(teamName, rank, matchesPlayed, wins, draws, loss, goalDiff, points, teamHomeLink));
                teamNames.add(teamName);
            }

            Global.teamNameList.addAll(teamNames); // Populate global team names list
            Global.sortedTeamNameList.addAll(teamNames);
            Collections.sort(Global.sortedTeamNameList);
            Global.sortedTeamNameList.add(ZERO_INDEX, FIRST_ENTRY);
            Global.teamList.addAll(teamList); // Populate global teat list of team objects
        }

        /**
         * On post execute
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
