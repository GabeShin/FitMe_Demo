package gabe.zabi.fitme_demo.utils;

import android.content.Context;
import android.content.Intent;
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
    public static String encodeEmail(String emailOrNumber){
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
     * Save SharedPreference Plan parameters
     */

    public static void saveSharedPreferenceReturningUser(Context context, boolean returning){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.KEY_RETURNING_USER, returning);
        editor.apply();
    }

    public static boolean getSharedPreferenceReturningUser(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.KEY_RETURNING_USER, false);
    }

    public static void saveSharedPreferenceUserUid(Context context, String uid){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.KEY_USER_UID, uid);
        editor.apply();
    }

    public static String getSharedPreferenceUserUid(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Constants.KEY_USER_UID, null);
    }


    public static void saveSharedPreferencePlanUid(Context context, String uid){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.KEY_WORKOUT_UID, uid);
        editor.apply();
    }

    public static String getSharedPreferencePlanUid(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(Constants.KEY_WORKOUT_UID, null);
    }

    public static void saveSharedPreferenceWorkoutWeek(Context context, int week){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.KEY_WORKOUT_WEEK, week);
        editor.apply();
    }

    public static int getSharedPreferenceWorkoutWeek(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.KEY_WORKOUT_WEEK, 1);
    }

    public static void saveSharedPreferenceWorkoutDay(Context context, int day){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.KEY_WORKOUT_DAY, day);
        editor.apply();
    }

    public static int getSharedPreferenceWorkoutDay(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.KEY_WORKOUT_DAY, 1);
    }

    public static int getSharedPreferenceNumberOfWeeks(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.KEY_NUMBER_OF_WEEKS, 4);
    }

    public static void saveSharedPreferenceNumberOfWeeks(Context context, int size){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.KEY_NUMBER_OF_WEEKS, size);
        editor.apply();
    }

    public static int getSharedPreferencePlanSize(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(Constants.KEY_PLAN_SIZE, 1);
    }

    public static void saveSharedPreferencePlanSize(Context context, int size){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.KEY_PLAN_SIZE, size);
        editor.apply();
    }

    public static boolean getSharedPreferenceCompletionStatus(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.KEY_COMPLETION_STATUS, false);
    }

    public static void saveSharedPreferenceCompletionStatus(Context context, boolean completed){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.KEY_COMPLETION_STATUS, completed);
        editor.apply();
    }

    public static void clearAllSharedPreferences(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
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
