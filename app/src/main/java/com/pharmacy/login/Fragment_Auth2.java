package com.pharmacy.login;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.pharmacy.AppConstants;
import com.pharmacy.CodeInput.CodeInput;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.agent.AgentPharmacyListWithNavigation;
import com.pharmacy.agent.AgentRegistration;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.models.GetUserDetailsRequest;
import com.pharmacy.models.VerifyResponse;
import com.pharmacy.models.VerifyUser;
import com.pharmacy.operations.Post;
import com.pharmacy.pharmacy.PharmacyRegistration;
import com.pharmacy.pharmacy.PharmacyRunningListWithNavigation;
import com.pharmacy.preferences.UserPreferences;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class Fragment_Auth2 extends Fragment implements AdapterView.OnItemSelectedListener , AppConstants{
    private static final String TAG = "PhoneAuthActivity";
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    public static TextView  phone_number_tv, otp_tv;
    public EditText         enter_phone_number;
    public String           otp_number;
    public String           mVerificationId;
    ProgressBar             progress_loading, progress_loading_otp;
    String[]                countryNames;
    RelativeLayout          right_image;
    ImageView               right_image_icon;
    TextView                bottom_bar_textview;
    TextView                bottom_bar_textview2;
    int                     spinner_position;
    String                  country_code;
    ImageView               edit_phone_number;
    RelativeLayout          bottom_bar;
    String                  _phoneNumber = "";
    String[]                code1;
    Spinner                 spin;

    LinearLayout            fragment3_layout, fragment2_layout;
    CodeInput               cInput;
    boolean                 verify = false;
    private String          CountryZipCode;
 //   private GoogleCloudMessaging gcm;
    private UserPreferences userPreferences;
    CommonMethods commonMethods;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;

    Gson gson;

    AlertDialog alertDialog;

    private String deviceUniqueID = "";
    public Fragment_Auth2() {
        // Required empty public constructor
        gson = new Gson();
        commonMethods   = new CommonMethods();
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment__auth2, container, false);

        spin                    =   view.findViewById(R.id.simpleSpinner);
        enter_phone_number      =   view.findViewById(R.id.enter_phone_number);
        right_image             =   view.findViewById(R.id.right_image);
        right_image_icon        =   view.findViewById(R.id.right_image_icon);
        bottom_bar_textview     =   view.findViewById(R.id.bottom_bar_textview);
        bottom_bar_textview2    =   view.findViewById(R.id.bottom_bar_textview2);
        fragment3_layout        =   view.findViewById(R.id.fragment3_layout);
        fragment2_layout        =   view.findViewById(R.id.fragment2_layout);
        progress_loading        =   view.findViewById(R.id.progress_loading);
        progress_loading_otp    =   view.findViewById(R.id.progress_loading_otp);
        progress_loading_otp.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
        progress_loading.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
        phone_number_tv         =   view.findViewById(R.id.phone_number_tv);
        edit_phone_number       =   view.findViewById(R.id.edit_phone_number);
        bottom_bar              =  view.findViewById(R.id.bottom_bar);
/*        relative_main_header = (RelativeLayout) view.findViewById(R.id.relative_main_header);*/
        cInput                  =  view.findViewById(R.id.code_input);
        otp_tv                  =  view.findViewById(R.id.otp_tv);
        cInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        userPreferences         = new UserPreferences(getActivity());

        alertDialog             =   new SpotsDialog(getContext());

        cInput.setCodeReadyListener(new CodeInput.codeReadyListener() {
            @Override
            public void onCodeReady(Character[] code) {
                if (CommonMethods.isInternetConnected(getActivity())) {
                    hideKeyboard(getActivity());
                    cInput.hideKeyBoard();
                    progress_loading_otp.setVisibility(View.VISIBLE);
                    bottom_bar_textview2.setClickable(false);
                    bottom_bar_textview2.setEnabled(false);
                    onNext();
                } else {
                    Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();

            if (mPhoneNumber != null) {
                enter_phone_number.setText(mPhoneNumber);
                enter_phone_number.setSelection(mPhoneNumber.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        edit_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                right_image.setVisibility(View.VISIBLE);
                enter_phone_number.setEnabled(true);
                enter_phone_number.setClickable(true);

                spin.setEnabled(true);
                spin.setClickable(true);

                cInput.setCode("");

                progress_loading.setVisibility(View.GONE);
                fragment2_layout.setVisibility(View.VISIBLE);
                fragment3_layout.setVisibility(View.GONE);
                bottom_bar_textview.setVisibility(View.VISIBLE);
                bottom_bar_textview.setClickable(true);
                bottom_bar_textview.setEnabled(true);

                bottom_bar_textview2.setVisibility(View.GONE);
                progress_loading_otp.setVisibility(View.GONE);

//                AppSettings_new.pager.setCurrentItem(AppSettings_new.pager.getCurrentItem() - 1, true);
                AppSettings_new.bar3.getBackground().setColorFilter(getResources().getColor(R.color.lightgray), PorterDuff.Mode.SRC_ATOP);
            }
        });


        otp_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonMethods.isInternetConnected(getActivity())) {
                    resendVerificationCode(phone_number_tv.getText().toString(), mResendToken);
                } else {
                    Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                }

            }
        });

        try {
            spin.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
            spin.setOnItemSelectedListener(this);

            countryNames = getActivity().getResources().getStringArray(R.array.country_code);

            CustomAdapter customAdapter = new CustomAdapter(getActivity(), countryNames);
            spin.setAdapter(customAdapter);

            String zipcode = getUserCountry(getActivity());
            userPreferences = new UserPreferences(getActivity());
            String name = "";
            if (!StringUtils.isBlank(zipcode)) {
                Locale loc = new Locale("", zipcode);
                name = loc.getDisplayCountry();
            }


            try {
                String[] rl = this.getResources().getStringArray(R.array.country_code);
                for (int i = 0; i < rl.length; i++) {
                    String[] g = rl[i].split("\\(");
                    if (g[0].trim().equals(name)) {
                        CountryZipCode = g[0];
                        country_code = g[1];
                        spinner_position = i;
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            String code = country_code;
            code1 = code.split("\\)");
            System.out.println("CountryZipCode " + CountryZipCode);


            spin.setSelection(spinner_position);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        if (enter_phone_number.getText().toString().length() > 3) {
            right_image_icon.setBackgroundResource(R.drawable.vector_check_right_white_icon);
            right_image_icon.getBackground().setColorFilter(Color.parseColor("#1f8005"), PorterDuff.Mode.SRC_ATOP);
            right_image.setClickable(true);
            right_image.setEnabled(true);
            bottom_bar_textview.setEnabled(true);
            bottom_bar_textview.setTextColor(getResources().getColor(R.color.colorWhite));
            bottom_bar_textview.setClickable(true);
        } else {
            right_image_icon.setBackgroundResource(R.drawable.vector_check_right_white_icon);
            right_image_icon.getBackground().setColorFilter(Color.parseColor("#33000000"), PorterDuff.Mode.SRC_ATOP);
            right_image.setClickable(false);
            bottom_bar_textview.setClickable(false);
            right_image.setEnabled(false);
            bottom_bar_textview.setEnabled(false);
            bottom_bar_textview.setTextColor(getResources().getColor(R.color.colorWhite));
        }

        enter_phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 3) {
                    right_image_icon.setBackgroundResource(R.drawable.vector_check_right_white_icon);
                    right_image_icon.getBackground().setColorFilter(Color.parseColor("#1f8005"), PorterDuff.Mode.SRC_ATOP);
                    right_image.setClickable(true);
                    right_image.setEnabled(true);
                    bottom_bar_textview.setEnabled(true);
                    bottom_bar_textview.setTextColor(getResources().getColor(R.color.colorWhite));
                    bottom_bar_textview.setClickable(true);
                } else {
                    right_image_icon.setBackgroundResource(R.drawable.vector_check_right_white_icon);
                    right_image_icon.getBackground().setColorFilter(Color.parseColor("#33000000"), PorterDuff.Mode.SRC_ATOP);
                    right_image.setClickable(false);
                    bottom_bar_textview.setClickable(false);
                    right_image.setEnabled(false);
                    bottom_bar_textview.setEnabled(false);
                    bottom_bar_textview.setTextColor(getResources().getColor(R.color.colorWhite));
                }

            }
        });
        right_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonMethods.isInternetConnected(getActivity())) {



                    onNextFrag();
                } else {
                    Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
        bottom_bar_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonMethods.isInternetConnected(getActivity())) {
                    onNextFrag();
                } else {
                    Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                }

            }
        });
        bottom_bar_textview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(cInput.getCode()) != null) {
                    if (CommonMethods.isInternetConnected(getActivity())) {
                        progress_loading_otp.setVisibility(View.VISIBLE);
                        bottom_bar_textview2.setClickable(false);
                        bottom_bar_textview2.setEnabled(false);
                        onNext();
                    } else {
                        Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                }


            }
        });

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                Log.d(TAG, "onVerificationCompleted string:" + credential.toString());
                Log.d(TAG, "onVerificationCompleted sms code:" + credential.getSmsCode());
                Log.d(TAG, "onVerificationCompleted get provider:" + credential.getProvider());

                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    enter_phone_number.setError("Invalid phone number");
                    progress_loading.setVisibility(View.GONE);
                    right_image.setVisibility(View.VISIBLE);
                    bottom_bar_textview.setClickable(true);
                    bottom_bar_textview.setEnabled(true);
                    enter_phone_number.setEnabled(true);
                    enter_phone_number.setClickable(true);

                    spin.setEnabled(true);
                    spin.setClickable(true);
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    progress_loading.setVisibility(View.GONE);
                    right_image.setVisibility(View.VISIBLE);
                    bottom_bar_textview.setClickable(true);
                    bottom_bar_textview.setEnabled(true);
                    enter_phone_number.setEnabled(true);
                    enter_phone_number.setClickable(true);

                    spin.setEnabled(true);
                    spin.setClickable(true);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseNetworkException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    progress_loading.setVisibility(View.GONE);
                    right_image.setVisibility(View.VISIBLE);
                    bottom_bar_textview.setClickable(true);
                    bottom_bar_textview.setEnabled(true);
                    enter_phone_number.setEnabled(true);
                    enter_phone_number.setClickable(true);

                    spin.setEnabled(true);
                    spin.setClickable(true);
                    Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    progress_loading.setVisibility(View.GONE);
                    right_image.setVisibility(View.VISIBLE);
                    bottom_bar_textview.setClickable(true);
                    bottom_bar_textview.setEnabled(true);
                    enter_phone_number.setEnabled(true);
                    enter_phone_number.setClickable(true);

                    spin.setEnabled(true);
                    spin.setClickable(true);
                    Toast.makeText(getActivity(), "Please try again Later", Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                Log.d(TAG, "onVerificationToken : " + token);
                Log.d(TAG, "onVerificationId : " + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };


        return view;


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) {
          //  showKeyboard(getActivity());
            try {
                TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                String mPhoneNumber = tMgr.getLine1Number();

                if (mPhoneNumber != null) {
                    enter_phone_number.setText(mPhoneNumber);
                    enter_phone_number.setSelection(mPhoneNumber.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void onNext() {

        try {
            String code = String.valueOf(Arrays.toString(cInput.getCode()));
            verifyPhoneNumberWithCode(mVerificationId, code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

       /* // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(enter_phone_number.getText().toString());
        }*/
        // [END_EXCLUDE]
    }





    public void onNextFrag() {
        AppSettings_new.bar3.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
        final String final_phone_number;
        enter_phone_number.setEnabled(false);
        enter_phone_number.setClickable(false);
        spin.setEnabled(false);
        spin.setClickable(false);
        bottom_bar_textview.setClickable(false);
        bottom_bar_textview.setEnabled(false);


        progress_loading.setVisibility(View.VISIBLE);
        right_image.setVisibility(View.GONE);

        if (enter_phone_number.getText().toString().startsWith("+")) {
            final_phone_number = enter_phone_number.getText().toString();
            phone_number_tv.setText(final_phone_number);
            if (isValidPhoneNumber(final_phone_number)) {
                befoeStartPhoneNumberFirebaseVerification(final_phone_number);
            } else {
                phoneNumberInValid("Invalid Phone Number");
            }
        } else {
            final_phone_number = code1[0].concat(enter_phone_number.getText().toString());
            phone_number_tv.setText(final_phone_number);
            if (isValidPhoneNumber(final_phone_number)) {
                boolean status = validateUsing_libphonenumber(code1[0], enter_phone_number.getText().toString());
                if (status) {
                    enter_phone_number.setError(null);
                    befoeStartPhoneNumberFirebaseVerification(final_phone_number);
                } else {
                    phoneNumberInValid("Invalid Phone Number");
                }
                } else
                {
                    phoneNumberInValid("Invalid Phone Number");
                }
        }


    }

    private void befoeStartPhoneNumberFirebaseVerification(final String _phoneNumber){
        final VerifyUser verifyUser = new VerifyUser();
        verifyUser.DistributorID="1";
        verifyUser.UserType     =   userPreferences.getUserLoginType();
        verifyUser.PhoneNumber  =  _phoneNumber;

        Post post = new Post(getContext(),CommonMethods.REGISTRATION_VERIFY_USER,gson.toJson(verifyUser)) {
            @Override
            public void onResponseReceived(String result) {
                if(result!=null) {
                    VerifyResponse verifyResponse = gson.fromJson(result,VerifyResponse.class);
                    if(verifyResponse!=null) {
                        if(verifyResponse.Status.toString().equalsIgnoreCase("Success")) {
                            startPhoneNumberVerification(_phoneNumber);
                        }
                        else
                        {if(verifyUser.UserType.toString().equalsIgnoreCase(getContext().getString(R.string.agent)))
                        {
                            phoneNumberInValid("Phone Number Already Registered with "+getContext().getString(R.string.pharmacy));
                        }
                        else
                        {
                            phoneNumberInValid("Phone Number Already Registered with "+getContext().getString(R.string.agent));
                        }
                        }
                    }
                    else
                    {
                        phoneNumberInValid("Server down..");
                    }
                }
                else
                {
                    phoneNumberInValid("Invalid Phone Number");
                }

            }
        };
        post.execute();

    }

    private void phoneNumberInValid(String message)
    {
        enter_phone_number.setError(message);
        progress_loading.setVisibility(View.GONE);
        right_image.setVisibility(View.VISIBLE);
        bottom_bar_textview.setClickable(true);
        bottom_bar_textview.setEnabled(true);
        enter_phone_number.setEnabled(true);
        enter_phone_number.setClickable(true);
        spin.setEnabled(true);
        spin.setClickable(true);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String[] g = countryNames[position].split("\\(");
        code1 = g[1].split("\\)");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    getActivity(),               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
            // [END start_phone_auth]

            mVerificationInProgress = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void verifyPhoneNumberWithCode(String verificationId, String code) {
        otp_tv.setEnabled(false);
        otp_tv.setClickable(false);
        cInput.setClickable(false);
        cInput.setEnabled(false);
        edit_phone_number.setEnabled(false);
        edit_phone_number.setClickable(false);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();
                            verify = true;
                            user.getToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                String idToken = task.getResult().getToken();
                                                // Send token to your backend via HTTPS
                                                userPreferences.setAccessToken(idToken);
                                                _phoneNumber = phone_number_tv.getText().toString();
                                                try {
                                                    TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                                                    deviceUniqueID = telephonyManager.getDeviceId();

                                                }catch (Exception e)
                                                {
                                                    e.printStackTrace();
                                                }
                                                registerInBackground();
                                            } else {
                                            }
                                        }
                                    });

                            // [START_EXCLUDE]
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {

                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
//                                number6.setError("Invalid code");
//
                                bottom_bar_textview2.setClickable(true);
                                bottom_bar_textview2.setEnabled(true);
                                otp_tv.setEnabled(true);
                                otp_tv.setClickable(true);

                                cInput.setEnabled(true);
                                cInput.setClickable(true);
                                if (verify == true) {

                                } else {
                                    progress_loading_otp.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Check the code", Toast.LENGTH_SHORT).show();
                                }

                                edit_phone_number.setEnabled(true);
                                edit_phone_number.setClickable(true);
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                break;
            case STATE_CODE_SENT:
                fragment3_layout.setVisibility(View.VISIBLE);
                fragment2_layout.setVisibility(View.GONE);
                bottom_bar_textview.setVisibility(View.GONE);
                bottom_bar_textview2.setVisibility(View.VISIBLE);
                progress_loading.setVisibility(View.GONE);
                hideKeyboard(getActivity());


                AppSettings_new.bar3.getBackground().setColorFilter(getResources().getColor(R.color.bottom_bar_color), PorterDuff.Mode.SRC_ATOP);
                Toast.makeText(getActivity(), "Code sent", Toast.LENGTH_SHORT).show();
                break;
            case STATE_VERIFY_FAILED:
                break;
            case STATE_VERIFY_SUCCESS:

                if (cred.getSmsCode() != null) {
                    otp_number = cred.getSmsCode();
                }
                if (otp_number != null) {
                    try {
                        cInput.setCode(otp_number);
                        progress_loading_otp.setVisibility(View.VISIBLE);
                        bottom_bar_textview2.setClickable(false);
                        bottom_bar_textview2.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = enter_phone_number.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            enter_phone_number.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private void addToken(String deviceuniqueid, String userid, String phonenumber,
                          String nickName, String email, String companyname, String profilePicture, String useridFromServer, String Personalstatus) {

        userPreferences.setUserGid(useridFromServer);
        userPreferences.setDeviceUniqueID(deviceuniqueid);


    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    private boolean validateUsing_libphonenumber(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            return true;
        } else {
            return false;
        }
    }

    private void registerInBackground() {

        if(CommonMethods.isInternetConnected(getContext())) {
            alertDialog.show();
            new LongRunningGetIO().execute();
        }
    }


    private String getDeviceID()
    {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.i("tag","token is"+token);
            userPreferences.setFirebaseToken(token);
            return token;

    }

    public class LongRunningGetIO extends AsyncTask<String, String, String> {
        StringBuilder s;
        String json;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            UserModel request = new UserModel();
            request.PhoneNumber = _phoneNumber;
            request.DeviceId    =getDeviceID() ;
            request.DeviceUniqueId = deviceUniqueID;
            request.DeviceType  = "Android";
            request.UserID      =   "0";
            request.UserType    =   userPreferences.getUserLoginType();
            request.DistributorID   = "1";
            request.IsVerified  =   true;
            Gson gson = new Gson();
            json = gson.toJson(request);
        }

        protected String doInBackground(String... params) {
            String result = "";
            URL url;
            try {
                url = new URL(CommonMethods.REGISTRATION_USER);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                //conn.setRequestProperty("token",mainActivity.GetServerToken(getActivity()));

                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(json);

                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        result += line;
                    }
                } else {
                    result = "";

                }

            } catch (Exception ex) {
                ex.printStackTrace();
                result = ex.getMessage();
            }
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
//                pdialogue.dismiss();
                if (!StringUtils.isBlank(response) && response.contains("Unable to resolve host")) {
//                    mainActivity.show_snakbar(getString(R.string.check_internet),relative_main_header);
//                    clearTwitterActiveSession();
                    Toast.makeText(getContext(),"Unable to resolve host",Toast.LENGTH_LONG).show();

                    alertDialog.dismiss();

                } else if (!StringUtils.isBlank(response) && response.contains("failed to connect")) {
//                    mainActivity.show_snakbar(getString(R.string.noServerResponse),relative_main_header);
//                    clearTwitterActiveSession();
                    Toast.makeText(getContext(),"failed to connect",Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                } else {
                    if (!StringUtils.isBlank(response)) {

                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.get("Status").toString().equalsIgnoreCase("Success"))
                        {
                            UserModel userModel = gson.fromJson(jsonObject.get("Response").toString(),UserModel.class);
                            UserDAO userDAO = new UserDAO(getContext());
                            if(userDAO.insert(userModel)!= -1) {
                                userPreferences.setUserGid(userModel.UserID);
                                Intent intent=null;
                                if(userPreferences.getUserLoginType().toString().equalsIgnoreCase(getContext().getString(R.string.agent))) {

                                    if(userModel.IsNewUser) {
                                        alertDialog.dismiss();
                                        intent = new Intent(getContext(), AgentRegistration.class);
                                    }
                                    else
                                    {
                                       // alertDialog.setMessage("Agent Details Loading..");
                                        GetUserDetailsRequest getUserDetailsRequest = new GetUserDetailsRequest();
                                        getUserDetailsRequest.UserID    =   userPreferences.getUserGid();
                                        getUserDetailsRequest.UserType  =   getContext().getString(R.string.agent);
                                        getUserDetailsRequest.DistributorID =   userModel.DistributorID;
                                        getUserDetailsRequest.LastUpdatedTimeTicks = userPreferences.getGetAllUserDetailsTimeticks();
                                        String json = gson.toJson(getUserDetailsRequest);
                                        Post post = new Post(getContext(),CommonMethods.GET_USER_DETAILS,json) {
                                            @Override
                                            public void onResponseReceived(String result) {
                                                if(result!=null)
                                                {
                                                    try {
                                                        JSONObject jsonObject1 = new JSONObject(result);
                                                        if(jsonObject1.get(getContext().getString(R.string.status)).toString().equalsIgnoreCase(getContext().getString(R.string.success)))
                                                        {
                                                           if(jsonObject1.get(getContext().getString(R.string.response))!=null) {
                                                               JSONObject jsonObject2 = jsonObject1.getJSONObject(getContext().getString(R.string.response));

                                                               try {
                                                                   if (jsonObject2.get(getContext().getString(R.string.userdetails)) != null) {
                                                                       AgentModel agentModel = gson.fromJson(jsonObject2.get(getContext().getString(R.string.userdetails)).toString(), AgentModel.class);
                                                                       Long id = commonMethods.renderLoginDataForAgent(getContext(), agentModel);
                                                                       if (id != -1) {
                                                                           alertDialog.dismiss();
                                                                           Intent intent = new Intent(getContext(), AgentPharmacyListWithNavigation.class);
                                                                           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                           startActivity(intent);
                                                                       }
                                                                   }
                                                               }catch (Exception e)
                                                               {
                                                                   e.printStackTrace();
                                                               }
                                                               String ticks = jsonObject2.get(getContext().getString(R.string.LastUpdatedTimeTicks)).toString();
                                                               userPreferences.setGetAllUserDetailsTimeticks(ticks);

                                                           }


                                                        }
                                                        else
                                                        {
                                                            alertDialog.dismiss();
                                                             Toast.makeText(getContext(),"Something wrong",Toast.LENGTH_LONG).show();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else
                                                {
                                                    alertDialog.dismiss();
                                                }
                                            }
                                        };
                                        post.execute();
                                    }
                                }
                                else
                                {
                                    if(userModel.IsNewUser)
                                    {
                                        alertDialog.dismiss();
                                        intent = new Intent(getContext(), PharmacyRegistration.class);
                                    }else
                                    {
                                    //    alertDialog.setMessage("Pharmacy Details Loading..");

                                        GetUserDetailsRequest getUserDetailsRequest = new GetUserDetailsRequest();
                                        getUserDetailsRequest.UserID    =   userPreferences.getUserGid();
                                        getUserDetailsRequest.UserType  =   getContext().getString(R.string.pharmacy);
                                        getUserDetailsRequest.DistributorID =   userModel.DistributorID;
                                        getUserDetailsRequest.LastUpdatedTimeTicks = userPreferences.getGetAllUserDetailsTimeticks();

                                        String json = gson.toJson(getUserDetailsRequest);
                                        Post post = new Post(getContext(),CommonMethods.GET_USER_DETAILS,json) {
                                            @Override
                                            public void onResponseReceived(String result) {
                                                if(result!=null)
                                                {
                                                    try {
                                                        JSONObject jsonObject1 = new JSONObject(result);
                                                        if(jsonObject1.get(getContext().getString(R.string.status)).toString().equalsIgnoreCase(getContext().getString(R.string.success)))
                                                        {
                                                            if(jsonObject1.get(getContext().getString(R.string.response))!=null) {
                                                                JSONObject jsonObject2 = jsonObject1.getJSONObject(getContext().getString(R.string.response));

                                                                String ticks = jsonObject2.get(getContext().getString(R.string.LastUpdatedTimeTicks)).toString();
                                                                userPreferences.setGetAllUserDetailsTimeticks(ticks);

                                                                if(jsonObject2.get(getContext().getString(R.string.userdetails))!=null) {
                                                                    PharmacyModel pharmacyModel = gson.fromJson(jsonObject2.get(getContext().getString(R.string.userdetails)).toString(), PharmacyModel.class);
                                                                    Long id = commonMethods.renderLoginDataForPharmacy(getContext(), pharmacyModel);
                                                                    if (id != -1) {
                                                                        userPreferences.setPharmacyRegisterLocalUserId(pharmacyModel.PharmacyLocalId);
                                                                        alertDialog.dismiss();
                                                                        Intent intent = new Intent(getContext(), PharmacyRunningListWithNavigation.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    alertDialog.dismiss();
                                                                }

                                                            }
                                                        }
                                                        else
                                                        {
                                                            alertDialog.dismiss();
                                                            Toast.makeText(getContext(),"Something wrong",Toast.LENGTH_LONG).show();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else{
                                                    alertDialog.dismiss();

                                                }
                                            }
                                        };
                                        post.execute();
                                    }

                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getContext(),
                                        R.anim.next_swipe2, R.anim.next_swipe1).toBundle();

                                startActivity(intent,bndlanimation);
                            }
                            else
                            {
                                alertDialog.dismiss();
                                Toast.makeText(getContext(),"Something wrong",Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            alertDialog.dismiss();
                        }

                    } else {
                        alertDialog.dismiss();
                        Toast.makeText(getContext(),"wrong",Toast.LENGTH_LONG).show();
                       // mainActivity.show_snakbar(getString(R.string.noServerResponse), relative_main_header);
                    }
                }
            } catch (Exception e) {
                alertDialog.dismiss();
                if (progress_loading.getVisibility() == View.VISIBLE) {
                    progress_loading.setVisibility(View.GONE);
                    right_image.setVisibility(View.VISIBLE);
                    bottom_bar_textview.setClickable(true);
                    bottom_bar_textview.setEnabled(true);
                    enter_phone_number.setEnabled(true);
                    enter_phone_number.setClickable(true);
                    spin.setClickable(true);
                    spin.setEnabled(true);
                }
                if (progress_loading_otp.getVisibility() == View.VISIBLE) {
                    progress_loading_otp.setVisibility(View.GONE);
                    edit_phone_number.setClickable(true);
                    edit_phone_number.setEnabled(true);

                    bottom_bar_textview2.setClickable(true);
                    bottom_bar_textview2.setEnabled(true);
                    cInput.setEnabled(true);
                    cInput.setClickable(true);
                }


                spin.setEnabled(true);
                spin.setClickable(true);
                Log.d("PostSignIn", "Error: " + e.getMessage());
                e.printStackTrace();
            }


        }
    }
}
