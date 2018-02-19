package com.pharmacy.pharmacy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


    public PharmacyDeliveredListFragment()
    {
        pharmacyCommonListAdapter = new PharmacyCommonListAdapter(getContext(),deliveredList);
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

    }

    private void initialiseObjects()
    {
        gson = new Gson();
    }

    @Override
    public void onResume() {
        super.onResume();
        inflateData();
    }


    private void inflateData()
    {
        OrderDAO orderDAO = new OrderDAO(getContext());
        List<OrderModel> orderModelList =orderDAO.getOrderData("delivered",null);
        Log.i("tag","order list is"+gson.toJson(orderModelList));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        deliveredListRecyclerView.setLayoutManager(linearLayoutManager);
        Collections.reverse(orderModelList);
        deliveredList.clear();
        deliveredList.addAll(orderModelList);
        deliveredListRecyclerView.setAdapter(pharmacyCommonListAdapter);
        pharmacyCommonListAdapter.notifyDataSetChanged();

    }
}
