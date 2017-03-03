package gabe.zabi.fitme_demo.ui.mainActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gabe.zabi.fitme_demo.model.Plan;
import gabe.zabi.fitme_demo.model.UserActivity;
import gabe.zabi.fitme_demo.model.Workouts;
import gabe.zabi.fitme_demo.ui.detailPlanActivity.DetailPlanActivity;
import gabe.zabi.fitme_demo.ui.detailPlanActivity.WorkoutFragment;
import gabe.zabi.fitme_demo.ui.searchPlanActivity.SearchPlanActivity;
import gabe.zabi.fitme_demo.ui.loginActivity.LoginActivity;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.ui.BaseActivity;
import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.utils.Utils;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by Gabe on 2017-01-31.
 */

public class MainActivity extends BaseActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static Firebase mUserActivityRef;
    private static ValueEventListener activityListener;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private ArrayAdapter<String> mAdapter;
    private ListView mDrawerList;

    private ImageView mAddPlanImageView;
    private Button mCompleteButton;

    private boolean isRunning;

    private UserActivity userActivity;
    private String currentPlan;
    private int currentWorkoutDay;
    private int currentPlanSize;
    private ArrayList<Workouts> workouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();
    }

    public void initializeScreen() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mAddPlanImageView = (ImageView) findViewById(R.id.add_a_plan_button);
        mCompleteButton = (Button) findViewById(R.id.main_complete_button);

        String[] navigationArray = getResources().getStringArray(R.array.navigation_drawer_list);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navigationArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // MyProfile
                        startActivity(new Intent(MainActivity.this, ProfileSettingActivity.class));
                        finish();
                        break;
                    case 1:
                        // MyPlan
                        Intent intent = new Intent(MainActivity.this, DetailPlanActivity.class);
                        intent.putExtra("KEY_PLAN_UID", currentPlan);
                        startActivity(intent);
                        break;
                    case 2:
                        // Search Plan
                        startActivity(new Intent(MainActivity.this, SearchPlanActivity.class));
                        finish();
                        break;
                    case 3:
                        // Logout
                        FirebaseAuth.getInstance().signOut();

                        /*
                         * Move user to LoginActivity and remove the backstack
                         */
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                        break;
                }
            }
        });
    }

    public void onAddPlanClicked(View view) {
        Intent intent = new Intent(MainActivity.this, SearchPlanActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing App")
                .setMessage("Are you sure you want to close the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        System.exit(0);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void onCompleteButtonClicked(View view) {
        Map<String, Object> map = new HashMap<>();
        if (currentWorkoutDay == currentPlanSize - 1) {
            // current workout day is the last day in the current plan
            // go back to day 1
            map.put("current_workout_day", 0);
        } else {
            map.put("current_workout_day", currentWorkoutDay + 1);
        }
        mUserActivityRef.updateChildren(map);
        Toast.makeText(getApplicationContext(), "Completed Today's Workout", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUserActivityRef = new Firebase(Constants.FIREBASE_URL_USER).child(mCreatedUid).child(Constants.FIREBASE_LOCATION_USER_ACTIVITY);

        /**
         * Add ValueEventListeners to Firebase references
         * to control get data and control behavior and visibility of elements
         */
        activityListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userActivity = snapshot.getValue(UserActivity.class);

                if (userActivity == null) {
                    mAddPlanImageView.setVisibility(View.VISIBLE);
                    mCompleteButton.setVisibility(View.GONE);
                } else {
                    currentPlan = userActivity.getCurrent_plan_uid();
                    currentWorkoutDay = userActivity.getCurrent_workout_day();

                    // grab current plan
                    Firebase planRef = new Firebase(Constants.FIREBASE_URL_PLAN_LISTS).child(currentPlan);
                    planRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Plan plan = dataSnapshot.getValue(Plan.class);
                            workouts = plan.getWorkouts();
                            currentPlanSize = workouts.size();

                            if (workouts != null) {
                                WorkoutFragment fragment = new WorkoutFragment().newInstance(currentWorkoutDay, workouts.get(currentWorkoutDay));
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.main_container, fragment);
                                transaction.commit();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.e(LOG_TAG, getString(R.string.log_error_the_read_failed) + firebaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, getString(R.string.log_error_the_read_failed) + firebaseError.getMessage());
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUserActivityRef.removeEventListener(activityListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        /**
         * Perform fragment transaction.
         */
        mUserActivityRef.addValueEventListener(activityListener);
    }


}