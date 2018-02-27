package com.pharmacy.agent;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.PickLocationActivity;
import com.pharmacy.R;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.models.PickLocation;
import com.pharmacy.preferences.UserPreferences;

import java.util.UUID;


public class AddNewPharmacyStepOne extends AppCompatActivity implements AppConstants {


    EditText anpPharmacyName,anpOwnerName,anpPhoneNumber,anpAddress,anpLanmark,anpCity,anpState,anpPincode,anpDoorNumber,anpEmail;
    TextView anpSelectLocation;
    Toolbar  anpToolbar;
    Button   anpSaveNewPharmacyDeatils;
    RelativeLayout mainLayout;
    UserPreferences userPreferences;
    PickLocation location;
    Gson gson;

    String pharmacyLocalId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_pharmacy);


        initialiseObjects();
        initialiseIDs();
        initialisePharmacyID();
        initialiseBackButton();
        initialiseClickListeners();
        reinitialiseData();
        initialiseAddress();



    }


    private void initialisePharmacyID()
    {
        if(userPreferences.getAddNewPharmacyId()!=null && userPreferences.getAddNewPharmacyId().trim().length()>2)
        {
            pharmacyLocalId = userPreferences.getAddNewPharmacyId();
        }
        else
        {
            pharmacyLocalId = UUID.randomUUID().toString();
            userPreferences.setAddNewPharmacyId(pharmacyLocalId);
        }
    }

    private void reinitialiseData()
    {

        try
        {
        if(pharmacyLocalId!=null) {

            PharmacyDAO agentDAO = new PharmacyDAO(this);
            PharmacyModel pharmacyModel =  agentDAO.getPharmacyDataByPharmacyID(pharmacyLocalId);

            if (pharmacyModel != null) {
                anpPharmacyName.setText(pharmacyModel.StoreName.toString());
                anpOwnerName.setText(pharmacyModel.Name.toString());
                anpPhoneNumber.setText(pharmacyModel.PhoneNumber.toString());
                anpAddress.setText(pharmacyModel.Address.toString());
                anpLanmark.setText(pharmacyModel.LandMark.toString());
                anpCity.setText(pharmacyModel.City.toString());
                anpState.setText(pharmacyModel.State.toString());
                anpPincode.setText(pharmacyModel.Pincode.toString());
                anpDoorNumber.setText(pharmacyModel.DoorNo.toString());
                anpEmail.setText(pharmacyModel.Email.toString());

            }
        }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }





    private void initialiseAddress()
    {
        try {
            String addresss =  getIntent().getStringExtra("LocationJson");
            if(addresss!=null) {
                location=gson.fromJson(addresss,PickLocation.class);
                anpAddress.setText(location.DetailAddress);
                anpCity.setText(location.City);
                anpState.setText(location.State);
                anpPincode.setText(location.Pincode);
                anpLanmark.setText(location.Landmark);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void initialiseObjects()
    {
        userPreferences =   new UserPreferences(this);
        gson    = new Gson();

    }

    private void initialiseIDs()
    {
        anpPharmacyName     =   findViewById(R.id.anp_pharmacy_name);
        anpOwnerName        =   findViewById(R.id.anp_owner_name);
        anpPhoneNumber      =   findViewById(R.id.anp_phone_number);
        anpAddress          =   findViewById(R.id.anp_address);
        anpDoorNumber       =   findViewById(R.id.anp_door_number);
        anpEmail            =   findViewById(R.id.anp_email);
        anpSelectLocation   =   findViewById(R.id.anp_select_location);

        anpToolbar          =   findViewById(R.id.anp_toolbar);
        anpSaveNewPharmacyDeatils   =   findViewById(R.id.anp_save_new_pharmacy_details);
        mainLayout          =   findViewById(R.id.mainLayout);

        anpLanmark          =   findViewById(R.id.anp_landmark);
        anpCity             =   findViewById(R.id.anp_city);
        anpState            =   findViewById(R.id.anp_state);
        anpPincode          =   findViewById(R.id.anp_pincode);
    }

    private void initialiseBackButton()
    {
        setSupportActionBar(anpToolbar);
        CommonMethods.setToolbar(this,getSupportActionBar());
    }

    private void initialiseClickListeners()
    {
        anpSaveNewPharmacyDeatils.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if(doValidatation()) {
                    persistAddNewPharmacyData();

                    Intent intent = new Intent(AddNewPharmacyStepOne.this, AddNewPharmacyStepTwo.class);
                    startActivity(intent);
                }
            }
        });

        anpSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               persistAddNewPharmacyData();
                Intent intent = new Intent(AddNewPharmacyStepOne.this,PickLocationActivity.class);
                intent.putExtra(getResources().getString(R.string.from),getResources().getString(R.string.add_new_pharmacy));
                startActivity(intent);

            }
        });


    }




    private void persistAddNewPharmacyData()
    {

        PharmacyModel pharmacyModel = new PharmacyModel();
        pharmacyModel.UserID    =   userPreferences.getUserGid();
        pharmacyModel.Name      =   anpOwnerName.getText().toString();
        pharmacyModel.StoreName =   anpPharmacyName.getText().toString();
        pharmacyModel.PhoneNumber   =   anpPhoneNumber.getText().toString();
        pharmacyModel.Address       =   anpAddress.getText().toString();
        pharmacyModel.LandMark      =   anpLanmark.getText().toString();
        pharmacyModel.City          =   anpCity.getText().toString();
        pharmacyModel.State         =   anpState.getText().toString();
        pharmacyModel.Pincode       =   anpPincode.getText().toString();
        pharmacyModel.DoorNo        =   anpDoorNumber.getText().toString();
        pharmacyModel.Email         =   anpEmail.getText().toString();
        pharmacyModel.PharmacyLocalId    =  pharmacyLocalId;




        PharmacyDAO pharmacyDAO = new PharmacyDAO(this);
        long val = pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);
        Log.i("tag","value is"+val);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean doValidatation()
    {

        if(anpPharmacyName.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Pharmacy Name");
            return false;
        }

        if(anpOwnerName.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Owner Name");
            return false;
        }

        if(anpPhoneNumber.getText().toString().length()>4)
        {
        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Phone Number");
            return false;
        }

        if(anpAddress.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Address");
            return false;
        }

        if(anpLanmark.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter LandMark");
            return false;
        }
        if(anpCity.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter City");
            return false;
        }

        if(anpState.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter State");
            return false;

        }

        if(anpPincode.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Pincode");
            return false;

        }

        if(anpDoorNumber.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Door Number");
            return false;

        }
        return true;
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
        Intent intent = new Intent(AddNewPharmacyStepOne.this,AgentPharmacyListWithNavigation.class);
        startActivity(intent);
    }
}
