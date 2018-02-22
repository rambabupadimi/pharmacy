package com.pharmacy.agent;

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
import android.widget.Toast;

import com.google.gson.Gson;
import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.models.GetUserDetailsRequest;
import com.pharmacy.operations.Post;
import com.pharmacy.pharmacy.PharmacyProcessing;
import com.pharmacy.pharmacy.PharmacyRunningListWithNavigation;
import com.pharmacy.preferences.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class AgentProcessing extends AppCompatActivity  implements AppConstants{

    Button ppGotoHomePage;
    CommonMethods commonMethods;

    TextView apRequestMessage;
    ProgressBar apProgressbar;
    Button apRefresh;
    Gson gson;
    UserPreferences userPreferences;
    UserDAO userDAO;
    AgentDAO agentDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_processing);
        intialiseObjects();
        initialiseIDs();
        initialiseClickListeners();
        initialiseStatus();
    }


    private void initialiseStatus()
    {
        commonMethods.maintainState(this,USER_VERIFIED_STATUS);

        AgentModel agentModel = agentDAO.getAgentData(userPreferences.getUserGid());
        if(agentModel.IsApproved)
        {
            apRequestMessage.setText("You are verified now, Click button to go Homepage");
            ppGotoHomePage.setVisibility(View.VISIBLE);
            apRefresh.setVisibility(View.GONE);
        }
        else
        {
            apRequestMessage.setText("Admin not yet approved");
            apRefresh.setVisibility(View.VISIBLE);
            ppGotoHomePage.setVisibility(View.GONE);
        }
    }


    private void intialiseObjects(){
        commonMethods   =   new CommonMethods();
        userPreferences =   new UserPreferences(this);
        gson            =   new Gson();
        userDAO         =   new UserDAO(this);
        agentDAO        =   new AgentDAO(this);
    }

    private void initialiseIDs(){
        ppGotoHomePage      =   findViewById(R.id.ap_go_to_homepage);
        apProgressbar       =   findViewById(R.id.request_processing_progressbar);
        apRefresh           =   findViewById(R.id.request_processing_refresh);
        apRequestMessage    =   findViewById(R.id.request_processing_message);
    }

    private void initialiseClickListeners()
    {
        ppGotoHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgentProcessing.this,AgentPharmacyListWithNavigation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        apRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel userModel = userDAO.getUserData(userPreferences.getUserGid());
                GetUserDetailsRequest getUserDetailsRequest = new GetUserDetailsRequest();
                getUserDetailsRequest.UserID    =   userPreferences.getUserGid();
                getUserDetailsRequest.UserType  =   getString(R.string.agent);
                getUserDetailsRequest.DistributorID =   userModel.DistributorID;
                getUserDetailsRequest.LastUpdatedTimeTicks = userPreferences.getGetAllUserDetailsTimeticks();
                String json = gson.toJson(getUserDetailsRequest);
                apProgressbar.setVisibility(View.VISIBLE);
                Post post = new Post(AgentProcessing.this,CommonMethods.GET_USER_DETAILS,json) {
                    @Override
                    public void onResponseReceived(String result) {
                        if(result!=null)
                        {
                            apProgressbar.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject1 = new JSONObject(result);
                                if(jsonObject1.get("Status").toString().equalsIgnoreCase("Success"))
                                {
                                    if(jsonObject1.get("Response")!=null) {
                                        JSONObject jsonObject2 = jsonObject1.getJSONObject("Response");
                                        if(jsonObject2.get("UserDetails")!=null) {
                                            AgentModel agentModel = gson.fromJson(jsonObject2.get("UserDetails").toString(), AgentModel.class);
                                            Long id = commonMethods.renderLoginDataForAgent(AgentProcessing.this, agentModel);
                                            if(id!=-1)
                                            {
                                                initialiseStatus();
                                            }
                                            else
                                            {
                                                showFailMessage();
                                            }

                                        }
                                        else
                                        {
                                            showFailMessage();
                                        }
                                        String ticks = jsonObject2.get("LastUpdatedTimeTicks").toString();
                                        userPreferences.setGetAllUserDetailsTimeticks(ticks);
                                    }
                                }
                                else
                                {
                                    showFailMessage();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            showFailMessage();
                            apProgressbar.setVisibility(View.GONE);

                        }

                    }
                };
                post.execute();

            }
        });
    }

    private void showFailMessage()
    {
        apRequestMessage.setText("Admin not yet approved");
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
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("agent_register_processing"));

    }


}
