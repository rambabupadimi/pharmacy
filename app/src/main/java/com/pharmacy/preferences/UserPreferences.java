package com.pharmacy.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by PCCS on 5/22/2017.
 */

public class UserPreferences {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    public static final String PREF_NAME = "app_flags";



    public static final String USER_LOGIN_TYPE      =   "user_login_type";
    public static final String USER_LOGIN_STATUS    =   "user_login_status";
    public static final String USER_BASIC_REGISTRATION_STATUS   =   "user_basic_registration_status";
    public static final String USER_PROCESSING_STATUS    =   "user_processing_status";
    public static final String USER_HOME_PAGE_STATUS    =   "user_home_page_status";



    public static final String ACCESS_TOKEN = "access_taken";
    public static final String USER_GID = "user_gid";
    public static final String DEVICE_UNIQUE_ID = "device_unique_id";
    public static final String SERVER_TOKEN = "server_token";


    public static final String AGENT_SELECTED_PHARMACY_ID = "agent_selected_pharmacy_id";

    public static final String ADD_NEW_PHARMACY_ID = "add_new_pharmacy_id";


    public UserPreferences(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setUserLoginType(String value){
        editor.putString(USER_LOGIN_TYPE,value);
        editor.commit();
    }

    public String getUserLoginType()
    {
        return   pref.getString(USER_LOGIN_TYPE,"");
    }


    public void setUserLoginStatus(boolean value)
    {
        editor.putBoolean(USER_LOGIN_STATUS,value);
        editor.commit();
    }

    public boolean getUserLoginStatus()
    {
        return pref.getBoolean(USER_LOGIN_STATUS,false);
    }

    public void setUserBasicRegistrationStatus(boolean value)
    {
        editor.putBoolean(USER_BASIC_REGISTRATION_STATUS,value);
        editor.commit();
    }

    public boolean getUserBasicRegistrationStatus()
    {
       return pref.getBoolean(USER_BASIC_REGISTRATION_STATUS,false);
    }

    public void setUserProcssingStatus(boolean value)
    {
        editor.putBoolean(USER_PROCESSING_STATUS,value);
        editor.commit();
    }

    public boolean getUserProcessingStatus()
    {
        return   pref.getBoolean(USER_PROCESSING_STATUS,false);
    }

    public void setAddNewPharmacyId(String value)
    {
        editor.putString(ADD_NEW_PHARMACY_ID,value);
        editor.commit();
    }

    public String getAddNewPharmacyId()
    {
        return   pref.getString(ADD_NEW_PHARMACY_ID,"");
    }

    public void setUserHomePageStatus(boolean value)
    {
        editor.putBoolean(USER_HOME_PAGE_STATUS,value);
        editor.commit();
    }

    public boolean getUserHomePageStatus()
    {
        return pref.getBoolean(USER_HOME_PAGE_STATUS,false);
    }


    public void setServerToken(String value) {
        value = value.replace("\"", "");
        editor.putString(SERVER_TOKEN, value);
        editor.commit();
    }

    public String getServerToken() {
        return pref.getString(SERVER_TOKEN, "");
    }

    public void setAccessToken(String value) {
        editor.putString(ACCESS_TOKEN, value);
        editor.commit();
    }

    public String getAccessToken() {
        return pref.getString(ACCESS_TOKEN, "");
    }

    public void setUserGid(String userid) {
        editor.putString(USER_GID, userid);
        editor.commit();
    }

    public String getUserGid() {
        return pref.getString(USER_GID, "");
    }

    public void setDeviceUniqueID(String deviceUniqueId) {
        editor.putString(DEVICE_UNIQUE_ID, deviceUniqueId);
        editor.commit();
    }


    public void setAgentSelectedPharmacyId(String agentSelectedPharmacyId)
    {
        editor.putString(AGENT_SELECTED_PHARMACY_ID, agentSelectedPharmacyId);
        editor.commit();
    }


    public String getAgentSelectedPharmacyId()
    {
        return pref.getString(AGENT_SELECTED_PHARMACY_ID, "");
    }
    public String getDeviceUniqueId() {
        return pref.getString(DEVICE_UNIQUE_ID, "");
    }




    public void clear() {
        editor.clear().commit();
    }

}