package com.pharmacy.agent.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;
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
    LinearLayoutManager runningListLayoutManager;
    private int lastVisibleItem, totalItemCount;
      private final int VISIBLE_THRESHOLD = 1;
    boolean _areLecturesLoaded = false;
    LinearLayout frlLayout;
    LinearLayoutManager layoutManager;
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



    public AgentRunningListFragment()
    {
            }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent_running_list,container,false);
        initialiseObjects();
        initialiseIDs(view);
        initialiseClickListeners();
        hideKeyboard(view);


        initialiseScrollListener();
        return view;
    }


    private void initialiseScrollListener()
    {
        runningListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        runningListRecyclerView.setVisibility(View.VISIBLE);

    }

    private void hideLayout()
    {
        runningListRecyclerView.setVisibility(View.GONE);
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
                        List<OrderModel> orderModelList =orderDAO.getOrderDataPagination("running",pharmacyLocalId,val);
                         orderListSize = orderModelList.size();
                        Collections.reverse(orderModelList);
                        if(totalItemCount==orderListSize){
                            isLastPage = true;
                        }
                        return orderModelList;
                    }
                });
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

         String pharmacyLocalId =  userPreferences.getAgentSelectedPharmacyId();

        OrderDAO orderDAO = new OrderDAO(getContext());
        List<OrderModel> orderModelList =orderDAO.getOrderData("running",pharmacyLocalId);
        if(orderModelList!=null && orderModelList.size()>0) {
            notFoundLayout.setVisibility(View.GONE);
            runningListLayoutManager = new LinearLayoutManager(getContext());
            runningListRecyclerView.setLayoutManager(runningListLayoutManager);
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

        frlLayout       =   view.findViewById(R.id.frl_layout);
        progressBar     =   view.findViewById(R.id.progress_bar);


        //afrlSearchView.setActivated(true);
        afrlSearchView.onActionViewExpanded();
        afrlSearchView.setIconified(false);
        afrlSearchView.clearFocus();

        agentCommonListAdapter = new AgentCommonListAdapter(getContext(),runningList,"running_list");
        layoutManager = new LinearLayoutManager(getContext());
        runningListRecyclerView.setLayoutManager(layoutManager);
        runningListRecyclerView.setAdapter(agentCommonListAdapter);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        runningListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                       // frlLayout.setVisibility(View.VISIBLE);
                        //slideUp(frlLayout);
                        break;

                    case RecyclerView.SCROLL_STATE_DRAGGING:
                       // frlLayout.setVisibility(View.GONE);
                       // slideDown(frlLayout);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

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
          //  inflateData();
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
      // inflateData();

        subscribeForData();


        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,new IntentFilter("product_status_running"));

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        if (isVisibleToUser && !_areLecturesLoaded ) {
         //   inflateData();
            _areLecturesLoaded = true;


        }
    }
}
