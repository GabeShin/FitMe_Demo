package gabe.zabi.fitme_demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

/**
 * Created by Gabe on 2017-01-31.
 */

public class Utils {

    /**
     * Encode and Decode Email Address
     *
     * @param emailOrNumber
     * @return
     */
    public static String getEncodedEmail(String emailOrNumber){
        return emailOrNumber.replace("%", "%25")
                .replace(".", "%2E")
                .replace("#", "%23")
                .replace("$", "%24")
                .replace("/", "%2F")
                .replace("[", "%5B")
                .replace("]", "%5D");
    }

    public static String getDecodedEmail(String encodedEmail) {
        return encodedEmail.replace("%5D", "]")
                .replace("%5B", "[")
                .replace("%2F", "/")
                .replace("%24", "$")
                .replace("%23", "#")
                .replace("%2E", ".")
                .replace("%25", "%");
    }



    /**
     * Save SharedPreferenceUid in order to auto log-in the user.
     *
     * @param context
     * @param uid
     */
    public static void saveSharedPreferenceUid(Context context, String uid){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.KEY_CREATED_UID, uid);
        editor.apply();
    }

    public static String getSharedPreferenceUid(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Constants.KEY_CREATED_UID, null);
    }

    public static void clearSharedPreferenceUid(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Methods for troll-checking user inputs.
     */

    public static boolean validateUserBirthday(int birthday){
        // returns true if user input is valid

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        if (birthday >= currentYear || birthday <= currentYear - 100){
            // birthday cannot be bigger than the current year
            // birthday cannot be smaller than (current year - 100)
            return false;
        }

        return true;
    }

    public static boolean validateHeight(int height){
        // returns true if user input is valid
        // height cannot be smaller than 90cm
        // height cannot be bigger than 250cm
        if (height < 90 || height > 250){
            return false;
        }
        return true;
    }

    public static boolean validateWeight(int weight){
        // returns true if user input is valid
        // weight cannot be smaller than 20kg
        // weight cannot be bigger than 400kg
        if (weight < 20 || weight > 400){
            return false;
        }
        return true;
    }

}
