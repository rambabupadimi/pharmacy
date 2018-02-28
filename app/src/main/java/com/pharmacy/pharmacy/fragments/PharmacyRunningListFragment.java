package com.pharmacy.pharmacy.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.adapters.SearchProductListAdapter;
import com.pharmacy.agent.AgentPharmacyListWithNavigation;
import com.pharmacy.db.daos.OrderDAO;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.models.ProductModel;
import com.pharmacy.operations.Post;
import com.pharmacy.pharmacy.adapters.PharmacyCommonListAdapter;
import com.pharmacy.preferences.UserPreferences;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    LinearLayout notFoundLayout;
    TextView notFoundText;
    ImageView notFoundIcon;


    public PharmacyRunningListFragment()
    {
        pharmacyCommonListAdapter = new PharmacyCommonListAdapter(getContext(),runningList,"running_list");
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



    private void inflateData()
    {
        OrderDAO orderDAO = new OrderDAO(getContext());
        List<OrderModel> orderModelList =orderDAO.getOrderData("running",null);
     if(orderModelList!=null && orderModelList.size()>0) {
         notFoundLayout.setVisibility(View.GONE);
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
         runningListRecyclerView.setLayoutManager(linearLayoutManager);
         Collections.reverse(orderModelList);
         runningList.clear();
         runningList.addAll(orderModelList);
         runningListRecyclerView.setAdapter(pharmacyCommonListAdapter);
         pharmacyCommonListAdapter.notifyDataSetChanged();
     }
     else
     {
         notFoundLayout.setVisibility(View.VISIBLE);
         notFoundText.setText("CLICK SEARCH ICON TO ADD MEDICINE");
         notFoundIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.running_icon));

     }
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

        notFoundLayout  =   view.findViewById(R.id.not_found_layout);
        notFoundText    =   view.findViewById(R.id.not_found_text);
        notFoundIcon    =   view.findViewById(R.id.not_found_icon);

        searchCardView.setVisibility(View.GONE);
        rlSearchView.onActionViewExpanded();
        rlSearchView.setIconified(false);
        rlSearchView.clearFocus();

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
                if(newText.toString().trim().length()>0) {
                   // getProductsList(newText);
                    getRxJavaSearch(newText);
                } else {
                    searchCardView.setVisibility(View.GONE);
                    rlSearchView.clearFocus();
                }
                return false;
            }
        });
        rlSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchCardView.setVisibility(View.GONE);
                rlSearchView.clearFocus();

                return false;
            }
        });


    }



    private void getRxJavaSearch(String text){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name",text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Rx2AndroidNetworking.post(CommonMethods.SEARCH_PRODUCT)
                .addJSONObjectBody(jsonObject)
                .build()
                .getJSONObjectObservable()
                .subscribeOn(Schedulers.io())
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {

                    @Override
                    public void onError(Throwable e) {
                        // handle error
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JSONObject response) {
                        //do anything with response

                        if(response!=null)
                        {
                            try {

                                if(response.get("Status").toString().equalsIgnoreCase("Success")) {
                                    ProductModel productModel = gson.fromJson(response.toString(), ProductModel.class);
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
                        Log.i("tag","response is"+response);
                    }
                });
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
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,new IntentFilter("product_status_running"));

    }


}
