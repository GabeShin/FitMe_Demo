package gabe.zabi.fitme_demo.ui.detailPlanActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.data.MyContract;
import gabe.zabi.fitme_demo.model.Exercise;
import gabe.zabi.fitme_demo.model.OneSet;
import gabe.zabi.fitme_demo.model.PlanOverview;
import gabe.zabi.fitme_demo.model.Workouts;
import gabe.zabi.fitme_demo.services.UpdateUserActivityService;
import gabe.zabi.fitme_demo.ui.mainActivity.ProgressFragment;
import gabe.zabi.fitme_demo.ui.mainActivity.TrackerFragment;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;
import gabe.zabi.fitme_demo.widget.TodayWidgetProvider;

import static com.facebook.FacebookSdk.getApplicationContext;
import static gabe.zabi.fitme_demo.R.id.widget;

/**
 * Created by Gabe on 2017-02-24.
 */

public class WorkoutFragment extends android.support.v4.app.Fragment {

    private static final String LOG_TAG = WorkoutFragment.class.getSimpleName();

    private int position;
    private Workouts currentWorkout;
    private boolean fromMainActivity;

    private ListView mListView;
    private Button mCompleteButton;
    private ArrayList<Exercise> exercises;

    public static WorkoutFragment newInstance(int position, Workouts workouts, Boolean mainActivity) {
        Bundle args = new Bundle();
        args.putInt("KEY_POSITION", position);
        args.putSerializable("KEY_WORKOUT", workouts);
        args.putBoolean("KEY_MAIN_ACTIVITY", mainActivity);

        WorkoutFragment fragment = new WorkoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        position = getArguments().getInt("KEY_POSITION") + 1;
        currentWorkout = (Workouts) getArguments().getSerializable("KEY_WORKOUT");
        fromMainActivity = getArguments().getBoolean("KEY_MAIN_ACTIVITY");

        exercises = currentWorkout.getExercises();
        Exercise[] data = new Exercise[exercises.size()];
        data = exercises.toArray(data);

        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);

        initializeScreen(rootView);

        ExerciseListAdapter adapter = new ExerciseListAdapter(getActivity(), R.layout.single_exercise_item, data);

        mListView.setAdapter(adapter);
        if (fromMainActivity) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Fragment fragment = new TrackerFragment().newInstance(exercises.get(position).getExerciseName());
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                            .beginTransaction().replace(R.id.main_container, fragment);
                    fragmentTransaction.addToBackStack(LOG_TAG);
                    fragmentTransaction.commit();
                }
            });
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initializeScreen(View rootView){
        TextView workoutDayTextView = (TextView) rootView.findViewById(R.id.workout_day_textview);
        mListView = (ListView) rootView.findViewById(R.id.workout_listview);
        mCompleteButton = (Button) rootView.findViewById(R.id.workout_complete_button);

        mCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompleteButtonClicked();
            }
        });
        workoutDayTextView.setText(getResources().getString(R.string.day_format_resource, position));
    }

    public void onCompleteButtonClicked() {

        int workout_week = Utils.getSharedPreferenceWorkoutWeek(getApplicationContext());
        int workout_day = Utils.getSharedPreferenceWorkoutDay(getApplicationContext());
        int plan_size = Utils.getSharedPreferencePlanSize(getApplicationContext());

        Cursor cursor = getActivity().getContentResolver().query(MyContract.UserHistoryEntry.CONTENT_URI, null,
                MyContract.UserHistoryEntry.COLUMN_WORKOUT_WEEK + " = " + workout_week + " AND "
                        + MyContract.UserHistoryEntry.COLUMN_WORKOUT_DAY + " = " + workout_day,
                null, null);

        ArrayList<OneSet> sets = new ArrayList<>();

        while (cursor != null && cursor.moveToNext()){
            OneSet oneSet = new OneSet();
            String weight = cursor.getString(cursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_WEIGHT));
            String reps = cursor.getString(cursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_REPS));
            String exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_EXERCISE_NAME));
            oneSet.setExerciseName(exerciseName);
            oneSet.setWeight(weight);
            oneSet.setReps(reps);
            sets.add(oneSet);
        }

        cursor.close();

        Map<String, Object> workoutHistory = new HashMap<>();
        workoutHistory.put("1", sets);

        String uid = Utils.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        Firebase historyRep = new Firebase(Constants.FIREBASE_URL_USER).child(uid).child(Constants.FIREBASE_LOCATION_HISTORY);
        historyRep.push().setValue(sets);

        // schedule a job to update user history
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
        Job job = dispatcher.newJobBuilder()
                .setService(UpdateUserActivityService.class)
                .setTag("update_user_activity_tag")
                // start after about 5 to 6 hours
                .setTrigger(Trigger.executionWindow(60*60*5, 60*60*6))
                // set arguments
                .build();
        dispatcher.mustSchedule(job);

