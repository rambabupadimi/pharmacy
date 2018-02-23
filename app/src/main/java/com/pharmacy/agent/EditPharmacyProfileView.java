package com.pharmacy.agent;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pharmacy.CommonMethods;
import com.pharmacy.PickLocationActivity;
import com.pharmacy.R;
import com.pharmacy.RobotoEditText;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.models.PickLocation;
import com.pharmacy.operations.Post;
import com.pharmacy.preferences.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class EditPharmacyProfileView extends AppCompatActivity {

    RobotoEditText
            agentName,
            agentEmail,
            agentAddress,
            agentLandmark,
            agentCity,
            agentState,
            agentPincode,
            agentDoorNumber,
            pharmacyStoreName;

    AgentDAO agentDAO;
    UserPreferences userPreferences;
    Toolbar toolbar;
    RelativeLayout mainLayout;
    TextView agentSelectLocation;
    PickLocation location;
    Gson gson;
    TextView save;
    AlertDialog alertDialog;
    UserDAO userDAO;
    PharmacyDAO pharmacyDAO;

    LinearLayout aPharmacyNameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pharmacy_profile_view);
        initialiseObjects();
        initialiseIDs();
        inflateProfile();
        initialiseBackButton();
        initialiseClickListeners();
        initialiseAddress();


    }

    private void initialiseAddress()
    {
        try {
            String addresss =  getIntent().getStringExtra("LocationJson");
            if(addresss!=null) {
                location=gson.fromJson(addresss,PickLocation.class);
                agentAddress.setText(location.DetailAddress);
                agentCity.setText(location.City);
                agentState.setText(location.State);
                agentPincode.setText(location.Pincode);
                agentLandmark.setText(location.Landmark);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void initialiseClickListeners()
    {
        agentSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistAgentData();
                Intent intent = new Intent(EditPharmacyProfileView.this,PickLocationActivity.class);
                intent.putExtra(getResources().getString(R.string.from),getResources().getString(R.string.pharmacy_new_edit));
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                View view = EditPharmacyProfileView.this.getCurrentFocus();
                if(view!=null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(CommonMethods.isInternetConnected(EditPharmacyProfileView.this)){
                    if(doValidatation())
                    {
                        persistAgentData();
                       alertDialog.show();
                        PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAgentSelectedLocalPharmacyId());
                        UserModel userModel = userDAO.getUserData(userPreferences.getUserGid());
                        pharmacyModel.DistributorID = userModel.DistributorID;
                        String json = gson.toJson(pharmacyModel);



                        // we
                        Post post = new Post(EditPharmacyProfileView.this, CommonMethods.ADD_NEW_PHARMACY, json) {
                            @Override
                            public void onResponseReceived(String result) {
                                alertDialog.dismiss();
                                if (result != null) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if (jsonObject.get("Status").toString().equalsIgnoreCase("Success")) {
                                            String response = jsonObject.get("Response").toString();
                                            PharmacyModel pharmacyModel1 = gson.fromJson(response,PharmacyModel.class);
                                            long id = pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel1);
                                            if(id!= -1) {
                                                userPreferences.setAddNewPharmacyId("");
                                                Intent intent = new Intent(EditPharmacyProfileView.this, ViewPharmacyDetails.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                        Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(intent);
                                            }
                                            else {
                                                Toast.makeText(EditPharmacyProfileView.this,"Something wrong",Toast.LENGTH_LONG).show();
                                            }

                                        } else {

                                            Toast.makeText(EditPharmacyProfileView.this, "Something wrong", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(EditPharmacyProfileView.this, "Something wrong", Toast.LENGTH_LONG).show();
                                }

                            }
                        };
                        post.execute();
                    }
                }
            }

        });
    }

    private void initialiseBackButton()
    {
        setSupportActionBar(toolbar);
        CommonMethods.setToolbar(this,getSupportActionBar());
    }

    private void initialiseIDs()
    {
        agentName           =   findViewById(R.id.a_pro_agent_name);
        agentEmail          =   findViewById(R.id.a_pro_agent_email);
        agentAddress        =   findViewById(R.id.a_pro_agent_address);
        agentLandmark       =   findViewById(R.id.a_pro_agent_landmark);
        agentCity           =   findViewById(R.id.a_pro_agent_city);
        agentState          =   findViewById(R.id.a_pro_agent_state);
        agentPincode        =   findViewById(R.id.a_pro_agent_pincode);
        agentDoorNumber     =   findViewById(R.id.a_pro_agent_door_number);

        toolbar             =   findViewById(R.id.toolbar);
        toolbar.setTitle("");

        mainLayout          =   findViewById(R.id.mainLayout);
        agentSelectLocation =   findViewById(R.id.a_pro_agent_select_location);
        save                =   findViewById(R.id.save_details);

        pharmacyStoreName   =   findViewById(R.id.a_pro_pharmacy_name);
        aPharmacyNameLayout =   findViewById(R.id.a_pro_pharmacy_name_layout);
        aPharmacyNameLayout.setVisibility(View.VISIBLE);

    }

    private void initialiseObjects()
    {
        agentDAO    =   new AgentDAO(this);
        userPreferences =   new UserPreferences(this);
        gson        =   new Gson();
        alertDialog =   new SpotsDialog(this);
        userDAO     =   new UserDAO(this);
        pharmacyDAO =   new PharmacyDAO(this);
    }

    private void inflateProfile(){
        PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAgentSelectedLocalPharmacyId());
        if(pharmacyModel!=null)
        {
            if(pharmacyModel.Name!=null)
            {
                agentName.setText(pharmacyModel.Name);
            }
            if(pharmacyModel.StoreName!=null)
            {
                pharmacyStoreName.setText(pharmacyModel.StoreName);
            }

            if(pharmacyModel.Email!=null)
            {
                agentEmail.setText(pharmacyModel.Email);
            }
            if(pharmacyModel.Address!=null)
            {
                agentAddress.setText(pharmacyModel.Address);
            }
            if(pharmacyModel.LandMark!=null)
            {
                agentLandmark.setText(pharmacyModel.LandMark);
            }
            if(pharmacyModel.City!=null)
            {
                agentCity.setText(pharmacyModel.City);
            }
            if(pharmacyModel.State!=null)
            {
                agentState.setText(pharmacyModel.State);
            }
            if(pharmacyModel.Pincode!=null)
            {
                agentPincode.setText(pharmacyModel.Pincode);
            }
            if(pharmacyModel.DoorNo!=null)
            {
                agentDoorNumber.setText(pharmacyModel.DoorNo);
            }
        }
    }



    private void persistAgentData()
    {

        PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAgentSelectedLocalPharmacyId());
        pharmacyModel.Name        =   agentName.getText().toString();
        pharmacyModel.Email       =   agentEmail.getText().toString();
        pharmacyModel.Address     =   agentAddress.getText().toString();
        pharmacyModel.City        =   agentCity.getText().toString();
        pharmacyModel.LandMark    =   agentLandmark.getText().toString();
        pharmacyModel.State       =   agentState.getText().toString();
        pharmacyModel.Pincode     =   agentPincode.getText().toString();
        pharmacyModel.DoorNo       =   agentDoorNumber.getText().toString();
        pharmacyModel.UserID      =   userPreferences.getUserGid();

        PharmacyDAO pharmacyDAO = new PharmacyDAO(this);
        long val = pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);


        Log.i("tag","value is"+val);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this,ViewPharmacyDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(EditPharmacyProfileView.this, R.anim.back_swipe2, R.anim.back_swipe1).toBundle();
        startActivity(intent,bndlanimation);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean doValidatation()
    {

        if(agentName.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Name");
            return false;
        }

        if(agentEmail.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Email");
            return false;
        }

        if(CommonMethods.isValidEmail(agentEmail.getText().toString()))
        {
        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Correct Email");
            return false;
        }

        if(agentAddress.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Address");
            return false;
        }

        if(agentLandmark.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter LandMark");
            return false;
        }
        if(agentCity.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter City");
            return false;
        }

        if(agentState.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter State");
            return false;

        }

        if(agentPincode.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Pincode");
            return false;

        }

        if(agentDoorNumber.getText().toString().length()>3)
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
