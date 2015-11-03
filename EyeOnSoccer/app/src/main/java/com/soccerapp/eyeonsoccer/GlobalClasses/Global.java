package com.soccerapp.eyeonsoccer.GlobalClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.soccerapp.eyeonsoccer.Model.League;
import com.soccerapp.eyeonsoccer.Model.Team;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Created by Asad on 27/10/2015. Represent global methods and fields
 */
public class Global {

    public static String selectedLeagueName = null; //To select selected team

    //To populate spinner in schedule fragment
    public static List<String> teamNameList = new ArrayList<String>();

    public static List<String> sortedTeamNameList = new ArrayList<String>();

    public static List<Team> teamList = new ArrayList<Team>();

    /**
     * Get leagues
     *
     * @return
     */
    public static List<League> leagues() {
        List<League> leagues = new ArrayList<League>();
        for (int index = 0; index < Constants.LEAGUE_NAMES.length; index++) {
            League league = new League();
            league.setName(Constants.LEAGUE_NAMES[index]);
            league.setLogoId(Constants.LEAGUE_LOGOS[index]);

            leagues.add(league);
        }
        return leagues;
    }

    /**
     * Show toast message
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
