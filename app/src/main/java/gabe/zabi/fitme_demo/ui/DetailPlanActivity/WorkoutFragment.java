package gabe.zabi.fitme_demo.ui.detailPlanActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.Exercise;
import gabe.zabi.fitme_demo.model.Workouts;

/**
 * Created by Gabe on 2017-02-24.
 */

public class WorkoutFragment extends android.support.v4.app.Fragment {

    private static final String LOG_TAG = WorkoutFragment.class.getSimpleName();

    private String uid;
    private Workouts currentWorkout;
    private ListView mListView;
    private ArrayList<Exercise> exercises;

    public static WorkoutFragment newInstance(int position, Workouts workouts) {
        Bundle args = new Bundle();
        args.putInt("KEY_POSITION", position);
        args.putSerializable("KEY_WORKOUT", workouts);

        WorkoutFragment fragment = new WorkoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int position = getArguments().getInt("KEY_POSITION") + 1;
        currentWorkout = (Workouts) getArguments().getSerializable("KEY_WORKOUT");
        exercises = currentWorkout.getExercises();
        Exercise[] data = new Exercise[exercises.size()];
        data = exercises.toArray(data);

        Log.v(LOG_TAG, "This is exercise : " + data.toString());

        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);

        TextView workoutDayTextView = (TextView) rootView.findViewById(R.id.workout_day_textview);
        mListView = (ListView) rootView.findViewById(R.id.workout_listview);

        workoutDayTextView.setText("Day " + position);

        ExerciseListAdapter adapter = new ExerciseListAdapter(getActivity(), R.layout.single_exercise_item, data);

        mListView.setAdapter(adapter);

        return rootView;
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
            tvSetAndRep.setText(sets + " sets x " + reps + " reps");

            return convertView;
        }
    }
}
