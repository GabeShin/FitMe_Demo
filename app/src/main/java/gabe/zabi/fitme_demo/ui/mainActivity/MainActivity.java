package gabe.zabi.fitme_demo.ui.mainActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.firebase.client.Firebase;

import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.ui.BaseActivity;
import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.utils.Utils;

/**
 * Created by Gabe on 2017-01-31.
 */

public class MainActivity extends BaseActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Firebase mFirebaseRef;
    private String mUid;
    private String mProfilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        /**
         * Link layout elements from XML and setup the toolbar
         */
        initializeScreen();
    }

    public void initializeScreen(){
        TextView textView = (TextView) findViewById(R.id.test_text_view);
        textView.setText(mCreatedUid + "\n" + mProvider + mProfilePath);
    }

}
