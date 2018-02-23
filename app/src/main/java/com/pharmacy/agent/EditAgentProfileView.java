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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pharmacy.CommonMethods;
import com.pharmacy.PickLocationActivity;
import com.pharmacy.R;
import com.pharmacy.RobotoEditText;
import com.pharmacy.RobotoTextView;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.models.PickLocation;
import com.pharmacy.operations.Post;
import com.pharmacy.preferences.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class EditAgentProfileView extends AppCompatActivity {

    RobotoEditText
            agentName,
            agentEmail,
            agentAddress,
            agentLandmark,
            agentCity,
            agentState,
            agentPincode,
            agentDoorNumber;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_profile_view);
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
                Intent intent = new Intent(EditAgentProfileView.this,PickLocationActivity.class);
                intent.putExtra(getResources().getString(R.string.from),getResources().getString(R.string.agentEdit));
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                View view = EditAgentProfileView.this.getCurrentFocus();
                if(view!=null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(CommonMethods.isInternetConnected(EditAgentProfileView.this)){
                    if(doValidatation())
                    {
                        persistAgentData();
                        AgentModel agentModel = agentDAO.getAgentData(userPreferences.getUserGid());
                        agentModel.UserType = "" + getString(R.string.agent);
                        UserModel userModel = userDAO.getUserData(userPreferences.getUserGid());
                        agentModel.PhoneNumber = userModel.PhoneNumber;
                        agentModel.DistributorID = userModel.DistributorID;
                        String json = gson.toJson(agentModel);
                        alertDialog.show();

                        Post post = new Post(EditAgentProfileView.this, CommonMethods.CONFIRM_USER_REGISTRATION, json) {
                            @Override
                            public void onResponseReceived(String result) {
                                alertDialog.dismiss();
                                if (result != null) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if (jsonObject.get("Status").toString().equalsIgnoreCase("Success")) {

                                            Intent intent = new Intent(EditAgentProfileView.this, AgentProfieView.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(EditAgentProfileView.this, "Something wrong", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(EditAgentProfileView.this, "Something wrong", Toast.LENGTH_LONG).show();
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

    }

    private void initialiseObjects()
    {
        agentDAO    =   new AgentDAO(this);
        userPreferences =   new UserPreferences(this);
        gson        =   new Gson();
        alertDialog =   new SpotsDialog(this);
        userDAO     =   new UserDAO(this);
    }

    private void inflateProfile(){
      AgentModel agentModel =  agentDAO.getAgentData(userPreferences.getUserGid());
      if(agentModel!=null)
      {
          if(agentModel.Name!=null)
            {
              agentName.setText(agentModel.Name);
            }

          if(agentModel.Email!=null)
          {
              agentEmail.setText(agentModel.Email);
          }
          if(agentModel.Address!=null)
          {
              agentAddress.setText(agentModel.Address);
          }
          if(agentModel.LandMark!=null)
          {
              agentLandmark.setText(agentModel.LandMark);
          }
          if(agentModel.City!=null)
          {
              agentCity.setText(agentModel.City);
          }
          if(agentModel.State!=null)
          {
              agentState.setText(agentModel.State);
          }
          if(agentModel.Pincode!=null)
          {
              agentPincode.setText(agentModel.Pincode);
          }
          if(agentModel.DoorNo!=null)
          {
              agentDoorNumber.setText(agentModel.DoorNo);
          }
      }
    }



    private void persistAgentData()
    {

        AgentModel agentModel = agentDAO.getAgentData(userPreferences.getUserGid());
        agentModel.Name        =   agentName.getText().toString();
        agentModel.Email       =   agentEmail.getText().toString();
        agentModel.Address     =   agentAddress.getText().toString();
        agentModel.City        =   agentCity.getText().toString();
        agentModel.LandMark    =   agentLandmark.getText().toString();
        agentModel.State       =   agentState.getText().toString();
        agentModel.Pincode     =   agentPincode.getText().toString();
        agentModel.DoorNo       =   agentDoorNumber.getText().toString();
        agentModel.UserID      =   userPreferences.getUserGid();

        AgentDAO agentDAO = new AgentDAO(this);
        long val = agentDAO.insertOrUpdate(agentModel);
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

        Intent intent = new Intent(this,AgentProfieView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(EditAgentProfileView.this, R.anim.back_swipe1, R.anim.back_swipe2).toBundle();
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
