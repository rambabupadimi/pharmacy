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

public class PharmacyDeliveredListFragment extends Fragment {


    PharmacyCommonListAdapter pharmacyCommonListAdapter;
    ArrayList<OrderModel> deliveredList = new ArrayList<>();
    RecyclerView deliveredListRecyclerView;
    Gson gson;
    LinearLayout notFoundLayout;
    TextView notFoundText;
    ImageView notFoundIcon;


    public PharmacyDeliveredListFragment()
    {
        pharmacyCommonListAdapter = new PharmacyCommonListAdapter(getContext(),deliveredList,"delivered_list");
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_delivered_list, container, false);

        initialiseIDs(view);
        initialiseObjects();
        return view;
    }


    private void initialiseIDs(View view)
    {
        deliveredListRecyclerView  =  view.findViewById(R.id.pharmacy_delivered_list_recyclerview);
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
        List<OrderModel> orderModelList =orderDAO.getOrderData("delivered",null);

       if(orderModelList!=null && orderModelList.size()>0) {
           LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
           deliveredListRecyclerView.setLayoutManager(linearLayoutManager);
           Collections.reverse(orderModelList);
           deliveredList.clear();
           deliveredList.addAll(orderModelList);
           deliveredListRecyclerView.setAdapter(pharmacyCommonListAdapter);
           pharmacyCommonListAdapter.notifyDataSetChanged();
       }
       else
       {
           notFoundLayout.setVisibility(View.VISIBLE);
           notFoundText.setText("NO DELIVERED PRODUCTS");
           notFoundIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.delived_icon));
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
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,new IntentFilter("product_status_delivered"));

    }
}
