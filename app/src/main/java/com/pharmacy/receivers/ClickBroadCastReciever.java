package com.pharmacy.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pharmacy.R;
import com.pharmacy.agent.AgentPharmacyListWithNavigation;
import com.pharmacy.pharmacy.PharmacyRunningListWithNavigation;
import com.pharmacy.preferences.UserPreferences;

/**
 * Created by PCCS-0007 on 26-Feb-18.
 */

public class ClickBroadCastReciever extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        Intent resultIntent;
        UserPreferences userPreferences = new UserPreferences(context);
        if(userPreferences.getUserLoginType().toString().equalsIgnoreCase(context.getString(R.string.agent)))
        {
             resultIntent = new Intent(context, AgentPharmacyListWithNavigation.class);

        }
        else
        {
            resultIntent = new Intent(context, PharmacyRunningListWithNavigation.class);

        }
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(resultIntent);

    }
}
