package com.pharmacy;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.gson.Gson;
import com.pharmacy.agent.AgentPharmacyListWithNavigation;
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.OrderDAO;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.models.GetUserDetailsRequest;
import com.pharmacy.operations.Post;
import com.pharmacy.operations.UploadFiles;
import com.pharmacy.preferences.UserPreferences;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PCCS-0007 on 05-Feb-18.
 */

public class CommonMethods implements AppConstants {


    public static String SITE_URL = "http://rameshbookmeds-001-site1.ftempurl.com/api/";

    public static String REGISTRATION_USER = "" + SITE_URL + "Registration/RegisterUser";
    public static String REGISTRATION_VERIFY_USER = "" + SITE_URL + "Registration/VerifyUser";
    public static String MEDIA_UPLOAD = SITE_URL + "Upload/MediaUpload";
    public static String CONFIRM_USER_REGISTRATION = SITE_URL + "Registration/ConfirmUserRegistration";
    public static String SEARCH_PRODUCT = SITE_URL + "User/SearchProduct";
    public static String ADD_TO_RUNNING_LIST = SITE_URL + "User/AddToRunningList";
    public static String ADD_NEW_PHARMACY = SITE_URL + "User/AddPharmacy";

    public static String GET_USER_DETAILS = SITE_URL + "Registration/GetUserDetails";
    public static String GET_ALL_MYLIST = SITE_URL + "User/GetPharmacyRunningList";
    public static String GET_AGENT_PHARMACY = SITE_URL + "User/GetAgentPharmacy";


    long id = -1;

    public void maintainState(Context context, String status) {
        UserPreferences userPreferences = new UserPreferences(context);
        if (status.toString().equalsIgnoreCase(LOGIN_REGISTRATION_STATUS)) {
            userPreferences.setUserBasicRegistrationStatus(true);
            userPreferences.setUserHomePageStatus(false);
            userPreferences.setUserProcssingStatus(false);
        } else if (status.toString().equalsIgnoreCase(USER_HOME_PAGE_STATUS)) {
            userPreferences.setUserBasicRegistrationStatus(false);
            userPreferences.setUserHomePageStatus(true);
            userPreferences.setUserProcssingStatus(false);

        } else if (status.toString().equalsIgnoreCase(USER_VERIFIED_STATUS)) {
            userPreferences.setUserBasicRegistrationStatus(false);
            userPreferences.setUserHomePageStatus(false);
            userPreferences.setUserProcssingStatus(true);

        }

    }


