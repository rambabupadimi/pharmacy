package com.pharmacy.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.agent.AgentPharmacyListWithNavigation;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.models.GetUserDetailsRequest;
import com.pharmacy.operations.Post;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PCCS-0007 on 20-Feb-18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message)
    {
            Log.i(TAG,"message is "+message);

// type agent

            Intent intent = new Intent("product_status_running");
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);

            Intent intent1 = new Intent("product_status_approved");
            LocalBroadcastManager localBroadcastManager1 = LocalBroadcastManager.getInstance(this);
            localBroadcastManager1.sendBroadcast(intent1);

            Intent intent2 = new Intent("product_status_delivered");
            LocalBroadcastManager localBroadcastManager2 = LocalBroadcastManager.getInstance(this);
            localBroadcastManager2.sendBroadcast(intent2);

            Intent intent4 = new Intent("add_new_pharmacy");
            LocalBroadcastManager localBroadcastManager4 = LocalBroadcastManager.getInstance(this);
            localBroadcastManager4.sendBroadcast(intent4);

            Intent intent5 = new Intent("agent_register_processing");
            LocalBroadcastManager localBroadcastManager5 = LocalBroadcastManager.getInstance(this);
            localBroadcastManager5.sendBroadcast(intent5);

// type pharmacy



    }

    private void handleDataMessage(JSONObject jsonObject)
    {
        Log.i(TAG,"message json is "+jsonObject);
    }


}

