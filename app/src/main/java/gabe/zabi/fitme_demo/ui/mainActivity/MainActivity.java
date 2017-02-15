package gabe.zabi.fitme_demo.ui.mainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import gabe.zabi.fitme_demo.model.User;
import gabe.zabi.fitme_demo.ui.myPlanActivity.MyPlanActivity;
import gabe.zabi.fitme_demo.ui.myReportActivity.MyReportActivity;
import gabe.zabi.fitme_demo.utils.CircleTransform;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.ui.BaseActivity;
import gabe.zabi.fitme_demo.R;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by Gabe on 2017-01-31.
 */

public class MainActivity extends BaseActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Firebase mUserRef;
    private ValueEventListener mUserRefListener;

    private AppBarLayout mAppBar;
    private Toolbar mToolbar;
    private CardView mPlanCardView;
    private CardView mReportCardView;
    private ImageView mProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserRef = new Firebase(Constants.FIREBASE_URL_USER).child(mCreatedUid).child(Constants.FIREBASE_LOCATION_ACCOUNT_INFO);
        Log.v(LOG_TAG, "Reference is " + mUserRef.toString());

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();

        /**
         * Add ValueEventListeners to Firebase references
         * to control get data and control behavior and visibility of elements
         */
        mUserRefListener = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                /**
                 * Set the activity title to current user name if user is not null
                 */
                if (user != null) {
                    String imagePath = user.getProfilePicturePath();
                    Picasso.with(getApplicationContext())
                            .load(imagePath)
                            .placeholder(R.drawable.ic_account_circle_white_24dp)
                            .transform(new CircleTransform())
                            .into(mProfilePic);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, getString(R.string.log_error_the_read_failed) + firebaseError.getMessage());
            }
        });
    }

    public void initializeScreen(){
        mToolbar = (Toolbar) findViewById(R.id.main_activity_tool_bar);
        MainActivity.this.setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mPlanCardView = (CardView) findViewById(R.id.cardview_plan);

        mReportCardView = (CardView) findViewById(R.id.cardview_report);

        mProfilePic = (ImageView) findViewById(R.id.main_profile_picture);

        TextView textView = (TextView) findViewById(R.id.test_text_view);
        textView.setText(mCreatedUid + "\n" + mProvider + mProfilePath);
    }

    public void onMyPlanClicked(View view){
        Intent intent = new Intent(MainActivity.this, MyPlanActivity.class);
        startActivity(intent);
    }

    public void onMyReportClicked(View view){
        Intent intent = new Intent(MainActivity.this, MyReportActivity.class);
        startActivity(intent);
    }

    public void onProfileSettingClicked(View view){
        Intent intent = new Intent(MainActivity.this, ProfileSettingActivity.class);
        startActivity(intent);
    }

}
