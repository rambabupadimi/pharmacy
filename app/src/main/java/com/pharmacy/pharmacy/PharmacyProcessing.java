package com.pharmacy.pharmacy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.agent.AgentProcessing;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.models.GetUserDetailsRequest;
import com.pharmacy.operations.Post;
import com.pharmacy.preferences.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class PharmacyProcessing extends AppCompatActivity implements AppConstants{

    Button ppGotoHomePage;
    CommonMethods commonMethods;

    TextView ppRequestMessage;
    ProgressBar ppProgressbar;
    Button ppRefresh;
    PharmacyDAO pharmacyDAO;
    UserDAO userDAO;
    UserPreferences userPreferences;
    Gson gson;
    PharmacyModel pharmacyModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_processing);

        initialiseObjects();
        initialiseIDs();
        initialiseClickListeners();
        initialiseStatus();

    }

    private void initialiseObjects(){
        commonMethods = new CommonMethods();
        pharmacyDAO = new PharmacyDAO(this);
        userPreferences =   new UserPreferences(this);
        userDAO         =   new UserDAO(this);
        gson            =   new Gson();
    }

    private void initialiseStatus()
    {

            commonMethods.maintainState(this,USER_VERIFIED_STATUS);

             pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getPharmacyRegisterLocalUserId());
            if(pharmacyModel.IsApproved)
            {
                ppRequestMessage.setText("Hi.."+pharmacyModel.Name+" Your "+pharmacyModel.StoreName+" is successfully verified, click home button go to Homepage");
                ppGotoHomePage.setVisibility(View.VISIBLE);
                ppRefresh.setVisibility(View.GONE);
            }
            else
            {
                showFailMessage();
                ppRefresh.setVisibility(View.VISIBLE);
                ppGotoHomePage.setVisibility(View.GONE);
            }

    }


    private void initialiseIDs(){
        ppGotoHomePage      =   findViewById(R.id.pp_go_to_homepage);
        ppRequestMessage    =   findViewById(R.id.request_processing_message);
        ppProgressbar       =   findViewById(R.id.request_processing_progressbar);
        ppRefresh           =   findViewById(R.id.request_processing_refresh);

    }

    private void initialiseClickListeners()
    {
        ppGotoHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacyProcessing.this,PharmacyRunningListWithNavigation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        |Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        ppRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CommonMethods.isInternetConnected(PharmacyProcessing.this)) {
                    UserModel userModel = userDAO.getUserData(userPreferences.getUserGid());
                    GetUserDetailsRequest getUserDetailsRequest = new GetUserDetailsRequest();
                    getUserDetailsRequest.UserID = userPreferences.getUserGid();
                    getUserDetailsRequest.UserType = getString(R.string.pharmacy);
                    getUserDetailsRequest.DistributorID = userModel.DistributorID;
                    getUserDetailsRequest.LastUpdatedTimeTicks = userPreferences.getGetAllUserDetailsTimeticks();
                    String json = gson.toJson(getUserDetailsRequest);
                    ppProgressbar.setVisibility(View.VISIBLE);
                    Post post = new Post(PharmacyProcessing.this, CommonMethods.GET_USER_DETAILS, json) {
                        @Override
                        public void onResponseReceived(String result) {
                            if (result != null) {
                                ppProgressbar.setVisibility(View.GONE);
                                try {
                                    JSONObject jsonObject1 = new JSONObject(result);
                                    if (jsonObject1.get("Status").toString().equalsIgnoreCase("Success")) {
                                        if (jsonObject1.get("Response") != null) {
                                            JSONObject jsonObject2 = jsonObject1.getJSONObject("Response");
                                            if (jsonObject2.get("UserDetails") != null) {
                                                PharmacyModel pharmacyModel = gson.fromJson(jsonObject2.get("UserDetails").toString(), PharmacyModel.class);
                                                Long id = commonMethods.renderLoginDataForPharmacy(PharmacyProcessing.this, pharmacyModel);
                                                if (id != -1) {
                                                    initialiseStatus();
                                                } else {
                                                    showFailMessage();
                                                }

                                            } else {
                                                showFailMessage();
                                            }
                                            String ticks = jsonObject2.get("LastUpdatedTimeTicks").toString();
                                            userPreferences.setGetAllUserDetailsTimeticks(ticks);
                                        }
                                    } else {
                                        showFailMessage();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showFailMessage();
                                ppProgressbar.setVisibility(View.GONE);

                            }

                        }
                    };
                    post.execute();
                }

            }
        });
    }



    private void showFailMessage()
    {

        ppRequestMessage.setText("Hi.."+pharmacyModel.Name+" Your "+pharmacyModel.StoreName+" is verifying now. It will take few minutes..");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("tag","yes its called");
            initialiseStatus();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("pharmacy_processing"));

    }

}
