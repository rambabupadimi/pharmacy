package com.pharmacy.agent.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.adapters.SearchProductListAdapter;
import com.pharmacy.agent.adapters.AgentCommonListAdapter;
import com.pharmacy.db.daos.OrderDAO;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.models.ProductModel;
import com.pharmacy.operations.Post;
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
    AgentCommonListAdapter agentCommonListAdapter;

    LinearLayout notFoundLayout;
    TextView notFoundText;
    ImageView notFoundIcon;

    public AgentRunningListFragment()
    {
        agentCommonListAdapter = new AgentCommonListAdapter(getContext(),runningList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_running_list,container,false);
        initialiseObjects();
        initialiseIDs(view);
        initialiseClickListeners();
        hideKeyboard(view);
        return view;
    }

    private void hideKeyboard(View view)
    {

        if(view!=null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void inflateData()
    {

       //String pharmacyLocalId =  getArguments().getString("pharmacy_id");

        String pharmacyLocalId =  userPreferences.getAgentSelectedPharmacyId();

        OrderDAO orderDAO = new OrderDAO(getContext());
        List<OrderModel> orderModelList =orderDAO.getOrderData("running",pharmacyLocalId);
        Log.i("tag","order list is"+gson.toJson(orderModelList));
        if(orderModelList!=null && orderModelList.size()>0) {
            notFoundLayout.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            runningListRecyclerView.setLayoutManager(linearLayoutManager);
            Collections.reverse(orderModelList);
            runningList.clear();
            runningList.addAll(orderModelList);
            runningListRecyclerView.setAdapter(agentCommonListAdapter);
            agentCommonListAdapter.notifyDataSetChanged();
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
        afrlSearchView    =   view.findViewById(R.id.frl_search_view );
        searchCardView  =   view.findViewById(R.id.frl_search_cardview);
        searchRecyclerView  =   view.findViewById(R.id.frl_search_recyclerview);
        runningListRecyclerView =   view.findViewById(R.id.frl_runninglist_recyclerview);

        notFoundLayout  =   view.findViewById(R.id.not_found_layout);
        notFoundText    =   view.findViewById(R.id.not_found_text);
        notFoundIcon    =   view.findViewById(R.id.not_found_icon);


        //afrlSearchView.setActivated(true);
        afrlSearchView.onActionViewExpanded();
        afrlSearchView.setIconified(false);
        afrlSearchView.clearFocus();

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
                if(newText.toString().trim().length()>0) {
                    //getProductsList(newText);
                    getRxJavaSearch(newText);

                }
                else {
                    searchCardView.setVisibility(View.GONE);
                    afrlSearchView.clearFocus();
                }

                return false;
            }
        });



        afrlSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                afrlSearchView.clearFocus();
                searchCardView.setVisibility(View.GONE);
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
