package com.pharmacy.agent.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharmacy.CommonMethods;
import com.pharmacy.R;
import com.pharmacy.agent.AgentRunningList;
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
    String from;
    CommonMethods commonMethods;
    public AgentCommonListAdapter(Context context, ArrayList<OrderModel> productModelArrayList,String from) {

        this.context    =   context;
        this.productModelArrayList =   productModelArrayList;
        gson    = new Gson();
        this.from = from;
        commonMethods   =   new CommonMethods();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public AgentCommonListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_pharmacy_running_list, parent, false);

        return new AgentCommonListAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView pharmacyTitle,pharmacyQuantity;
        ImageView done,doneAll,doneAllColor;
        LinearLayout dateTimeLayout;
        TextView dateTimeLayoutTextView,productType,productCreatedTime;
        LinearLayout parentLayout;

         @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
         public MyViewHolder(View view) {
            super(view);
            pharmacyTitle   =   view.findViewById(R.id.pharmacy_list_title);
            pharmacyQuantity    =   view.findViewById(R.id.pharmacy_list_quantity);
            done                =   view.findViewById(R.id.done);
            doneAll             =   view.findViewById(R.id.done_all);
            doneAllColor        =   view.findViewById(R.id.done_all_color);
            dateTimeLayout      =   view.findViewById(R.id.date_time_layout);
            dateTimeLayoutTextView  =   view.findViewById(R.id.date_time_layout_textview);
            productType         =   view.findViewById(R.id.pharmacy_list_product_type);
            productCreatedTime  =   view.findViewById(R.id.pharmacy_list_created_time);

            parentLayout        =   view.findViewById(R.id.parent_layout);


            if(from.toString().equalsIgnoreCase("running_list"))
            {
                done.setVisibility(View.VISIBLE);
            }
            else if(from.toString().equalsIgnoreCase("approved_list")){
                doneAll.setVisibility(View.VISIBLE);
            }
            else if(from.toString().equalsIgnoreCase("delivered_list")){
                doneAllColor.setVisibility(View.VISIBLE);
            }
     }

    }


    @Override
    public void onBindViewHolder(final AgentCommonListAdapter.MyViewHolder holder, final int position) {
       final OrderModel orderModel = productModelArrayList.get(position);
        holder.pharmacyTitle.setText(orderModel.ProductName);
        holder.pharmacyQuantity.setText(""+orderModel.Quantity);
        if(orderModel.ProductType!=null && orderModel.ProductType.toString().length()>0) {
            holder.productType.setText(orderModel.ProductType+" - ");
            holder.productType.setVisibility(View.VISIBLE);
        } else {
            holder.productType.setVisibility(View.GONE);
        }
        showDateAndTimeLayout(holder,productModelArrayList,position);

        holder.productCreatedTime.setText(commonMethods.getOnlyTime(orderModel.CreatedDate));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AgentRunningList) context).showProductDetailsBottomSheet(orderModel,from);
            }
        });

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


    private void showDateAndTimeLayout(MyViewHolder holder,List<OrderModel> orderModelList,int position)
    {
        if(orderModelList!=null  && orderModelList.size()>0)
        {
            String prevTime="";
            String currentTime="";

            int count=0;

                if(from.equalsIgnoreCase("approved_list")) {
                    String list = gson.toJson(orderModelList);
                    if(position==0)
                       prevTime = orderModelList.get(position).ApprovedDate;
                    else
                        prevTime = orderModelList.get(position-1).ApprovedDate;

                    currentTime = orderModelList.get(position).ApprovedDate;
                }
                else if(from.equalsIgnoreCase("delivered_list"))
                {
                    String list = gson.toJson(orderModelList);
                    if(position==0)
                        prevTime = orderModelList.get(position).DeliveredDate;
                    else
                        prevTime = orderModelList.get(position-1).DeliveredDate;
                    currentTime = orderModelList.get(position).DeliveredDate;

                }
                else if(from.equalsIgnoreCase("running_list"))
                {
                    String list = gson.toJson(orderModelList);
                    if(position==0)
                        prevTime = orderModelList.get(position).CreatedDate;
                    else
                        prevTime = orderModelList.get(position-1).CreatedDate;
                    currentTime = orderModelList.get(position).CreatedDate;
                }


                    String prevDate = prevTime.split("T")[0];
                    String currentDate = currentTime.split("T")[0];

                    if (position == 0) {
                        holder.dateTimeLayout.setVisibility(View.VISIBLE);
                        holder.dateTimeLayout.setActivated(false);
                        if (prevDate.equalsIgnoreCase(CommonMethods.getCurrentOnlyDate())) {
                            holder.dateTimeLayoutTextView.setText("TODAY");
                        } else if (prevDate.equalsIgnoreCase(CommonMethods.getYesterdayDate())) {
                            holder.dateTimeLayoutTextView.setText("YESTERDAY");
                        } else {
                            holder.dateTimeLayoutTextView.setText(""+CommonMethods.getConvertedTime(prevDate));
                        }
                    }
                    if (position > 0) {
                        if (!prevDate.toString().equalsIgnoreCase(currentDate)) {
                            holder.dateTimeLayout.setVisibility(View.VISIBLE);
                            if (currentDate.equalsIgnoreCase(CommonMethods.getCurrentOnlyDate())) {
                                holder.dateTimeLayoutTextView.setText("TODAY");
                            } else if (currentDate.equalsIgnoreCase(CommonMethods.getYesterdayDate())) {
                                holder.dateTimeLayoutTextView.setText("YESTERDAY");
                            } else {
                                holder.dateTimeLayoutTextView.setText(""+CommonMethods.getConvertedTime(currentDate));
                            }
                        } else {
                            holder.dateTimeLayout.setVisibility(View.GONE);
                        }
                    }
            }


//

    }

}