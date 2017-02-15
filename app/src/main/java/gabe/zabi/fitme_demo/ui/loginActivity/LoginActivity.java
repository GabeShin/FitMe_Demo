package gabe.zabi.fitme_demo.ui.loginActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import java.util.Map;

import gabe.zabi.fitme_demo.R;
import gabe.zabi.fitme_demo.model.User;
import gabe.zabi.fitme_demo.ui.mainActivity.MainActivity;
import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

/**
 * Created by Gabe on 2017-02-07.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set OnClickListeners
        findViewById(R.id.fb_login_button).setOnClickListener(this);
        findViewById(R.id.google_login_button).setOnClickListener(this);
        findViewById(R.id.kakao_login_button).setOnClickListener(this);

        /*
        Setup the Google API object to allow Google login
         */
        // Configure sign-in to request the user's ID, email address and basic profile.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        checkUserLogin();
        initializeScreen();
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
                saveFacebookLoginData(loginResult.getAccessToken());
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
        mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
    }

    private void saveFacebookLoginData(AccessToken accessToken) {
        Log.v(LOG_TAG, "Saving Facebook Log in Data");
        user = new User();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.v(LOG_TAG, "Saving Facebook Log in Data");
                        FirebaseUser firebaseUser = task.getResult().getUser();

                        uid = firebaseUser.getUid();
                        name = firebaseUser.getDisplayName();
                        email = firebaseUser.getEmail();
                        imagePath = firebaseUser.getPhotoUrl().toString();

                        saveUser(Constants.KEY_VALUE_FACEBOOK_PROVIDER);

                        /* create uid using encodedEmail and provider.
                         * and save it as SharedPreferenceUid.
                         */
                        String sharedPreferenceUid = Utils.getEncodedEmail(email) + "%20" + user.getProvider();
                        Utils.saveSharedPreferenceUid(getApplicationContext(), sharedPreferenceUid);

                        // create intent to start MainActivity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        Log.v(LOG_TAG, "Saved the user: " + name + "/" + uid + "/" + email + "/" + imagePath);
                        startActivity(intent);
                        finish();

                        if (!task.isSuccessful()){
                            /*
                             * sign in failed. Display a message to the user.
                             */
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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

    private void handleSignInResult(GoogleSignInAccount account){
        uid = account.getId();
        name = account.getDisplayName();
        email = account.getEmail();
        imagePath = account.getPhotoUrl().toString();

        saveUser(Constants.KEY_VALUE_GOOGLE_PROVIDER);

        /* create uid using encodedEmail and provider.
         * and save it as SharedPreferenceUid.
         */
        String sharedPreferenceUid = Utils.getEncodedEmail(email) + "%20" + user.getProvider();
        Utils.saveSharedPreferenceUid(getApplicationContext(), sharedPreferenceUid);

        // create intent to start MainActivity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Log.v(LOG_TAG, "Saved the user: " + name + "/" + uid + "/" + email + "/" + imagePath);
        startActivity(intent);
        finish();
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
        Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, LoginActivity.this);
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
                    finish();
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

                Map<String, String> map = userProfile.getProperties();

                saveUser(Constants.KEY_VALUE_KAKAO_PROVIDER);

                /* create uid using encodedEmail and provider.
                 * and save it as SharedPreferenceUid.
                 */

                String sharedPreferenceUid = uid + "%20" + user.getProvider();
                Utils.saveSharedPreferenceUid(getApplicationContext(), sharedPreferenceUid);

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GOOGLE_SIGN_IN:
                Log.v(LOG_TAG, "Google Log In: onActivityResult");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()){
                    Log.v(LOG_TAG, "Google Log In: onActivityResult - result is successful.");
                    // Google Sign In was successful, authenticate with Firebase.
                    GoogleSignInAccount account = result.getSignInAccount();
                    handleSignInResult(account);
                }
                break;
            case FACEBOOK_SIGN_IN:
                Log.v(LOG_TAG, "Facebook Log In: onActivityResult");
                // Pass the activity result back to the Facebook SDK
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
    protected void onStart() {
        /**
         * At onStart, we need to check if user had logged in before using SharedPreference.
         * Log the user in immediately, if there is createdUid SharedPreference.
         */
        String createdUid = Utils.getSharedPreferenceUid(getApplicationContext());
        Log.v(LOG_TAG, "CreatedUid is " + createdUid);

        if (createdUid != null) {
            // create intent to start MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
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

    private void saveUser(String provider){
        user = new User();

        user.setId(uid);
        user.setName(name);
        user.setEmail(email);
        user.setProfilePicturePath(imagePath);
        user.setProvider(provider);

        user.saveUser();
    }
}
