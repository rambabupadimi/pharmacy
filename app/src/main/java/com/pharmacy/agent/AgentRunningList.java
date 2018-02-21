package com.pharmacy.agent;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.agent.fragments.AgentApprovedListFragment;
import com.pharmacy.agent.fragments.AgentDeliveredListFragment;
import com.pharmacy.agent.fragments.AgentRunningListFragment;
import com.pharmacy.db.daos.OrderDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.operations.Post;
import com.pharmacy.preferences.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AgentRunningList extends AppCompatActivity {

    private Toolbar prlToolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private String toolbarHeading="";
    private TextView toolbarTitle;
    private TextView viewPharmacy;
    PharmacyModel pharmacyModel;
    Gson gson;
    UserPreferences userPreferences;
    OrderDAO orderDAO;
    ProgressBar agentHomeProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_running_list);



        initialiseObjects();
        initialiseIDs();
        getIntentData();
        initialiseApiCall();

     //   initialiseArguments();

        initialiseFragments();
        addViewPagerToTabLayout();
        initialiseBackButton();

        setToolbarTitle();
        initialiseClickListeners();
        hideKeyboard();
    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if(view!=null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void initialiseObjects()
    {
        userPreferences = new UserPreferences(this);
        orderDAO = new OrderDAO(AgentRunningList.this);

    }

    private void initialiseClickListeners()
    {
        viewPharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgentRunningList.this,ViewPharmacyDetails.class);
                intent.putExtra("pharmacy_object",gson.toJson(pharmacyModel));
                startActivity(intent);
            }
        });
    }

    private void getIntentData()
    {
        try {
            String json = getIntent().getStringExtra("pharmacy_object");
             pharmacyModel = gson.fromJson(json,PharmacyModel.class);
            toolbarHeading = pharmacyModel.StoreName;
            userPreferences.setAgentSelectedPharmacyId(pharmacyModel.PharmacyID);



        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setToolbarTitle()
    {
        toolbarTitle.setText(toolbarHeading);
    }

    private void initialiseIDs()
    {
        viewPager   =   findViewById(R.id.arl_pager);
        tabLayout   =   findViewById(R.id.arl_tabs);
        adapter     =   new ViewPagerAdapter(getSupportFragmentManager());
        prlToolbar  =   findViewById(R.id.arl_toolbar);
        toolbarTitle    =   findViewById(R.id.arl_toolbar_heading);
        gson    =   new Gson();

        viewPharmacy    =   findViewById(R.id.arl_toolbar_view_pharmacy);
        agentHomeProgress   =   findViewById(R.id.agent_home_progress);
    }

    private void initialiseFragments()
    {
        Bundle bundle = new Bundle();
        bundle.putString("pharmacy_id", pharmacyModel.PharmacyID);


        AgentRunningListFragment agentRunningListFragment = new AgentRunningListFragment();
        AgentApprovedListFragment agentApprovedListFragment = new AgentApprovedListFragment();
        AgentDeliveredListFragment agentDeliveredListFragment = new AgentDeliveredListFragment();

        agentRunningListFragment.setArguments(bundle);
        agentApprovedListFragment.setArguments(bundle);
        agentDeliveredListFragment.setArguments(bundle);
        // Add Fragments to adapter one by one
        adapter.addFragment(agentRunningListFragment, "RUNNING LIST");
        adapter.addFragment(agentApprovedListFragment, "APPROVED");
        adapter.addFragment(agentDeliveredListFragment, "DELIVERED");



        viewPager.setAdapter(adapter);
    }

    private void addViewPagerToTabLayout()
    {
        tabLayout.setupWithViewPager(viewPager);

    }

    private void initialiseBackButton()
    {
        setSupportActionBar(prlToolbar);
        CommonMethods.setToolbar(this,getSupportActionBar());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        userPreferences.setAgentSelectedPharmacyId("");
        Intent intent = new Intent(AgentRunningList.this,AgentPharmacyListWithNavigation.class);
        startActivity(intent);
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void initialiseApiCall()
    {
        UserDAO userDAO = new UserDAO(this);
        UserModel  userModel = userDAO.getUserData(userPreferences.getUserGid());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DistributorID",userModel.DistributorID);
            jsonObject.put("UserID",userModel.UserID);
            jsonObject.put("LastUpdatedTimeTicks",userPreferences.getGetAllMyListTimeticks());
            jsonObject.put("PharmacyID",pharmacyModel.PharmacyID);
            String json = jsonObject.toString();
            agentHomeProgress.setVisibility(View.VISIBLE);
            Post post = new Post(this,CommonMethods.GET_ALL_MYLIST,json) {
                @Override
                public void onResponseReceived(String result) {
                    if(result!=null)
                    {
                        agentHomeProgress.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject1 = new JSONObject(result);
                            if(jsonObject1.get("Status").toString().equalsIgnoreCase("Success")){
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("Response");

                                String lastUpdatedTicks = jsonObject2.get("LastUpdatedTimeTicks").toString();
                                userPreferences.setGetAllMyListTimeticks(lastUpdatedTicks);
                                if(jsonObject2.get("OrderList")!=null){
                                   JSONArray jsonArray = jsonObject2.getJSONArray("OrderList");
                                   if(jsonArray.length()>0)
                                   {
                                       ArrayList<OrderModel> orderModelArrayList = new ArrayList<>();
                                       for(int i=0;i<jsonArray.length();i++)
                                       {
                                           OrderModel orderModel = new OrderModel();
                                           orderModel = gson.fromJson(jsonArray.getJSONObject(i).toString(),OrderModel.class);
                                           orderModelArrayList.add(orderModel);
                                       }

                                       if(orderModelArrayList!=null && orderModelArrayList.size()>0) {
                                           try {
                                               for (int i = 0; i < orderModelArrayList.size(); i++) {
                                                   Long id = orderDAO.insert(orderModelArrayList.get(i));
                                                   Log.i("tag", "inserted order" + id);
                                               }
                                           }catch (Exception e)
                                           {
                                               e.printStackTrace();
                                           }
                                       }
                                   }
                                    Log.i("Tag","json array"+jsonArray);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        agentHomeProgress.setVisibility(View.GONE);
                    }
                }
            };
            post.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
