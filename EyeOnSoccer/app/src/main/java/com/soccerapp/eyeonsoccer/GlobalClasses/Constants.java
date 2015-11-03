package com.soccerapp.eyeonsoccer.GlobalClasses;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.soccerapp.eyeonsoccer.Model.League;
import com.soccerapp.eyeonsoccer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asad on 17/10/2015. Global constants used in app
 */
public class Constants {

    // Broadcasts
    public static final String LEAGUE_CHANGED_FILTER = "leagueChanged";
    public static final IntentFilter INTENT_FILTER = new IntentFilter(LEAGUE_CHANGED_FILTER);

    // Preferences
    public static final String PREF_DATE_FORMAT = "dateFormat";
    public static final String LIST_PREFERENCE_KEY = "date_format";
    public static final String DEFAULT_PREF_SUMMARY = "No preference set";
    public static final String DRAWER_PREF_FILE_NAME = "drawerPrefs";
    public static final String KEY_USER_AWARE_OF_DRAWER = "userAwareOfDrawer";

    // Fragments
    public static final int NUMBER_OF_FRAGMENTS = 3;
    public static final int TABLE_FRAGMENT_INDEX = 0;
    public static final int SCHEDULE_FRAGMENT_INDEX = 1;
    public static final int NEWS_FRAGMENT_INDEX = 2;

    // Tabs
    public static final String[] TAB_NAMES = {"Table", "Fixtures", "News"};
    public static final String[] LEAGUE_NAMES = {"Barclays Premier League", "German Bundesliga",
            "Italian Serie A", "Spanish Primera División", "French Ligue 1"};

    public static final int[] LEAGUE_LOGOS = {R.drawable.premier_league, R.drawable.bundesliga,
            R.drawable.serie_a, R.drawable.la_liga, R.drawable.ligue};

    // Toast message upon item in navigation is clicked
    public static final String LEAGUE_SELECTED_MESSAGE = "%s has been selected";

    // Link to get the schedule data
    public static final String BASE_WEBLINK = "http://www.espnfc.com.au/";

    // Query to get current standings
    public static final String TABLE_WEBLINK = BASE_WEBLINK + "%s/%s/table";

    // Query to get league schedule
    public static final String LEAGUE_SCHEDULE_WEBLINK = BASE_WEBLINK +
            "%s/%s/fixtures?date=%s";

    public static final String LEAGUE_NEWS_WEBLINK = BASE_WEBLINK + "%s/%s/rss";

    // Html tag of fixtures in retrieved document
    public static final String TEAM_FIXTURES_CLASS = "score-list";

    // Class for score
    public static final String LEAGUE_FIXTURES_CLASS = "score-content";

    //Class for league fixture group
    public static final String LEAGUE_FIXTURES_GROUP = "score-league";

    // User agent to ensure that everytime same html source is retrieved
    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36";

    public static final String KEY_ACTION_BAR_TITLE = "actionBarTitle";
    public static final String[] MONTHS = {"January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"};

    public static final String[] TEAM_CODES = {"23", // premier league
            "10", // Bundesliga
            "12", // Serie A
            "15", // La liga
            "9"   // Ligue 1
    };

    public static final String KEY_MONTHS_SPINNER = "monthsSpinner";
    public static final String KEY_TEAMS_SPINNER = "teamsSpinner";
}
