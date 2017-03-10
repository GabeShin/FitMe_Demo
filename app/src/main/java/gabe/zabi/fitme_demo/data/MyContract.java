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
    public static final String PATH_WORKOUT_HISTORY = "user_history";

    /* Inner class that defines the table contents of the location table */
    public static final class UserHistoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORKOUT_HISTORY).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT_HISTORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT_HISTORY;

        // Table Name
        public static final String TABLE_NAME = "user_workout_history";

        public static final String COLUMN_PLAN_UID = "plan_uid";
        public static final String COLUMN_EXERCISE_NAME = "exercise_name";
        public static final String COLUMN_WORKOUT_WEEK = "workout_week";
        public static final String COLUMN_WORKOUT_DAY = "workout_day";
        public static final String COLUMN_SET_NUMBER = "set_number";
        public static final String COLUMN_REPS = "reps";
        public static final String COLUMN_WEIGHT = "weight";

    }
}
