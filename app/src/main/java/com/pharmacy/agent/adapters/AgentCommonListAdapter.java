package com.pharmacy.agent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.R;
import com.pharmacy.db.models.OrderModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PCCS-0007 on 15-Feb-18.
 */


public class AgentCommonListAdapter extends RecyclerView.Adapter<AgentCommonListAdapter.MyViewHolder> {

    Context context;
    ArrayList<OrderModel> productModelArrayList;
    Gson gson;
    public AgentCommonListAdapter(Context context, ArrayList<OrderModel> productModelArrayList) {

        this.context    =   context;
        this.productModelArrayList =   productModelArrayList;
        gson    = new Gson();
    }

    @Override
    public AgentCommonListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pharmacy_running_list, parent, false);

        return new AgentCommonListAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView pharmacyTitle,pharmacyQuantity;
         public MyViewHolder(View view) {
            super(view);
            pharmacyTitle   =   view.findViewById(R.id.pharmacy_list_title);
            pharmacyQuantity    =   view.findViewById(R.id.pharmacy_list_quantity);
     }

    }


    @Override
    public void onBindViewHolder(final AgentCommonListAdapter.MyViewHolder holder, final int position) {
       OrderModel orderModel = productModelArrayList.get(position);
        holder.pharmacyTitle.setText(orderModel.ProductName);
        holder.pharmacyQuantity.setText(""+orderModel.Quantity);
    }

    @Override
    public int getItemCount() {
        return (productModelArrayList != null) ? productModelArrayList.size() : 0;
    }

    public void addItems( List<OrderModel> prod)
    {
       productModelArrayList.clear();
        productModelArrayList.addAll(prod);
        notifyDataSetChanged();
    }

}