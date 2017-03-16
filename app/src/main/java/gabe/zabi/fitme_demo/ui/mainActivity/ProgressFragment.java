package gabe.zabi.fitme_demo.ui.mainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import java.util.HashMap;
import java.util.Map;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.ui.detailPlanActivity.WorkoutFragment;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Gabe on 2017-03-15.
 */

public class ProgressFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView percentage;
    private TextView progressToGo;
    private TextView startNextWorkout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_main_progress, container, false);

        initializeScreen(view);

        populateViews();


        return view;
    }

    private void initializeScreen(View view){
        progressBar = (ProgressBar) view.findViewById(R.id.progress_prog_bar);
        progressToGo = (TextView) view.findViewById(R.id.progress_to_go_textview);
        percentage = (TextView) view.findViewById(R.id.progress_percentage);
        startNextWorkout = (TextView) view.findViewById(R.id.progress_start_next_workout);
    }

    private void populateViews(){
        int weeks = Utils.getSharedPreferenceNumberOfWeeks(getActivity());
        int workoutInAWeek = Utils.getSharedPreferencePlanSize(getActivity());
        int currentDay = Utils.getSharedPreferenceWorkoutDay(getActivity());
        int currentWeek = Utils.getSharedPreferenceWorkoutWeek(getActivity());

        int totalWorkouts = weeks * workoutInAWeek;
        int finishedWorkouts = ((currentWeek - 1) * workoutInAWeek) + currentDay + 1;

        int int_percentage = Math.round(finishedWorkouts * 100 /totalWorkouts);

        Log.v("PROGRESSFRAG", "total workouts: " + totalWorkouts + "\nfinished workouts: " + finishedWorkouts
                + "\nweeks: " + weeks
                + "\nworkout in a week: " + workoutInAWeek
                + "\ncurrent day: " + currentDay
                + "\nwcurrent week: " + currentWeek
                + "\npercentage: " + int_percentage
        );

        progressBar.setProgress(int_percentage);
        progressToGo.setText(getResources().getString(R.string.progress_to_go, totalWorkouts - finishedWorkouts));
        percentage.setText(getResources().getString(R.string.progress_percentage, int_percentage));

        startNextWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int workout_week = Utils.getSharedPreferenceWorkoutWeek(getApplicationContext());
                int workout_day = Utils.getSharedPreferenceWorkoutDay(getApplicationContext());
                int plan_size = Utils.getSharedPreferencePlanSize(getApplicationContext());
                int length = Utils.getSharedPreferenceNumberOfWeeks(getApplicationContext());

                String uid = Utils.getSharedPreferenceUserUid(getApplicationContext());

                Map<String, Object> map = new HashMap<>();
                Firebase userActivityRef = new Firebase(Constants.FIREBASE_URL_USER).child(uid).child(Constants.FIREBASE_LOCATION_USER_ACTIVITY);
                if (workout_day == plan_size - 1) {
                    // current workout day is the last day in the current plan

                    // if this is last week of the plan
                    if (workout_week == length){
                        // end of the workout

                    } else {
                        // is not the last week of the plan
                        // go back to day 1 & increase current workout week by 1.
                        map.put("current_workout_day", 0);
                        map.put("current_workout_week", workout_week + 1);

                        Utils.saveSharedPreferenceWorkoutDay(getApplicationContext(), 0);
                        Utils.saveSharedPreferenceWorkoutWeek(getApplicationContext(), workout_week + 1);
                    }
                } else {
                    map.put("current_workout_day", workout_day + 1);
                    Utils.saveSharedPreferenceWorkoutDay(getApplicationContext(), workout_day + 1);
                }
                userActivityRef.updateChildren(map);

                Utils.saveSharedPreferenceCompletionStatus(getApplicationContext(), false);

                // cancel Firebase Job Dispatcher that will update Firebase
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
                dispatcher.cancel("update_user_activity_tag");

                // restart Activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