    public static boolean isInternetConnected(Context context) {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        isConnected = true;
                    }
                }
            }
        }
        return isConnected;
    }


    public static void setToolbar(Activity context, ActionBar toolbar) {
        toolbar.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = context.getResources().getDrawable(R.drawable.vector_back_white_icon);
        upArrow.setColorFilter(context.getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbar.setHomeAsUpIndicator(upArrow);

    }

    public static Uri createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        if (storageDir.exists()) {
            image = File.createTempFile(timeStamp, /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            );
        } else {
            storageDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (storageDir.exists()) {
                image = File.createTempFile(timeStamp, /* prefix */
                        ".jpg", /* suffix */
                        storageDir /* directory */
                );
            }
        }
        // Save a file: path for use with ACTION_VIEW intents
        // mCurrentPhotoPath = image.getAbsolutePath();
        return Uri.fromFile(image);
    }


    public static String SaveImageGallery(Bitmap bitmap, byte[] byteArrayData, String filename, String imgActualPath) {
        File file = null, videoDirectory, AudioDirectory, galleryDirectory, directory, documentDirectory, profileDirectory;
        String root;
        Bitmap localBitMap;
        FileOutputStream fos;
        boolean isGallery = false, isOthers = false;
        try {
            root = Environment.getExternalStorageDirectory().toString();
            directory = new File(root + "/Pharmacy");
            if (!directory.exists())
                directory.mkdirs();
            videoDirectory = directory.getParentFile();
            AudioDirectory = directory.getParentFile();
            galleryDirectory = directory.getParentFile();
            videoDirectory = new File(directory + "/Pharmacy Videos");
            AudioDirectory = new File(directory + "/Pharmacy Audio");
            galleryDirectory = new File(directory + "/Pharmacy Images");
            documentDirectory = new File(directory + "/Pharmacy Documents");
            profileDirectory = new File(directory + "/ProfileImages");

            if (!videoDirectory.exists())
                videoDirectory.mkdirs();
            if (!AudioDirectory.exists())
                AudioDirectory.mkdirs();
            if (!galleryDirectory.exists())
                galleryDirectory.mkdirs();
            if (!documentDirectory.exists())
                documentDirectory.mkdirs();
            if (!profileDirectory.exists())
                profileDirectory.mkdirs();

            if (filename != null) {
                if (filename.toString().endsWith(".jpg")
                        || filename.toString().endsWith(".png")
                        || filename.toString().endsWith(".jpeg")) {

                    if (StringUtils.equalsIgnoreCase(filename, "profileImage.jpg"))
                        file = new File(profileDirectory, filename);
                    else
                        file = new File(galleryDirectory, filename);

                    isGallery = true;

                } else if (filename.toString().endsWith(".pdf")
                        || filename.toString().endsWith(".doc")
                        || filename.toString().endsWith(".docx")
                        || filename.toString().endsWith(".ppt")
                        || filename.toString().endsWith(".pptx")
                        || filename.toString().endsWith(".xls")
                        || filename.toString().endsWith(".xlsx")
                        || filename.toString().endsWith(".txt")) {
                    file = new File(documentDirectory, filename);
                    isOthers = true;
                } else if (filename.toString().endsWith(".mp3")
                        || filename.toString().endsWith(".ogg")
                        || filename.toString().endsWith(".m4a")
                        || filename.toString().endsWith(".amr")
                        || filename.toString().endsWith(".3gpp")) {
                    file = new File(AudioDirectory, filename);
                    isOthers = true;
                } else if (filename.toString().endsWith(".mp4")
                        || filename.toString().endsWith(".3gp")
                        || filename.toString().endsWith(".avi")
                        || filename.toString().endsWith(".mkv")) {
                    file = new File(videoDirectory, filename);
                    isOthers = true;
                }
            }
            if (file != null && file.exists())
                file.delete();
            try {
                if (file != null) {
                    fos = new FileOutputStream(file, false);
                    if (isGallery) {
                        if (imgActualPath != null) {
                            localBitMap = BitmapFactory.decodeFile(imgActualPath);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            localBitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byteArrayData = stream.toByteArray();
                            fos.write(byteArrayData);
                        } else if (byteArrayData != null) {
                            // bitmap = BitmapFactory.decodeFile(filpath);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            // getByteArray = stream.toByteArray();
                            fos = new FileOutputStream(file, false);
                            fos.write(byteArrayData);
                            fos.flush();
                            fos.close();
                        } else
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                    } else if (isOthers) {
                        fos.write(byteArrayData);
                    }
                    fos.flush();
                    fos.close();
                    return galleryDirectory + "/" + filename;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return "";
    }


    public static String containsPinCode(String addressDetails) {
        String result = "";
        try {
            final Pattern p = Pattern.compile("(\\d{5,})");
            final Matcher m = p.matcher(addressDetails);
            if (m.find()) {
                return m.group(0);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void showSnackBar(RelativeLayout relativeLayout, Context context, String message) {
        Snackbar.make(relativeLayout, message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(context.getColor(android.R.color.holo_red_light))
                .show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public void uploadPhotoToBlob(final ProgressBar uploadProgress,
                                  final ImageView uploadProgressSent,
                                  final TextView uploadProgressTryAgain,
                                  final String filename,
                                  final String path,
                                  final PharmacyDAO pharmacyDAO,
                                  final UserPreferences userPreferences,
                                  Context context,
                                  final String type) {
        uploadProgress.setVisibility(View.VISIBLE);
        UploadFiles uploadFiles = new UploadFiles(filename, path, context) {
            @Override
            public void onResponseReceived(String result) {
                Log.i("tag", "result of image" + result);
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.get("Status").toString().equalsIgnoreCase("Success")) {

                            try {
                                JSONObject urlObject = (JSONObject) jsonObject.get("Response");
                                String url = urlObject.get("url").toString();
                                uploadProgress.setVisibility(View.GONE);
                                uploadProgressSent.setVisibility(View.VISIBLE);

                                if (type.toString().equalsIgnoreCase("licence_photo")) {
                                    com.pharmacy.db.models.PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getPharmacyRegisterLocalUserId());
                                    pharmacyModel.LicenceLocalPath = path;
                                    pharmacyModel.Licence = url;
                                    pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);

                                } else if (type.toString().equalsIgnoreCase("register_photo")) {
                                    com.pharmacy.db.models.PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getPharmacyRegisterLocalUserId());
                                    pharmacyModel.BillingLocalPath = path;
                                    pharmacyModel.Billing = url;
                                    pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);

                                } else if (type.toString().equalsIgnoreCase("pharmacy_photo")) {
                                    com.pharmacy.db.models.PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getPharmacyRegisterLocalUserId());
                                    pharmacyModel.ImageLocalPath = path;
                                    pharmacyModel.Image = url;
                                    pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);

                                }

                            } catch (Exception e) {
                                uploadProgress.setVisibility(View.GONE);
                                uploadProgressTryAgain.setVisibility(View.VISIBLE);

                            }
                        } else {
                            uploadProgress.setVisibility(View.GONE);
                            uploadProgressTryAgain.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    uploadProgress.setVisibility(View.GONE);
                    uploadProgressTryAgain.setVisibility(View.VISIBLE);
                }

            }
        };
        uploadFiles.execute();


    }


    public void uploadPhotoToBlobNewPharmacy(final ProgressBar uploadProgress,
                                  final ImageView uploadProgressSent,
                                  final TextView  uploadProgressTryAgain,
                                  final   String filename,
                                  final String path,
                                  final PharmacyDAO pharmacyDAO,
                                  final UserPreferences userPreferences,
                                  Context context,
                                  final String type)
    {
        uploadProgress.setVisibility(View.VISIBLE);
        UploadFiles uploadFiles = new UploadFiles(filename,path,context) {
            @Override
            public void onResponseReceived(String result) {
                Log.i("tag","result of image"+result);
                if(result!=null)
                {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.get("Status").toString().equalsIgnoreCase("Success")){

                            try {
                                JSONObject urlObject = (JSONObject) jsonObject.get("Response");
                                String url = urlObject.get("url").toString();
                                uploadProgress.setVisibility(View.GONE);
                                uploadProgressSent.setVisibility(View.VISIBLE);

                                if(type.toString().equalsIgnoreCase("licence_photo"))
                                {
                                    com.pharmacy.db.models.PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAddNewPharmacyId());
                                    pharmacyModel.LicenceLocalPath = path;
                                    pharmacyModel.Licence       =   url;
                                    pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);

                                }
                                else if(type.toString().equalsIgnoreCase("register_photo"))
                                {
                                    com.pharmacy.db.models.PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAddNewPharmacyId());
                                    pharmacyModel.BillingLocalPath  =   path;
                                    pharmacyModel.Billing           =   url;
                                    pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);

                                }
                                else if(type.toString().equalsIgnoreCase("pharmacy_photo"))
                                {
                                    com.pharmacy.db.models.PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAddNewPharmacyId());
                                    pharmacyModel.ImageLocalPath    =   path;
                                    pharmacyModel.Image     =   url;
                                    pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);

                                }

                            }catch (Exception e)
                            {
                                uploadProgress.setVisibility(View.GONE);
                                uploadProgressTryAgain.setVisibility(View.VISIBLE);

                            }
                        }
                        else
                        {
                            uploadProgress.setVisibility(View.GONE);
                            uploadProgressTryAgain.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    uploadProgress.setVisibility(View.GONE);
                    uploadProgressTryAgain.setVisibility(View.VISIBLE);
                }

            }
        };
        uploadFiles.execute();


    }

    public Bitmap getBitmapFromURL(String src) {
        try {

            Log.i("tag", "url for bitmap is" + src);
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public long renderLoginDataForAgent(Context context, AgentModel agentModel)
    {
        AgentDAO agentDAO = new AgentDAO(context);
        PharmacyDAO pharmacyDAO = new PharmacyDAO(context);
        OrderDAO orderDAO = new OrderDAO(context);

        long id =-1;
        try {
            id = agentDAO.insertOrUpdate(agentModel);

            ArrayList<PharmacyModel> pharmacyModelArrayList = agentModel.AgentPharmacyList;
            if (pharmacyModelArrayList != null && pharmacyModelArrayList.size() > 0) {
                for (int i = 0; i < pharmacyModelArrayList.size(); i++) {
                    id = pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModelArrayList.get(i));
                }
            }

            ArrayList<OrderModel> orderModelArrayList = agentModel.RunningList;
            if (orderModelArrayList != null && orderModelArrayList.size() > 0) {
                for (int i = 0; i < orderModelArrayList.size(); i++) {
                    id = orderDAO.insert(orderModelArrayList.get(i));
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return id;
    }


    public long renderLoginDataForPharmacy(Context context, PharmacyModel pharmacyModel)
    {
        PharmacyDAO pharmacyDAO = new PharmacyDAO(context);
        OrderDAO orderDAO = new OrderDAO(context);

        long id =-1;
        try {
             id = pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel);

            ArrayList<OrderModel> orderModelArrayList = pharmacyModel.RunningList;
            if (orderModelArrayList != null && orderModelArrayList.size() > 0) {
                for (int i = 0; i < orderModelArrayList.size(); i++) {
                    id = orderDAO.insert(orderModelArrayList.get(i));
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return id;
    }



    public String getAgentRequestData(Context context,String type) {
        UserPreferences userPreferences = new UserPreferences(context);
        UserDAO userDAO                 = new UserDAO(context);
        Gson gson                       = new Gson();
        UserModel userModel             = userDAO.getUserData(userPreferences.getUserGid());
        GetUserDetailsRequest getUserDetailsRequest = new GetUserDetailsRequest();
        getUserDetailsRequest.UserID        =   userPreferences.getUserGid();
        if(type.toString().equalsIgnoreCase(context.getString(R.string.agent)))
           getUserDetailsRequest.UserType   =   context.getString(R.string.agent);
        else
            getUserDetailsRequest.UserType  = context.getString(R.string.pharmacy);
        getUserDetailsRequest.DistributorID =   userModel.DistributorID;
        getUserDetailsRequest.LastUpdatedTimeTicks  = userPreferences.getGetAllUserDetailsTimeticks();
        String json = gson.toJson(getUserDetailsRequest);

        return json;
    }




    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = true;
                            break;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = true;
            }
        }

        return isInBackground;
    }



    public static String getCurrentOnlyDate() {
        String strDate = "";
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);//dd/MM/yyyy
            Date now = new Date();
            strDate = sdfDate.format(now);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    public static String getYesterdayDate() {

        Date date = DateUtils.addDays(new Date(), -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String getConvertedTime(String currentDate) {
        Log.i("tag","current date"+currentDate);

        return currentDate.split("-")[2] + " " + new DateFormatSymbols().getMonths()[Integer.parseInt(currentDate.split("-")[1]) - 1] + " " + currentDate.split("-")[0];
    }

    public String getOnlyTime(String dateTime)
    {
        String dateStr  = dateTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("PST"));

        try {
            Date date = simpleDateFormat.parse(dateStr);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm a");
            dateStr = simpleDateFormat1.format(date);
            Log.i("tag","date value is"+dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;


    }
}