//        Map<String, Object> map = new HashMap<>();
//        Firebase userActivityRef = new Firebase(Constants.FIREBASE_URL_USER).child(uid).child(Constants.FIREBASE_LOCATION_USER_ACTIVITY);
//        if (workout_day == plan_size - 1) {
//            // current workout day is the last day in the current plan
//            // go back to day 1 & increase current workout week by 1.
//            map.put("current_workout_day", 0);
//            map.put("current_workout_week", workout_week + 1);
//
//            Utils.saveSharedPreferenceWorkoutDay(getApplicationContext(), 0);
//            Utils.saveSharedPreferenceWorkoutWeek(getApplicationContext(), workout_week + 1);
//        } else {
//            map.put("current_workout_day", workout_day + 1);
//            Utils.saveSharedPreferenceWorkoutDay(getApplicationContext(), workout_day + 1);
//        }
//        userActivityRef.updateChildren(map);

        Toast.makeText(getApplicationContext(), R.string.toast_message_completed_todays_workout, Toast.LENGTH_SHORT).show();

        // completion status is true now.
        Utils.saveSharedPreferenceCompletionStatus(getApplicationContext(), true);

        // start ProgressFragment
        ProgressFragment fragment = new ProgressFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.commit();

    }



    /**
     * ExerciseListAdapter
     */

    public class ExerciseListAdapter extends ArrayAdapter<Exercise>{

        Context mContext;
        int layoutResource;
        Exercise[] exerciseArrayList = null;

        public ExerciseListAdapter(Context context, int resource, Exercise[] objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.layoutResource = resource;
            this.exerciseArrayList = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            convertView = inflater.inflate(layoutResource, parent, false);

            Exercise currentExercise = exerciseArrayList[position];

            TextView tvTitle = (TextView) convertView.findViewById(R.id.single_exercise_title);
            TextView tvSetAndRep = (TextView) convertView.findViewById(R.id.single_exercise_reps_sets);

            String title = currentExercise.getExerciseName();
            String reps = currentExercise.getRecommendedReps();
            String sets = currentExercise.getNumberOfSets();

            tvTitle.setText(title);
            tvSetAndRep.setText(getString(R.string.rep_x_weight, reps, sets));

            // if there is data of the current exercise on same workout week and day, change the identifier color
            int workout_week = Utils.getSharedPreferenceWorkoutWeek(getApplicationContext());
            int workout_day = Utils.getSharedPreferenceWorkoutDay(getApplicationContext());
            String selection = MyContract.UserHistoryEntry.COLUMN_EXERCISE_NAME + " =?"
                    + " AND " + MyContract.UserHistoryEntry.COLUMN_WORKOUT_WEEK + " = " + workout_week
                    + " AND " + MyContract.UserHistoryEntry.COLUMN_WORKOUT_DAY + " = " + workout_day;
            String[] selectionArg = new String[]{title};

            Cursor cursor = getContext().getContentResolver().query(MyContract.UserHistoryEntry.CONTENT_URI, null,
                    selection, selectionArg, null);

            if (cursor != null && cursor.moveToFirst()){
                View completeMarkerView = convertView.findViewById(R.id.single_exercise_complete_marker);
                completeMarkerView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                cursor.close();
            }

            return convertView;
        }
    }


}
