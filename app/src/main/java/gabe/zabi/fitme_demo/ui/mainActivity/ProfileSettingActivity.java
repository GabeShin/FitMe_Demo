package gabe.zabi.fitme_demo.ui.mainActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.data.MyContract;
import gabe.zabi.fitme_demo.model.UserProfile;
import gabe.zabi.fitme_demo.ui.BaseActivity;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Gabe on 2017-02-14.
 */

public class ProfileSettingActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = ProfileSettingActivity.class.getSimpleName();

    private String mProfileImage;
    private int mGoal;
    private String mNickname;
    private String mHeight;
    private String mWeight;
    private int mExperience;
    private String mQuestion1;
    private String mQuestion2;
    private String mQuestion3;
    private String mQuestion4;
    private String mQuestion5;
    private String mQuestion6;
    private String mSpecialQuestion;

    private UserProfile mUserProfile;

    private ImageView mImageViewProfileImage;
    private Spinner mSpinnerGoal;
    private EditText mEditTextNickname;
    private EditText mEditTextHeight;
    private EditText mEditTextWeight;
    private Spinner mSpinnerExperience;
    private EditText mEditTextQ1;
    private EditText mEditTextQ2;
    private EditText mEditTextQ3;
    private EditText mEditTextQ4;
    private EditText mEditTextQ5;
    private EditText mEditTextQ6;
    private EditText mEditTextSpecialQ;

    private Toolbar mToolbar;

    private Uri mProfileUri;
    private static final int PROFILE_LOADER = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        // set user database uri
        mProfileUri = MyContract.UserEntry.CONTENT_URI;
        getSupportLoaderManager().initLoader(PROFILE_LOADER, null, this);

        initializeScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save_profile:
                getAllProfileDataFromApp();
                saveUserProfileToFirebase();
                insertOrUpdateUserProfileFromFirebaseToContentProvider();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeScreen(){
        mImageViewProfileImage = (ImageView) findViewById(R.id.profile_activity_profile_picture);
        mSpinnerGoal = (Spinner) findViewById(R.id.spinner_goal);
        mEditTextNickname = (EditText) findViewById(R.id.et_nickname);
        mEditTextHeight = (EditText) findViewById(R.id.et_height);
        mEditTextWeight = (EditText) findViewById(R.id.et_weight);
        mSpinnerExperience = (Spinner) findViewById(R.id.spinner_experience);
        mEditTextQ1 = (EditText) findViewById(R.id.et_question_1);
        mEditTextQ2 = (EditText) findViewById(R.id.et_question_2);
        mEditTextQ3 = (EditText) findViewById(R.id.et_question_3);
        mEditTextQ4 = (EditText) findViewById(R.id.et_question_4);
        mEditTextQ5 = (EditText) findViewById(R.id.et_question_5);
        mEditTextQ6 = (EditText) findViewById(R.id.et_question_6);
        mEditTextSpecialQ = (EditText) findViewById(R.id.et_special_info);

        mToolbar = (Toolbar) findViewById(R.id.profile_activity_tool_bar);
        ProfileSettingActivity.this.setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void getAllProfileDataFromApp(){
        mGoal = mSpinnerGoal.getSelectedItemPosition();
        mNickname = mEditTextNickname.getText().toString();
        mHeight = mEditTextHeight.getText().toString();
        mWeight = mEditTextWeight.getText().toString();
        mExperience = mSpinnerExperience.getSelectedItemPosition();
        mQuestion1 = mEditTextQ1.getText().toString();
        mQuestion2 = mEditTextQ2.getText().toString();
        mQuestion3 = mEditTextQ3.getText().toString();
        mQuestion4 = mEditTextQ4.getText().toString();
        mQuestion5 = mEditTextQ5.getText().toString();
        mQuestion6 = mEditTextQ6.getText().toString();
        mSpecialQuestion = mEditTextSpecialQ.getText().toString();
    }

    public void saveUserProfileToFirebase(){
        mUserProfile = new UserProfile();

//        mUserProfile.setName();
//        mUserProfile.setEmail();
//        mUserProfile.setBirthday();
//        mUserProfile.setGender();
//        mUserProfile.setProfileImage();

        mUserProfile.setExperience(mExperience);
        mUserProfile.setGoal(mGoal);
        mUserProfile.setHeight(mHeight);
        mUserProfile.setWeight(mWeight);
        mUserProfile.setNickname(mNickname);
        mUserProfile.setQuestion1(mQuestion1);
        mUserProfile.setQuestion2(mQuestion2);
        mUserProfile.setQuestion3(mQuestion3);
        mUserProfile.setQuestion4(mQuestion4);
        mUserProfile.setQuestion5(mQuestion5);
        mUserProfile.setQuestion6(mQuestion6);
        mUserProfile.setSpecialQuestion(mSpecialQuestion);

        mUserProfile.saveUser(mCreatedUid);
    }

    private void insertOrUpdateUserProfileFromFirebaseToContentProvider(){

        String selection = MyContract.UserEntry.COLUMN_FITME_UID + "=?";
        String[] selectionArg = {mCreatedUid};

        ContentValues cv = new ContentValues();

        Log.v(LOG_TAG, "Nickname input is... " + mNickname);

        cv.put(MyContract.UserEntry.COLUMN_FITME_UID, mCreatedUid);
        cv.put(MyContract.UserEntry.COLUMN_EXPERIENCE, mExperience);
        cv.put(MyContract.UserEntry.COLUMN_GOAL, mGoal);
        cv.put(MyContract.UserEntry.COLUMN_HEIGHT, mHeight);
        cv.put(MyContract.UserEntry.COLUMN_WEIGHT, mWeight);
        cv.put(MyContract.UserEntry.COLUMN_NICKNAME, mNickname);
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_ONE, mQuestion1);
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_TWO, mQuestion2);
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_THREE, mQuestion3);
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_FOUR, mQuestion4);
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_FIVE, mQuestion5);
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_SIX, mQuestion6);
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_SPECIAL, mSpecialQuestion);

        Cursor cursor = getContentResolver().query(MyContract.UserEntry.CONTENT_URI, null, selection, selectionArg, null);

        if (!cursor.moveToFirst()) {
            // if current user's profile does NOT exist, insert.
            // if current user's profile ALREADY exists, update instead.
            // insert user profile data into the database.
            Uri insertedRow = getContentResolver().insert(MyContract.UserEntry.CONTENT_URI, cv);
            Log.v(LOG_TAG, "inserted Row into : " + insertedRow);
        } else {
            // update user profile data into the database.
            getContentResolver().update(MyContract.UserEntry.CONTENT_URI, cv, selection, selectionArg);
            Log.v(LOG_TAG, "updated user profile");
        }

        cursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(), mProfileUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()){
            // retrieve user profile data from the database...
            mGoal = data.getInt(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_GOAL));
            mNickname = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_NICKNAME));
            mHeight = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_HEIGHT));
            mWeight = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_WEIGHT));
            mExperience = data.getInt(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_EXPERIENCE));
            mQuestion1 = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_QUESTION_ONE));
            mQuestion2 = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_QUESTION_TWO));
            mQuestion3 = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_QUESTION_THREE));
            mQuestion4 = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_QUESTION_FOUR));
            mQuestion5 = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_QUESTION_FIVE));
            mQuestion6 = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_QUESTION_SIX));
            mSpecialQuestion = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_QUESTION_SPECIAL));
            Log.v(LOG_TAG, "Nickname from database is... " + mNickname);
        }

        // populate views with loaded values
        mSpinnerGoal.setSelection(mGoal, true);
        mEditTextNickname.setText(mNickname);
        mEditTextHeight.setText(mHeight);
        mEditTextWeight.setText(mWeight);
        mSpinnerExperience.setSelection(mExperience);
        mEditTextQ1.setText(mQuestion1);
        mEditTextQ2.setText(mQuestion2);
        mEditTextQ3.setText(mQuestion3);
        mEditTextQ4.setText(mQuestion4);
        mEditTextQ5.setText(mQuestion5);
        mEditTextQ6.setText(mQuestion6);
        mEditTextSpecialQ.setText(mSpecialQuestion);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
