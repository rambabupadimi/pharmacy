package com.pharmacy.agent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

public class PaginationActivity extends AppCompatActivity {

    UserPreferences userPreferences;
    Gson gson;
    AgentCommonListAdapter agentCommonListAdapter;
    ArrayList<OrderModel> runningList = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private boolean loading = false;
    private boolean isLastPage= false;
    private int pageNumber = 1;
    private final int VISIBLE_THRESHOLD = 1;
    private int lastVisibleItem, totalItemCount;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PublishProcessor<Integer> paginator = PublishProcessor.create();
    OrderDAO orderDAO;
    ProgressBar progressBar;
    int orderListSize;
    int pageWithSelectedItemsSize;
    int firstVisibleItemPosition;
    int visibleItemCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagination);
        userPreferences = new UserPreferences(this);
        gson    =   new Gson();
        orderDAO  = new OrderDAO(this);
        recyclerView = findViewById(R.id.recyclerview);
        agentCommonListAdapter = new AgentCommonListAdapter(this,runningList,"running_list");
        layoutManager   =new LinearLayoutManager(this);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(agentCommonListAdapter);

       /* List<OrderModel> orderModelList =orderDAO.getOrderData("running",null);
        Log.i("tag","order list is"+gson.toJson(orderModelList));
        if(orderModelList!=null && orderModelList.size()>0) {

            recyclerView.setLayoutManager(layoutManager);
            Collections.reverse(orderModelList);
            runningList.clear();
            runningList.addAll(orderModelList);
            recyclerView.setAdapter(agentCommonListAdapter);
            agentCommonListAdapter.notifyDataSetChanged();
        }
*/

        initialiseScrollListener();
        subscribeForData();
    }

    private void initialiseScrollListener()
    {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
/*
                Log.i("tag","dx and dy "+dx+" and "+dy);
               if(dy>0)
               {
                   if (!loading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                       if (orderListSize >= totalItemCount) {
                           pageNumber++;
                           paginator.onNext(pageNumber);
                           loading = true;
                       }
                   }
               }
*/
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
                        progressBar.setVisibility(View.VISIBLE);
                        return dataFromNetwork(page);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<OrderModel>>() {
                    @Override
                    public void accept(@NonNull List<OrderModel> items) throws Exception {

                        agentCommonListAdapter.addItems(items);
                    //    agentCommonListAdapter.notifyDataSetChanged();
                        loading = false;
                        progressBar.setVisibility(View.GONE);
                    }
                });

        compositeDisposable.add(disposable);

        paginator.onNext(pageNumber);

    }


    private Flowable<List<OrderModel>> dataFromNetwork(final int page) {
        return Flowable.just(true)
                .delay(1, TimeUnit.SECONDS)
                .map(new Function<Boolean, List<OrderModel>>() {
                    @Override
                    public List<OrderModel> apply(@NonNull Boolean value) throws Exception {
                       // List<OrderModel> items = new ArrayList<>();
                        /*for (int i = 1; i <= 10; i++) {
                            items.add("Item " + (page * 10 + i));
                        }*/
                        int val = page*15;
                        pageWithSelectedItemsSize = val;
                        List<OrderModel> orderModelList =orderDAO.getOrderDataPagination("running",null,val);
                        orderListSize = orderModelList.size();

                        if(totalItemCount==orderListSize){
                            isLastPage = true;
                        }
                        return orderModelList;
                    }
                });
    }
}
