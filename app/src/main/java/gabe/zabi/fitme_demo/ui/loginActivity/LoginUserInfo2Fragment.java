package gabe.zabi.fitme_demo.ui.loginActivity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.UserProfile;
import gabe.zabi.fitme_demo.ui.mainActivity.MainActivity;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginUserInfo2Fragment extends Fragment {

    private int mGender;
    private String mHeight;
    private String mWeight;
    private int mExperience;
    private String mBirthday;

    private Spinner mSpinnerGender;
    private EditText mEtHeight;
    private EditText mEtWeight;
    private Spinner mSpinnerExperience;
    private EditText mEtBirthday;
    private Button mCompleteButton;

    public LoginUserInfo2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_user_info2, container, false);

        initializeScreen(rootView);

        mCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserInputs();

                if (allViewsAreFilled()){

                    saveUserProfileToFirebase();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    private void initializeScreen(View view){
        mSpinnerGender = (Spinner) view.findViewById(R.id.spinner_gender);
        mEtHeight = (EditText) view.findViewById(R.id.login_user_height);
        mEtWeight = (EditText) view.findViewById(R.id.login_user_weight);
        mSpinnerExperience = (Spinner) view.findViewById(R.id.spinner_experience);
        mEtBirthday = (EditText) view.findViewById(R.id.login_user_birthday);
        mCompleteButton = (Button) view.findViewById(R.id.login_complete_button);
    }

    private boolean allViewsAreFilled(){
        boolean allViewsAreFilled = true;

        // Height/ Weight/ Birthday/ Experience isn't MUST required.
        // Gender is required
        if (mSpinnerGender == null || mSpinnerGender.getSelectedItem()== null) {
            Toast.makeText(getActivity(), "Your gender is required", Toast.LENGTH_SHORT).show();
            allViewsAreFilled = false;
        }


        // check if height/ weight/ birthday is filled. If NOT NULL, validate user input
        if (!mHeight.equals("")){
            if (!Utils.validateHeight(Integer.parseInt(mHeight))) {
                // if height is NOT NULL and validateHeight is FALSE. Invalid input by user.
                mEtHeight.setError("Invalid height value");
                allViewsAreFilled = false;
            }
        }

        if (!mWeight.equals("")){
            if (!Utils.validateWeight(Integer.parseInt(mWeight))) {
                // if weight is NOT NULL and validateWeight is FALSE. Invalid input by user.
                mEtWeight.setError("Invalid weight value");
                allViewsAreFilled = false;
            }
        }

        if (!mBirthday.equals("")){
            if (!Utils.validateUserBirthday(Integer.parseInt(mBirthday))) {
                // if height is NOT NULL and validateHeight is FALSE. Invalid input by user.
                mEtBirthday.setError("Invalid year value");
                allViewsAreFilled = false;
            }
        }

        return allViewsAreFilled;
    }

    private void getUserInputs(){
        mGender = mSpinnerGender.getSelectedItemPosition();
        mHeight = mEtHeight.getText().toString().trim();
        mWeight = mEtWeight.getText().toString().trim();
        mExperience = mSpinnerExperience.getSelectedItemPosition();
        mBirthday = mEtBirthday.getText().toString().trim();
    }

    private void saveUserProfileToFirebase(){

        Map<String, Object> map = new HashMap<>();
        map.put("gender", mGender);
        map.put("height", mHeight);
        map.put("weight", mWeight);
        map.put("experience", mExperience);
        map.put("birthday", mBirthday);

        String createdUid = Utils.getSharedPreferenceUid(getApplicationContext());

        Firebase userRef = new Firebase(Constants.FIREBASE_URL_USER).child(createdUid).child(Constants.FIREBASE_LOCATION_PROFILE_INFO);
        userRef.updateChildren(map);
    }
}
