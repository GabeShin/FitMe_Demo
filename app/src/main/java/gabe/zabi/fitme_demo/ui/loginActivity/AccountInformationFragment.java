package gabe.zabi.fitme_demo.ui.loginActivity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.User;
import gabe.zabi.fitme_demo.model.UserProfile;
import gabe.zabi.fitme_demo.ui.mainActivity.MainActivity;
import gabe.zabi.fitme_demo.utils.CircleTransform;
import gabe.zabi.fitme_demo.utils.Utils;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountInformationFragment extends Fragment {

    private final static String LOG_TAG = AccountInformationFragment.class.getSimpleName();

    private Button mNextButton;

    private EditText mEtName;
    private EditText mEtEmail;
    private ImageView mProfilePic;
    private EditText mEtPassword;
    private EditText mEtPasswordCheck;
    private Spinner mSpinnerGender;
    private EditText mEtBirthyear;
    private Spinner mSpinnerGoal;

    private FirebaseUser mFirebaseUser;
    private String mName;
    private String mEmail;
    private String mImagePath;
    private String mPassword;
    private int mGender;
    private String mBirthyear;
    private int mGoal;

    private Boolean isFromProvider;

    public AccountInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_account_info, container, false);

        if (getArguments() != null) {
            isFromProvider = getArguments().getBoolean("KEY_FROM_PROVIDER");
        }

        Log.v(LOG_TAG, "IS from provider: " + isFromProvider);
        initializeScreen(rootView, isFromProvider);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null){
            mEtEmail.setText(mFirebaseUser.getEmail());
            mEtEmail.setEnabled(false);
            mEtName.setText(mFirebaseUser.getDisplayName());
            Picasso.with(getApplicationContext())
                    .load(mFirebaseUser.getPhotoUrl())
                    .placeholder(R.drawable.ic_account_circle_white_24dp)
                    .transform(new CircleTransform())
                    .into(mProfilePic);
        }

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserInputs();
                Log.v(LOG_TAG, "all views are filled? " + allViewsAreFilled());
                if (allViewsAreFilled()) {

                    if (isFromProvider) {
                        // sign up from provider. Authentication is already completed.
                        Log.v(LOG_TAG, "all views are filled and button is clicked?");
                        saveToFirebase();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        // signing up with email/pw. Need to authenticate the user.
                        Log.v(LOG_TAG, "try to signup with email and pw");
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.createUserWithEmailAndPassword(mEmail, mPassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.v(LOG_TAG, "at complete");

                                        if (!task.isSuccessful()){
                                            /* sign up failed. Display a message to the user. */
                                            // FOR SOME REASON, FirebaseAuthWeakPasswordException is NEVER caught!
                                            if (mPassword.length() < 6){
                                                Toast.makeText(getApplicationContext(), "Password is too weak", Toast.LENGTH_SHORT).show();
                                            }


                                            // throw exceptions & show according toast
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthWeakPasswordException e){
                                                Log.v(LOG_TAG, "password is too weak");
                                                Toast.makeText(getApplicationContext(), "Password is too weak", Toast.LENGTH_SHORT).show();
                                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                                Log.v(LOG_TAG, "Email is invalid");
                                                Toast.makeText(getApplicationContext(), "Email is invalid", Toast.LENGTH_SHORT).show();
                                            } catch(FirebaseAuthUserCollisionException e) {
                                                Log.v(LOG_TAG, "User with Email id already exists");
                                                Toast.makeText(getApplicationContext(), "User with Email id already exists", Toast.LENGTH_SHORT).show();
                                            } catch(Exception e) {
                                                Log.e(LOG_TAG, e.getMessage());
                                            }
                                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                                        } else {
                                            // sign up successful
                                            Log.v(LOG_TAG, "created user with email and pw: " + task.isComplete());
                                            saveToFirebase();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                }
            }
        });

        return rootView;
    }

    private void initializeScreen(View view, Boolean fromProvider){
        mNextButton = (Button) view.findViewById(R.id.login_next_button);
        mProfilePic = (ImageView) view.findViewById(R.id.login_user_profile_picture);
        mEtName = (EditText) view.findViewById(R.id.login_user_name);
        mEtEmail = (EditText) view.findViewById(R.id.login_user_email);
        mEtPassword = (EditText) view.findViewById(R.id.login_account_password);
        mEtPasswordCheck = (EditText) view.findViewById(R.id.login_account_password_check);
        mEtBirthyear = (EditText) view.findViewById(R.id.login_user_birthday);
        mSpinnerGender = (Spinner) view.findViewById(R.id.spinner_gender);
        mSpinnerGoal = (Spinner) view.findViewById(R.id.spinner_goal);

        LinearLayout genderLayout = (LinearLayout) view.findViewById(R.id.login_account_layout_gender);
        LinearLayout birthyearLayout = (LinearLayout) view.findViewById(R.id.login_account_layout_birthyear);
        LinearLayout goalLayout = (LinearLayout) view.findViewById(R.id.login_account_layout_goal);

        if (fromProvider){
            // user came from provider. no need for password
            mProfilePic.setVisibility(View.VISIBLE);
            mEtName.setVisibility(View.VISIBLE);
            mEtEmail.setVisibility(View.VISIBLE);
            birthyearLayout.setVisibility(View.VISIBLE);
            genderLayout.setVisibility(View.VISIBLE);
            goalLayout.setVisibility(View.VISIBLE);
        } else {
            // use came from email sign-in. no need for profile pic
            mEtName.setVisibility(View.VISIBLE);
            mEtEmail.setVisibility(View.VISIBLE);
            mEtPassword.setVisibility(View.VISIBLE);
            mEtPasswordCheck.setVisibility(View.VISIBLE);
            birthyearLayout.setVisibility(View.VISIBLE);
            genderLayout.setVisibility(View.VISIBLE);
            goalLayout.setVisibility(View.VISIBLE);
        }
    }

    private boolean allViewsAreFilled() {
        boolean allViewsAreFilled = false;

        if (mEtName.getText().toString().matches(""))
            mEtName.setError("Your name is required");
        else if (mEtEmail.getText().toString().matches(""))
            mEtEmail.setError("Your email address is required");
        else if (!mBirthyear.equals("") && !Utils.validateUserBirthday(Integer.parseInt(mBirthyear)))
            mEtBirthyear.setError(mBirthyear + "? For real?");
        else
            allViewsAreFilled = true;

        if (!isFromProvider) {
            // not coming from provider
            if (mEtPassword.getText().toString().matches("")) {
                mEtPassword.setError("Password is required");
                allViewsAreFilled = false;
            } else if (mEtPasswordCheck.getText().toString().matches("")) {
                mEtPassword.setError("Please check your password");
                allViewsAreFilled = false;
            }
            else if (!mEtPassword.getText().toString().equals(mEtPasswordCheck.getText().toString())) {
                mEtPassword.setError("Passwords are different");
                allViewsAreFilled = false;
            }
        }

        return allViewsAreFilled;
    }

    private void getUserInputs(){
        mEmail = mEtEmail.getText().toString().trim();
        mName = mEtName.getText().toString().trim();
        mPassword = mEtPassword.getText().toString();
        mGoal = mSpinnerGender.getSelectedItemPosition();
        mGender = mSpinnerGender.getSelectedItemPosition();
        mBirthyear = mEtBirthyear.getText().toString().trim();
    }

    private void saveToFirebase(){
        UserProfile user = new UserProfile();
        user.setName(mName);
        user.setEmail(mEmail);
        user.setPassword(mPassword);

        user.setGender(mGender);
        user.setGoal(mGoal);
        user.setBirthday(mBirthyear);

        user.saveUser();
    }
}
