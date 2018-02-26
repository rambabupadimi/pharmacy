package com.pharmacy;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

/**
 * Created by PCCS-0007 on 05-Feb-18.
 */

public class PharmacyApplication extends Application {

   public void onCreate() {
       super.onCreate();
       AndroidNetworking.initialize(getApplicationContext());
   }
}
