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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.R;
import com.pharmacy.agent.adapters.AgentCommonListAdapter;
import com.pharmacy.db.daos.OrderDAO;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.preferences.UserPreferences;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by PCCS-0007 on 06-Feb-18.
 */

public class AgentDeliveredListFragment extends Fragment {

    ArrayList<OrderModel> deliveredList = new ArrayList<>();
    RecyclerView deliveredListRecyclerView;
    AgentCommonListAdapter agentCommonListAdapter;
    Gson gson;
    UserPreferences userPreferences;
    LinearLayout notFoundLayout;
    TextView notFoundText;
    ImageView notFoundIcon;
    boolean _delivedlistloaded=false;



    LinearLayoutManager layoutManager;
    private int  totalItemCount,lastVisibleItem;
    ProgressBar progressBar;
    int orderListSize;
    int pageWithSelectedItemsSize;
    int firstVisibleItemPosition;
    int visibleItemCount;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PublishProcessor<Integer> paginator = PublishProcessor.create();
    private boolean loading = false;
    private boolean isLastPage= false;
    private int pageNumber = 1;

    public AgentDeliveredListFragment()
    {
        agentCommonListAdapter  = new AgentCommonListAdapter(getContext(),deliveredList,"delivered_list");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_delivered_list, container, false);
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
        deliveredListRecyclerView =   view.findViewById(R.id.agent_delivered_list_recyclerview);

        notFoundLayout  =   view.findViewById(R.id.not_found_layout);
        notFoundText    =   view.findViewById(R.id.not_found_text);
        notFoundIcon    =   view.findViewById(R.id.not_found_icon);
        progressBar     =   view.findViewById(R.id.progress_bar);


        agentCommonListAdapter = new AgentCommonListAdapter(getContext(),deliveredList,"delivered_list");
        layoutManager = new LinearLayoutManager(getContext());
        deliveredListRecyclerView.setLayoutManager(layoutManager);
        deliveredListRecyclerView.setAdapter(agentCommonListAdapter);

    }




    private void initialiseScrollListener()
    {
        deliveredListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                visibleItemCount = layoutManager.getChildCount();
                if(dy>0) {
                    if (!loading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            pageNumber++;
                            paginator.onNext(pageNumber);
                            loading = true;
                        }
                    }
                }

            }
        });
    }


    private void subscribeForData() {

        Disposable disposable = paginator
                .onBackpressureDrop()
                .concatMap(new Function<Integer, Publisher<List<OrderModel>>>() {
                    @Override
                    public Publisher<List<OrderModel>> apply(@NonNull Integer page) throws Exception {
                        loading = true;
                        if(isLastPage) {
                            progressBar.setVisibility(View.GONE);
                        }
                        else if(page==1)
                        {
                            progressBar.setVisibility(View.GONE);
                        }
                        else
                        {
                            progressBar.setVisibility(View.VISIBLE);
                        }


                        return dataFromNetwork(page);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<OrderModel>>() {
                    @Override
                    public void accept(@NonNull List<OrderModel> items) throws Exception {

                        if(items!=null && items.size()>0) {
                            showLayout();
                            agentCommonListAdapter.addItems(items);
                            loading = false;
                            progressBar.setVisibility(View.GONE);
                        }
                        else
                        {
                            hideLayout();
                        }
                    }
                });

        compositeDisposable.add(disposable);

        paginator.onNext(pageNumber);

    }

    private void showLayout()
    {
        notFoundLayout.setVisibility(View.GONE);
        deliveredListRecyclerView.setVisibility(View.VISIBLE);

    }

    private void hideLayout()
    {
        deliveredListRecyclerView.setVisibility(View.GONE);
        notFoundLayout.setVisibility(View.VISIBLE);
        notFoundText.setText("CLICK SEARCH ICON TO ADD MEDICINE");
        notFoundIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.running_icon));

    }
    private Flowable<List<OrderModel>> dataFromNetwork(final int page) {
        return Flowable.just(true)
                .delay(1, TimeUnit.SECONDS)
                .map(new Function<Boolean, List<OrderModel>>() {
                    @Override
                    public List<OrderModel> apply(@NonNull Boolean value) throws Exception {
                        int val = page*30;
                        pageWithSelectedItemsSize = val;
                        String pharmacyLocalId =  userPreferences.getAgentSelectedPharmacyId();
                        OrderDAO orderDAO = new OrderDAO(getContext());
                        List<OrderModel> orderModelList =orderDAO.getOrderDataPagination("delivered",pharmacyLocalId,val);
                        orderListSize = orderModelList.size();
                        Collections.reverse(orderModelList);
                        if(totalItemCount==orderListSize){
                            isLastPage = true;
                        }
                        return orderModelList;
                    }
                });
    }





    private void inflateData()
    {

        String pharmacyLocalId =   userPreferences.getAgentSelectedPharmacyId();
        OrderDAO orderDAO = new OrderDAO(getContext());
        List<OrderModel> orderModelList =orderDAO.getOrderData("delivered",pharmacyLocalId);
        if(orderModelList!=null && orderModelList.size()>0) {
            notFoundLayout.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            deliveredListRecyclerView.setLayoutManager(linearLayoutManager);
            Collections.reverse(orderModelList);
            deliveredList.clear();
            deliveredList.addAll(orderModelList);
            deliveredListRecyclerView.setAdapter(agentCommonListAdapter);
            agentCommonListAdapter.notifyDataSetChanged();
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
            //inflateData();
            subscribeForData();
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
        //inflateData();
        subscribeForData();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,new IntentFilter("product_status_delivered"));

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser ) {
          //  inflateData();
            _delivedlistloaded = true;
        }
    }
}
