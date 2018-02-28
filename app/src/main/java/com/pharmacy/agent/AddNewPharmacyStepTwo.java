package com.pharmacy.agent;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pharmacy.AppConstants;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.operations.Post;
import com.pharmacy.pharmacy.PharmacyProcessing;
import com.pharmacy.pharmacy.PharmacyRegistration;
import com.pharmacy.pharmacy.PharmacyRegistrationTwo;
import com.pharmacy.preferences.UserPreferences;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNewPharmacyStepTwo extends AppCompatActivity implements AppConstants {

    ImageView pr2UploadLicenceCertificate,pr2UploadRegisterCertificate,pr2UploadPharmacyPhoto;
    Button pr2SavePharmacyDetails;
    Toolbar pr2Toolbar;
    String upload_type;

    ProgressBar pr2UploadLicenceProgress,pr2UploadRegisterProgress,pr2UploadePharmacyPhotoProgress;
    ImageView pr2UploadLicenceSent,pr2UploadRegisterSent,pr2UploadPharmacyPhotoSent;
    TextView pr2UploadLicenceTryAgain,pr2UploadRegisterTryAgain,pr2UploadPharmacyPhotoTryAgain;

    private AlertDialog alert;
    Uri currentImageUri=null;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"};
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static int SELECT_FILE_CAMERA = 1;
    private static int SELECT_FILE_GALLERY = 2;

    CommonMethods commonMethods;
    UserPreferences userPreferences;
    PharmacyDAO pharmacyDAO;
    RelativeLayout mainLayout;
    ProgressBar progressBar;
    UserDAO userDAO;
    Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_pharmacy_step_two);

        initialiseObjects();
        initialiseIDs();
        initialiseListeners();
        initialiseBackButton();
        reinitialise();
    }



    private void reinitialise()
    {
        PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAddNewPharmacyId());
        if(pharmacyModel!=null)
        {
            if(pharmacyModel.LicenceLocalPath!=null)
            {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(pharmacyModel.LicenceLocalPath);
                    if (bitmap != null) {
                        pr2UploadLicenceCertificate.setImageBitmap(bitmap);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if(pharmacyModel.BillingLocalPath!=null)
            {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(pharmacyModel.BillingLocalPath);
                    if (bitmap != null) {
                        pr2UploadRegisterCertificate.setImageBitmap(bitmap);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }


    private void initialiseObjects()
    {
        commonMethods   =   new CommonMethods();
        userPreferences =   new UserPreferences(this);
        pharmacyDAO     =   new PharmacyDAO(this);
        userDAO         =   new UserDAO(this);
        gson            =   new Gson();
    }

    private void initialiseIDs()
    {
        pr2UploadLicenceCertificate     =   findViewById(R.id.anp2_upload_licence_certificate);
        pr2UploadRegisterCertificate    =   findViewById(R.id.anp2_upload_register_certificate);
        pr2UploadPharmacyPhoto          =   findViewById(R.id.anp2_upload_pharmacy_photo);
        pr2SavePharmacyDetails          =   findViewById(R.id.anp2_save_pharmacy_details);
        pr2Toolbar                      =   findViewById(R.id.anp2_toolbar);

        pr2UploadLicenceProgress        =   findViewById(R.id.anp2_upload_licence_progress);
        pr2UploadLicenceSent            =   findViewById(R.id.anp2_upload_licence_sent);
        pr2UploadLicenceTryAgain        =   findViewById(R.id.anp2_upload_licence_try_again);

        pr2UploadRegisterProgress       =   findViewById(R.id.anp2_upload_register_progress);
        pr2UploadRegisterSent           =   findViewById(R.id.anp2_upload_register_sent);
        pr2UploadRegisterTryAgain       =   findViewById(R.id.anp2_upload_register_try_again);

        pr2UploadePharmacyPhotoProgress =   findViewById(R.id.anp2_upload_pharmacy_photo_progress);
        pr2UploadPharmacyPhotoSent      =   findViewById(R.id.anp2_upload_pharmacy_photo_sent);
        pr2UploadPharmacyPhotoTryAgain  =   findViewById(R.id.anp2_upload_pharmacy_photo_try_again);

        mainLayout                      =   findViewById(R.id.mainLayout);

        progressBar                     =   findViewById(R.id.anp2_save_pharmacy_details_progress_bar);

    }

    private void initialiseListeners()
    {
        pr2SavePharmacyDetails.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (CommonMethods.isInternetConnected(AddNewPharmacyStepTwo.this)) {
                   // if (doValidation()) {


                        PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAddNewPharmacyId());
                        pharmacyModel.PharmacyID = "0";
                      //  pharmacyModel.UserType = "" + getString(R.string.pharmacy);
                        UserModel userModel = userDAO.getUserData(userPreferences.getUserGid());
                        pharmacyModel.DistributorID = userModel.DistributorID;
                        String json = gson.toJson(pharmacyModel);
                        progressBar.setVisibility(View.VISIBLE);
                        pr2SavePharmacyDetails.setVisibility(View.GONE);

                        Post post = new Post(AddNewPharmacyStepTwo.this, CommonMethods.ADD_NEW_PHARMACY, json) {
                            @Override
                            public void onResponseReceived(String result) {

                                progressBar.setVisibility(View.GONE);
                                if (result != null) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if (jsonObject.get(getString(R.string.status)).toString().equalsIgnoreCase(getString(R.string.success))) {
                                            String response = jsonObject.get(getString(R.string.response)).toString();
                                            PharmacyModel pharmacyModel1 = gson.fromJson(response,PharmacyModel.class);

                                           long id = pharmacyDAO.insertOrUpdateAddNewPharmacy(pharmacyModel1);
                                            if(id!= -1) {
                                                userPreferences.setAddNewPharmacyId("");
                                                Intent intent = new Intent(AddNewPharmacyStepTwo.this, AgentPharmacyListWithNavigation.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                        Intent.FLAG_ACTIVITY_NEW_TASK);

                                                startActivity(intent);
                                            }
                                            else {
                                                Toast.makeText(AddNewPharmacyStepTwo.this,"Something wrong",Toast.LENGTH_LONG).show();
                                            }

                                        } else {
                                            pr2SavePharmacyDetails.setVisibility(View.VISIBLE);

                                            Toast.makeText(AddNewPharmacyStepTwo.this, "Something wrong", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    pr2SavePharmacyDetails.setVisibility(View.VISIBLE);

                                    Toast.makeText(AddNewPharmacyStepTwo.this, "Something wrong", Toast.LENGTH_LONG).show();
                                }

                            }
                        };
                        post.execute();



                  //  }
                }
                else
                {
                    CommonMethods.showSnackBar(mainLayout,AddNewPharmacyStepTwo.this,"Please check internet");
                }
            }
        });

        pr2UploadLicenceCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_type = UPLOAD_LICENCE_CERTIFICATE;
                marshmallowDialog();

            }
        });

        pr2UploadRegisterCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_type =   UPLOAD_PHARMACY_REGISTER_CERTIFICATE;
                marshmallowDialog();
            }
        });

        pr2UploadPharmacyPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_type =   UPLOAD_PHARMACY_PHOTO;
                marshmallowDialog();
            }
        });
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean doValidation()
    {

        PharmacyModel pharmacyModel = pharmacyDAO.getPharmacyDataByPharmacyID(userPreferences.getAddNewPharmacyId());

        if(pharmacyModel.ImageLocalPath!=null && pharmacyModel.ImageLocalPath.toString().trim().length()>2)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,this,"Upload your pharmacy photo");
            return false;
        }

        if(pharmacyModel.LicenceLocalPath!=null && pharmacyModel.LicenceLocalPath.toString().trim().length()>2)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,this,"Upload your licence photo");
            return false;
        }
        if(pharmacyModel.BillingLocalPath!=null && pharmacyModel.BillingLocalPath.toString().trim().length()>2)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,this,"Upload your pharmacy register photo");
            return false;
        }
        return true;
    }



    private void marshmallowDialog( ) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                ShowOptions();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void requestPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                    ) {

                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {

                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialiseBackButton()
    {
        setSupportActionBar(pr2Toolbar);
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
        Intent intent = new Intent(AddNewPharmacyStepTwo.this,AddNewPharmacyStepOne.class);
         startActivity(intent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_FILE_GALLERY) {
                Log.i("tag","request code"+requestCode);
                Log.i("tag","request result code"+resultCode);
                Log.i("tag","request result code type"+upload_type);
                Log.i("tag","request result code type1"+data.getData());
                if(data.getData()!=null)
                    performCrop(data.getData());

            } else if (requestCode == SELECT_FILE_CAMERA) {
                Log.i("tag","request code"+requestCode);
                Log.i("tag","request result code"+resultCode);
                Log.i("tag","request result code type"+upload_type);
                Log.i("tag","request result code type1"+currentImageUri);
                if(currentImageUri!=null)
                    performCrop(currentImageUri);


            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        Bitmap thePic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        String dateformat = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        String filename = upload_type+dateformat+".jpg";
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        thePic.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                        String path =  CommonMethods.SaveImageGallery(thePic, null, filename, null);
                        //String imagePath = Environment.getExternalStorageDirectory().toString() + "/"+IMAGE_PATH+"/Images/" + filename;
                        File file = new File(path);
                        if (file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            if(upload_type!=null)
                            {
                                if(upload_type.toString().equalsIgnoreCase(UPLOAD_LICENCE_CERTIFICATE))
                                {
                                    pr2UploadLicenceCertificate.setImageBitmap(bitmap);
                                    commonMethods.uploadPhotoToBlobNewPharmacy(pr2UploadLicenceProgress,
                                            pr2UploadLicenceSent,
                                            pr2UploadLicenceTryAgain,
                                            filename,
                                            path,
                                            pharmacyDAO,
                                            userPreferences,
                                            this,
                                            "licence_photo"
                                    );
                                }
                                else if(upload_type.toString().equalsIgnoreCase(UPLOAD_PHARMACY_REGISTER_CERTIFICATE))
                                {
                                    pr2UploadRegisterCertificate.setImageBitmap(bitmap);

                                    commonMethods.uploadPhotoToBlobNewPharmacy(pr2UploadRegisterProgress,
                                            pr2UploadRegisterSent,
                                            pr2UploadRegisterTryAgain,
                                            filename,
                                            path,
                                            pharmacyDAO,
                                            userPreferences,
                                            this,
                                            "register_photo"
                                    );

                                }
                                else if(upload_type.toString().equalsIgnoreCase(UPLOAD_PHARMACY_PHOTO))
                                {
                                    pr2UploadPharmacyPhoto.setImageBitmap(bitmap);

                                    commonMethods.uploadPhotoToBlobNewPharmacy(pr2UploadePharmacyPhotoProgress,
                                            pr2UploadPharmacyPhotoSent,
                                            pr2UploadPharmacyPhotoTryAgain,
                                            filename,
                                            path,
                                            pharmacyDAO,
                                            userPreferences,
                                            this,
                                            "pharmacy_photo"
                                    );

                                }


                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }

            }
        } else {
            Log.i("tag", "result code = -1");
        }

    }







    private void performCrop(Uri tempUri) {
        try {
            CropImage.activity(tempUri)
                    .setAspectRatio(1, 1)
                    .start(AddNewPharmacyStepTwo.this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void getPhotoFromCamera()
    {
        try {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                currentImageUri = CommonMethods.createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
            } else {
                File file = new File(currentImageUri.getPath());

                //Uri photoUri = Uri.fromFile(file);
                Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                startActivityForResult(intent, SELECT_FILE_CAMERA);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void getPhotoFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select picture to upload "), SELECT_FILE_GALLERY);

    }
    protected void ShowOptions() {

        final CharSequence[] items = {"Take New Photo", "Choose from Gallery",
                "Remove", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                Boolean isSDPresent = Environment
                        .getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED);
                if (item == 0) {
                    if (isSDPresent)
                        getPhotoFromCamera();
                    else
                        Toast.makeText(
                                AddNewPharmacyStepTwo.this,
                                "Please turn off USB storage or insert your SD card and try again",
                                Toast.LENGTH_SHORT).show();

                    return;
                } else if (item == 1) {
                    if (isSDPresent)
                        getPhotoFromGallery();
                    else
                        Toast.makeText(
                                AddNewPharmacyStepTwo.this,
                                "Please turn off USB storage or insert your SD card and try again",
                                Toast.LENGTH_SHORT).show();
                    return;

                } else if (item == 2) {
                    if(upload_type!=null)
                    {
                        if(upload_type.toString().equalsIgnoreCase(UPLOAD_LICENCE_CERTIFICATE))
                        {
                            currentImageUri = null;
                            pr2UploadLicenceCertificate.setImageBitmap(null);
                            pr2UploadLicenceCertificate.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
                        }
                        else if(upload_type.toString().equalsIgnoreCase(UPLOAD_PHARMACY_REGISTER_CERTIFICATE))
                        {
                            currentImageUri = null;
                            pr2UploadRegisterCertificate.setImageBitmap(null);
                            pr2UploadRegisterCertificate.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
                        }
                        else if(upload_type.toString().equalsIgnoreCase(UPLOAD_PHARMACY_PHOTO))
                        {
                            currentImageUri = null;
                            pr2UploadPharmacyPhoto.setImageBitmap(null);
                            pr2UploadPharmacyPhoto.setImageDrawable(getResources().getDrawable(R.drawable.default_image));

                        }
                    }
                    return;

                } else
                    alert.cancel();
            }

        });

        alert = builder.create();
        alert.show();
    }


}
