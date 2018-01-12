package com.google.firebase.quickstart.fcm;

/**
 * Created by kashishbhatia on 1/8/18.
 */

public class MyUser {

    private String userDeviceToken;
    private String userName;
    private boolean pushPreference;

    public int getUserAccountBalance() {
        return userAccountBalance;
    }

    public void setUserAccountBalance(int userAccountBalance) {
        this.userAccountBalance = userAccountBalance;
    }

    private int userAccountBalance;

    public MyUser() {
    }

    /*public MyUser(String userDeviceToken, String userName, boolean pushPreference){

        this.userDeviceToken = userDeviceToken;
        this.userName = userName;
        this.pushPreference = pushPreference;

    }*/
    public MyUser(String userDeviceToken, String userName, boolean pushPreference, int userAccountBalance){

        this.userDeviceToken = userDeviceToken;
        this.userName = userName;
        this.pushPreference = pushPreference;
        this.userAccountBalance = userAccountBalance;

    }

    public String getUserDeviceToken() {
        return userDeviceToken;
    }

    public void setUserDeviceToken(String userDeviceToken) {
        this.userDeviceToken = userDeviceToken;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getPushPreference(){
        return pushPreference;
    }
    public void setPushPreference(boolean pushPreference){
        this.pushPreference = pushPreference;
    }

}
