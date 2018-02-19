package com.pharmacy.login;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.google.firebase.FirebaseApp;
import com.pharmacy.R;

import java.util.ArrayList;
import java.util.List;

public class AppSettings_new extends AppCompatActivity {

    public static Button bar1, bar2, bar3, bar4;
    public static LockableViewPager pager;
    MyPageAdapter pageAdapter;
    ImageView settings;

    Fragment_Auth2 fragment_auth2;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_app_settings_new);
            FirebaseApp.initializeApp(this);


            List<Fragment> fragments = getFragments();
            pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
            pager = (LockableViewPager) findViewById(R.id.pager);
            bar1 = (Button) findViewById(R.id.bar1);
            bar2 = (Button) findViewById(R.id.bar2);
            bar3 = (Button) findViewById(R.id.bar3);
            bar4 = (Button) findViewById(R.id.bar4);
          /*  settings = (ImageView) findViewById(R.id.settings);
            settings.setVisibility(View.GONE);
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //showDialogForUrlChange();
                }
            });
*/
            toolbar = findViewById(R.id.top_bar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow =getResources().getDrawable(R.drawable.vector_back_white_icon);
            upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);


            pager.setAdapter(pageAdapter);
            pager.setSwipeable(false);

            if (pager.getCurrentItem() == 0) {
                bar1.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                bar2.getBackground().setColorFilter(getResources().getColor(R.color.lightgray), PorterDuff.Mode.SRC_ATOP);
                bar3.getBackground().setColorFilter(getResources().getColor(R.color.lightgray), PorterDuff.Mode.SRC_ATOP);
                bar4.getBackground().setColorFilter(getResources().getColor(R.color.lightgray), PorterDuff.Mode.SRC_ATOP);
            }
            if (pager.getCurrentItem() == 1) {
                bar1.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                bar2.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                bar3.getBackground().setColorFilter(getResources().getColor(R.color.lightgray), PorterDuff.Mode.SRC_ATOP);
                bar4.getBackground().setColorFilter(getResources().getColor(R.color.lightgray), PorterDuff.Mode.SRC_ATOP);
            }
            if (pager.getCurrentItem() == 2) {
                bar1.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                bar2.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                bar3.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                bar4.getBackground().setColorFilter(getResources().getColor(R.color.lightgray), PorterDuff.Mode.SRC_ATOP);
            }
            if (pager.getCurrentItem() == 3) {
                bar1.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                bar2.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                bar3.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                bar4.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        try {

            fragment_auth2 = new Fragment_Auth2();
            fList.add(fragment_auth2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fList;
    }


    @Override
    protected void onResume() {
        try {
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(AppSettings_new.this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(AppSettings_new.this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(AppSettings_new.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(AppSettings_new.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    ) {
                bar1.setVisibility(View.VISIBLE);
                pager.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pager.setCurrentItem(0);
                    }
                }, 100);

            } else {
                pager.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (pager.getCurrentItem() == 2) {

                        } else {
                            pager.setCurrentItem(1);
                        }

                    }
                }, 100);


                bar1.setVisibility(View.GONE);
                bar2.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
