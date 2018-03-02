package com.pharmacy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pharmacy.db.models.OrderModel;

/**
 * Created by PCCS-0007 on 02-Mar-18.
 */

public class ViewProductDetailsBottomSheet extends BottomSheetDialogFragment {


    TextView titile,shortDescription;
    ImageView productImage;
    TextView delete;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.view_product_details_bottom_sheet, container);

        titile  =   contentView.findViewById(R.id.product_detail_title);
        shortDescription    =   contentView.findViewById(R.id.product_detail_short_description);
        productImage        =   contentView.findViewById(R.id.product_detail_image);
        delete              =   contentView.findViewById(R.id.product_detail_delete);

        OrderModel orderModel = (OrderModel) getArguments().getSerializable("object");
           try {

               String from = getArguments().getString("from");
               if (from != null && from.toString().equalsIgnoreCase("running_list")) {
                   delete.setVisibility(View.VISIBLE);
               }
           }catch (Exception e)
           {
               e.printStackTrace();
           }
        titile.setText(orderModel.ProductName);
        shortDescription.setText(orderModel.ProductType);
        return contentView;

    }
}
