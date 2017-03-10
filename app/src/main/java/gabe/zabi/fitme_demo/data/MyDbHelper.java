package gabe.zabi.fitme_demo.data;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static gabe.zabi.fitme_demo.data.MyContract.UserHistoryEntry;

/**
 * Created by Gabe on 2017-02-15.
 */

public class MyDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "fitme.db";

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + UserHistoryEntry.TABLE_NAME + " (" +
                UserHistoryEntry._ID + " INTEGER PRIMARY KEY," +
                UserHistoryEntry.COLUMN_PLAN_UID+ " TEXT NOT NULL, " +
                UserHistoryEntry.COLUMN_WORKOUT_WEEK+ " INTEGER NOT NULL, " +
                UserHistoryEntry.COLUMN_WORKOUT_DAY+ " INTEGER NOT NULL, " +
                UserHistoryEntry.COLUMN_EXERCISE_NAME+ " TEXT NOT NULL, " +
                UserHistoryEntry.COLUMN_SET_NUMBER+ " INTEGER NOT NULL, " +
                UserHistoryEntry.COLUMN_REPS+ " INTEGER NOT NULL, " +
                UserHistoryEntry.COLUMN_WEIGHT + " REAL NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /*
     * Helper function for AndroidDatabaseManager
     */

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
