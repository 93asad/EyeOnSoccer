package com.soccerapp.eyeonsoccer.GlobalClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.soccerapp.eyeonsoccer.Model.League;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asad on 27/10/2015.
 */
public class Global {

    public static String selectedLeagueName = null;

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
}
