package gabe.zabi.fitme_demo.ui.detailPlanActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import gabe.zabi.fitme_demo.model.Plan;
import gabe.zabi.fitme_demo.model.PlanOverview;
import gabe.zabi.fitme_demo.model.Workouts;
import gabe.zabi.fitme_demo.utils.Constants;

/**
 * Created by Gabe on 2017-02-27.
 */

public class WorkoutPagerAdapter extends FragmentPagerAdapter {

    private static final String LOG_TAG = WorkoutPagerAdapter.class.getSimpleName();

    final ArrayList<String> uids = new ArrayList<>();
    private final Bundle recievedData;

    private int numberOfItems;

    private Firebase mRef;

    private Plan currentPlan;
    private ArrayList<Workouts> workoutList;
    private PlanOverview planOverview;

    public WorkoutPagerAdapter(FragmentManager fm, Bundle bundle) {
        super(fm);

        recievedData = bundle;
        String uid = bundle.getString("KEY_PLAN_UID");

        mRef = new Firebase(Constants.FIREBASE_URL_PLAN_LISTS + "/" + uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentPlan = dataSnapshot.getValue(Plan.class);

                workoutList = currentPlan.getWorkouts();
                numberOfItems = workoutList.size();
                Log.v(LOG_TAG, "Number of items are " + numberOfItems);
                planOverview = currentPlan.getOverview();

                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public Fragment getItem(int position) {
        return WorkoutFragment.newInstance(position, workoutList.get(position), false);
    }

    @Override
    public int getCount() {
        return numberOfItems;
    }
}
