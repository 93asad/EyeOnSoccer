package com.soccerapp.eyeonsoccer.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
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
    public static final String BASE_WEBLINK = "https://www.google.com/search?q=";
    public static final String TABLE_WEBLINK= BASE_WEBLINK + "%s standings";
    //public static final String ID_FOR_TABLE = "vk_tbl";
    public static final String TEAMS_CLASS = "sol-tr-hstts";
    public static final String RANK_CLASS = "sol-td-rank";
    public static final String TEAM_NAME_CLASS = "_h1c";
    public static final String STATS_CLASS = "vk_bk";
    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36";
    private static final String IMAGE_BASE_ENCODING = ";base64,";
    public static final String TEAM_LOGO_CLASS = "lr-logo-img";

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

    public static Bitmap getDecodedImage(String encodedImage) {
        String key = IMAGE_BASE_ENCODING;
        int index = encodedImage.indexOf(key);
        String base64Code = encodedImage.substring(index+key.length());
        Log.d("clear", base64Code);
        byte[] decodedString = Base64.decode(base64Code.getBytes(), Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
