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
import com.pharmacy.db.daos.AgentDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.operations.Post;
import com.pharmacy.operations.UploadFiles;
import com.pharmacy.pharmacy.PharmacyRegistrationTwo;
import com.pharmacy.preferences.UserPreferences;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class AgentRegistrationTwo extends AppCompatActivity implements AppConstants {

    ImageView ar2UploadPhoto,ar2UploadIdProof;
    Button ar2SaveAgentDetails;
    Toolbar ar2Toolbar;
    private AlertDialog alert;
    Uri currentImageUri=null;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"};
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static int SELECT_FILE_CAMERA = 1;
    private static int SELECT_FILE_GALLERY = 2;

    String upload_type;
    ProgressBar ar2UploadPhotoProgress,ar2UploadIdProofProgress;
    ImageView ar2UploadPhotoSent,ar2UploadIdProofSent;
    TextView ar2UploadPhotoTryAgain,ar2UploadIdProofTryAgain;
    AgentDAO agentDAO;
    UserPreferences userPreferences;
    RelativeLayout mainLayout;
    Gson gson;
    ProgressBar saveAgentDetailsProgressbar;
    UserDAO userDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_registration_two);

        initialiseObjects();
        initialiseIDs();
        initialiseListeners();
        initialiseBackButton();
        reinitialise();
    }


    private void reinitialise()
    {
      AgentModel agentModel =  agentDAO.getAgentData(userPreferences.getUserGid());
      if(agentModel!=null)
      {
          if(agentModel.ImageLocalPath!=null)
          {
              try {
                  Bitmap bitmap = BitmapFactory.decodeFile(agentModel.ImageLocalPath);
                  if (bitmap != null) {
                      ar2UploadPhoto.setImageBitmap(bitmap);
                  }
              }catch (Exception e)
              {
                  e.printStackTrace();
              }
          }

          if(agentModel.IdProofLocalPath!=null)
          {
              try {
                  Bitmap bitmap = BitmapFactory.decodeFile(agentModel.IdProofLocalPath);
                  if (bitmap != null) {
                      ar2UploadIdProof.setImageBitmap(bitmap);
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
        agentDAO    =   new AgentDAO(this);
        userDAO     =   new UserDAO(this);
        userPreferences =   new UserPreferences(this);
        gson    =   new Gson();

    }
    private void initialiseIDs()
    {
        ar2UploadPhoto          =   findViewById(R.id.ar2_upload_photo);
        ar2UploadIdProof        =   findViewById(R.id.ar2_upload_id_proof);
        ar2SaveAgentDetails     =   findViewById(R.id.ar2_save_agent_details);
        ar2Toolbar              =   findViewById(R.id.ar2_toolbar);

        ar2UploadPhotoProgress  =   findViewById(R.id.ar2_upload_photo_progress);
        ar2UploadPhotoSent      =   findViewById(R.id.ar2_upload_photo_sent);
        ar2UploadPhotoTryAgain  =   findViewById(R.id.ar2_upload_photo_try_again);

        ar2UploadIdProofProgress    =   findViewById(R.id.ar2_upload_id_proof_progress);
        ar2UploadIdProofSent        =   findViewById(R.id.ar2_upload_id_proof_sent);
        ar2UploadPhotoTryAgain      =   findViewById(R.id.ar2_upload_id_proof_try_again);

        mainLayout              =   findViewById(R.id.mainLayout);
        saveAgentDetailsProgressbar =   findViewById(R.id.ar2_save_agent_details_progress);
    }

    private void initialiseListeners()
    {
        ar2SaveAgentDetails.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if(CommonMethods.isInternetConnected(AgentRegistrationTwo.this)) {
                    if (doValidation()) {
                        AgentModel agentModel = agentDAO.getAgentData(userPreferences.getUserGid());
                        agentModel.UserType = "" + getString(R.string.agent);
                        UserModel userModel = userDAO.getUserData(userPreferences.getUserGid());
                        agentModel.PhoneNumber = userModel.PhoneNumber;
                        agentModel.DistributorID = userModel.DistributorID;
                        String json = gson.toJson(agentModel);
                        saveAgentDetailsProgressbar.setVisibility(View.VISIBLE);

                        Post post = new Post(AgentRegistrationTwo.this, CommonMethods.CONFIRM_USER_REGISTRATION, json) {
                            @Override
                            public void onResponseReceived(String result) {

                                saveAgentDetailsProgressbar.setVisibility(View.GONE);
                                if (result != null) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if (jsonObject.get("Status").toString().equalsIgnoreCase("Success")) {

                                            Intent intent = new Intent(AgentRegistrationTwo.this, AgentProcessing.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(AgentRegistrationTwo.this, "Something wrong", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(AgentRegistrationTwo.this, "Something wrong", Toast.LENGTH_LONG).show();
                                }

                            }
                        };
                        post.execute();
                    }
                }
                else
                {
                    CommonMethods.showSnackBar(mainLayout,AgentRegistrationTwo.this,"Please check internet");

                }
            }
        });

        ar2UploadIdProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_type = UPLOAD_ID_PROOF;
                marshmallowDialog();
            }
        });

        ar2UploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_type = UPLOAD_PHOTO;
                marshmallowDialog();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean doValidation()
    {
        AgentModel agentModel = agentDAO.getAgentData(userPreferences.getUserGid());
        if(agentModel.ImageLocalPath!=null && agentModel.ImageLocalPath.toString().length()>2)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,this,"Upload your photo");
            return false;
        }
        if(agentModel.IdProofLocalPath!=null && agentModel.IdProofLocalPath.toString().length()>2)
        {

        }
        else
        {
            CommonMethods.showSnackBar(mainLayout,this,"Upload your id proof");
            return false;
        }
        return true;
    }

    private void initialiseBackButton()
    {
        setSupportActionBar(ar2Toolbar);
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
        Intent intent = new Intent(AgentRegistrationTwo.this,AgentRegistration.class);
        startActivity(intent);
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
                        String filename = upload_type+".jpg";
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        thePic.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                       final String path =  CommonMethods.SaveImageGallery(thePic, null, filename, null);
                        //String imagePath = Environment.getExternalStorageDirectory().toString() + "/"+IMAGE_PATH+"/Images/" + filename;
                        File file = new File(path);
                        if (file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            if(upload_type!=null)
                            {
                                if(upload_type.toString().equalsIgnoreCase(UPLOAD_ID_PROOF))
                                {
                                    ar2UploadIdProof.setImageBitmap(bitmap);

                                    ar2UploadIdProofProgress.setVisibility(View.VISIBLE);
                                    UploadFiles uploadFiles = new UploadFiles(filename,path,this) {
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
                                                            ar2UploadIdProofProgress.setVisibility(View.GONE);
                                                            ar2UploadIdProofSent.setVisibility(View.VISIBLE);
                                                            AgentModel agentModel = agentDAO.getAgentData(userPreferences.getUserGid());
                                                            agentModel.IdProofLocalPath =   path;
                                                            agentModel.IdProof  =   url;
                                                            agentDAO.insertOrUpdate(agentModel);

                                                        }catch (Exception e)
                                                        {
                                                            ar2UploadIdProofProgress.setVisibility(View.GONE);
                                                            ar2UploadIdProofTryAgain.setVisibility(View.VISIBLE);

                                                        }
                                                    }
                                                    else
                                                    {
                                                        ar2UploadIdProofProgress.setVisibility(View.GONE);
                                                        ar2UploadIdProofTryAgain.setVisibility(View.VISIBLE);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else
                                            {
                                                ar2UploadIdProofProgress.setVisibility(View.GONE);
                                                ar2UploadIdProofTryAgain.setVisibility(View.VISIBLE);
                                            }

                                        }
                                    };
                                    uploadFiles.execute();
                                }
                                else
                                {
                                    ar2UploadPhoto.setImageBitmap(bitmap);


                                    ar2UploadPhotoProgress.setVisibility(View.VISIBLE);
                                    UploadFiles uploadFiles = new UploadFiles(filename,path,this) {
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
                                                            ar2UploadPhotoProgress.setVisibility(View.GONE);
                                                            ar2UploadPhotoSent.setVisibility(View.VISIBLE);
                                                            AgentModel agentModel = agentDAO.getAgentData(userPreferences.getUserGid());
                                                            agentModel.ImageLocalPath   =   path;
                                                            agentModel.Image    =   url;
                                                            agentDAO.insertOrUpdate(agentModel);

                                                        }catch (Exception e)
                                                        {
                                                            ar2UploadPhotoProgress.setVisibility(View.GONE);
                                                            ar2UploadPhotoTryAgain.setVisibility(View.VISIBLE);

                                                        }
                                                    }
                                                    else
                                                    {
                                                        ar2UploadPhotoProgress.setVisibility(View.GONE);
                                                        ar2UploadPhotoTryAgain.setVisibility(View.VISIBLE);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else
                                            {
                                                ar2UploadPhotoProgress.setVisibility(View.GONE);
                                                ar2UploadPhotoTryAgain.setVisibility(View.VISIBLE);
                                            }

                                        }
                                    };
                                    uploadFiles.execute();


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
                    .start(AgentRegistrationTwo.this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                    if (isSDPresent) {
                        hideAll();
                        getPhotoFromCamera();
                    }
                    else
                        Toast.makeText(
                                AgentRegistrationTwo.this,
                                "Please turn off USB storage or insert your SD card and try again",
                                Toast.LENGTH_SHORT).show();

                    return;
                } else if (item == 1) {
                    if (isSDPresent) {
                        hideAll();
                        getPhotoFromGallery();
                    }
                    else
                        Toast.makeText(
                                AgentRegistrationTwo.this,
                                "Please turn off USB storage or insert your SD card and try again",
                                Toast.LENGTH_SHORT).show();
                    return;

                } else if (item == 2) {
                    if(upload_type!=null)
                    {
                        hideAll();
                        if(upload_type.toString().equalsIgnoreCase(UPLOAD_ID_PROOF))
                        {
                            currentImageUri = null;
                            ar2UploadIdProof.setImageBitmap(null);
                            ar2UploadIdProof.setImageDrawable(getResources().getDrawable(R.drawable.default_image));

                        }
                        else
                        {
                            currentImageUri = null;
                            ar2UploadPhoto.setImageBitmap(null);
                            ar2UploadPhoto.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
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


    private void hideAll(){
        ar2UploadPhotoProgress.setVisibility(View.GONE);
        ar2UploadPhotoSent.setVisibility(View.GONE);
        ar2UploadPhotoTryAgain.setVisibility(View.GONE);
    }


}
