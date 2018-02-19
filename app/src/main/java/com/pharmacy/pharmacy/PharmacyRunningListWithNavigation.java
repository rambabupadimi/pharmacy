package com.pharmacy.pharmacy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.db.AndroidDatabaseManager;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.pharmacy.fragments.PharmacyApprovedListFragment;
import com.pharmacy.pharmacy.fragments.PharmacyDeliveredListFragment;
import com.pharmacy.pharmacy.fragments.PharmacyRunningListFragment;
import com.pharmacy.preferences.UserPreferences;

import java.util.ArrayList;
import java.util.List;

public class PharmacyRunningListWithNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AppConstants {

    private Toolbar prlToolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter adapter;
    DrawerLayout drawer;
    NavigationView navigationView;
    ImageView profileIcon;
    TextView profileName;
    CommonMethods commonMethods;
    UserPreferences userPreferences;
    PharmacyDAO pharmacyDAO;
    TextView toolbarHeading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_running_list_with_navigation);

        initialiseObjects();
        initialiseIDs();
        initialiseFragments();
        addViewPagerToTabLayout();
        initialiseBackButton();
        initialiseDrawerLayout();
        initialiseClickListeners();
        initialiseStatus();
        setToolbarAndNavbarData();

    }


    private void setToolbarAndNavbarData()
    {
        PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyData(userPreferences.getUserGid());
        profileName.setText(pharmacyModel.StoreName);
        toolbarHeading.setText(pharmacyModel.StoreName);

        if(pharmacyModel.ImageLocalPath!=null)
        {
            try {
                Glide.with(this)
                        .load(pharmacyModel.ImageLocalPath)
                        .placeholder(R.drawable.default_icon)
                        .error(R.drawable.default_icon)
                        .into(profileIcon);


            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }




    }

    private void initialiseStatus()
    {
        commonMethods.maintainState(this,USER_HOME_PAGE_STATUS);
    }


    private void initialiseObjects()
    {
        commonMethods   =   new CommonMethods();
        userPreferences =   new UserPreferences(this);
        pharmacyDAO     =   new PharmacyDAO(this);
    }

    private void initialiseDrawerLayout()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, prlToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initialiseIDs()
    {
        viewPager   =   findViewById(R.id.prln_pager);
        tabLayout   =   findViewById(R.id.prln_tabs);
        adapter     =   new ViewPagerAdapter(getSupportFragmentManager());
        prlToolbar  =   findViewById(R.id.prln_toolbar);

        drawer      =   findViewById(R.id.drawer_layout);
        setSupportActionBar(prlToolbar);
        navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        profileIcon =   headerLayout.findViewById(R.id.pr_nav_imageview);
        profileName =   headerLayout.findViewById(R.id.pr_nav_title);
        toolbarHeading  =   findViewById(R.id.prln_toolbar_heading);

    }

    private void initialiseFragments()
    {
        // Add Fragments to adapter one by one
        adapter.addFragment(new PharmacyRunningListFragment(), "RUNNING LIST");
        adapter.addFragment(new PharmacyApprovedListFragment(), "APPROVED");
        adapter.addFragment(new PharmacyDeliveredListFragment(), "DELIVERED");
        viewPager.setAdapter(adapter);
    }

    private void initialiseClickListeners()
    {
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacyRunningListWithNavigation.this,PharmacyProfileView.class);
                startActivity(intent);
            }
        });
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
    public void onBackPressed() {
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
        getMenuInflater().inflate(R.menu.pharmacy_running_list_with_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent dbmanager = new Intent(PharmacyRunningListWithNavigation.this, AndroidDatabaseManager.class);
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
}
