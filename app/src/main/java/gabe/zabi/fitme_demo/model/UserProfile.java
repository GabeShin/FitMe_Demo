package gabe.zabi.fitme_demo.model;

import com.firebase.client.Firebase;

import gabe.zabi.fitme_demo.utils.Constants;
import gabe.zabi.fitme_demo.utils.Utils;

/**
 * Created by Gabe on 2017-02-15.
 */

public class UserProfile {

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private int gender;
    private String birthday;
    private String profileImage;
    private int goal;
    private String nickname;
    private String height;
    private String weight;
    private int experience;

    public UserProfile() {
    }

    public void saveUser() {
        String createdUid = Utils.encodeEmail(email);
        Firebase userRef = new Firebase(Constants.FIREBASE_URL_USER).child(createdUid).child(Constants.FIREBASE_LOCATION_PROFILE_INFO);
        userRef.setValue(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void saveUser(String createdUid) {
        Firebase userRef = new Firebase(Constants.FIREBASE_URL_USER).child(createdUid).child(Constants.FIREBASE_LOCATION_PROFILE_INFO);
        userRef.setValue(this);
    }
}