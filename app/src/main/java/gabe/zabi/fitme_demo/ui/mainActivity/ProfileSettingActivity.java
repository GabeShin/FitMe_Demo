package gabe.zabi.fitme_demo.ui.mainActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.data.MyContract;
import gabe.zabi.fitme_demo.model.UserProfile;
import gabe.zabi.fitme_demo.ui.BaseActivity;
import gabe.zabi.fitme_demo.utils.CircleTransform;
import gabe.zabi.fitme_demo.utils.Constants;

/**
 * Created by Gabe on 2017-02-14.
 */

public class ProfileSettingActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = ProfileSettingActivity.class.getSimpleName();

    private String mProfileImage;
    private String mName;
    private String mEmail;
    private int mGender;
    private String mBirthday;
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

    private TextView mTextViewName;
    private TextView mTextViewBirthday;
    private TextView mTextViewGender;
    private TextView mTextViewEmail;
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

    private Firebase mRef;

    private Uri mProfileUri;
    private static final int PROFILE_LOADER = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        mRef = new Firebase(Constants.FIREBASE_URL_USER).child(mCreatedUid).child(Constants.FIREBASE_LOCATION_PROFILE_INFO);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                if (userProfile != null) mUserProfile = userProfile;
                else finish();

                addToContentProvider(mUserProfile);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, getString(R.string.log_error_the_read_failed) + firebaseError.getMessage());
            }
        });

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
                updateUserProfileToFirebase();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeScreen(){
        mTextViewName = (TextView) findViewById(R.id.profile_activity_name);
        mTextViewBirthday = (TextView) findViewById(R.id.profile_activity_birthday);
        mTextViewGender = (TextView) findViewById(R.id.profile_activity_gender);
        mTextViewEmail = (TextView) findViewById(R.id.profile_activity_email);
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

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void populateViews(UserProfile userProfile){
        mTextViewName.setText(userProfile.getName());
        mTextViewBirthday.setText(userProfile.getBirthday());
        mTextViewEmail.setText(userProfile.getEmail());

        if (userProfile.getGender() == 0) mTextViewGender.setText("Male");
        else mTextViewGender.setText("Female");

        mSpinnerGoal.setSelection(userProfile.getGoal());
        mEditTextNickname.setText(userProfile.getNickname());
        mEditTextHeight.setText(userProfile.getHeight());
        mEditTextWeight.setText(userProfile.getWeight());
        mSpinnerExperience.setSelection(userProfile.getExperience());
        mEditTextQ1.setText(userProfile.getQuestion1());
        mEditTextQ2.setText(userProfile.getQuestion2());
        mEditTextQ3.setText(userProfile.getQuestion3());
        mEditTextQ4.setText(userProfile.getQuestion4());
        mEditTextQ5.setText(userProfile.getQuestion5());
        mEditTextQ6.setText(userProfile.getQuestion6());
        mEditTextSpecialQ.setText(userProfile.getSpecialQuestion());
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

    public void updateUserProfileToFirebase(){

        Map<String, Object> map = new HashMap<>();
        map.put("question1", mQuestion1);
        map.put("question2", mQuestion2);
        map.put("question3", mQuestion3);
        map.put("question4", mQuestion4);
        map.put("question5", mQuestion5);
        map.put("question6", mQuestion6);
        map.put("specialQuestion", mSpecialQuestion);

        map.put("goal", mGoal);
        map.put("height", mHeight);
        map.put("weight", mWeight);
        map.put("nickname", mNickname);
        map.put("experience", mExperience);

        mRef.updateChildren(map);
    }

    private void addToContentProvider(UserProfile userProfile){

        String selection = MyContract.UserEntry.COLUMN_FITME_UID + "=?";
        String[] selectionArg = {mCreatedUid};

        ContentValues cv = new ContentValues();

        Log.v(LOG_TAG, "Nickname input is... " + mNickname);

        cv.put(MyContract.UserEntry.COLUMN_FITME_UID, mCreatedUid);

        cv.put(MyContract.UserEntry.COLUMN_NAME, userProfile.getName());
        cv.put(MyContract.UserEntry.COLUMN_GENDER, userProfile.getGender());
        cv.put(MyContract.UserEntry.COLUMN_BIRTHDAY, userProfile.getBirthday());
        cv.put(MyContract.UserEntry.COLUMN_EMAIL, userProfile.getEmail());
        cv.put(MyContract.UserEntry.COLUMN_PROFILE_IMAGE, userProfile.getProfileImage());

        cv.put(MyContract.UserEntry.COLUMN_EXPERIENCE, userProfile.getExperience());
        cv.put(MyContract.UserEntry.COLUMN_GOAL, userProfile.getGoal());
        cv.put(MyContract.UserEntry.COLUMN_HEIGHT, userProfile.getHeight());
        cv.put(MyContract.UserEntry.COLUMN_WEIGHT, userProfile.getWeight());
        cv.put(MyContract.UserEntry.COLUMN_NICKNAME, userProfile.getNickname());
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_ONE, userProfile.getQuestion1());
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_TWO, userProfile.getQuestion2());
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_THREE, userProfile.getQuestion3());
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_FOUR, userProfile.getQuestion4());
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_FIVE, userProfile.getQuestion5());
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_SIX, userProfile.getQuestion6());
        cv.put(MyContract.UserEntry.COLUMN_QUESTION_SPECIAL, userProfile.getSpecialQuestion());

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

            mName = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_NAME));
            mBirthday = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_BIRTHDAY));
            mEmail = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_EMAIL));
            mGender = data.getInt(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_GENDER));
            mProfileImage = data.getString(data.getColumnIndexOrThrow(MyContract.UserEntry.COLUMN_PROFILE_IMAGE));

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
        mTextViewName.setText(mName);
        mTextViewBirthday.setText(mBirthday);
        mTextViewEmail.setText(mEmail);
        if (mGender == 0) mTextViewGender.setText("Male");
        else mTextViewGender.setText("Female");

        Picasso.with(getApplicationContext())
                .load(mProfileImage)
                .placeholder(R.drawable.ic_account_circle_white_24dp)
                .transform(new CircleTransform())
                .into(mImageViewProfileImage);

        mSpinnerGoal.setSelection(mGoal);
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
