package com.pharmacy.adapters;

/**
 * Created by PCCS-0007 on 15-Feb-18.
 */

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.R;
import com.pharmacy.ShowProductQuantityDialog;
import com.pharmacy.agent.fragments.AgentRunningListFragment;
import com.pharmacy.models.ProductModel;

import java.util.ArrayList;

/**
 * Created by PCCS-0007 on 06-Feb-18.
 */



public class SearchProductListAdapter extends RecyclerView.Adapter<SearchProductListAdapter.MyViewHolder> {

    Context context;
    ArrayList<ProductModel> productModelArrayList;
    Gson gson;
    Fragment fragment;
    public SearchProductListAdapter(Context context, ArrayList<ProductModel> productModelArrayList,Fragment agentRunningListFragment) {

        this.context    =   context;
        this.productModelArrayList =   productModelArrayList;
        gson    = new Gson();
        this.fragment = agentRunningListFragment;
    }

    @Override
    public SearchProductListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_product_list_adapter, parent, false);

        return new SearchProductListAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView spProductName;
        LinearLayout spParentLayout;
         public MyViewHolder(View view) {
            super(view);

             spProductName =   view.findViewById(R.id.sp_product_name);
             spParentLayout =   view.findViewById(R.id.sp_parent_layout);
        }

    }


    @Override
    public void onBindViewHolder(final SearchProductListAdapter.MyViewHolder holder, final int position) {
            final ProductModel productModel = productModelArrayList.get(position);
            holder.spProductName.setText(productModel.Name);

            final Intent intent = new Intent(context, ShowProductQuantityDialog.class);
            holder.spParentLayout.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    intent.putExtra("product_model",gson.toJson(productModel));

                    if(fragment instanceof AgentRunningListFragment)
                        ((AgentRunningListFragment) fragment).closeListAndKeyboard();

                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fade_out).toBundle();
                    context.startActivity(intent,bndlanimation);

                }
            });

    }

    @Override
    public int getItemCount() {
        return (productModelArrayList != null) ? productModelArrayList.size() : 0;
    }



}