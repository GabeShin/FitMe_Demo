package gabe.zabi.fitme_demo.ui.loginActivity;


import android.accounts.Account;
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

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.ui.mainActivity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginUserInfo2Fragment extends Fragment {

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
                if (allViewsAreFilled()){
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
        boolean allViewsAreFilled = false;

        // Height/ Weight/ Birthday/ Experience isn't MUST required.
        // Gender is required
        if (mSpinnerGender == null || mSpinnerGender.getSelectedItem()== null)
            Toast.makeText(getActivity(), "Your gender is required", Toast.LENGTH_SHORT).show();
        else
            allViewsAreFilled = true;

        return allViewsAreFilled;
    }
}
