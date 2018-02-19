package com.pharmacy.agent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.pharmacy.PharmacyProcessing;
import com.pharmacy.pharmacy.PharmacyRunningListWithNavigation;

public class AgentProcessing extends AppCompatActivity  implements AppConstants{

    Button ppGotoHomePage;
    CommonMethods commonMethods;

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
    }


    private void intialiseObjects(){
        commonMethods   =   new CommonMethods();
    }
    private void initialiseIDs(){
        ppGotoHomePage      =   findViewById(R.id.ap_go_to_homepage);
    }

    private void initialiseClickListeners()
    {
        ppGotoHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgentProcessing.this,AgentPharmacyListWithNavigation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
