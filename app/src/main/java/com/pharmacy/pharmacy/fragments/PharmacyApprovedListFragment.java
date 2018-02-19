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

public class PharmacyApprovedListFragment extends Fragment {

    PharmacyCommonListAdapter pharmacyCommonListAdapter;
    ArrayList<OrderModel> approvedList = new ArrayList<>();
    RecyclerView approvedListRecyclerView;
    Gson gson;

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
        List<OrderModel> orderModelList =orderDAO.getOrderData("approved");
        Log.i("tag","order list is"+gson.toJson(orderModelList));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        approvedListRecyclerView.setLayoutManager(linearLayoutManager);
        Collections.reverse(orderModelList);
        approvedList.clear();
        approvedList.addAll(orderModelList);
        approvedListRecyclerView.setAdapter(pharmacyCommonListAdapter);
        pharmacyCommonListAdapter.notifyDataSetChanged();

    }

}
