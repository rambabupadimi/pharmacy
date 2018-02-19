package com.pharmacy.pharmacy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;

public class PharmacyProcessing extends AppCompatActivity implements AppConstants{

    Button ppGotoHomePage;
    CommonMethods commonMethods;
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
    }

    private void initialiseStatus()
    {
        commonMethods.maintainState(this,USER_VERIFIED_STATUS);
    }


    private void initialiseIDs(){
        ppGotoHomePage      =   findViewById(R.id.pp_go_to_homepage);
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
