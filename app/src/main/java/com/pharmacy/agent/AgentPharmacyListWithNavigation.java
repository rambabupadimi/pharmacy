package com.pharmacy.agent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.agent.adapters.AgentPharmacyListAdapter;
import com.pharmacy.db.AndroidDatabaseManager;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.preferences.UserPreferences;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_pharmacy_list_with_navigation);
        initialiseObjects();
        initialiseIDs();
        initialiseNavigationDrawer();

        initialiseAdapterData();
        initialiseClickListeners();
        initialiseStatus();
        setToolbarAndNavbarData();
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

            if (agentModel.ImageLocalPath != null) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(agentModel.ImageLocalPath);
                    if (bitmap != null) {
                        profileIcon.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

        setSupportActionBar(aplnToolbar);
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
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AgentPharmacyListWithNavigation.this,AddNewPharmacyStepOne.class);
                    startActivity(intent);
                }
            });

            profileIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AgentPharmacyListWithNavigation.this,AgentProfieView.class);
                    startActivity(intent);
                }
            });
    }



    private void initialiseAdapter(ArrayList<com.pharmacy.db.models.PharmacyModel> pharmacyModelArrayList)
    {

        agentPharmacyListAdapter =   new AgentPharmacyListAdapter(this, pharmacyModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        aplnRecyclerView.setLayoutManager(linearLayoutManager);
        aplnRecyclerView.setAdapter(agentPharmacyListAdapter);

    }

    @Override
    public void onBackPressed() {


        finish();
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
