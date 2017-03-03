package gabe.zabi.fitme_demo.ui.loginActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import gabe.zabi.fitme_demo.R;

/**
 * Created by Gabe on 2017-02-07.
 */

public class LoginActivity extends AppCompatActivity implements LoginFragment.Communicator {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null){
            // create fragment and add it to the activity using a fragment transaction.

            LoginFragment initialFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, initialFragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.login_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void sendUserInfo(Fragment fragment, boolean loggedInFromProvider) {
        Bundle args = new Bundle();
        args.putBoolean("KEY_FROM_PROVIDER", loggedInFromProvider);
        fragment.setArguments(args);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        Log.v(LOG_TAG, "BackStack count is " + count);
        if (count == 0) {
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
        } else {
            getFragmentManager().popBackStack();
        }
    }
}