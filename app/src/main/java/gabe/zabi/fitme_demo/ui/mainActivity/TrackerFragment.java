package gabe.zabi.fitme_demo.ui.mainActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.data.MyContract;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

import static com.fasterxml.jackson.databind.util.ISO8601Utils.format;

/**
 * Created by Gabe on 2017-03-06.
 */

public class TrackerFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    String exercise_name;

    String planUid;
    int workoutWeek;
    int workoutDay;

    TextView textViewExerciseName;
    Button weightSubtractButton;
    Button weightAddButton;
    EditText editTextWeight;

    Button repsSubtractButton;
    Button repsPlusButton;
    EditText editTextReps;

    Button trackerAddButton;
    Button trackerClearButton;

    RecyclerView mRecyclerView;

    TrackerListAdapter mAdapter;

    public static TrackerFragment newInstance(String exerciseId) {
        Bundle args = new Bundle();
        args.putString(Constants.KEY_VALUE_EXERCISE_NAME, exerciseId);
        TrackerFragment fragment = new TrackerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exercise_name = getArguments().getString(Constants.KEY_VALUE_EXERCISE_NAME);

        planUid = Utils.getSharedPreferencePlanUid(getActivity());
        workoutWeek = Utils.getSharedPreferenceWorkoutWeek(getActivity());
        workoutDay = Utils.getSharedPreferenceWorkoutDay(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get the layout inflater
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_tracker, container, false);

        initializeScreen(rootView);

        mAdapter = new TrackerListAdapter(getActivity(), new TrackerListAdapter.TrackerAdapterOnLongClickHandler() {
            @Override
            public void onLongClick(int set, TrackerListAdapter.TrackerAdapterViewHolder vh) {
                // delete the row and update other rows with higher set number by subtracting one.
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        getActivity().getSupportLoaderManager().restartLoader(1, null, this);

        return rootView;
    }

    private void initializeScreen(View view){
        textViewExerciseName = (TextView) view.findViewById(R.id.tracker_exercise_name);
        textViewExerciseName.setText(exercise_name);

        weightSubtractButton = (Button) view.findViewById(R.id.tracker_weight_subtract);
        weightAddButton = (Button) view.findViewById(R.id.tracker_weight_add);
        editTextWeight = (EditText) view.findViewById(R.id.tracker_weight_edittext);

        weightSubtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrSubtractToEditText(-2.5d, editTextWeight);
            }
        });
        weightAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrSubtractToEditText(2.5d, editTextWeight);
            }
        });

        repsSubtractButton = (Button) view.findViewById(R.id.tracker_reps_subtract);
        repsPlusButton = (Button) view.findViewById(R.id.tracker_reps_add);
        editTextReps = (EditText) view.findViewById(R.id.tracker_reps_edittext);
        repsSubtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrSubtractToEditText(-1, editTextReps);
            }
        });
        repsPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrSubtractToEditText(1, editTextReps);
            }
        });

        trackerAddButton = (Button) view.findViewById(R.id.tracker_add_button);
        trackerClearButton = (Button) view.findViewById(R.id.tracker_clear_button);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.tracker_recyclerview);

        trackerClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextReps.setText("0");
                editTextWeight.setText("0.0");
            }
        });
        trackerAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSetToSqliteDb();
            }
        });

    }

    private void addOrSubtractToEditText(double delta, EditText editTextToChange){
        double currentValue = Double.parseDouble(editTextToChange.getText().toString());
        double value = currentValue + delta;
        if (value < 0) {
            value = 0;
        }
        editTextToChange.setText(String.valueOf(value));
    }

    private void addSetToSqliteDb(){

        ContentValues values = new ContentValues();

        values.put(MyContract.UserHistoryEntry.COLUMN_PLAN_UID, planUid);
        values.put(MyContract.UserHistoryEntry.COLUMN_WORKOUT_WEEK, workoutWeek);
        values.put(MyContract.UserHistoryEntry.COLUMN_WORKOUT_DAY, workoutDay);
        values.put(MyContract.UserHistoryEntry.COLUMN_EXERCISE_NAME, exercise_name);
        values.put(MyContract.UserHistoryEntry.COLUMN_SET_NUMBER, mRecyclerView.getAdapter().getItemCount() + 1);
        values.put(MyContract.UserHistoryEntry.COLUMN_REPS, editTextReps.getText().toString());
        values.put(MyContract.UserHistoryEntry.COLUMN_WEIGHT, editTextWeight.getText().toString());

        getContext().getContentResolver().insert(MyContract.UserHistoryEntry.CONTENT_URI, values);

        Log.v("TrackerFrag", "Workout Identifier = " + planUid + "-"
                + workoutWeek + "-" + workoutDay
                + "\nexercise name = " + exercise_name
                + " set number = " + mRecyclerView.getAdapter().getItemCount() + 1
                + " reps = " + editTextReps.getText().toString()
                + " weight = " + editTextWeight.getText().toString());
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = MyContract.UserHistoryEntry.COLUMN_EXERCISE_NAME + " =?"
                + " AND " + MyContract.UserHistoryEntry.COLUMN_WORKOUT_WEEK + " = " + workoutWeek
                + " AND " + MyContract.UserHistoryEntry.COLUMN_WORKOUT_DAY + " = " + workoutDay;
        String[] selectionArg = new String[]{exercise_name};
        Log.v("trackerFrag", "Workout Identifier = " + planUid + "-" + workoutWeek + "-" + workoutDay + " - " + exercise_name);

        return new CursorLoader(getActivity(),
                MyContract.UserHistoryEntry.CONTENT_URI,
                null,
                selection, selectionArg, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()){
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    /**
     * TrackerListAdapter
     */
    public static class TrackerListAdapter extends RecyclerView.Adapter<TrackerListAdapter.TrackerAdapterViewHolder> {

        private Cursor mCursor;
        final private Context mContext;
        final private TrackerAdapterOnLongClickHandler mLongClickHandler;

        public interface TrackerAdapterOnLongClickHandler {
            void onLongClick(int set, TrackerAdapterViewHolder vh);
        }

        public class TrackerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
            public final TextView setNumberView;
            public final TextView setWeightView;
            public final TextView setRepsView;
            public final TextView setHistoryView;



            public TrackerAdapterViewHolder(View view) {
                super(view);
                setNumberView = (TextView) view.findViewById(R.id.single_set_number);
                setWeightView = (TextView) view.findViewById(R.id.single_set_weight);
                setRepsView = (TextView) view.findViewById(R.id.single_set_reps);
                setHistoryView = (TextView) view.findViewById(R.id.single_set_history);
            }

            @Override
            public boolean onLongClick(View v) {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);
                int set = mCursor.getInt(mCursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_SET_NUMBER));
                mLongClickHandler.onLongClick(set, this);
                return true;
            }
        }

        public TrackerListAdapter(Context mContext, TrackerAdapterOnLongClickHandler dh) {
            this.mContext = mContext;
            this.mLongClickHandler = dh;
        }

        @Override
        public TrackerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_set_item, parent, false);

            return new TrackerAdapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TrackerAdapterViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            String setNumber = mCursor.getString(mCursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_SET_NUMBER));
            String setWeight = mCursor.getString(mCursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_WEIGHT));
            String setReps = mCursor.getString(mCursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_REPS));
            String exerciseName = mCursor.getString(mCursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_EXERCISE_NAME));
            String set = String.valueOf(position + 1);

            holder.setNumberView.setText(set);
            holder.setRepsView.setText(setReps);
            holder.setWeightView.setText(setWeight);

            /*
             * Previous workout history
             */
            String selection = MyContract.UserHistoryEntry.COLUMN_EXERCISE_NAME + " =?"
                    + " AND " + MyContract.UserHistoryEntry.COLUMN_SET_NUMBER + " = " + setNumber;
            String[] selectionArg = new String[]{exerciseName};
            Cursor historyCursor = mContext.getContentResolver().query(MyContract.UserHistoryEntry.CONTENT_URI
                    , null
                    , selection
                    , selectionArg
                    , null
            );
            if (historyCursor != null && historyCursor.moveToLast()){
                if (historyCursor.moveToPrevious()) {
                    String previousWeight = historyCursor.getString(historyCursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_WEIGHT));
                    String previousReps = historyCursor.getString(historyCursor.getColumnIndexOrThrow(MyContract.UserHistoryEntry.COLUMN_REPS));
                    holder.setHistoryView.setText(previousWeight + "kg x " + previousReps);
                }
                historyCursor.close();
            }
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) return 0;
            else return mCursor.getCount();
        }

        public void swapCursor(Cursor newCursor) {
            mCursor = newCursor;
            notifyDataSetChanged();
        }
    }
}
