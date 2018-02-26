package com.pharmacy;

import android.*;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.PixelCopy;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.pharmacy.adapters.GooglePlacesSearchAdapter;
import com.pharmacy.agent.AddNewPharmacyStepOne;
import com.pharmacy.agent.AgentRegistration;
import com.pharmacy.agent.EditAgentProfileView;
import com.pharmacy.agent.EditPharmacyProfileView;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.models.PickLocation;
import com.pharmacy.pharmacy.PharmacyProfileView;
import com.pharmacy.pharmacy.PharmacyRegistration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PickLocationActivity extends AppCompatActivity implements OnMapReadyCallback ,AppConstants {
    GoogleMap googleMap;
    Geocoder geocoder;
    MarkerOptions markerOptions;
    LatLng coordinate;
    List<Address> addresses;
    private String TAG = "GoogleMapBottomSheet";
    SharedPreferences app_preference = null;
    public static final String MyPREFERENCES = null;
    EditText searchPlaceET;
    TextView searchIcon, LocationTitle, LocationDetails, picked_text, next_text;
    PickLocation locationModel;
    String  agentModel;

    private static final String LOG_TAG = "AutoCompleteSearch";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyAeM8X9xW-LCe0cnECUjxGqybBMcHxzoIg";
    RelativeLayout searchListLayout, mapFullLayout;
    RelativeLayout backLayout;
    ListView listView;
    boolean  isAddressEditClicked = false, addressDisplayed = false;
    CardView searchHeaderLayout;
    Gson gson;
    Toolbar loginToolbar;
    String json ;
    String countryCode;


    String[] PERMISSIONS = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    private static final int PERMISSION_REQUEST_CODE = 1;

    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        loginToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(loginToolbar);
       // initialiseBackButton();
        backButton      =   findViewById(R.id.back_icon);

        locationModel = new PickLocation();
        gson = new Gson();
        app_preference = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mapFullLayout = (RelativeLayout) findViewById(R.id.mapRelLayout);
        searchHeaderLayout = (CardView) findViewById(R.id.searchHeaderLayout);
        FloatingActionButton gpslocation = (FloatingActionButton) findViewById(R.id.myLocationButton);
        gpslocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowGoogleMapWithCurrentLocation();
            }
        });
        searchHeaderLayout.setVisibility(View.VISIBLE);
        searchPlaceET = (EditText) findViewById(R.id.search_field_editT);
        searchIcon = (TextView) findViewById(R.id.searchicon);
        listView = (ListView) findViewById(R.id.lisiview);
      //  backLayout = (RelativeLayout) findViewById(R.id.backLayout);
        LocationTitle = (TextView) findViewById(R.id.LocationTitle);
        LocationDetails = (TextView) findViewById(R.id.LocationDetails);
        searchListLayout = (RelativeLayout) findViewById(R.id.searchListLayout);
        app_preference = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Intent intent = getIntent();
        MapFragment supportMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.googleMap);
        supportMapFragment.getMapAsync(this);



        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPlaceET.getText().toString() != null &&
                        !searchPlaceET.getText().toString().equalsIgnoreCase("")) {
                    searchPlaceET.setText(null);
                }
            }
        });
        searchPlaceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence input, int start, int before,
                                      int count) {
                try {
                    mapFullLayout.setVisibility(View.GONE);
                    boolean isLive = CommonMethods.isInternetConnected(PickLocationActivity.this);
                    if (!isLive) {
                        Toast.makeText(PickLocationActivity.this,"Check Internet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (input.length() >= 2) {
                        DemoTask getPlaces = new DemoTask(input.toString());
                        ArrayList<String> resultList = getPlaces.execute().get();
                        searchIcon.setText(R.string.fontello_x_mark_masked);
                        if (resultList != null) {
                            mapFullLayout.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            searchListLayout.setVisibility(View.VISIBLE);
                            listView.setAdapter(new GooglePlacesSearchAdapter(PickLocationActivity.this, resultList));
                        }
                    } else {
                        listView.setAdapter(null);
                        listView.setVisibility(View.GONE);
                        searchListLayout.setVisibility(View.GONE);
                        searchIcon.setText(R.string.fontello_search);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedPlace = null;
                selectedPlace = (String) parent.getItemAtPosition(position);
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);

                    String[] placeArray = selectedPlace.split("\\,");
                    String firstName = placeArray[0];
                    SharedPreferences.Editor edit = app_preference.edit();
                    edit.putString("LocalName", firstName);
                    edit.commit();
                    searchPlaceET.setText(firstName);
                    searchListLayout.setVisibility(View.GONE);
                    mapFullLayout.setVisibility(View.VISIBLE);
                    if (Geocoder.isPresent()) {
                        GetCoordinatesFromPlace(selectedPlace);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent1=null;
                if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.pharmacyRegistration)))
                {
                    intent1 = new Intent(PickLocationActivity.this, PharmacyRegistration.class);
                }
                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.agentRegistration)))
                {
                    intent1 = new Intent(PickLocationActivity.this, AgentRegistration.class);
                }
                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.add_new_pharmacy)))
                {
                    intent1 = new Intent(PickLocationActivity.this, AddNewPharmacyStepOne.class);

                }
                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.agentEdit)))
                {
                    intent1 = new Intent(PickLocationActivity.this, EditAgentProfileView.class);

                }
                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.pharmacy_new_edit)))
                {
                    intent1 = new Intent(PickLocationActivity.this, EditPharmacyProfileView.class);

                }
                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.pharmacyEdit)))
                {
                    intent1 = new Intent(PickLocationActivity.this, EditPharmacyProfileView.class);

                }


                Bundle bndlanimation1 = ActivityOptions
                        .makeCustomAnimation(getApplicationContext(),
                                R.anim.back_swipe2, R.anim.back_swipe1).toBundle();
                startActivity(intent1, bndlanimation1);

            }
        });


    }






    private void ShowGoogleMapWithCurrentLocation() {
        double latitude = 0.0, longitude = 0.0;
        try {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            }
            else {
                googleMap.setMyLocationEnabled(true);
                googleMap.setMyLocationEnabled(true);
                geocoder = new Geocoder(PickLocationActivity.this, Locale.getDefault());

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Location location = null;
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    onLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
                } else {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        onLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                }
                if (!isAddressEditClicked) {
                    if (location != null) {
                        onLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
                coordinate = new LatLng(latitude, longitude);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(coordinate));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 1));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                markerOptions.position(coordinate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }




    private void requestPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                    ) {

                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {

                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLocationChanged(LatLng latLng) {
        String detailAddressShowing = null;
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        coordinate = new LatLng(latitude, longitude);
        googleMap.clear();
        String postalcode;
        markerOptions = new MarkerOptions();
        SharedPreferences.Editor edit;
        geocoder = new Geocoder(PickLocationActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.v("log_tag", "addresses+)_+++" + addresses);

            locationModel.Landmark = addresses.get(0).getSubLocality();
            locationModel.City = addresses.get(0).getLocality();
            locationModel.State = addresses.get(0).getAdminArea();
            locationModel.Country = addresses.get(0).getCountryName();
            locationModel.DetailAddress = addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1);
            postalcode = addresses.get(0).getPostalCode();
            locationModel.Pincode = postalcode;
            locationModel.Latitude = addresses.get(0).getLatitude();
            locationModel.Longitude = addresses.get(0).getLongitude();
            countryCode = addresses.get(0).getCountryCode();
            try {

                edit = app_preference.edit();
                edit.putString("CountryCode", countryCode);
                edit.putString("Longitude",String.valueOf(locationModel.Longitude));
                edit.putString("Latitude", String.valueOf(locationModel.Latitude));
                edit.commit();

                if (postalcode != null) {
                    edit.putString("NewPincode", postalcode);
                    edit.commit();
                } else {
                    for (int i = 0; i < 10; i++) {
                        String addressDetails = addresses.get(0).getAddressLine(i);
                        if (addressDetails != null) {
                            postalcode = CommonMethods.containsPinCode(addressDetails);
                            if (postalcode != null && !postalcode.equalsIgnoreCase("")) {
                                edit = app_preference.edit();
                                edit.putString("NewPincode", postalcode);
                                edit.commit();
                                locationModel.Pincode = postalcode;
                                break;
                            }

                        } else
                            break;
                    }
                }
                if (!addressDisplayed) {
                    String localArea = addresses.get(0).getAddressLine(0) + ","
                            + addresses.get(0).getAddressLine(1) + "," + locationModel.State + " " + postalcode;
                    LocationTitle.setText(locationModel.Landmark);
                    LocationDetails.setText(localArea);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        markerOptions.position(coordinate);
        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(coordinate));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 1));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }


    class DemoTask extends AsyncTask<String, String, ArrayList<String>> {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        String input;

        public DemoTask(String inputtext) {
            input = inputtext;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=" + API_KEY);
                sb.append("&components=country:" + countryCode);
                sb.append("&input=" + URLEncoder.encode(input, "utf8"));

                URL url = new URL(sb.toString());

                System.out.println("URL: " + url);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error processing Places API URL", e);
                return resultList;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to Places API", e);
                return resultList;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            try {

                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());



                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                // Extract the Place descriptions from the results
                resultList = new ArrayList<String>(predsJsonArray.length());
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                    System.out.println("============================================================");
                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Cannot process JSON results", e);
            }
            return resultList;
        }

        protected void onPostExecute(Void result) {
            // TODO: do something with the feed
        }


    }

    public void GetCoordinatesFromPlace(String location) {
        try {
            SharedPreferences.Editor edit = app_preference.edit();
            Geocoder gc = new Geocoder(this);
            List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects
            List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
            for (Address addr : addresses) {
                if (addr.hasLatitude() && addr.hasLongitude()) {
                    edit.putString("Longitude", String.valueOf(addr.getLongitude()));
                    edit.putString("Latitude", String.valueOf(addr.getLatitude()));
                    edit.commit();
                    onLocationChanged(new LatLng(addr.getLatitude(), addr.getLongitude()));
                }
            }
        } catch (IOException e) {
        }
    }

    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        ShowGoogleMapWithCurrentLocation();

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "Map clicked");
                onLocationChanged(latLng);
            }
        });
    }

    private void initGoogleMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        supportMapFragment.getMapAsync(this);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap Map) {
                googleMap = Map;
                ShowGoogleMapWithCurrentLocation();
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.google_map_next, menu);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.next:
                json = gson.toJson(locationModel);
                Intent intent=null;
                if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.pharmacyRegistration)))
                {
                    intent = new Intent(PickLocationActivity.this, PharmacyRegistration.class);

                }
                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.agentRegistration)))
                {
                    intent = new Intent(PickLocationActivity.this, AgentRegistration.class);
                }
                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.add_new_pharmacy)))
                {
                    intent = new Intent(PickLocationActivity.this, AddNewPharmacyStepOne.class);

                }

                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.agentEdit)))
                {
                    intent = new Intent(PickLocationActivity.this, EditAgentProfileView.class);

                }
                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.pharmacy_new_edit)))
                {
                    intent = new Intent(PickLocationActivity.this, EditPharmacyProfileView.class);

                }
                else if(getIntent().getStringExtra(getResources().getString(R.string.from)).equalsIgnoreCase(getResources().getString(R.string.pharmacyEdit)))
                {
                    intent = new Intent(PickLocationActivity.this, EditPharmacyProfileView.class);

                }



                intent.putExtra("LocationJson", json);
                Bundle bndlanimation = ActivityOptions
                        .makeCustomAnimation(getApplicationContext(),
                                R.anim.back_swipe2, R.anim.back_swipe1).toBundle();
                startActivity(intent, bndlanimation);
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
