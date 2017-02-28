package gabe.zabi.fitme_demo.ui.loginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import gabe.zabi.fitme_demo.R;

/**
 * Created by Gabe on 2017-02-07.
 */

public class LoginActivity extends AppCompatActivity implements LoginInitialFragment.Communicator {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null){
            // create fragment and add it to the activity using a fragment transaction.

            LoginInitialFragment initialFragment = new LoginInitialFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, initialFragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (android.support.v4.app.Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void sendUserInfo(android.support.v4.app.Fragment fragment, String name, String email, String imagePath) {
        Bundle args = new Bundle();
        args.putString("KEY_NAME", name);
        args.putString("KEY_EMAIL", email);
        args.putString("KEY_IMAGE", imagePath);
        fragment.setArguments(args);
    }
}