package gabe.zabi.fitme_demo.ui.loginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.AuthData;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.util.Arrays;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.User;
import gabe.zabi.fitme_demo.ui.mainActivity.MainActivity;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginInitialFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = LoginInitialFragment.class.getSimpleName();
    private static final int GOOGLE_SIGN_IN = 9001;
    private static final int FACEBOOK_SIGN_IN = 64206;

    private CallbackManager mCallbackManager;
    private LoginManager mLoginManager;

    private GoogleApiClient mGoogleApiClient;

    private SessionCallback mKakaoCallback;

    public User user;
    String uid = null;
    String name = null;
    String email = null;
    String imagePath = null;
    String provider = null;
    String encodedUid;

    Communicator communicator;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Firebase mUserRef;
    private AuthData mAuthData;

    public LoginInitialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login_initial, container, false);

        // set OnClickListeners
        rootView.findViewById(R.id.fb_login_button).setOnClickListener(this);
        rootView.findViewById(R.id.google_login_button).setOnClickListener(this);
        rootView.findViewById(R.id.kakao_login_button).setOnClickListener(this);

        /*
        Setup the Google API object to allow Google login
         */
        // Configure sign-in to request the user's ID, email address and basic profile.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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

        checkUserLogin();
        initializeScreen();

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
            case R.id.kakao_login_button:
                onKakaoLogInClicked();
                break;
        }
    }

    private void initializeScreen() {

    }

    /**
     * Setup AuthStateListener that responds to changes in the user's sign-in state.
     */
    private void checkUserLogin(){

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null){
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        /**
         * At onStart, we need to check if user is new to the app.
         * This needs to be checked through Firebase.
         * If the user is existing user, login the user immediately, if there is createdUid SharedPreference.
         */
        String createdUid = Utils.getSharedPreferenceUid(getApplicationContext());
        Log.v(LOG_TAG, "CreatedUid is " + createdUid);

        if (createdUid != null) {
            // create intent to start MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    private void saveUserToFirebase(){
        user = new User();

        user.setId(uid);
        user.setName(name);
        user.setEmail(email);
        user.setProfilePicturePath(imagePath);
        user.setProvider(provider);

        user.saveUser();
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
                retrieveFacebookLoginData(loginResult.getAccessToken());
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

    private void retrieveFacebookLoginData(AccessToken accessToken) {
        Log.v(LOG_TAG, "Saving Facebook Log in Data");
        user = new User();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.v(LOG_TAG, "Saving Facebook Log in Data");
                        FirebaseUser firebaseUser = task.getResult().getUser();

                        uid = firebaseUser.getUid();
                        name = firebaseUser.getDisplayName();
                        email = firebaseUser.getEmail();
                        imagePath = firebaseUser.getPhotoUrl().toString();
                        provider = Constants.KEY_VALUE_FACEBOOK_PROVIDER;
                        encodedUid = Utils.getEncodedEmail(email) + "%20" + provider;

                        checkAndSaveFirebaseAndLaunchMainActivity();

                        if (!task.isSuccessful()){
                            /* sign in failed. Display a message to the user. */

                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Google Log In Methods
     */
    public void onGoogleLogInClicked(){
        Log.v(LOG_TAG, "onGoogleLogInClicked!");
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, GOOGLE_SIGN_IN);
    }

    private void retrieveGoogleSignInData(GoogleSignInAccount account){

        uid = account.getId();
        name = account.getDisplayName();
        email = account.getEmail();
        imagePath = account.getPhotoUrl().toString();
        provider = Constants.KEY_VALUE_GOOGLE_PROVIDER;
        encodedUid = Utils.getEncodedEmail(email) + "%20" + provider;

        checkAndSaveFirebaseAndLaunchMainActivity();
    }

    /**
     * Kakao Log In Methods
     */
    private void onKakaoLogInClicked(){
        // 헤쉬키를 가져온다
        getAppKeyHash();

        Log.d(LOG_TAG, "Kakao Log In is Clicked");
        // Create KakaoCallback
        mKakaoCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(mKakaoCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
        Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, getActivity());
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d(LOG_TAG, "Session is open");
            // Bring user data, if not registered, automatically signs up.
            kakaoRequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Log.d(LOG_TAG, exception.getMessage());
            }
        }
    }

    protected void kakaoRequestMe() {
        Log.d(LOG_TAG, "at KakaoRequestMe");
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                int ErrorCode = errorResult.getErrorCode();
                int ClientErrorCode = -777;

                if (ErrorCode == ClientErrorCode) {
                    Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(LOG_TAG , "오류로 카카오로그인 실패 ");
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d(LOG_TAG , "오류로 카카오로그인 실패 ");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.d(LOG_TAG , "Kakao Login onSuccess! ");

                imagePath = userProfile.getProfileImagePath();
                uid = String.valueOf(userProfile.getId());
                name = userProfile.getNickname();
                provider = Constants.KEY_VALUE_KAKAO_PROVIDER;
            }

            @Override
            public void onNotSignedUp() {
                // 자동가입이 아닐경우 동의창
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
                    retrieveGoogleSignInData(account);
                }
                break;
            case FACEBOOK_SIGN_IN:
                Log.v(LOG_TAG, "Facebook Log In: onActivityResult");
                // Pass the activity result back to the Facebook SDK
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void checkAndSaveFirebaseAndLaunchMainActivity(){
        mUserRef.child(encodedUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // data already exists
                    // the user is a returning user
                    Log.v(LOG_TAG, "User is returning user");
                    Utils.saveSharedPreferenceUid(getApplicationContext(), encodedUid);
//                    startActivity(new Intent(getActivity(), MainActivity.class));

                    ////// REPLACE THIS ///////
                    Fragment fragment = new LoginUserInfo1Fragment();
                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.login_container, fragment).commit();

                    communicator.sendUserInfo(name);
                } else {
                    // data does not yet exist
                    // the user is a new user.
                    Log.v(LOG_TAG, "User is new user");

                    saveUserToFirebase();

                    Utils.saveSharedPreferenceUid(getApplicationContext(), encodedUid);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public interface Communicator{
        public void sendUserInfo(String userInfo);
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