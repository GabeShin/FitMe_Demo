package gabe.zabi.fitme_demo.ui.loginActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.User;
import gabe.zabi.fitme_demo.ui.mainActivity.MainActivity;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = LoginFragment.class.getSimpleName();
    private static final int GOOGLE_SIGN_IN = 9001;
    private static final int FACEBOOK_SIGN_IN = 64206;

    private CallbackManager mCallbackManager;
    private LoginManager mLoginManager;

    private GoogleApiClient mGoogleApiClient;

    private EditText mEtEmail;
    private EditText mEtPassword;

    private String email = null;
    private String password = null;

    Communicator communicator;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Firebase mUserRef;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // set OnClickListeners
        rootView.findViewById(R.id.fb_login_button).setOnClickListener(this);
        rootView.findViewById(R.id.google_login_button).setOnClickListener(this);

        /*
        Setup the Google API object to allow Google login
         */
        // Configure sign-in to request the user's ID, email address and basic profile.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // create Firebase reference
        mUserRef = new Firebase(Constants.FIREBASE_URL_USER);

        initializeScreen(rootView);

        Button signInButton = (Button) rootView.findViewById(R.id.login_sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUsingEmailandPassword();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fb_login_button:
                setUpFacebookStuff();
                onFacebookLogInClicked();
                break;
            case R.id.google_login_button:
                onGoogleLogInClicked();
                break;
        }
    }

    private void initializeScreen(View view ) {
        mEtEmail = (EditText) view.findViewById(R.id.login_email_edit_text);
        mEtPassword = (EditText) view.findViewById(R.id.login_password_edit_text);

        TextView signUpWithEmail = (TextView) view.findViewById(R.id.sign_up_with_email);
        signUpWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    Fragment fragment = new AccountInformationFragment();
                    communicator.sendUserInfo(fragment, false);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container, fragment).commit();
                }
            }
        });
    }

    /**
     * Setup AuthStateListener that responds to changes in the user's sign-in state.
     */
    private void checkForAuthState(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    checkForNewUser();
                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStart() {
        /**
         * At onStart, we need to check if user is new to the app.
         * This needs to be checked through Firebase.
         * If the user is existing user, login the user immediately, if there is createdUid SharedPreference.
         */
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            checkForNewUser();
        }
        // set up AuthStateListener
        checkForAuthState();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Facebook Log In  Methods
     */
    private void setUpFacebookStuff(){
        Log.v(LOG_TAG, "Setting Up Facebook Stuff");

        mLoginManager = LoginManager.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v(LOG_TAG, "Successfully Log In Using Facebook");
                authWithFacebookCredential(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.v(LOG_TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v(LOG_TAG, "onError");
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onFacebookLogInClicked() {
        Log.v(LOG_TAG, "on Facebook Log In Clicked");
        mLoginManager.logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
    }

    private void authWithFacebookCredential(AccessToken accessToken) {
        Log.v(LOG_TAG, "Authenticate with Facebook credential");

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), R.string.toast_message_email_conflict, Toast.LENGTH_SHORT).show();
                        }
                        if (!task.isSuccessful()){
                            /* sign in failed. Display a message to the user. */
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.toast_message_auth_failed, Toast.LENGTH_SHORT).show();
                        }
                        checkForAuthState();
                    }
                });
    }

    /**
     * Google Log In Methods
     */
    public void onGoogleLogInClicked(){
        Log.v(LOG_TAG, "onGoogleLogInClicked!");
        Intent signinIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signinIntent, GOOGLE_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), R.string.toast_message_email_conflict, Toast.LENGTH_SHORT).show();
                        }

                        if (!task.isSuccessful()) {
                            /* sign in failed. Display a message to the user. */
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.toast_message_email_conflict, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GOOGLE_SIGN_IN:
                Log.v(LOG_TAG, "Google Log In: onActivityResult");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()){
                    Log.v(LOG_TAG, "Google Log In: onActivityResult - result is successful.");
                    // Google Sign In was successful, authenticate with Firebase.
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }
                break;
            case FACEBOOK_SIGN_IN:
                Log.v(LOG_TAG, "Facebook Log In: onActivityResult");
                // Pass the activity result back to the Facebook SDK
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void checkForNewUser(){
        String encodedUid = Utils.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        mUserRef.child(encodedUid + "/").child(Constants.FIREBASE_LOCATION_PROFILE_INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isReturningUser;

                if (!dataSnapshot.exists()) {
                    // the user is a new user.
                    Log.v(LOG_TAG, "User is new user");
                    isReturningUser = false;
                } else {
                    // the user is a returning user
                    Log.v(LOG_TAG, "User is returning user");
                    isReturningUser = true;
                }

                if (isReturningUser){
                    Activity activity = getActivity();
                    if (isAdded() && activity != null) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    if (isAdded()) {
                        Fragment fragment = new AccountInformationFragment();
                        communicator.sendUserInfo(fragment, true);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container, fragment).commit();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void signInUsingEmailandPassword(){
        email = mEtEmail.getText().toString().trim();
        password = mEtPassword.getText().toString();

        if (email == "" || password == "" || email.isEmpty() || password.isEmpty()){
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithEmail:failed", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseNetworkException e){
                                Toast.makeText(getApplicationContext(), R.string.toast_message_connectivity_issue, Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Log.v(LOG_TAG, "invalid credential");
                                Toast.makeText(getApplicationContext(), R.string.toast_message_invalid_email_password, Toast.LENGTH_SHORT).show();
                            } catch(Exception e) {
                                Log.e(LOG_TAG, e.getMessage());
                                Toast.makeText(getApplicationContext(), R.string.toast_message_auth_failed, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }

    public interface Communicator{
        public void sendUserInfo(Fragment fragment, boolean loggedInFromProvider);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            communicator = (Communicator) getActivity();
        } catch (Exception e){
            throw new ClassCastException(context.toString() + " must implement onButtonPressed");
        }
    }
}