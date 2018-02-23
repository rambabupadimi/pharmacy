package com.pharmacy.agent.adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pharmacy.R;
import com.pharmacy.agent.AgentPharmacyListWithNavigation;
import com.pharmacy.agent.AgentRunningList;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.preferences.UserPreferences;

import java.util.ArrayList;

/**
 * Created by PCCS-0007 on 06-Feb-18.
 */



public class AgentPharmacyListAdapter extends RecyclerView.Adapter<AgentPharmacyListAdapter.MyViewHolder> {

    Context context;
    ArrayList<PharmacyModel> pharmacyModelArrayList;
    Gson gson;
    UserPreferences userPreferences;
    public AgentPharmacyListAdapter(Context context, ArrayList<PharmacyModel> pharmacyModelArrayList) {

        this.context    =   context;
        this.pharmacyModelArrayList =   pharmacyModelArrayList;
        gson    = new Gson();
        userPreferences = new UserPreferences(context);
    }

    @Override
    public AgentPharmacyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_agent_pharmacy_list, parent, false);

        return new AgentPharmacyListAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView aplAdapterName;
        LinearLayout aplParentLayout;
        ImageView approvedIcon;
        public MyViewHolder(View view) {
            super(view);
            aplAdapterName   =   view.findViewById(R.id.apl_adapter_name);
            aplParentLayout  =   view.findViewById(R.id.apl_parent_layout);
            approvedIcon     =  view.findViewById(R.id.apl_approved_icon);
        }

    }


    @Override
    public void onBindViewHolder(final AgentPharmacyListAdapter.MyViewHolder holder, final int position) {
        final PharmacyModel pharmacyModel = pharmacyModelArrayList.get(position);
        holder.aplAdapterName.setText(pharmacyModel.StoreName);
        holder.aplParentLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(pharmacyModel.IsApproved) {
                    Intent intent = new Intent(context, AgentRunningList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(context, R.anim.next_swipe2, R.anim.next_swipe1).toBundle();

                    userPreferences.setAgentSelectedLocalPharmacyId(pharmacyModel.PharmacyLocalId);
                    context.startActivity(intent,bndlanimation);
                }
                else
                {
                    Toast.makeText(context,"Admin needs to approve",Toast.LENGTH_LONG).show();
                }
            }
        });

        if (pharmacyModel.IsApproved)
        {
            holder.approvedIcon.setVisibility(View.GONE);
        }
        else
        {
            holder.approvedIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (pharmacyModelArrayList != null) ? pharmacyModelArrayList.size() : 0;
    }



}