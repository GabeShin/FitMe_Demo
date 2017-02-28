package gabe.zabi.fitme_demo.ui.loginActivity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.UserProfile;
import gabe.zabi.fitme_demo.utils.CircleTransform;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginUserInfo1Fragment extends Fragment {

    private final static String LOG_TAG = LoginUserInfo1Fragment.class.getSimpleName();

    private Button mNextButton;

    private EditText mEtName;
    private EditText mEtEmail;
    private ImageView mProfilePic;
    private EditText mEtNickname;
    private Spinner mSpinnerGoal;

    private String mName;
    private String mEmail;
    private String mImagePath;
    private int mGoal;
    private String mNickname;

    private UserProfile mUser;

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

                    getUserInputs();
                    saveToFirebase();

                    LoginUserInfo2Fragment fragment = new LoginUserInfo2Fragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container, fragment).commit();
                }
            }
        });

        if (getArguments() != null){
            mName = getArguments().getString("KEY_NAME");
            mEtName.setText(mName);

            mEmail = getArguments().getString("KEY_EMAIL");
            mEtEmail.setText(mEmail);

            mImagePath = getArguments().getString("KEY_IMAGE");
            Picasso.with(getApplicationContext())
                    .load(mImagePath)
                    .placeholder(R.drawable.ic_account_circle_white_24dp)
                    .transform(new CircleTransform())
                    .into(mProfilePic);

            mEtName.setEnabled(false);
            mEtEmail.setEnabled(false);
        }

        return rootView;
    }

    private void initializeScreen(View view){
        mNextButton = (Button) view.findViewById(R.id.login_next_button);
        mProfilePic = (ImageView) view.findViewById(R.id.login_user_profile_picture);
        mEtName = (EditText) view.findViewById(R.id.login_user_name);
        mEtNickname = (EditText) view.findViewById(R.id.login_user_nickname);
        mEtEmail = (EditText) view.findViewById(R.id.login_user_email);
        mSpinnerGoal = (Spinner) view.findViewById(R.id.spinner_goal);
    }

    private boolean allViewsAreFilled() {
        boolean allViewsAreFilled = false;

        if (mEtName.getText().toString().matches(""))
            mEtName.setError("Your name is required");
        else if (mEtEmail.getText().toString().matches(""))
            mEtEmail.setError("Your email address is required");
        else if (mEtNickname.getText().toString().matches(""))
            mEtNickname.setError("Your nickname is required");
        else if (mSpinnerGoal == null || mSpinnerGoal.getSelectedItem() == null)
            Toast.makeText(getActivity(), "Your fitness goal is required", Toast.LENGTH_SHORT).show();
        else {
            allViewsAreFilled = true;
        }
        return allViewsAreFilled;
    }

    private void getUserInputs(){
        mEmail = mEtEmail.getText().toString().trim();
        mName = mEtName.getText().toString().trim();
        mNickname = mEtNickname.getText().toString().trim();
        mGoal = mSpinnerGoal.getSelectedItemPosition();
    }

    private void saveToFirebase(){
        mUser = new UserProfile();
        mUser.setName(mName);
        mUser.setEmail(mEmail);
        mUser.setNickname(mNickname);
        mUser.setGoal(mGoal);
        mUser.setProfileImage(mImagePath);

        String createdUid = Utils.getSharedPreferenceUid(getApplicationContext());

        Firebase userRef = new Firebase(Constants.FIREBASE_URL_USER).child(createdUid).child(Constants.FIREBASE_LOCATION_PROFILE_INFO);

        userRef.setValue(mUser);
    }
}
