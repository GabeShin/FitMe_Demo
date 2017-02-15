package gabe.zabi.fitme_demo.ui.mainActivity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.UserProfile;
import gabe.zabi.fitme_demo.ui.BaseActivity;

/**
 * Created by Gabe on 2017-02-14.
 */

public class ProfileSettingActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

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
}
