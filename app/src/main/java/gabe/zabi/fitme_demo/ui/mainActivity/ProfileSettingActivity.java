package gabe.zabi.fitme_demo.ui.mainActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

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

public class ProfileSettingActivity extends BaseActivity {

    private final String LOG_TAG = ProfileSettingActivity.class.getSimpleName();

    private String mWeight;
    private String mHeight;
    private int mGoal;

    private UserProfile mUserProfile;

    private Toolbar mToolbar;

    private TextView mTextViewName;
    private TextView mTextViewBirthday;
    private TextView mTextViewGender;
    private TextView mTextViewEmail;
    private ImageView mImageViewProfileImage;
    private Spinner mSpinnerGoal;
    private EditText mEditTextHeight;
    private EditText mEditTextWeight;

    private Firebase mRef;

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

                populateViews(userProfile);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, getString(R.string.log_error_the_read_failed) + firebaseError.getMessage());
            }
        });

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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.save_profile))
                        .setMessage(getString(R.string.do_you_want_to_save))
                        .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAllProfileDataFromApp();
                        updateUserProfileToFirebase();
                    }
                })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeScreen(){
        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mTextViewName = (TextView) findViewById(R.id.profile_activity_name);
        mTextViewBirthday = (TextView) findViewById(R.id.profile_activity_birthday);
        mTextViewGender = (TextView) findViewById(R.id.profile_activity_gender);
        mTextViewEmail = (TextView) findViewById(R.id.profile_activity_email);
        mImageViewProfileImage = (ImageView) findViewById(R.id.profile_activity_profile_picture);
        mSpinnerGoal = (Spinner) findViewById(R.id.spinner_goal);
        mEditTextWeight = (EditText) findViewById(R.id.profile_activity_weight);
        mEditTextHeight = (EditText) findViewById(R.id.profile_activity_height);
    }

    private void populateViews(UserProfile userProfile){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Picasso.with(getApplicationContext())
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.ic_account_circle_white_24dp)
                .transform(new CircleTransform())
                .into(mImageViewProfileImage);

        mTextViewName.setText(userProfile.getName());
        mTextViewBirthday.setText(userProfile.getBirthday());
        mTextViewEmail.setText(userProfile.getEmail());
        mEditTextHeight.setText(userProfile.getHeight());
        mEditTextWeight.setText(userProfile.getWeight());

        if (userProfile.getGender() == 0) mTextViewGender.setText(getString(R.string.gender_male));
        else mTextViewGender.setText(getString(R.string.gender_female));

        mSpinnerGoal.setSelection(userProfile.getGoal());
    }

    public void getAllProfileDataFromApp(){
        mGoal = mSpinnerGoal.getSelectedItemPosition();
        mHeight = mEditTextHeight.getText().toString();
        mWeight = mEditTextWeight.getText().toString();
    }

    public void updateUserProfileToFirebase(){

        Map<String, Object> map = new HashMap<>();
        map.put("goal", mGoal);
        map.put("height", mHeight);
        map.put("weight", mWeight);

        mRef.updateChildren(map);
    }

    private void addToContentProvider(UserProfile userProfile){

//        String selection = MyContract.UserHistoryEntry.COLUMN_FITME_UID + "=?";
//        String[] selectionArg = {mCreatedUid};
//
//        ContentValues cv = new ContentValues();
//
//        cv.put(MyContract.UserHistoryEntry.COLUMN_FITME_UID, mCreatedUid);
//
//        cv.put(MyContract.UserHistoryEntry.COLUMN_NAME, userProfile.getName());
//        cv.put(MyContract.UserHistoryEntry.COLUMN_GENDER, userProfile.getGender());
//        cv.put(MyContract.UserHistoryEntry.COLUMN_BIRTHDAY, userProfile.getBirthday());
//        cv.put(MyContract.UserHistoryEntry.COLUMN_EMAIL, userProfile.getEmail());
//        cv.put(MyContract.UserHistoryEntry.COLUMN_PROFILE_IMAGE, userProfile.getProfileImage());
//
//        cv.put(MyContract.UserHistoryEntry.COLUMN_GOAL, userProfile.getGoal());
//
//        Cursor cursor = getContentResolver().query(MyContract.UserHistoryEntry.CONTENT_URI, null, selection, selectionArg, null);
//
//        if (!cursor.moveToFirst()) {
//            // if current user's profile does NOT exist, insert.
//            // if current user's profile ALREADY exists, update instead.
//            // insert user profile data into the database.
//            Uri insertedRow = getContentResolver().insert(MyContract.UserHistoryEntry.CONTENT_URI, cv);
//            Log.v(LOG_TAG, "inserted Row into : " + insertedRow);
//        } else {
//            // update user profile data into the database.
//            getContentResolver().update(MyContract.UserHistoryEntry.CONTENT_URI, cv, selection, selectionArg);
//            Log.v(LOG_TAG, "updated user profile");
//        }
//
//        cursor.close();
    }
}
