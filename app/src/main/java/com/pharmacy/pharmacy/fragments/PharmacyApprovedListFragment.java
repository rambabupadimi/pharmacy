package com.pharmacy.pharmacy.fragments;


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
import com.pharmacy.db.daos.OrderDAO;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.pharmacy.adapters.PharmacyCommonListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by PCCS-0007 on 05-Feb-18.
 */

public class PharmacyApprovedListFragment extends Fragment {

    PharmacyCommonListAdapter pharmacyCommonListAdapter;
    ArrayList<OrderModel> approvedList = new ArrayList<>();
    RecyclerView approvedListRecyclerView;
    Gson gson;
    LinearLayout notFoundLayout;
    TextView notFoundText;
    ImageView notFoundIcon;

    public PharmacyApprovedListFragment()
    {
        pharmacyCommonListAdapter = new PharmacyCommonListAdapter(getContext(),approvedList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_approved_list, container, false);

        initialiseIDs(view);
        initialiseObjects();
        return view;
    }

    private void initialiseIDs(View view)
    {
        approvedListRecyclerView  =  view.findViewById(R.id.pharmacy_approved_list_recyclerview);
        notFoundLayout  =   view.findViewById(R.id.not_found_layout);
        notFoundText    =   view.findViewById(R.id.not_found_text);
        notFoundIcon    =   view.findViewById(R.id.not_found_icon);
    }

    private void initialiseObjects()
    {
        gson = new Gson();
    }


    private void inflateData()
    {
        OrderDAO orderDAO = new OrderDAO(getContext());
        List<OrderModel> orderModelList =orderDAO.getOrderData("approved",null);
        if(orderModelList!=null && orderModelList.size()>0) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            approvedListRecyclerView.setLayoutManager(linearLayoutManager);
            Collections.reverse(orderModelList);
            approvedList.clear();
            approvedList.addAll(orderModelList);
            approvedListRecyclerView.setAdapter(pharmacyCommonListAdapter);
            pharmacyCommonListAdapter.notifyDataSetChanged();
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
