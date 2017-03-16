package gabe.zabi.fitme_demo.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.Exercise;
import gabe.zabi.fitme_demo.model.Plan;
import gabe.zabi.fitme_demo.model.Workouts;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Gabe on 2017-03-16.
 */

public class TodayWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Intent mIntent;
    private Plan plan;
    private ArrayList<Exercise> exercises = new ArrayList<>();

    public TodayWidgetRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        this.mIntent = intent;
    }

    private void populateExerciseList(){
        String planUid = Utils.getSharedPreferencePlanUid(getApplicationContext());

        Firebase ref = new Firebase(Constants.FIREBASE_URL_PLAN_LISTS).child(planUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plan = dataSnapshot.getValue(Plan.class);
                exercises = plan.getWorkouts().get(Utils.getSharedPreferenceWorkoutDay(getApplicationContext())).getExercises();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        populateExerciseList();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return exercises.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION){
            plan = null;
        }
        final int currentDay = Utils.getSharedPreferenceWorkoutDay(getApplicationContext());
        Workouts currentWorkout = plan.getWorkouts().get(currentDay);
        ArrayList<Exercise> exercises = currentWorkout.getExercises();

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_workout_list_item);

        views.setTextViewText(R.id.single_exercise_title, exercises.get(position).getExerciseName());
        views.setTextViewText(R.id.single_exercise_reps_sets, exercises.get(position).getRecommendedReps() + " Reps");

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
