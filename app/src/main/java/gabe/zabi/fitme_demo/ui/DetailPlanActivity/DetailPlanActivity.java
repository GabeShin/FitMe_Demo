package gabe.zabi.fitme_demo.ui.detailPlanActivity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.ui.BaseActivity;
import gabe.zabi.fitme_demo.utils.Constants;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Gabe on 2017-02-23.
 */

public class DetailPlanActivity extends BaseActivity {

    private String mUid;

    private TextView mTextViewUid;
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
        mViewPager = (ViewPager) findViewById(R.id.plan_viewpager);
        mIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
    }

    public void onPlanButtonClicked(View view){
        Firebase userRef = new Firebase(Constants.FIREBASE_URL_USER + "/" + mCreatedUid + "/" + Constants.FIREBASE_LOCATION_USER_ACTIVITY);

        Map<String, Object> map = new HashMap<>();
        map.put("current_plan_uid", mUid);
        map.put("current_workout_day", 0);

        userRef.updateChildren(map);

        Toast.makeText(getApplicationContext(), "added to your plan!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
