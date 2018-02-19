package com.pharmacy;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pharmacy.db.daos.OrderDAO;
import com.pharmacy.db.daos.PharmacyDAO;
import com.pharmacy.db.daos.UserDAO;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.db.models.PharmacyModel;
import com.pharmacy.db.models.UserModel;
import com.pharmacy.models.AddToRunningListRequest;
import com.pharmacy.models.ProductModel;
import com.pharmacy.operations.Post;
import com.pharmacy.pharmacy.PharmacyRunningListWithNavigation;
import com.pharmacy.preferences.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowProductQuantityDialog extends Activity {

    TextView close;
    Gson gson;
    TextView productName;
    Button plus,minus;
    EditText quantity;
    Button addToRunningList;
    ProgressBar progressBar;
    UserDAO userDAO;
    PharmacyDAO pharmacyDAO;
    ProductModel productModel;
    UserModel userModel;
    PharmacyModel pharmacyModel;
    UserPreferences userPreferences;
    RelativeLayout mainLayout;
    OrderDAO orderDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_product_quantity_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        initialiseObjects();
        initialiseIDs();

        inflatedData();
        initialiseClickListeners();
    }

    private void initialiseIDs()
    {
        close           =   findViewById(R.id.spqd_close);
        productName     =   findViewById(R.id.spqd_product_name);
        plus            =   findViewById(R.id.spqd_plus);
        minus           =   findViewById(R.id.spqd_minus);
        quantity        =   findViewById(R.id.spqd_quantity);
        addToRunningList    =   findViewById(R.id.spqd_add_to_runninglist);
        progressBar     =   findViewById(R.id.spqd_progressbar);
        mainLayout      =   findViewById(R.id.mainLayout);
    }

    private void inflatedData()
    {
        String json = getIntent().getStringExtra("product_model");
        productModel = gson.fromJson(json,ProductModel.class);
        productName.setText(productModel.Name);
    }

    private void initialiseObjects()
    {
        userPreferences =   new UserPreferences(this);
        gson    =   new Gson();
        userDAO =   new UserDAO(this);
        pharmacyDAO =   new PharmacyDAO(this);
        orderDAO    =   new OrderDAO(this);
        userModel   =   userDAO.getUserData(userPreferences.getUserGid());
        pharmacyModel   =   pharmacyDAO.getPharmacyData(userPreferences.getUserGid());
    }

    private void initialiseClickListeners()
    {
        close.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent(ShowProductQuantityDialog.this, PharmacyRunningListWithNavigation.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ShowProductQuantityDialog.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                startActivity(intent,bndlanimation);
      */    finish();
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity.getText().toString().trim().length() > 0)
                {
                    if(Integer.parseInt(quantity.getText().toString())>0)
                    {
                        int val = Integer.parseInt(quantity.getText().toString());
                        val++;
                        quantity.setText(""+val);
                    }
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity.getText().toString().trim().length() > 0)
                {
                    if(Integer.parseInt(quantity.getText().toString())>1)
                    {
                        int val = Integer.parseInt(quantity.getText().toString());
                        val--;
                        quantity.setText(""+val);
                    }
                }

            }
        });

        addToRunningList.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
               if(doValidation()) {

                   progressBar.setVisibility(View.VISIBLE);
                   addToRunningList.setVisibility(View.GONE);
                   OrderModel orderModel = new OrderModel();
                   orderModel.UserID = userModel.UserID;
                   orderModel.DistributorID = userModel.DistributorID;

                   try {
                       if (userPreferences.getAgentSelectedPharmacyId() != null && userPreferences.getAgentSelectedPharmacyId().toString().trim().length()>0 )
                           orderModel.PharmacyID = userPreferences.getAgentSelectedPharmacyId();
                       else
                           orderModel.PharmacyID = userModel.UserID;
                   }catch (Exception e)
                   {
                       e.printStackTrace();
                   }

                   orderModel.ProductID = productModel.ProductId;
                   orderModel.Quantity = Integer.parseInt(quantity.getText().toString());
                   String json = gson.toJson(orderModel);
                   Post post = new Post(ShowProductQuantityDialog.this,CommonMethods.ADD_TO_RUNNING_LIST,json) {
                       @Override
                       public void onResponseReceived(String result) {
                           Log.i("tag","result is"+result);

                           if(result!=null)
                           {
                               try {
                                   JSONObject jsonObject = new JSONObject(result);
                                   if(jsonObject.get("Status").toString().equalsIgnoreCase("Success"))
                                   {
                                       JSONObject responseObject = jsonObject.getJSONObject("Response");
                                        String json = responseObject.getJSONObject("order").toString();
                                       OrderModel orderModel1 = gson.fromJson(json,OrderModel.class);
                                       orderModel1.IsRunning = true;
                                       if(orderModel1!=null)
                                       {
                                           try {
                                              long id =  orderDAO.insert(orderModel1);
                                              if(id!=-1)
                                              {
                                                  close.performClick();
                                              }
                                              else{
                                                  progressBar.setVisibility(View.GONE);
                                                  addToRunningList.setVisibility(View.VISIBLE);
                                                  Toast.makeText(ShowProductQuantityDialog.this,"Please try again",Toast.LENGTH_LONG).show();
                                              }
                                           }catch (Exception e)
                                           {
                                               e.printStackTrace();
                                           }
                                       }
                                   }
                                   else
                                   {
                                       addToRunningList.setVisibility(View.VISIBLE);
                                   }
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }
                           }
                           else
                           {
                               addToRunningList.setVisibility(View.VISIBLE);
                           }
                       }
                   };
                   post.execute();

               }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean doValidation()
    {
        if(quantity.getText().toString().trim().length() > 0 && Integer.parseInt(quantity.getText().toString())>0){

        }else
        {
            CommonMethods.showSnackBar(mainLayout,this,"Enter quantity");
            return false;
        }
        return true;
    }
}
