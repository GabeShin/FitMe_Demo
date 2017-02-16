package gabe.zabi.fitme_demo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gabe.zabi.fitme_demo.data.MyContract;
import gabe.zabi.fitme_demo.model.User;

import static gabe.zabi.fitme_demo.data.MyContract.UserEntry;

/**
 * Created by Gabe on 2017-02-15.
 */

public class MyDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "fitme.db";

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY," +
                UserEntry.COLUMN_FITME_UID+ " TEXT NOT NULL, " +
//                UserEntry.COLUMN_UID + " TEXT NOT NULL, " +
                UserEntry.COLUMN_NAME + " TEXT, " +
                UserEntry.COLUMN_EMAIL + " TEXT, " +
                UserEntry.COLUMN_PHONE_NUMBER + " TEXT, " +
                UserEntry.COLUMN_GENDER + " TEXT, " +
                UserEntry.COLUMN_BIRTHDAY + " TEXT, " +
                UserEntry.COLUMN_PROFILE_IMAGE + " TEXT, " +
                UserEntry.COLUMN_NICKNAME + " TEXT, " +
                UserEntry.COLUMN_GOAL + " TEXT, " +
                UserEntry.COLUMN_HEIGHT + " TEXT, " +
                UserEntry.COLUMN_WEIGHT + " TEXT, " +
                UserEntry.COLUMN_EXPERIENCE + " TEXT, " +
                UserEntry.COLUMN_QUESTION_ONE + " TEXT, " +
                UserEntry.COLUMN_QUESTION_TWO + " TEXT, " +
                UserEntry.COLUMN_QUESTION_THREE + " TEXT, " +
                UserEntry.COLUMN_QUESTION_FOUR + " TEXT, " +
                UserEntry.COLUMN_QUESTION_FIVE + " TEXT, " +
                UserEntry.COLUMN_QUESTION_SIX + " TEXT, " +
                UserEntry.COLUMN_QUESTION_SPECIAL + " TEXT" +
                " );";

        db.execSQL(SQL_CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
