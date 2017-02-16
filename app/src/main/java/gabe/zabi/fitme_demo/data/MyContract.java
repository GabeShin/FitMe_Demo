package gabe.zabi.fitme_demo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Gabe on 2017-02-15.
 */

public class MyContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "gabe.zabi.fitme_demo";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/
    public static final String PATH_USER_PROFILE = "user_profile";
    public static final String PATH_WORKOUT_PLANS = "workout_plans";

    /* Inner class that defines the table contents of the location table */
    public static final class UserEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_PROFILE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_PROFILE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_PROFILE;

        // Table Name
        public static final String TABLE_NAME = "profile";

        // Basic user informations
        public static final String COLUMN_FITME_UID = "fitme_uid";
        public static final String COLUMN_UID = "uid";

        public static final String COLUMN_NAME = "user_name";
        public static final String COLUMN_EMAIL = "user_email";
        public static final String COLUMN_PHONE_NUMBER = "user_phone_number";
        public static final String COLUMN_GENDER = "user_gender";
        public static final String COLUMN_BIRTHDAY = "user_birthday";
        public static final String COLUMN_PROFILE_IMAGE = "user_profile_image_path";
        public static final String COLUMN_NICKNAME = "user_nickname";

        // workout
        public static final String COLUMN_GOAL = "workout_goal";
        public static final String COLUMN_HEIGHT = "user_height";
        public static final String COLUMN_WEIGHT = "user_weight";
        public static final String COLUMN_EXPERIENCE = "workout_experience";

        // Questions regarding user's dietary habit & workout experiences
        public static final String COLUMN_QUESTION_ONE = "question_one";
        public static final String COLUMN_QUESTION_TWO = "question_two";
        public static final String COLUMN_QUESTION_THREE = "question_three";
        public static final String COLUMN_QUESTION_FOUR = "question_four";
        public static final String COLUMN_QUESTION_FIVE = "question_five";
        public static final String COLUMN_QUESTION_SIX = "question_six";
        public static final String COLUMN_QUESTION_SPECIAL = "question_special";

    }
}
