package com.soccerapp.eyeonsoccer.Classes;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.soccerapp.eyeonsoccer.Classes.Model.League;
import com.soccerapp.eyeonsoccer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asad on 17/10/2015.
 */
public class Constants {
    public static final String PREF_DATE_FORMAT = "dateFormat";
    public static final String LIST_PREFERENCE_KEY = "date_format";
    public static final String DEFAULT_PREF_SUMMARY = "No preference set";
    public static final int NUMBER_OF_FRAGMENTS = 4;
    public static final int TABLE_FRAGMENT_INDEX = 0;
    public static final int SCHEDULE_FRAGMENT_INDEX = 1;
    public static final int NEWS_FRAGMENT_INDEX = 2;
    public static final int WATCH_FRAGMENT_INDEX = 3;
    public static final String DRAWER_PREF_FILE_NAME = "drawerPrefs";
    public static final String KEY_USER_AWARE_OF_DRAWER = "userAwareOfDrawer";
    public static final String[] TAB_NAMES = {"Table", "Schedule", "News", "Watch"};
    public static final String[] LEAGUE_NAMES = {"Premier League", "Bundesliga", "Serie A", "La Liga", "Ligue 1"};
    public static final int[] LEAGUE_LOGOS = {R.drawable.premier_league,R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher};
    public static final String LEAGUE_SELECTED_MESSAGE = "%s has been selected";

    public static List<League> leagues() {
        List<League> leagues = new ArrayList<League>();
        for (int index = 0; index < LEAGUE_NAMES.length; index++) {
            League league = new League();
            league.setName(LEAGUE_NAMES[index]);
            league.setLogoId(LEAGUE_LOGOS[index]);

            leagues.add(league);
        }
        return leagues;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
