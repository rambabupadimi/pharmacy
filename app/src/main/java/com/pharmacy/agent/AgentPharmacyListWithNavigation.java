package com.pharmacy.agent;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.InformationBottomSheet;
import com.pharmacy.R;
import com.pharmacy.agent.adapters.AgentPharmacyListAdapter;
import com.pharmacy.db.AndroidDatabaseManager;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.operations.Post;
import com.pharmacy.preferences.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AgentPharmacyListWithNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , AppConstants{

    Toolbar aplnToolbar;
    FloatingActionButton floatingActionButton;
    DrawerLayout drawer;
    NavigationView navigationView;
    RecyclerView aplnRecyclerView;
    AgentPharmacyListAdapter agentPharmacyListAdapter;
    SearchView searchView;
    ArrayList<PharmacyModel> pharmacyModelArrayList = new ArrayList<>();
    RoundedImageView profileIcon;
    TextView profileName;
    CommonMethods commonMethods;
    AgentDAO agentDAO;
    UserPreferences userPreferences;
    TextView toolbarHeading;
    PharmacyDAO pharmacyDAO;

    LinearLayout notFoundLayout;
    TextView notFoundText;
    ImageView notFoundIcon;
    Gson gson;

    ImageView gridViewIcon,listViewIcon;



    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_pharmacy_list_with_navigation);

        initialiseObjects();
        initialiseIDs();
        initialiseNavigationDrawer();
        initialiseDisplayListType();

        initialiseClickListeners();
        initialiseStatus();
        setToolbarAndNavbarData();
        refreshList();
        initialiseAdapterData();

    }

    private void initialiseDisplayListType()
    {
        gridViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPreferences.setSelectListType("list");
                gridViewIcon.setVisibility(View.GONE);
                listViewIcon.setVisibility(View.VISIBLE);
                initialiseAdapterData();

            }
        });

        listViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewIcon.setVisibility(View.GONE);
                gridViewIcon.setVisibility(View.VISIBLE);
                userPreferences.setSelectListType("grid");
                initialiseAdapterData();
            }
        });
    }

    private void refreshList()
    {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
               if(CommonMethods.isInternetConnected(AgentPharmacyListWithNavigation.this))
                    refreshItems();
               else
                   Toast.makeText(AgentPharmacyListWithNavigation.this,"Check Internet Connection",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void refreshItems()
    {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserID",userPreferences.getUserGid());
            jsonObject.put("LastUpdatedTimeTicks",userPreferences.getGetAgentPharmacyTimeticks());
            String json = jsonObject.toString();
            Post post = new Post(AgentPharmacyListWithNavigation.this,CommonMethods.GET_AGENT_PHARMACY,json) {
                @Override
                public void onResponseReceived(String result) {
                    Log.i("tag","result is"+result);
                    if(result!=null){
                        try {
                            JSONObject jsonObject1 = new JSONObject(result);
                            if(jsonObject1.get("Status").toString().equalsIgnoreCase("Success"))
                            {
                                if(jsonObject1.get("Response")!=null)
                                {
                                    try {
                                        JSONObject newObject = jsonObject1.getJSONObject("Response");
                                        String ticks = newObject.get("LastUpdatedTimeTicks").toString();
                                        userPreferences.setGetAgentPharmacyTimeticks(ticks);

                                        if (newObject.get("Pharmacies") != null) {
                                            JSONArray jsonArray = newObject.getJSONArray("Pharmacies");
                                            if (jsonArray != null && jsonArray.length() > 0) {
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    PharmacyModel pharmacyModel = gson.fromJson(jsonArray.getJSONObject(i).toString(), PharmacyModel.class);
                                                    pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);

                                                }
                                                initialiseAdapterData();
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        }
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    swipeRefreshLayout.setRefreshing(false);
                                }

                             }
                            else
                            {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            };
            post.execute();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initialiseAdapterData()
    {

        agentPharmacyListAdapter = new AgentPharmacyListAdapter(this,pharmacyModelArrayList);
      ArrayList<com.pharmacy.db.models.PharmacyModel> pharmacyModelList1 =   pharmacyDAO.getListOfPharmacys(userPreferences.getUserGid());
      pharmacyModelArrayList.clear();
      pharmacyModelArrayList.addAll(pharmacyModelList1);
      initialiseAdapter(pharmacyModelArrayList);
    }

    private void setToolbarAndNavbarData()
    {
        try {
            AgentModel agentModel = agentDAO.getAgentData(userPreferences.getUserGid());
            profileName.setText(agentModel.Name);
            toolbarHeading.setText(agentModel.Name);

            if (agentModel.ImageLocalPath != null && agentModel.IdProofLocalPath.trim().length()>0) {
                Glide.with(this)
                        .load(agentModel.ImageLocalPath)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(profileIcon);

            }
            else
            {
                if(agentModel.Image!=null) {
                    Glide.with(this)
                            .load(agentModel.Image)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .into(profileIcon);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initialiseStatus()
    {
        commonMethods.maintainState(this,USER_HOME_PAGE_STATUS);

    }
    private void initialiseObjects()
    {
        commonMethods   =   new CommonMethods();
        agentDAO        =   new AgentDAO(this);
        userPreferences =   new UserPreferences(this);
        pharmacyDAO     =   new PharmacyDAO(this);
        gson            =   new Gson();
    }

    private void initialiseIDs()
    {
        aplnToolbar             =   findViewById(R.id.apln_toolbar);
        floatingActionButton    =   findViewById(R.id.fab);
        drawer                  =  findViewById(R.id.drawer_layout);
        navigationView          =  findViewById(R.id.nav_view);
        aplnRecyclerView        =   findViewById(R.id.apln_recyclerview);


        View headerLayout = navigationView.getHeaderView(0);
        profileIcon =   headerLayout.findViewById(R.id.ar_nav_profile_imageview);
        profileName =   headerLayout.findViewById(R.id.ar_nav_profile_name);
        toolbarHeading  =   findViewById(R.id.agent_toolbar_heading);

        notFoundLayout          =   findViewById(R.id.not_found_layout);
        notFoundText            =   findViewById(R.id.not_found_text);
        notFoundIcon            = findViewById(R.id.not_found_icon);

        swipeRefreshLayout      =   findViewById(R.id.swipeRefreshLayout);
        setSupportActionBar(aplnToolbar);

           gridViewIcon            =   findViewById(R.id.grid_view_icon);
        listViewIcon            =   findViewById(R.id.list_view_icon);

        if(userPreferences.getSelectListType().toString().equalsIgnoreCase("grid")) {
            gridViewIcon.setVisibility(View.VISIBLE);
            listViewIcon.setVisibility(View.GONE);
        }else {
            listViewIcon.setVisibility(View.VISIBLE);
            gridViewIcon.setVisibility(View.GONE);
        }
    }

    private void initialiseNavigationDrawer()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, aplnToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initialiseClickListeners()
    {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AgentPharmacyListWithNavigation.this,AddNewPharmacyStepOne.class);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(AgentPharmacyListWithNavigation.this, R.anim.next_swipe2, R.anim.next_swipe1).toBundle();
                    startActivity(intent,bndlanimation);
                }
            });

            profileIcon.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AgentPharmacyListWithNavigation.this,AgentProfieView.class);
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(AgentPharmacyListWithNavigation.this, R.anim.next_swipe2, R.anim.next_swipe1).toBundle();
                    startActivity(intent,bndlanimation);
                }
            });
    }



    private void initialiseAdapter(ArrayList<com.pharmacy.db.models.PharmacyModel> pharmacyModelArrayList)
    {

        if(pharmacyModelArrayList!=null && pharmacyModelArrayList.size()>0) {
            notFoundLayout.setVisibility(View.GONE);
            aplnRecyclerView.setVisibility(View.VISIBLE);
            agentPharmacyListAdapter = new AgentPharmacyListAdapter(this, pharmacyModelArrayList);
           // LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                   // aplnRecyclerView.setLayoutManager(linearLayoutManager);
            if(userPreferences.getSelectListType().toString().equalsIgnoreCase("grid"))
                aplnRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            else
                aplnRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            aplnRecyclerView.setAdapter(agentPharmacyListAdapter);
        }
        else
        {
            aplnRecyclerView.setVisibility(View.GONE);
            notFoundLayout.setVisibility(View.VISIBLE);
            notFoundText.setText("CLICK + TO ADD NEW PHARMACY");
        }

    }

    @Override
    public void onBackPressed() {


        AgentPharmacyListWithNavigation.this.finish();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.agent_pharmacy_list_with_navigation, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                searchRecyclerview(s);
                return false;
            }
        });
        return true;
    }


    private void searchRecyclerview(String s)
    {
        if(s!=null && s.length()>0)
        {
            ArrayList<PharmacyModel> pharmacyModelArrayListWithFilter = new ArrayList<>();
            if(pharmacyModelArrayList!=null && pharmacyModelArrayList.size()>0)
            {
                for(int i=0;i<pharmacyModelArrayList.size();i++)
                {
                    PharmacyModel pharmacyModel = pharmacyModelArrayList.get(i);
                    if(pharmacyModel.StoreName.toLowerCase().trim().toString().contains(s.toLowerCase().trim()))
                    {
                        pharmacyModelArrayListWithFilter.add(pharmacyModel);
                    }
                }
                initialiseAdapter(pharmacyModelArrayListWithFilter);
            }
        }
        else
        {
            initialiseAdapter(pharmacyModelArrayList);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent dbmanager = new Intent(AgentPharmacyListWithNavigation.this, AndroidDatabaseManager.class);
            startActivity(dbmanager);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            // Handle the camera action
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("tag","yes its called");
            initialiseAdapterData();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("add_new_pharmacy"));

    }

    public void showDialog()
    {
        BottomSheetDialogFragment bottomSheetDialogFragment = new InformationBottomSheet();
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

    }

}
