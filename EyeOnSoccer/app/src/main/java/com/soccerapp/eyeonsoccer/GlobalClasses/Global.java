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
 * Created by Asad on 27/10/2015.
 */
public class Global {

    //Used to keep track of action bar title between the fragments' life cycles
    public static final java.lang.String KEY_ACTION_BAR_TITLE = "actionBarTitle";

    public static String selectedLeagueName = null;

    //To populate spinner in schedule fragment
    public static List<String> teamNameList = new ArrayList<String>();

    public static List<String> sortedTeamNameList = new ArrayList<String>();
    public static List<Team> teamList = new ArrayList<Team>();

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

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getDecodedImage(String encodedImage) {
        String key = Constants.IMAGE_BASE_ENCODING;
        int index = encodedImage.indexOf(key);
        String base64Code = encodedImage.substring(index + key.length());
        Log.d("clear", base64Code);
        byte[] decodedString = Base64.decode(base64Code.getBytes(), Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String formatDateTime(String dateTime) {

        return /*Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR) + " " + */dateTime.replaceAll("st", "")
                .replaceAll("nd", "").replaceAll("rd", "").replaceAll("th", "").trim();
    }

    public static String getLocalTime(String dateTime) {

        String formattedDate = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy E dd MMMM, HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = df.parse(formatDateTime(dateTime));
            df.setTimeZone(TimeZone.getDefault());
            formattedDate = df.format(date);
        } catch (ParseException e) {
            Log.e("test", e.getMessage());
        }
        return formattedDate;
    }

}
