package gabe.zabi.fitme_demo.ui.loginActivity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import gabe.zabi.fitme_demo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginUserInfo1Fragment extends Fragment {

    private final static String LOG_TAG = LoginUserInfo1Fragment.class.getSimpleName();

    private Button mNextButton;

    private TextView mTvName;
    private TextView mTvEmail;
    private EditText mEtNickname;
    private Spinner mSpinnerGoal;

    public LoginUserInfo1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_user_info1, container, false);

        initializeScreen(rootView);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allViewsAreFilled()){
                    LoginUserInfo2Fragment fragment = new LoginUserInfo2Fragment();
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.login_container, fragment).commit();
                }
            }
        });

        return rootView;
    }

    private void initializeScreen(View view){
        mNextButton = (Button) view.findViewById(R.id.login_next_button);
        mTvName = (TextView) view.findViewById(R.id.login_user_name);
        mEtNickname = (EditText) view.findViewById(R.id.login_user_nickname);
        mTvEmail = (TextView) view.findViewById(R.id.login_user_email);
        mSpinnerGoal = (Spinner) view.findViewById(R.id.spinner_goal);
    }

    private boolean allViewsAreFilled() {
        boolean allViewsAreFilled = false;

        if (mTvName.getText().toString().matches(""))
            mTvName.setError("Your name is required");
        else if (mTvEmail.getText().toString().matches(""))
            mTvEmail.setError("Your email address is required");
        else if (mEtNickname.getText().toString().matches(""))
            mEtNickname.setError("Your nickname is required");
        else if (mSpinnerGoal == null || mSpinnerGoal.getSelectedItem() == null)
            Toast.makeText(getActivity(), "Your fitness goal is required", Toast.LENGTH_SHORT).show();
        else {
            allViewsAreFilled = true;
        }
        return allViewsAreFilled;
    }

    public void updateInfo(String name){
        Log.v(LOG_TAG, "Update Info");
        mTvName.setText(name);
    }
}
