package com.pharmacy.pharmacy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.R;
import com.pharmacy.db.models.OrderModel;
import com.pharmacy.db.models.PharmacyModel;

import java.util.ArrayList;

/**
 * Created by PCCS-0007 on 15-Feb-18.
 */


public class PharmacyCommonListAdapter extends RecyclerView.Adapter<PharmacyCommonListAdapter.MyViewHolder> {

    Context context;
    ArrayList<OrderModel> productModelArrayList;
    Gson gson;
    String from;
    public PharmacyCommonListAdapter(Context context, ArrayList<OrderModel> productModelArrayList,String from) {

        this.context    =   context;
        this.productModelArrayList =   productModelArrayList;
        gson    = new Gson();
        this.from = from;

    }

    @Override
    public PharmacyCommonListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pharmacy_running_list, parent, false);

        return new PharmacyCommonListAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView pharmacyTitle,pharmacyQuantity;
        ImageView done,doneAll,doneAllColor;
         public MyViewHolder(View view) {
            super(view);
            pharmacyTitle   =   view.findViewById(R.id.pharmacy_list_title);
            pharmacyQuantity    =   view.findViewById(R.id.pharmacy_list_quantity);

            done        =   view.findViewById(R.id.done);
            doneAll     =   view.findViewById(R.id.done_all);
            doneAllColor    =   view.findViewById(R.id.done_all_color);
            if(from.toString().equalsIgnoreCase("running_list"))
            {
                done.setVisibility(View.VISIBLE);
            }
            else  if(from.toString().equalsIgnoreCase("approved_list"))
            {
                doneAll.setVisibility(View.VISIBLE);
            }
            else  if(from.toString().equalsIgnoreCase("delivered_list"))
            {
                doneAllColor.setVisibility(View.VISIBLE);
            }
     }

    }


    @Override
    public void onBindViewHolder(final PharmacyCommonListAdapter.MyViewHolder holder, final int position) {
       OrderModel orderModel = productModelArrayList.get(position);
        holder.pharmacyTitle.setText(orderModel.ProductName);
        holder.pharmacyQuantity.setText(""+orderModel.Quantity);
    }

    @Override
    public int getItemCount() {
        return (productModelArrayList != null) ? productModelArrayList.size() : 0;
    }


    public void addAll(ArrayList<OrderModel> orderModels)
    {
        productModelArrayList.addAll(orderModels);
        notifyDataSetChanged();

    }

}