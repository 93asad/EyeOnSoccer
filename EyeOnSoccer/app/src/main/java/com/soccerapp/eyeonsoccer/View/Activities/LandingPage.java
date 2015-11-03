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

/**Represents main activity that hosts all fragments
 *
 */
public class LandingPage extends AppCompatActivity {

    private ViewPager mViewPager;
    private FragmentManager mFragmentManager;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SlidingTabLayout mTabs; //Layout to manage tabs
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    // To manage broadcast messages
    private LocalBroadcastManager mLocalBroadcastManager;

    // Fields to check whether app is being launched for first time
    private boolean mUserAwareOfDrawer;
    private boolean mStartingFirstTime;

    /**Cteates instance of this landing page
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) getSupportActionBar().setTitle(savedInstanceState.getString(Constants.KEY_ACTION_BAR_TITLE));
        else getSupportActionBar().setTitle("Barclays Premier League");

        setContentView(R.layout.landing_page);

        //Set action bar
        Toolbar actionBarToolbar = (Toolbar)findViewById(R.id.action_bar);
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

    /**Sets home button to options button
     *
     */
    private void setHomeButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**On resume, fetchs table data
     *
     */
    @Override
    protected void onResume() {
        super.onResume();

        new TableDataAsync().execute(getLeagueIndex(getSupportActionBar().getTitle().toString()));
    }

    /**Save objects to bundle
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.KEY_ACTION_BAR_TITLE, getSupportActionBar().getTitle().toString());

        super.onSaveInstanceState(outState);
    }

    /**Create options menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_landing_page, menu);
        return true;
    }

    /**Setup oprions item select
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

    /**Setup navigation drawer
     *
     * @param savedInstanceState
     */
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

    /**Setup drawer toggle
     *
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
                else
                {
                    getSupportActionBar().setTitle(Global.selectedLeagueName);

                    //Reset global team list
                    Global.teamList.clear();
                    Log.d("test", "teamlist cleared");
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

    /**Given the league name, return index position of it in list of leagues
     *
     * @param currentActionBarTitle
     * @return
     */
    private int getLeagueIndex(String currentActionBarTitle) {
        return Arrays.asList(Constants.LEAGUE_NAMES).indexOf(currentActionBarTitle);
    }

    /**Saves information about whether app is being run for first time
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

    /**Reads information about whether app is being run for first time
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

    /**Adapter to manage tabs
     *
     */
    private class FragmentsPagerAdapter extends FragmentStatePagerAdapter {

        /**Constructor
         *
         * @param fm
         */
        public FragmentsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        /**Given the tab position, return fragment at that position
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

        @Override
        public int getCount() {
            return Constants.NUMBER_OF_FRAGMENTS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Constants.TAB_NAMES[position];
        }
    }

    private class TableDataAsync extends AsyncTask<Object, Void, Void> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Object... params) {
            int itemPosition = (int) params[0];

            try {
                String s = String.format(getTableLink(itemPosition));
                Document doc = Jsoup.connect(getTableLink(itemPosition))
                        .userAgent(Constants.USER_AGENT).get();
                Log.d("clear", doc.html());

                Elements teams = doc.select("table").first().children().get(1).children().select("tr");
                //Element image = doc.select("img[class=lr-logo-img lr-standings-logo-img").first();
                getTeamsData(teams);

                Log.d("clear", doc.html());


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String getTableLink(int itemPosition) {
            return String.format(Constants.TABLE_WEBLINK,
                    Constants.LEAGUE_NAMES[itemPosition].replaceAll(" ", "-").toLowerCase(), Constants.TEAM_CODES[itemPosition] );
        }

        private void getTeamsData(Elements teams) {

            int numberOfTeams = teams.size();
            List<String> teamNames = new ArrayList<String>();
            List<Team> teamList = new ArrayList<Team>();

            // 1 to ignore table header
            for (int index = 1; index < numberOfTeams; index++) {

                /**********HTML parsing to get data************/

                Element team = teams.get(index);

                String rank = team.child(0).text();
                String teamName = team.child(1).text();
                String teamHomeLink = team.child(1).child(0).attr("href");


                //Elements stats = team.getElementsByClass(Constants.STATS_CLASS);
                String matchesPlayed = team.child(3).text();
                String wins = team.child(4).text();
                String draws = team.child(5).text();
                String loss = team.child(6).text();
                String goalDiff = team.child(22).text();
                String points = team.child(23).text();

                /**********HTML parsing to get data************/

                teamList.add(new Team(teamName, rank, matchesPlayed, wins, draws, loss, goalDiff, points, teamHomeLink));
                teamNames.add(teamName);
            }

            Global.teamNameList.addAll(teamNames);
            Global.sortedTeamNameList.addAll(teamNames);
            Collections.sort(Global.sortedTeamNameList);
            Global.sortedTeamNameList.add(0, "All");
            Global.teamList.addAll(teamList);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
