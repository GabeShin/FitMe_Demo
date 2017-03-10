package gabe.zabi.fitme_demo.ui.splashActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.ui.loginActivity.LoginActivity;
import gabe.zabi.fitme_demo.ui.mainActivity.MainActivity;

/**
 * Created by Gabe on 2017-03-03.
 */

public class SplashActivity extends Activity {
    private int splashTime = 1000;
    private Thread thread;
    private ProgressBar mSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        mSpinner = (ProgressBar) findViewById(R.id.splash_progressbar);
        mSpinner.setIndeterminate(true);
        thread = new Thread(runable);
        thread.start();
    }

    public Runnable runable = new Runnable() {
        public void run() {
            try {
                Thread.sleep(splashTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.v("SplashActivity.class", "Exception: ", e);
            }
        }
    };
}

