package gabe.zabi.fitme_demo.utils;

/**
 * Created by Gabe on 2017-02-07.
 */

public class Constants {

    public static final String FIREBASE_LOCATION_USER = "user";
    public static final String FIREBASE_LOCATION_ACCOUNT_INFO = "account_information";
    public static final String FIREBASE_LOCATION_PROFILE_INFO = "profile_information";
    public static final String FIREBASE_LOCATION_PLAN_LIST = "plans";
    public static final String FIREBASE_LOCATION_PLAN_OVERVIEW = "plan_overview";
    public static final String FIREBASE_LOCATION_WORKOUTS = "workouts";
    public static final String FIREBASE_LOCATION_USER_ACTIVITY = "user_activity";

    public static final String FIREBASE_URL = "https://fitme-fe04c.firebaseio.com/";
    public static final String FIREBASE_URL_USER = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER;
    public static final String FIREBASE_URL_PLAN_LISTS = FIREBASE_URL + "/" + FIREBASE_LOCATION_PLAN_LIST;

    /**
     * Constants for SharedPreference Keys
     */
    public static String KEY_CREATED_UID = "CREATED_UID_KEY";

    public static String KEY_PROVIDER = "PROVIDER_KEY";
    public static final String KEY_VALUE_GOOGLE_PROVIDER = "google";
    public static final String KEY_VALUE_FACEBOOK_PROVIDER = "facebook";
    public static final String KEY_VALUE_KAKAO_PROVIDER = "kakao";

    /**
     * Constants for Firebase Log In
     */

}
