package com.pharmacy.agent.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.R;
import com.pharmacy.adapters.SearchProductListAdapter;
import com.pharmacy.agent.adapters.AgentCommonListAdapter;
import com.pharmacy.db.daos.OrderDAO;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.preferences.UserPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by PCCS-0007 on 06-Feb-18.
 */

public class AgentApprovedListFragment  extends Fragment{

    ArrayList<OrderModel> approvedList = new ArrayList<>();
    RecyclerView approvedListRecyclerView;
    AgentCommonListAdapter agentCommonListAdapter;
    Gson gson;
    UserPreferences userPreferences;
    LinearLayout notFoundLayout;
    TextView notFoundText;
    ImageView notFoundIcon;
    public AgentApprovedListFragment()
    {
        agentCommonListAdapter  = new AgentCommonListAdapter(getContext(),approvedList);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_agent_approved_list, container, false);
        initialiseObjects();
        initialiseIDs(view);

       return view;
    }

    private void initialiseObjects()
    {
        gson    =   new Gson();
        userPreferences =   new UserPreferences(getContext());
    }

    private void initialiseIDs(View view)
    {
        approvedListRecyclerView =   view.findViewById(R.id.agent_approved_list_recyclerview);

        notFoundLayout  =   view.findViewById(R.id.not_found_layout);
        notFoundText    =   view.findViewById(R.id.not_found_text);
        notFoundIcon    =   view.findViewById(R.id.not_found_icon);
    }


    private void inflateData()
    {

        String pharmacyLocalId =  userPreferences.getAgentSelectedPharmacyId();
        OrderDAO orderDAO = new OrderDAO(getContext());
        List<OrderModel> orderModelList =orderDAO.getOrderData("approved",pharmacyLocalId);
         if(orderModelList!=null && orderModelList.size()>0) {
            notFoundLayout.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            approvedListRecyclerView.setLayoutManager(linearLayoutManager);
            Collections.reverse(orderModelList);
            approvedList.clear();
            approvedList.addAll(orderModelList);
            approvedListRecyclerView.setAdapter(agentCommonListAdapter);
            agentCommonListAdapter.notifyDataSetChanged();
        }
        else
        {
            notFoundLayout.setVisibility(View.VISIBLE);
            notFoundText.setText("NO APPROVED PRODUCTS");
            notFoundIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.approved_icon));
        }

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("tag","yes its called");
            inflateData();

        }
    };


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onResume() {
        super.onResume();
        inflateData();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,new IntentFilter("product_status_approved"));

    }

}
