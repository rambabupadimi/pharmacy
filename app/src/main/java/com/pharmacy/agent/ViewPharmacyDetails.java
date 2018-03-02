package com.pharmacy.agent;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.RobotoTextView;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.pharmacy.PharmacyRegistration;
import com.pharmacy.preferences.UserPreferences;

public class ViewPharmacyDetails extends AppCompatActivity {



    RobotoTextView  pharmacyName,
            pharmacyOwnerName,
            pharmacyEmail,
            pharmacyAddress,
            pharmacyLandmark,
            pharmacyCity,
            pharmacyState,
            pharmacyPincode,
            pharmacyDoorNumber,
            pharmacyPhoneNumber,
            edit;

    ImageView pharmacyLicencePhoto,
            pharmacyRegisterPhoto,
            pharmacyProfileImage;

    TextView pharmacyHeading;
    PharmacyModel pharmacyModel;
    Gson gson;
    Toolbar toolbar;
    PharmacyDAO pharmacyDAO;
    UserPreferences userPreferences;
    LinearLayout pharmacyPhoneLayout;
    View        pharmacyPhoneView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pharmacy_details);
        initialiseObjects();
        initialiseData();
        initialiseIDs();
        inflateProfileView();
        initialiseBackButton();
        initialiseClickListeners();
    }



    private void initialiseClickListeners()
    {
        edit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPharmacyDetails.this,EditPharmacyProfileView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ViewPharmacyDetails.this, R.anim.next_swipe2, R.anim.next_swipe1).toBundle();
                startActivity(intent,bndlanimation);
            }
        });
    }


    private void initialiseBackButton()
    {
        setSupportActionBar(toolbar);
        CommonMethods.setToolbar(this,getSupportActionBar());
    }


    private void initialiseObjects()
    {
        gson    =   new Gson();
        pharmacyDAO = new PharmacyDAO(this);
        userPreferences =   new UserPreferences(this);
    }

    private void initialiseData()
    {

        pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAgentSelectedLocalPharmacyId());
    }
    private void initialiseIDs()
    {

        toolbar             =   findViewById(R.id.toolbar);
        pharmacyName        =   findViewById(R.id.p_pro_pharmacy_name);
        pharmacyOwnerName   =   findViewById(R.id.p_pro_pharmacy_owner_name);
        pharmacyEmail       =   findViewById(R.id.p_pro_pharmacy_email);
        pharmacyAddress     =   findViewById(R.id.p_pro_pharmacy_address);
        pharmacyLandmark    =   findViewById(R.id.p_pro_pharmacy_landmark);
        pharmacyCity        =   findViewById(R.id.p_pro_pharmacy_city);
        pharmacyState       =   findViewById(R.id.p_pro_pharmacy_state);
        pharmacyPincode     =   findViewById(R.id.p_pro_pharmacy_pincode);
        pharmacyDoorNumber  =   findViewById(R.id.p_pro_pharmacy_door_number);

        pharmacyLicencePhoto     =   findViewById(R.id.p_pro_licence_photo);
        pharmacyRegisterPhoto    =   findViewById(R.id.p_pro_register_photo);

        pharmacyHeading     =   findViewById(R.id.toolbar_heading);

        pharmacyProfileImage    =   findViewById(R.id.p_pro_pharmacy_photo);
        edit                =   findViewById(R.id.toolbar_edit);

        pharmacyPhoneNumber =   findViewById(R.id.p_pro_pharmacy_phone_number);
        pharmacyPhoneLayout =   findViewById(R.id.p_pro_pharmacy_phone_layout);
        pharmacyPhoneView   =   findViewById(R.id.p_pro_pharmacy_phone_layout_view);

        pharmacyPhoneLayout.setVisibility(View.VISIBLE);
        pharmacyPhoneView.setVisibility(View.VISIBLE);
    }


    private void inflateProfileView()
    {
         if(pharmacyModel!=null)
        {

            if(pharmacyModel.PhoneNumber.toString().trim().length()>0)
            {
                pharmacyPhoneNumber.setText(pharmacyModel.PhoneNumber.toString());
            }
            else
            {
                pharmacyPhoneNumber.setVisibility(View.GONE);
            }

            if(pharmacyModel.StoreName.toString().trim().length()>0)
            {

                pharmacyName.setText(pharmacyModel.StoreName.toString());
                pharmacyHeading.setText(pharmacyModel.StoreName.toString());
            }
            else
            {
                pharmacyName.setVisibility(View.GONE);
            }

            if(pharmacyModel.Name.toString().trim().length()>0)
            {
                pharmacyOwnerName.setText(pharmacyModel.Name.toString());
            }
            else
            {
                pharmacyOwnerName.setVisibility(View.GONE);
            }

            if(pharmacyModel.Email.toString().trim().length()>0)
            {
                pharmacyEmail.setText(pharmacyModel.Email.toString());
            }
            else
            {
                pharmacyEmail.setVisibility(View.GONE);
            }

            if(pharmacyModel.Address.toString().trim().length()>0)
            {
                pharmacyAddress.setText(pharmacyModel.Address.toString());
            }
            else
            {
                pharmacyAddress.setVisibility(View.GONE);
            }

            if(pharmacyModel.LandMark.toString().trim().length()>0)
            {
                pharmacyLandmark.setText(pharmacyModel.LandMark.toString());
            }
            else
            {
                pharmacyLandmark.setVisibility(View.GONE);
            }
            if(pharmacyModel.City.toString().trim().length()>0)
            {
                pharmacyCity.setText(pharmacyModel.City.toString());
            }
            else
            {
                pharmacyCity.setVisibility(View.GONE);
            }
            if(pharmacyModel.State.toString().trim().length()>0)
            {
                pharmacyState.setText(pharmacyModel.State.toString());
            }
            else
            {
                pharmacyState.setVisibility(View.GONE);
            }
            if(pharmacyModel.Pincode.toString().trim().length()>0)
            {
                pharmacyPincode.setText(pharmacyModel.Pincode.toString());
            }
            else
            {
                pharmacyPincode.setVisibility(View.GONE);
            }
            if(pharmacyModel.DoorNo.toString().trim().length()>0)
            {
                pharmacyDoorNumber.setText(pharmacyModel.DoorNo.toString());
            }
            else
            {
                pharmacyDoorNumber.setVisibility(View.GONE);
            }

            if(pharmacyModel.ImageLocalPath!=null)
            {
                     Glide.with(this)
                            .load(pharmacyModel.ImageLocalPath)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(pharmacyProfileImage);
                } else {
                    Glide.with(this)
                            .load(pharmacyModel.Image)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(pharmacyProfileImage);
                }



            try {
                if (pharmacyModel.LicenceLocalPath != null && pharmacyModel.LicenceLocalPath.length()>0) {
                    Glide.with(this)
                            .load(pharmacyModel.LicenceLocalPath)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(pharmacyLicencePhoto);
                } else {
                    Glide.with(this)
                            .load(pharmacyModel.Licence)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(pharmacyLicencePhoto);
                }

                if (pharmacyModel.BillingLocalPath != null && pharmacyModel.BillingLocalPath.length()>0) {
                    Glide.with(this)
                            .load(pharmacyModel.BillingLocalPath)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(pharmacyRegisterPhoto);

                } else {
                    Glide.with(this)
                            .load(pharmacyModel.Billing)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(pharmacyRegisterPhoto);
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }


        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewPharmacyDetails.this,AgentRunningList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ViewPharmacyDetails.this, R.anim.next_swipe2, R.anim.next_swipe1).toBundle();
        startActivity(intent,bndlanimation);
    }

}

