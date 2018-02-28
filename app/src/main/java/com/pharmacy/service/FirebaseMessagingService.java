package com.pharmacy.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.agent.AgentPharmacyListWithNavigation;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.models.GetUserDetailsRequest;
import com.pharmacy.operations.Post;
import com.pharmacy.preferences.UserPreferences;
import com.pharmacy.receivers.ClickBroadCastReciever;
import com.pharmacy.receivers.SwipeBroadCastReciever;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PCCS-0007 on 20-Feb-18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
CommonMethods commonMethods;
    NotificationCompat.Builder builder;

    private NotificationManager mNotificationManager;
     UserPreferences userPreferences;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        commonMethods = new CommonMethods();
            userPreferences =new UserPreferences(this);

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
        if(userPreferences.getUserLoginType().toString().equalsIgnoreCase(getString(R.string.agent))) {
            String json = commonMethods.getAgentRequestData(this, getString(R.string.agent));

            Post post = new Post(this, CommonMethods.GET_USER_DETAILS, json) {
                @Override
                public void onResponseReceived(String result) {
                    if (result != null) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(result);
                            if (jsonObject1.get("Status").toString().equalsIgnoreCase("Success")) {
                                if (jsonObject1.get("Response") != null) {
                                    JSONObject jsonObject2 = jsonObject1.getJSONObject("Response");

                                    try {
                                        if (jsonObject2.get("UserDetails") != null) {
                                            AgentModel agentModel = gson.fromJson(jsonObject2.get("UserDetails").toString(), AgentModel.class);
                                            long id = commonMethods.renderLoginDataForAgent(FirebaseMessagingService.this, agentModel);
                                            if (id != -1) {
                                                callBroadcast(message);
                                            }
                                        }
                                    } catch (Exception e) {
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
        else
        {
            String json = commonMethods.getAgentRequestData(this, getString(R.string.pharmacy));

            Post post = new Post(this, CommonMethods.GET_USER_DETAILS, json) {
                @Override
                public void onResponseReceived(String result) {
                    if (result != null) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(result);
                            if (jsonObject1.get(getString(R.string.status)).toString().equalsIgnoreCase(getString(R.string.success))) {
                                if (jsonObject1.get(getString(R.string.response)) != null) {
                                    JSONObject jsonObject2 = jsonObject1.getJSONObject(getString(R.string.response));

                                    try {
                                        if (jsonObject2.get(getString(R.string.userdetails)) != null) {
                                            PharmacyModel pharmacyModel = gson.fromJson(jsonObject2.get(getString(R.string.userdetails)).toString(), PharmacyModel.class);
                                            long id = commonMethods.renderLoginDataForPharmacy(FirebaseMessagingService.this, pharmacyModel);
                                            if (id != -1) {
                                                callBroadcast(message);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    String ticks = jsonObject2.get(getString(R.string.LastUpdatedTimeTicks)).toString();
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
    }


    private void callBroadcast(String message)
    {
        String value="";


        if(userPreferences.getUserLoginType().equalsIgnoreCase(getString(R.string.agent))) {
            if (message.toString().equalsIgnoreCase("OrderUpdate")) {

                value = "Product";
                //showNotification(this,value);

                Intent intent = new Intent("product_status_running");
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent);

                Intent intent1 = new Intent("product_status_approved");
                LocalBroadcastManager localBroadcastManager1 = LocalBroadcastManager.getInstance(this);
                localBroadcastManager1.sendBroadcast(intent1);

                Intent intent2 = new Intent("product_status_delivered");
                LocalBroadcastManager localBroadcastManager2 = LocalBroadcastManager.getInstance(this);
                localBroadcastManager2.sendBroadcast(intent2);


            } else if (message.toString().equalsIgnoreCase("AgentUpdate")) {
                Intent intent5 = new Intent("agent_register_processing");
                LocalBroadcastManager localBroadcastManager5 = LocalBroadcastManager.getInstance(this);
                localBroadcastManager5.sendBroadcast(intent5);
            } else if (message.toString().equalsIgnoreCase("PharmacyUpdate")) {
                value = "Pharmacy";
                //   showNotification(this,value);
                Intent intent4 = new Intent("add_new_pharmacy");
                LocalBroadcastManager localBroadcastManager4 = LocalBroadcastManager.getInstance(this);
                localBroadcastManager4.sendBroadcast(intent4);



            }
        }
        else
        {
            if (message.toString().equalsIgnoreCase("OrderUpdate")) {

                value = "Product";
                //showNotification(this,value);

                Intent intent = new Intent("product_status_running");
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent);

                Intent intent1 = new Intent("product_status_approved");
                LocalBroadcastManager localBroadcastManager1 = LocalBroadcastManager.getInstance(this);
                localBroadcastManager1.sendBroadcast(intent1);

                Intent intent2 = new Intent("product_status_delivered");
                LocalBroadcastManager localBroadcastManager2 = LocalBroadcastManager.getInstance(this);
                localBroadcastManager2.sendBroadcast(intent2);


            } else if (message.toString().equalsIgnoreCase("AgentUpdate")) {

            } else if (message.toString().equalsIgnoreCase("PharmacyUpdate")) {
                value = "Pharmacy";
                //   showNotification(this,value);

                Intent intent5 = new Intent("pharmacy_processing");
                LocalBroadcastManager localBroadcastManager5 = LocalBroadcastManager.getInstance(this);
                localBroadcastManager5.sendBroadcast(intent5);

            }

        }

    }






    public void showNotification(Context context,String type) {
      PendingIntent pendingClickIntent = null, pendingSwipeIntent = null;
        Intent swipeIntent = null, clickIntent = null;
        Uri notificationSound = null;
        NotificationCompat.InboxStyle multiNotificationStyle;
        Gson gson = new Gson();
        try {
            notificationSound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.chatringtone);
            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            builder = new android.support.v7.app.NotificationCompat.Builder(this);

                if (!CommonMethods.isAppIsInBackground(this) || !isScreenOn())
                    builder.setSound(notificationSound);
                else
                    builder.setSound(null);

                    String activityName = null;
                    multiNotificationStyle = new NotificationCompat.InboxStyle();


            swipeIntent = new Intent(this, SwipeBroadCastReciever.class);

            clickIntent = new Intent(this, ClickBroadCastReciever.class);
            clickIntent.putExtra("ActivityGuid", type);

            pendingSwipeIntent = PendingIntent.getBroadcast(this.getApplicationContext(),
                    0, swipeIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            pendingClickIntent = PendingIntent.getBroadcast(this.getApplicationContext(),
                    0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    if (StringUtils.isBlank(activityName))
                        activityName = getString(R.string.app_name);

                    ReformatMessages(context,type, multiNotificationStyle, activityName, pendingSwipeIntent, pendingClickIntent, false);




        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }











    private void ReformatMessages(Context context,String message, NotificationCompat.InboxStyle multiNotificationStyle,
                                  String activityName, PendingIntent pendingSwipeIntent, PendingIntent pendingClickIntent, boolean isMultiActivity) {
        String newMsg = "";
        int limit = 0, overAllNotificationCount = 0;
        try {

            multiNotificationStyle.setBigContentTitle(activityName);

            newMsg = "Your "+message+" updated";

            if (overAllNotificationCount > 0) {

                if (!StringUtils.isBlank(activityName)) {
                    builder.setVibrate(new long[]{500, 100, 500, 100, 500})
                            // Each element then alternates between delay, vibrate, sleep, vibrate, sleep
                            .setContentTitle(activityName)
                            .setContentText(overAllNotificationCount + newMsg)
                            .setSmallIcon(R.drawable.zibmi_small_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.zibmiicon)).setAutoCancel(true)
                            //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.zibmiicon))
                            .setDeleteIntent(pendingSwipeIntent)
                            .setContentIntent(pendingClickIntent)
                            .setPriority(Notification.PRIORITY_HIGH);

                } else {
                    builder.setVibrate(new long[]{500, 100, 500, 100, 500})
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(overAllNotificationCount + newMsg)
                            .setSmallIcon(R.drawable.zibmi_small_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.zibmiicon)).setAutoCancel(true)
                            .setDeleteIntent(pendingSwipeIntent)
                            .setContentIntent(pendingClickIntent)
                            .setPriority(Notification.PRIORITY_HIGH);
                }
                multiNotificationStyle.setSummaryText(overAllNotificationCount + newMsg);
                builder.setStyle(multiNotificationStyle);
                mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, builder.build());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }





    private boolean isScreenOn() {
        boolean isOn = false;
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                isOn = pm.isInteractive();
            } else {
                isOn = pm.isScreenOn();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isOn;
    }


}

