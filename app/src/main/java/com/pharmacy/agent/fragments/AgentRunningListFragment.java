package com.pharmacy.agent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.adapters.SearchProductListAdapter;
import com.pharmacy.db.daos.OrderDAO;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.models.ProductModel;
import com.pharmacy.operations.Post;
import com.pharmacy.pharmacy.adapters.PharmacyCommonListAdapter;
import com.pharmacy.preferences.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by PCCS-0007 on 06-Feb-18.
 */

public class AgentRunningListFragment extends Fragment {

    SearchView afrlSearchView;
    Gson gson;
    SearchProductListAdapter searchProductListAdapter;
    ArrayList<ProductModel> searchProductList = new ArrayList<>();
    UserPreferences userPreferences;
    ArrayList<OrderModel> runningList = new ArrayList<>();
    CardView searchCardView;
    RecyclerView searchRecyclerView,runningListRecyclerView;
    PharmacyCommonListAdapter pharmacyCommonListAdapter;

    public AgentRunningListFragment()
    {
        pharmacyCommonListAdapter = new PharmacyCommonListAdapter(getContext(),runningList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_running_list,container,false);
        initialiseObjects();
        initialiseIDs(view);
        initialiseClickListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        inflateData();
    }

    private void inflateData()
    {
        OrderDAO orderDAO = new OrderDAO(getContext());
        List<OrderModel> orderModelList =orderDAO.getOrderData("running");
        Log.i("tag","order list is"+gson.toJson(orderModelList));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        runningListRecyclerView.setLayoutManager(linearLayoutManager);
        Collections.reverse(orderModelList);
        runningList.clear();
        runningList.addAll(orderModelList);
        runningListRecyclerView.setAdapter(pharmacyCommonListAdapter);
        pharmacyCommonListAdapter.notifyDataSetChanged();

    }


    private void initialiseObjects()
    {
        gson    =   new Gson();
        searchProductListAdapter    =   new SearchProductListAdapter(getContext(),searchProductList);
        userPreferences =   new UserPreferences(getContext());
    }

    private void initialiseIDs(View view)
    {
        afrlSearchView    =   view.findViewById(R.id.frl_search_view );
        searchCardView  =   view.findViewById(R.id.frl_search_cardview);
        searchRecyclerView  =   view.findViewById(R.id.frl_search_recyclerview);
        runningListRecyclerView =   view.findViewById(R.id.frl_runninglist_recyclerview);

    }

    private void initialiseClickListeners()
    {
        afrlSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.toString().trim().length()>0)
                    getProductsList(newText);


                return false;
            }
        });
    }


    private void getProductsList(String text){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name",text);
            Post post = new Post(getContext(), CommonMethods.SEARCH_PRODUCT,jsonObject.toString()) {
                @Override
                public void onResponseReceived(String result) {
                    if(result!=null)
                    {
                        try {
                            JSONObject jsonObject1 = new JSONObject(result);
                            if(jsonObject1.get("Status").toString().equalsIgnoreCase("Success")) {

                                ProductModel productModel = gson.fromJson(result, ProductModel.class);
                                ArrayList<ProductModel> productModelArrayList = productModel.Response;
                                if (productModelArrayList != null && productModelArrayList.size() > 0) {
                                    searchCardView.setVisibility(View.VISIBLE);
                                    showSearchProductList(productModelArrayList);
                                }
                            }
                            else
                            {
                                searchCardView.setVisibility(View.GONE);

                            }
                        }
                        catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            };
            post.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void showSearchProductList(ArrayList<ProductModel> productModelArrayList)
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchProductList.clear();
        searchProductList.addAll(productModelArrayList);
        searchRecyclerView.setAdapter(null);
        searchRecyclerView.setAdapter(searchProductListAdapter);
        searchProductListAdapter.notifyDataSetChanged();
    }

}
