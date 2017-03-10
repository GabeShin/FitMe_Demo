package gabe.zabi.fitme_demo.ui.detailPlanActivity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.ui.BaseActivity;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Gabe on 2017-02-23.
 */

public class DetailPlanActivity extends BaseActivity {

    private String mUid;

    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private CircleIndicator mIndicator;
    private WorkoutPagerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        if (getIntent().getExtras() != null){
            mUid = getIntent().getExtras().getString("KEY_PLAN_UID");
        }

        Bundle bundle = new Bundle();
        bundle.putString("KEY_PLAN_UID", mUid);

        initializeScreen();

        mAdapter = new WorkoutPagerAdapter(getSupportFragmentManager(), bundle);
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager);
        mAdapter.registerDataSetObserver(mIndicator.getDataSetObserver());
    }

    public void initializeScreen(){
        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.plan_viewpager);
        mIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
    }

    public void onPlanButtonClicked(View view){
        Firebase userRef = new Firebase(Constants.FIREBASE_URL_USER + "/" + mCreatedUid + "/" + Constants.FIREBASE_LOCATION_USER_ACTIVITY);

        if (Utils.getSharedPreferencePlanUid(getApplicationContext()) != null &&
                Utils.getSharedPreferencePlanUid(getApplicationContext()).equals(mUid)){
            // this is current plan user is on. Tell user this.
            Toast.makeText(getApplicationContext(), R.string.toast_message_current_plan_conflict, Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("current_plan_uid", mUid);
            map.put("current_workout_day", 0);
            map.put("current_workout_week", 1);

            userRef.updateChildren(map);

            Utils.saveSharedPreferencePlanUid(getApplicationContext(), mUid);
            Utils.saveSharedPreferenceWorkoutDay(getApplicationContext(), 0);
            Utils.saveSharedPreferenceWorkoutWeek(getApplicationContext(), 1);

            Toast.makeText(getApplicationContext(), R.string.toast_message_successfully_added_new_plan, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
