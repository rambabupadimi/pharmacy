package com.pharmacy.agent;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.PickLocationActivity;
import com.pharmacy.R;
import com.pharmacy.SignupOrLoginActivity;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.models.PickLocation;
import com.pharmacy.pharmacy.PharmacyRegistration;
import com.pharmacy.preferences.UserPreferences;

public class AgentRegistration extends AppCompatActivity implements View.OnClickListener,AppConstants {


    EditText arAgentName,arAddress,arDoorNumber,arCity,arState,arPincode,arLandmark,arEmailAddress;
    TextView arSelectLocation;
    Button arRegisterAgent;
    Toolbar arToolbar;
    PickLocation location;
    Gson gson;
    RelativeLayout mainLayout;
    CommonMethods commonMethods;
    UserPreferences userPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_registration);

        initialiseObjects();
        initialiseIDs();
        initialiseClickListeners();
       // initialiseBackButton();

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



    private void initialiseObjects()
    {
        gson    =   new Gson();
        commonMethods   =   new CommonMethods();
        userPreferences =   new UserPreferences(this);
    }

    private void initialiseAddress()
    {
        try {
           String addresss =  getIntent().getStringExtra("LocationJson");
            if(addresss!=null) {
                location=gson.fromJson(addresss,PickLocation.class);
                arAddress.setText(location.DetailAddress);
                arCity.setText(location.City);
                arState.setText(location.State);
                arPincode.setText(location.Pincode);
                arLandmark.setText(location.Landmark);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void initialiseIDs()
    {

        arAgentName         =   findViewById(R.id.ar_agent_name);
        arAddress           =   findViewById(R.id.ar_address);
        arDoorNumber        =   findViewById(R.id.ar_door_number);
        arSelectLocation    =   findViewById(R.id.ar_select_location);
        arRegisterAgent     =   findViewById(R.id.ar_save_agent_details);
        arToolbar           =   findViewById(R.id.ar_toolbar);
        arEmailAddress      =   findViewById(R.id.ar_email);


        arCity              =   findViewById(R.id.ar_city);
        arState             =   findViewById(R.id.ar_state);
        arPincode           =   findViewById(R.id.ar_pincode);
        arLandmark          =   findViewById(R.id.ar_landmark);
        mainLayout          =   findViewById(R.id.mainLayout);

    }

    private void initialiseClickListeners()
    {
        arRegisterAgent.setOnClickListener(this);
        arSelectLocation.setOnClickListener(this);
    }
    private void initialiseBackButton()
    {
        setSupportActionBar(arToolbar);
        CommonMethods.setToolbar(this,getSupportActionBar());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ar_save_agent_details)
        {

            if(doValidatation())
            {
                persistAgentData();
                Intent intent = new Intent(this,AgentRegistrationTwo.class);
                startActivity(intent);
            }

/*
            Intent intent = new Intent(this,AgentRegistrationTwo.class);
            startActivity(intent);
*/
        }

        if(v.getId() == R.id.ar_select_location)
        {

            persistAgentData();
            Intent intent = new Intent(AgentRegistration.this,PickLocationActivity.class);
            intent.putExtra(getResources().getString(R.string.from),getResources().getString(R.string.agentRegistration));
             startActivity(intent);

        }
    }



    private void persistAgentData()
    {

        AgentModel agentModel = new AgentModel();
        agentModel.Name        =   arAgentName.getText().toString();
        agentModel.Email       =   arEmailAddress.getText().toString();
        agentModel.Address     =   arAddress.getText().toString();
        agentModel.City        =   arCity.getText().toString();
        agentModel.LandMark    =   arLandmark.getText().toString();
        agentModel.State       =   arState.getText().toString();
        agentModel.Pincode     =   arPincode.getText().toString();
        agentModel.DoorNo       =   arDoorNumber.getText().toString();
        agentModel.UserID      =   userPreferences.getUserGid();

        AgentDAO agentDAO = new AgentDAO(this);
        long val = agentDAO.insertOrUpdate(agentModel);
        Log.i("tag","value is"+val);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean doValidatation()
    {

        if(arAgentName.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Name");
            return false;
        }

        if(arEmailAddress.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Email");
            return false;
        }

        if(CommonMethods.isValidEmail(arEmailAddress.getText().toString()))
        {
        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Correct Email");
            return false;
        }

        if(arAddress.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Address");
            return false;
        }

        if(arLandmark.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter LandMark");
            return false;
        }
        if(arCity.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter City");
            return false;
        }

        if(arState.getText().toString().length()>1)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter State");
            return false;

        }

        if(arPincode.getText().toString().length()>3)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,getBaseContext(),"Enter Pincode");
            return false;

        }

        if(arDoorNumber.getText().toString().length()>3)
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
        finish();
         }

    public void reInitialiseData(String id)
    {
        AgentDAO agentDAO = new AgentDAO(this);
        AgentModel agentModel =  agentDAO.getAgentData(id);
        if(agentModel!=null)
        {
            arAgentName.setText(agentModel.Name.toString());
            arEmailAddress.setText(agentModel.Email.toString());
            arAddress.setText(agentModel.Address.toString());
            arLandmark.setText(agentModel.LandMark.toString());
            arCity.setText(agentModel.City.toString());
            arState.setText(agentModel.State.toString());
            arPincode.setText(agentModel.Pincode.toString());
            arDoorNumber.setText(agentModel.DoorNo.toString());
        }
    }

}
