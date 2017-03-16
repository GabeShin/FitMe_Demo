package gabe.zabi.fitme_demo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import gabe.zabi.fitme_demo.model.UserActivity;
import gabe.zabi.fitme_demo.ui.loginActivity.LoginActivity;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    protected GoogleApiClient mGoogleApiClient;

    protected String mCreatedUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the
         * options specified by gso.
         */

        /*
        Setup the Google API object to allow Google login
         */
        // Configure sign-in to request the user's ID, email address and basic profile.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /**
         * Getting mProvider and mCreatedUid from SharedPreference
         */
        mCreatedUid = Utils.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // trigger for new log-in
        if (!Utils.getSharedPreferenceReturningUser(getApplicationContext())){

            Log.v("BaseActivity", "New Log in");
            // means new log-in
            Utils.saveSharedPreferenceUserUid(getApplicationContext(), mCreatedUid);
            Firebase userActivityRef = new Firebase(Constants.FIREBASE_URL_USER)
                    .child(mCreatedUid).child(Constants.FIREBASE_LOCATION_USER_ACTIVITY);
            userActivityRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserActivity user = dataSnapshot.getValue(UserActivity.class);
                    if (user != null) {
                        // new log-in, but returning user
                        Utils.saveSharedPreferencePlanUid(getApplicationContext(), user.getCurrent_plan_uid());
                        Utils.saveSharedPreferenceWorkoutDay(getApplicationContext(), user.getCurrent_workout_day());
                        Utils.saveSharedPreferenceWorkoutWeek(getApplicationContext(), user.getCurrent_workout_week());
                        Utils.saveSharedPreferenceNumberOfWeeks(getApplicationContext(), user.getWorkout_length_in_weeks());

                        Log.v("BaseActivity", "plan uid: " + user.getCurrent_plan_uid()
                                + "\ncurrent workout day: " + user.getCurrent_workout_day()
                                + "\ncurrent workout week: " + user.getCurrent_workout_week()
                                + "\nworkout length : " + user.getWorkout_length_in_weeks());

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            // switch off the trigger for new log-in
            Utils.saveSharedPreferenceReturningUser(getApplicationContext(), true);
        } else {
            Log.v("BaseActivity", "Returning user");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void takeUserToLoginScreenOnUnAuth(){
        /*
         * Move user to LoginActivity and remove the backstack
         */
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
