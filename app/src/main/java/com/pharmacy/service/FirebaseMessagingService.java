package com.pharmacy.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.agent.AgentPharmacyListWithNavigation;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.models.GetUserDetailsRequest;
import com.pharmacy.operations.Post;
import com.pharmacy.preferences.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PCCS-0007 on 20-Feb-18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
CommonMethods commonMethods;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        commonMethods = new CommonMethods();


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            String messageBody = remoteMessage.getData().get("MessageBody");
            String messageType = remoteMessage.getData().get("MessageType");
            handleNotification(messageType);
        }
    }



    private void handleNotification(final String message)
    {




         /*   Log.i(TAG,"message is "+message);
           long id = commonMethods.updateAgentData(this);
// type agent*/


        final Gson gson = new Gson();
        String json = commonMethods.getAgentRequestData(this,getString(R.string.agent));
        final UserPreferences userPreferences =new UserPreferences(this);

        Post post = new Post(this, CommonMethods.GET_USER_DETAILS,json) {
            @Override
            public void onResponseReceived(String result) {
                if(result!=null)
                {
                    try {
                        JSONObject jsonObject1 = new JSONObject(result);
                        if(jsonObject1.get("Status").toString().equalsIgnoreCase("Success"))
                        {
                            if(jsonObject1.get("Response")!=null) {
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("Response");

                                try {
                                    if (jsonObject2.get("UserDetails") != null) {
                                        AgentModel agentModel = gson.fromJson(jsonObject2.get("UserDetails").toString(), AgentModel.class);
                                        long id = commonMethods.renderLoginDataForAgent(FirebaseMessagingService.this, agentModel);
                                        if(id!=-1)
                                        {
                                            callBroadcast(message);
                                        }
                                    }
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                String ticks = jsonObject2.get("LastUpdatedTimeTicks").toString();
                                userPreferences.setGetAllUserDetailsTimeticks(ticks);

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        post.execute();

    }


    private void callBroadcast(String message)
    {
        if (message.toString().equalsIgnoreCase("OrderUpdate")) {


            Intent intent = new Intent("product_status_running");
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);

            Intent intent1 = new Intent("product_status_approved");
            LocalBroadcastManager localBroadcastManager1 = LocalBroadcastManager.getInstance(this);
            localBroadcastManager1.sendBroadcast(intent1);

            Intent intent2 = new Intent("product_status_delivered");
            LocalBroadcastManager localBroadcastManager2 = LocalBroadcastManager.getInstance(this);
            localBroadcastManager2.sendBroadcast(intent2);

        }
        else if(message.toString().equalsIgnoreCase("AgentUpdate"))
        {
            Intent intent5 = new Intent("agent_register_processing");
            LocalBroadcastManager localBroadcastManager5 = LocalBroadcastManager.getInstance(this);
            localBroadcastManager5.sendBroadcast(intent5);

        }
        else if(message.toString().equalsIgnoreCase("PharmacyUpdate")) {
            Intent intent4 = new Intent("add_new_pharmacy");
            LocalBroadcastManager localBroadcastManager4 = LocalBroadcastManager.getInstance(this);
            localBroadcastManager4.sendBroadcast(intent4);
        }

    }

    private void handleDataMessage(JSONObject jsonObject)
    {
        Log.i(TAG,"message json is "+jsonObject);
    }


}

