package com.soccerapp.eyeonsoccer.GlobalClasses;

import android.content.Context;
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
 * Created by Asad on 17/10/2015.
 */
public class Constants {

    // Preferences
    public static final String PREF_DATE_FORMAT = "dateFormat";
    public static final String LIST_PREFERENCE_KEY = "date_format";
    public static final String DEFAULT_PREF_SUMMARY = "No preference set";
    public static final String DRAWER_PREF_FILE_NAME = "drawerPrefs";
    public static final String KEY_USER_AWARE_OF_DRAWER = "userAwareOfDrawer";

    // Fragments
    public static final int NUMBER_OF_FRAGMENTS = 4;
    public static final int TABLE_FRAGMENT_INDEX = 0;
    public static final int SCHEDULE_FRAGMENT_INDEX = 1;
    public static final int NEWS_FRAGMENT_INDEX = 2;
    public static final int WATCH_FRAGMENT_INDEX = 3;

    // Tabs
    public static final String[] TAB_NAMES = {"Table", "Schedule", "News", "Watch"};
    public static final String[] LEAGUE_NAMES = {"Premier League", "Bundesliga",
            "Serie A", "La Liga", "Ligue 1"};
    public static final int[] LEAGUE_LOGOS = {R.drawable.premier_league, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};

    // Toast message upon item in navigation is clicked
    public static final String LEAGUE_SELECTED_MESSAGE = "%s has been selected";

    // Link to get the data (except news) from
    public static final String BASE_WEBLINK = "https://www.google.com/search?q=";

    // Query to get current standings
    public static final String TABLE_WEBLINK = BASE_WEBLINK + "%s standings";

    // Class names within retrieved html page
    public static final String TEAMS_CLASS = "sol-tr-hstts";
    public static final String RANK_CLASS = "sol-td-rank";
    public static final String TEAM_NAME_CLASS = "_h1c";
    public static final String STATS_CLASS = "vk_bk";
    public static final String TEAM_LOGO_CLASS = "lr-logo-img";

    // User agent to ensure that everytime same html source is retrieved
    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36";

    // Base encoding for images retrieved
    public static final String IMAGE_BASE_ENCODING = ";base64,";
}
