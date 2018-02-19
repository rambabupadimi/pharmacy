package com.pharmacy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import com.pharmacy.agent.AgentPharmacyListWithNavigation;
import com.pharmacy.agent.AgentProcessing;
import com.pharmacy.agent.AgentRegistration;
import com.pharmacy.pharmacy.PharmacyProcessing;
import com.pharmacy.pharmacy.PharmacyRegistration;
import com.pharmacy.pharmacy.PharmacyRunningListWithNavigation;
import com.pharmacy.preferences.UserPreferences;

public class SplashActivity extends AppCompatActivity {

    UserPreferences userPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        userPreferences = new UserPreferences(this);
        startAnimating();

    }


    private void startAnimating() {

        // Load animations for all views within the TableLayout
        Animation spinin = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        LayoutAnimationController controller = new LayoutAnimationController(
                spinin);
        RelativeLayout table = (RelativeLayout) findViewById(R.id.Splashlayout);
        table.setLayoutAnimation(controller);
        // Transition to Main Menu when bottom title finishes animating
        spinin.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent=null;
                if(userPreferences.getUserLoginType().toString().equalsIgnoreCase(getString(R.string.agent))) {
                    if (userPreferences.getUserHomePageStatus() == true) {
                       intent   =   new Intent(SplashActivity.this,AgentPharmacyListWithNavigation.class);
                    } else if (userPreferences.getUserBasicRegistrationStatus() == true) {
                        intent   =   new Intent(SplashActivity.this,AgentRegistration.class);
                    } else if (userPreferences.getUserProcessingStatus() == true) {
                        intent   =   new Intent(SplashActivity.this,AgentProcessing.class);
                    }
                    else{
                        intent   =   new Intent(SplashActivity.this,SignupOrLoginActivity.class);
                    }
                }
                else if(userPreferences.getUserLoginType().toString().equalsIgnoreCase(getString(R.string.pharmacy)))
                {
                    if (userPreferences.getUserHomePageStatus() == true) {
                        intent   =   new Intent(SplashActivity.this,PharmacyRunningListWithNavigation.class);
                    } else if (userPreferences.getUserBasicRegistrationStatus() == true) {
                        intent   =   new Intent(SplashActivity.this,PharmacyRegistration.class);

                    } else if (userPreferences.getUserProcessingStatus() == true) {
                        intent   =   new Intent(SplashActivity.this,PharmacyProcessing.class);
                    }
                    else
                    {
                        intent   =   new Intent(SplashActivity.this,SignupOrLoginActivity.class);

                    }
                }
                else
                {
                    intent   =   new Intent(SplashActivity.this,SignupOrLoginActivity.class);


                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                SplashActivity.this.finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }
}
