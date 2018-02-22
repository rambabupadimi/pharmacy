package com.pharmacy.pharmacy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.pharmacy.R;
import com.pharmacy.RobotoTextView;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.preferences.UserPreferences;

public class PharmacyProfileView extends AppCompatActivity {


    RobotoTextView  pharmacyName,
                    pharmacyOwnerName,
                    pharmacyEmail,
                    pharmacyAddress,
                    pharmacyLandmark,
                    pharmacyCity,
                    pharmacyState,
                    pharmacyPincode,
                    pharmacyDoorNumber;

    ImageView       pharmacyLicencePhoto,
                    pharmacyRegisterPhoto,
                    pharmacyProfileImage;

    LinearLayout    pharmacyProfileViewLayout;
    UserPreferences userPreferences;
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_profile_view);

        initialiseObjects();
        initialiseIDs();
        initialiseToolbar();
        inflateProfileView();
    }


    private void initialiseObjects()
    {
        userPreferences = new UserPreferences(this);
    }

    private void initialiseToolbar()
    {
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
    }

    private void initialiseIDs()
    {
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

        pharmacyProfileImage    =   findViewById(R.id.p_pro_profile_image);

        pharmacyProfileViewLayout   =   findViewById(R.id.p_pro_pharmacy_photo_layout);
        pharmacyProfileViewLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent(this,PharmacyRunningListWithNavigation.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void inflateProfileView()
    {
        PharmacyDAO pharmacyDAO = new PharmacyDAO(this);
        PharmacyModel   pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getPharmacyRegisterLocalUserId());
        if(pharmacyModel!=null)
        {

            if(pharmacyModel.StoreName.toString().trim().length()>0)
            {
                collapsingToolbarLayout.setTitle(pharmacyModel.StoreName.toString());
                pharmacyName.setText(pharmacyModel.StoreName.toString());
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

            if(pharmacyModel.ImageLocalPath!=null && pharmacyModel.ImageLocalPath.toString().trim().length()>0)
            {
                Glide.with(this)
                        .load(pharmacyModel.ImageLocalPath)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(pharmacyProfileImage);
            }
            else
            {
                if(pharmacyModel.Image!=null) {
                    Glide.with(this)
                            .load(pharmacyModel.Image)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(pharmacyProfileImage);
                }
            }


            if(pharmacyModel.LicenceLocalPath!=null && pharmacyModel.LicenceLocalPath.toString().trim().length()>0)
            {
                Glide.with(this)
                        .load(pharmacyModel.LicenceLocalPath)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(pharmacyLicencePhoto);

            }
            else
            {
                Glide.with(this)
                        .load(pharmacyModel.Licence)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(pharmacyLicencePhoto);
            }

            if(pharmacyModel.BillingLocalPath!=null && pharmacyModel.BillingLocalPath.trim().length()>0)
            {
                Glide.with(this)
                        .load(pharmacyModel.BillingLocalPath)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(pharmacyRegisterPhoto);
            }
            else
            {
                Glide.with(this)
                        .load(pharmacyModel.Billing)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(pharmacyRegisterPhoto);

            }




        }
    }
}
