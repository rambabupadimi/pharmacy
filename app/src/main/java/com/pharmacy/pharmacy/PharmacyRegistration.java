package com.pharmacy.pharmacy;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.PickLocationActivity;
import com.pharmacy.R;
import com.pharmacy.SignupOrLoginActivity;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.models.PickLocation;
import com.pharmacy.preferences.UserPreferences;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PharmacyRegistration extends AppCompatActivity implements View.OnClickListener,AppConstants{

    EditText    prPharmacyName,prOwnerName,prAddress,prDoorNumber,prCity,prState,prLandmark,prPincode,prEmail;
    TextView    prSelectLocation;
    Button      prRegisterPharmacy;
    Toolbar     prToolbar;
    PickLocation location=null;
    Gson        gson;
    RelativeLayout mainLayout;
    CommonMethods commonMethods;
    UserPreferences userPreferences;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_registration);

        initialiseObjects();
        initialiseIDs();
        initialiseClickListeners();
        initialiseBackButton();
         reinitialiseData();
        initialiseAddress();
        initialiseStatus();
    }


    private void initialiseStatus()
    {
        commonMethods.maintainState(this,LOGIN_REGISTRATION_STATUS);

    }

    private void reinitialiseData()
    {
        reInitialiseData(userPreferences.getUserGid());
    }



    public void reInitialiseData(String id)
    {
        PharmacyDAO pharmacyDAO = new PharmacyDAO(this);
        PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyData(id);

        if(pharmacyModel!=null)
        {
            prPharmacyName.setText(pharmacyModel.StoreName.toString());
            prOwnerName.setText(pharmacyModel.Name.toString());
            prEmail.setText(pharmacyModel.Email.toString());
            prAddress.setText(pharmacyModel.Address.toString());
            prLandmark.setText(pharmacyModel.LandMark.toString());
            prCity.setText(pharmacyModel.City.toString());
            prState.setText(pharmacyModel.State.toString());
            prPincode.setText(pharmacyModel.Pincode.toString());
            prDoorNumber.setText(pharmacyModel.DoorNo.toString());


        }
    }

    private void initialiseObjects(){
         gson           =   new Gson();
         commonMethods  =   new CommonMethods();
         userPreferences    =   new UserPreferences(this);
    }

    private void initialiseAddress()
    {
        try {
            String addresss =  getIntent().getStringExtra("LocationJson");
            if(addresss!=null) {
                location=gson.fromJson(addresss,PickLocation.class);
                prAddress.setText(location.DetailAddress);
                prCity.setText(location.City);
                prState.setText(location.State);
                prPincode.setText(location.Pincode);
                prLandmark.setText(location.Landmark);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initialiseIDs()
    {

        prPharmacyName      =   findViewById(R.id.pr_pharmacy_name);
        prOwnerName         =   findViewById(R.id.pr_owner_name);
        prAddress           =   findViewById(R.id.pr_address);
        prDoorNumber        =   findViewById(R.id.pr_door_number);
        prSelectLocation    =   findViewById(R.id.pr_select_location);
        prRegisterPharmacy      =   findViewById(R.id.pr_save_pharmacy_details);
        prToolbar               =   findViewById(R.id.pr_toolbar);

        prCity              =   findViewById(R.id.pr_city);
        prState             =   findViewById(R.id.pr_state);
        prLandmark          =   findViewById(R.id.pr_landmark);
        prPincode           =   findViewById(R.id.pr_pincode);
        prEmail             =   findViewById(R.id.pr_email_id);
        mainLayout          =   findViewById(R.id.mainLayout);

    }

    private void initialiseClickListeners()
    {
        prRegisterPharmacy.setOnClickListener(this);
        prSelectLocation.setOnClickListener(this);
    }
    private void initialiseBackButton()
    {
        setSupportActionBar(prToolbar);
        CommonMethods.setToolbar(this,getSupportActionBar());
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
       switch(v.getId()){
           case R.id.pr_select_location:
                redirectPickLocation();
               return;
           case R.id.pr_save_pharmacy_details:
               redirectSavePharmacy2();
               return;
       }
    }


    private void redirectPickLocation()
    {

        persistPharmacyData();
        Intent intent = new Intent(PharmacyRegistration.this,PickLocationActivity.class);
        intent.putExtra(getResources().getString(R.string.from),getResources().getString(R.string.pharmacyRegistration));
        startActivity(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void redirectSavePharmacy2()
    {

       if(doValidatation()) {
           persistPharmacyData();
           Intent intent = new Intent(PharmacyRegistration.this, PharmacyRegistrationTwo.class);
           startActivity(intent);
       }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PharmacyRegistration.this.finish();
        }

    private void persistPharmacyData()
    {

            PharmacyModel pharmacyModel = new PharmacyModel();
            pharmacyModel.Name          =   prOwnerName.getText().toString();
            pharmacyModel.StoreName     =   prPharmacyName.getText().toString();
            pharmacyModel.Email         =   prEmail.getText().toString();
            pharmacyModel.Address       =   prAddress.getText().toString();
            pharmacyModel.LandMark      =   prLandmark.getText().toString();
            pharmacyModel.City          =   prCity.getText().toString();
            pharmacyModel.State         =   prState.getText().toString();
            pharmacyModel.Pincode       =   prPincode.getText().toString();
            pharmacyModel.DoorNo        =   prDoorNumber.getText().toString();
            pharmacyModel.UserID        =   userPreferences.getUserGid();

            PharmacyDAO pharmacyDAO = new PharmacyDAO(this);
            long val = pharmacyDAO.insertOrUpdate(pharmacyModel);
            Log.i("tag","value is"+val);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean doValidatation()
    {

        if(prPharmacyName.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Pharmacy Name");
            return false;
        }

        if(prOwnerName.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Pharmacy Owner name");
            return false;
        }

        if(prEmail.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Email-Id ");
            return false;
        }

        if(CommonMethods.isValidEmail(prEmail.getText().toString()))
        {
        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Correct Email");
            return false;
        }

        if(prAddress.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Address");
            return false;
        }

        if(prLandmark.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter LandMark");
            return false;
        }
        if(prCity.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter City");
            return false;
        }

        if(prState.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter State");
            return false;

        }

        if(prPincode.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Pincode");
            return false;

        }

        if(prDoorNumber.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Door Number");
            return false;

        }
        return true;
    }


}
