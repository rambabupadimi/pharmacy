package com.pharmacy.agent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.RobotoTextView;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.preferences.UserPreferences;

public class AgentProfieView extends AppCompatActivity {


    RobotoTextView
            agentName,
            agentEmail,
            agentAddress,
            agentLandmark,
            agentCity,
            agentState,
            agentPincode,
            agentDoorNumber;

    ImageView agentProfileImageView;
    UserPreferences userPreferences;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView agentPhoto,agentIdProof;
    CommonMethods commonMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_profie_view);

        initialiseObjects();
        initialiseIDs();
        initialiseToolbar();
        inflateProfileView();

    }


    private void initialiseObjects()
    {
        userPreferences = new UserPreferences(this);
        commonMethods   =   new CommonMethods();

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
        agentName           =   findViewById(R.id.a_pro_agent_name);
        agentEmail          =   findViewById(R.id.a_pro_agent_email);
        agentAddress        =   findViewById(R.id.a_pro_agent_address);
        agentLandmark       =   findViewById(R.id.a_pro_agent_landmark);
        agentCity           =   findViewById(R.id.a_pro_agent_city);
        agentState          =   findViewById(R.id.a_pro_agent_state);
        agentPincode        =   findViewById(R.id.a_pro_agent_pincode);
        agentDoorNumber     =   findViewById(R.id.a_pro_agent_door_number);
        agentPhoto          =   findViewById(R.id.a_pro_agent_photo);
        agentIdProof        =   findViewById(R.id.a_pro_agent_id_proof);

        agentProfileImageView   =   findViewById(R.id.a_pro_profile_image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent(this,AgentPharmacyListWithNavigation.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void inflateProfileView()
    {
        AgentDAO    agentDAO = new AgentDAO(this);
        final AgentModel  agentModel = agentDAO.getAgentData(userPreferences.getUserGid());
        if(agentModel!=null)
        {
            if(agentModel.Name.toString().trim().length()>0)
            {
                collapsingToolbarLayout.setTitle(agentModel.Name.toString());
                agentName.setText(agentModel.Name.toString());
            }
            else
            {
                agentName.setVisibility(View.GONE);
            }

            if(agentModel.Email.toString().trim().length()>0)
            {
                agentEmail.setText(agentModel.Email.toString());
            }
            else
            {
                agentEmail.setVisibility(View.GONE);
            }

             if(agentModel.Address.toString().trim().length()>0)
            {
                agentAddress.setText(agentModel.Address.toString());
            }
            else
            {
                agentAddress.setVisibility(View.GONE);
            }

            if(agentModel.LandMark.toString().trim().length()>0)
            {
                agentLandmark.setText(agentModel.LandMark.toString());
            }
            else
            {
                agentLandmark.setVisibility(View.GONE);
            }
            if(agentModel.City.toString().trim().length()>0)
            {
                agentCity.setText(agentModel.City.toString());
            }
            else
            {
                agentCity.setVisibility(View.GONE);
            }
            if(agentModel.State.toString().trim().length()>0)
            {
                agentState.setText(agentModel.State.toString());
            }
            else
            {
                agentState.setVisibility(View.GONE);
            }
            if(agentModel.Pincode.toString().trim().length()>0)
            {
                agentPincode.setText(agentModel.Pincode.toString());
            }
            else
            {
                agentPincode.setVisibility(View.GONE);
            }
            if(agentModel.DoorNo.toString().trim().length()>0)
            {
                agentDoorNumber.setText(agentModel.DoorNo.toString());
            }
            else
            {
                agentDoorNumber.setVisibility(View.GONE);
            }

            if(agentModel.ImageLocalPath!=null && agentModel.ImageLocalPath.trim().length()>0)
            {
                    Glide.with(this)
                            .load(agentModel.ImageLocalPath)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(agentProfileImageView);

                    Glide.with(this)
                            .load(agentModel.ImageLocalPath)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(agentPhoto);

            }
            else
            {
                if(agentModel.Image!=null) {
                    Glide.with(this)
                            .load(agentModel.Image)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(agentProfileImageView);

                    Glide.with(this)
                            .load(agentModel.Image)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(agentPhoto);

                }


            }

            if(agentModel.IdProofLocalPath!=null && agentModel.IdProofLocalPath.trim().length()>0)
            {
                try {
                    Glide.with(this)
                            .load(agentModel.IdProofLocalPath)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(agentIdProof);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                if(agentModel.IdProof!=null) {

                        Glide.with(this)
                                .load(agentModel.IdProof)
                                .placeholder(R.drawable.default_image)
                                .error(R.drawable.default_image)
                                .into(agentIdProof);

                }
            }


        }
    }
}
