package com.pharmacy.pharmacy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
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
 * Created by PCCS-0007 on 05-Feb-18.
 */

public class PharmacyRunningListFragment extends Fragment {

    SearchView rlSearchView;
    Gson gson;

    CardView searchCardView;
    RecyclerView searchRecyclerView,runningListRecyclerView;
    SearchProductListAdapter searchProductListAdapter;
    ArrayList<ProductModel> searchProductList = new ArrayList<>();
    UserPreferences userPreferences;
    PharmacyCommonListAdapter pharmacyCommonListAdapter;
    ArrayList<OrderModel> runningList = new ArrayList<>();

    public PharmacyRunningListFragment()
    {
        pharmacyCommonListAdapter = new PharmacyCommonListAdapter(getContext(),runningList);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_running_list,container,false);
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
        List<OrderModel> orderModelList =orderDAO.getOrderData("running",null);
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
        rlSearchView    =   view.findViewById(R.id.frl_search_view);
        searchCardView  =   view.findViewById(R.id.frl_search_cardview);
        searchRecyclerView  =   view.findViewById(R.id.frl_search_recyclerview);
        runningListRecyclerView =   view.findViewById(R.id.frl_runninglist_recyclerview);

        searchCardView.setVisibility(View.GONE);
    }

    private void initialiseClickListeners()
    {
        rlSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        rlSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchCardView.setVisibility(View.GONE);
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
