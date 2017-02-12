package gabe.zabi.fitme_demo.model;

import com.firebase.client.Firebase;

import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

/**
 * Created by Gabe on 2017-02-07.
 */

public class User {

    private String id;
    private String password;

    private String provider;
    private String name;

    private String phoneNumber;
    private String email;
    private String profilePicturePath;

    public User() {
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }
    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public void saveUser() {
        Firebase userRef = new Firebase(Constants.FIREBASE_URL_USER);

        if (getProvider() == Constants.KEY_VALUE_KAKAO_PROVIDER){
            userRef.child(getId()).setValue(this);
        } else {
            userRef.child(Utils.getEncodedEmail(email) + "%20" + provider).setValue(this);
        }
    }
}
