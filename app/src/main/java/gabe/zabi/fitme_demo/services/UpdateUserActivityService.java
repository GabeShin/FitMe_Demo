package gabe.zabi.fitme_demo.services;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gabe.zabi.fitme_demo.model.OneSet;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Gabe on 2017-03-14.
 */

public class UpdateUserActivityService extends JobService {

    private static String LOG_TAG = UpdateUserActivityService.class.getSimpleName();

    private Thread mFetchDataThread;

    @Override
    public boolean onStartJob(final JobParameters job) {
        Log.v(LOG_TAG, "At onStartJob");
        mFetchDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "At onStartJob - inside Runnable");
                final Context context = getApplicationContext();

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

                Utils.saveSharedPreferenceCompletionStatus(context, false);

                jobFinished(job, false);
            }
        });

        mFetchDataThread.start();

        return true; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.v(LOG_TAG, "At onStopJob");
        if (mFetchDataThread != null){
            mFetchDataThread.interrupt();
            mFetchDataThread = null;
        }

        return true; // Answers the question: "Should this job be retried?"
    }


}
