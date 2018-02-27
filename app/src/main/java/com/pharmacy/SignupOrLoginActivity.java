package com.pharmacy;

import android.*;
import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pharmacy.agent.AgentRegistration;
import com.pharmacy.db.DatabaseManager;
import com.pharmacy.login.AppSettings_new;
import com.pharmacy.pharmacy.PharmacyRegistration;
import com.pharmacy.preferences.UserPreferences;

public class SignupOrLoginActivity extends AppCompatActivity {



    Button              solLoginButton,solRegisterButton;
    RadioButton         solPharmacy,solAgent;
    RadioGroup          solRadioGroup;
    RadioButton         testButton;
    UserPreferences     userPreferences;
    String[] PERMISSIONS = {
            "android.permission.READ_PHONE_STATE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"};
    private static final int PERMISSION_REQUEST_CODE = 1;

    ImageView settings;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_or_login);


        initialiseObjects();
        initialiseIDs();
        initialiseListeners();
        initialiseRadioButtons();

        initialiseDatabase();
        requestPermission();
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
             //   isRationaleDialogShown = true;
                requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {
             //   isPermissionDeniedNever = true;
                requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                try {
                    boolean isPhoneStateDone = false,
                            isShowRationalePhoneState = false,

                            isAccessFineLocation=false,
                            isShowRationaleAccessFineLocation =false,

                            isAccessCoarseLocation = false,
                            isShowRationaleCoarseLocation = false,

                            isWriteExtStorageDone = false,
                            isShowRationaleWriteExtStorage = false,

                            isReadExtStorageDone = false,
                            isShowRationaleReadExtStorage = false;
                    for (int i = 0; i < permissions.length; i++) {
                        String permission = permissions[i];
                        int grantResult = grantResults[i];
                        if (permission.equals(android.Manifest.permission.READ_PHONE_STATE)) {
                            isShowRationaleAccessFineLocation = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                                isPhoneStateDone = true;
                            } else {
                                isPhoneStateDone = false;
                            }
                        } else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            isShowRationaleAccessFineLocation = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                                isAccessFineLocation = true;
                            } else {
                                isAccessFineLocation = false;
                            }
                        }
                        else if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            isShowRationaleCoarseLocation = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                                isAccessCoarseLocation = true;
                            } else {
                                isAccessCoarseLocation = false;
                            }
                        }
                        else if (permission.equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            isShowRationaleWriteExtStorage = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                                isWriteExtStorageDone = true;
                            } else {
                                isWriteExtStorageDone = false;
                            }
                        } else if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            isShowRationaleReadExtStorage = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                                isReadExtStorageDone = true;
                            } else {
                                isReadExtStorageDone = false;
                            }
                        }
                    }


                    Log.i("tag","isPhoneStateDone"+isPhoneStateDone);
                    Log.i("tag","isShowRationalePhoneState"+isShowRationalePhoneState);
                    Log.i("tag","isAccessFineLocation"+isAccessFineLocation);
                    Log.i("tag","isShowRationaleAccessFineLocation"+isShowRationaleAccessFineLocation);
                    Log.i("tag","isAccessCoarseLocation"+isAccessCoarseLocation);
                    Log.i("tag","isShowRationaleCoarseLocation"+isShowRationaleCoarseLocation);
                    Log.i("tag","isWriteExtStorageDone"+isWriteExtStorageDone);
                    Log.i("tag","isShowRationaleWriteExtStorage"+isShowRationaleWriteExtStorage);
                    Log.i("tag","isReadExtStorageDone"+isReadExtStorageDone);
                    Log.i("tag","isShowRationaleReadExtStorage"+isShowRationaleReadExtStorage);



                    boolean isRationale = (isShowRationalePhoneState
                            && isShowRationaleAccessFineLocation
                            && isShowRationaleCoarseLocation
                            && isShowRationaleReadExtStorage
                            && isShowRationaleWriteExtStorage);

                   if (isRationale) {
                        solLoginButton.setVisibility(View.VISIBLE);
                        settings.setVisibility(View.GONE);
                    } else if (!isRationale && (isPhoneStateDone &&
                                                isAccessFineLocation &&
                                                isAccessCoarseLocation &&
                                                isWriteExtStorageDone &&
                                                isReadExtStorageDone)) {
                       solLoginButton.setVisibility(View.VISIBLE);
                       settings.setVisibility(View.GONE);
                    } else if (!isRationale && (!isPhoneStateDone ||
                                                !isAccessFineLocation ||
                                                !isAccessCoarseLocation ||
                                                !isWriteExtStorageDone ||
                                                !isReadExtStorageDone)) {
                       solLoginButton.setVisibility(View.GONE);
                       settings.setVisibility(View.VISIBLE);
                    } else {
                       solLoginButton.setVisibility(View.GONE);
                       settings.setVisibility(View.VISIBLE);
                    }
                    break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void initialiseDatabase()
    {
        DatabaseManager databaseManager = new DatabaseManager(this);

        databaseManager.getWritableDatabase();

    }

    private void initialiseRadioButtons(){
       if(userPreferences.getUserLoginType()!=null && userPreferences.getUserLoginType().length()>0)
       {
           if(userPreferences.getUserLoginType().toString().equalsIgnoreCase(getString(R.string.pharmacy)))
           {
               solPharmacy.setChecked(true);
           }
           else{
               solAgent.setChecked(true);
           }
       }
       else{
           solPharmacy.setChecked(true);
       }
    }

    private void initialiseObjects()
    {
        userPreferences =   new UserPreferences(SignupOrLoginActivity.this);
    }

    private void initialiseIDs()
    {
        solLoginButton      =   findViewById(R.id.sol_login_button);
        solRegisterButton   =   findViewById(R.id.sol_register_button);
        solPharmacy         =   findViewById(R.id.sol_pharmacy);
        solAgent            =   findViewById(R.id.sol_agent);
        solRadioGroup       =   findViewById(R.id.sol_group);
        settings            =   findViewById(R.id.settings);
    }

    private void initialiseListeners()
    {
        solRegisterButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                int selectedId      =   solRadioGroup.getCheckedRadioButtonId();
                testButton          =   findViewById(selectedId);
                Log.i("tag,","radio is"+testButton.getText().toString());
                Intent intent=null;
                if(testButton.getText().toString().equalsIgnoreCase(getString(R.string.pharmacy)))
                {
                    userPreferences.setUserLoginType(getString(R.string.pharmacy));
                    intent               =   new Intent(SignupOrLoginActivity.this, PharmacyRegistration.class);
                }
                else
                {
                    userPreferences.setUserLoginType(getString(R.string.agent));
                    intent               =   new Intent(SignupOrLoginActivity.this, AgentRegistration.class);
                }

                Bundle bundleAnimation      =   ActivityOptions.makeCustomAnimation(SignupOrLoginActivity.this, R.anim.next_swipe2,
                        R.anim.next_swipe1).toBundle();
                startActivity(intent,bundleAnimation);
            }
        });

        solLoginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                int selectedId      =   solRadioGroup.getCheckedRadioButtonId();
                testButton          =   findViewById(selectedId);
                Log.i("tag,","radio is"+testButton.getText().toString());
                Intent intent=null;
                if(testButton.getText().toString().equalsIgnoreCase(getString(R.string.pharmacy)))
                {
                    userPreferences.setUserLoginType(getString(R.string.pharmacy));
                    intent               =   new Intent(SignupOrLoginActivity.this, AppSettings_new.class);
                }
                else
                {
                    userPreferences.setUserLoginType(getString(R.string.agent));
                    intent               =   new Intent(SignupOrLoginActivity.this, AppSettings_new.class);
                }


                Bundle bundleAnimation      =   ActivityOptions.makeCustomAnimation(SignupOrLoginActivity.this, R.anim.next_swipe2,
                        R.anim.next_swipe1).toBundle();
                startActivity(intent,bundleAnimation);
            }
        });



        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marshmallowSetting();
            }
        });

}

    private void marshmallowSetting() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
