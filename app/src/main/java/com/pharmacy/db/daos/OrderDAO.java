package com.pharmacy.db.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pharmacy.db.models.OrderModel;
import com.pharmacy.db.models.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PCCS-0007 on 09-Feb-18.
 */

public class OrderDAO  extends AbstractDAO{


    public OrderDAO(Context context) {
        super(context);
    }


    public long insert(OrderModel orderModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_USERID,orderModel.UserID);
            values.put(COLUMN_PHARMACY_ID,orderModel.PharmacyID);
            values.put(COLUMN_PRODUCT_ID,orderModel.ProductID);
            values.put(COLUMN_PRODUCT_QUANTITY,orderModel.Quantity);
            values.put(COLUMN_ORDER_IS_DELIVERED,orderModel.IsDelivered);
            values.put(COLUMN_ORDER_IS_APPROVED,orderModel.IsApproved);
            values.put(COLUMN_ORDER_IS_RUNNING,orderModel.IsRunning);
            values.put(COLUMN_ORDER_IS_RUNNING_DATE,orderModel.CreatedDate);
            values.put(COLUMN_ORDER_IS_APPROVED_BY,orderModel.ApprovedBy);
            values.put(COLUMN_ORDER_IS_APPROVED_DATE,orderModel.ApprovedDate);
            values.put(COLUMN_ORDER_APPROVED_QUANTITY,orderModel.ApprovedQuantity);
            values.put(COLUMN_ORDER_IS_DELIVERED_DATE,orderModel.DeliveredDate);
            values.put(COLUMN_PRODUCT_NAME,orderModel.ProductName);
            values.put(COLUMN_PRODUCT_IMAGE,orderModel.Image);
            values.put(COLUMN_PRODUCT_TYPE,orderModel.ProductType);

            values.put(COLUMN_ORDER_ID,orderModel.OrderID);
            values.put(COLUMN_ORDER_DETAIL_ID,orderModel.OrderDetailID);
            long id=0;

            Cursor cursor = db.query(TABLE_ORDERS, new String[]{COLUMN_ORDER_DETAIL_ID}, COLUMN_ORDER_DETAIL_ID + " = ?", new String[]{orderModel.OrderDetailID},
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                id =  db.updateWithOnConflict(TABLE_ORDERS, values,
                        COLUMN_ORDER_DETAIL_ID+" ='" + orderModel.OrderDetailID + "'",
                        null, SQLiteDatabase.CONFLICT_IGNORE);
            }
            else
            {
                id = db.insert(TABLE_ORDERS, null, values);

            }

            closeDatabase();
            return id;
        }catch (Exception e) {
           Log.i("tag","exception is"+ e);
            closeDatabase();
            return 0;
        }

    }




    public List<OrderModel> getOrderData( String from,String pharmacyId) {

        ArrayList<OrderModel> orderModels = new ArrayList<>();

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String  query=null;


            int val=1,valIs=0;
            if(from.equalsIgnoreCase("running")) {

                if(pharmacyId==null)
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_APPROVED+" = "+valIs+ " AND "+COLUMN_ORDER_IS_DELIVERED+" = "+valIs;
                else
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_APPROVED+" = "+valIs+" AND "+COLUMN_ORDER_IS_DELIVERED+" = "+valIs+" AND "+COLUMN_PHARMACY_ID+" ="+pharmacyId+" ";
            }
            else if(from.equalsIgnoreCase("approved")){
               if(pharmacyId==null)
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_APPROVED+" = "+val+" AND "+COLUMN_ORDER_IS_DELIVERED+" = "+valIs;
               else
                   query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_APPROVED+" = "+val+" AND "+COLUMN_ORDER_IS_DELIVERED+" = "+valIs+" AND "+COLUMN_PHARMACY_ID+" ="+pharmacyId+"  ";

            }
            else if(from.equalsIgnoreCase("delivered")){
                if(pharmacyId==null)
                   query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_DELIVERED+" = "+val;
                else
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_DELIVERED+" = "+val+" AND "+COLUMN_PHARMACY_ID+" ="+pharmacyId+" ";

            }
            Cursor cursor = db.rawQuery(query,null);
            if (cursor!=null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    try {


                    OrderModel orderModel  =new OrderModel();

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))!=null)
                    {
                        orderModel.UserID  =  cursor.getString(cursor.getColumnIndex(COLUMN_USERID));
                    }
                    else
                    {
                        orderModel.UserID     =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID))!=null)
                    {
                        orderModel.PharmacyID    =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID));
                    }
                    else
                    {
                        orderModel.PharmacyID     =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID))!=null)
                    {
                        orderModel.ProductID  =   cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID));
                    }
                    else
                    {
                        orderModel.ProductID  =   "";
                    }
                    if(cursor.getInt(cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY))!=0)
                    {
                        orderModel.Quantity  =  Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY)));
                    }
                    else
                    {
                        orderModel.Quantity  =   0;
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_DELIVERED))!=null)
                    {
                        orderModel.IsDelivered  = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_DELIVERED)));
                    }
                    else
                    {
                        orderModel.IsDelivered  =   false;
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED))!=null)
                    {
                        orderModel.IsApproved  = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED)));
                    }
                    else
                    {
                        orderModel.IsApproved  =   false;
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_RUNNING))!=null)
                    {
                        orderModel.IsRunning  = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_RUNNING)));
                    }
                    else
                    {
                        orderModel.IsRunning  =   false;
                    }

                    try {
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_RUNNING_DATE)) != null) {
                            orderModel.CreatedDate = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_RUNNING_DATE));
                        } else {
                            orderModel.CreatedDate = "";
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED_BY))!=null)
                    {
                        orderModel.ApprovedBy  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED_BY));
                    }
                    else
                    {
                        orderModel.ApprovedBy  =   "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED_DATE))!=null)
                    {
                        orderModel.ApprovedDate  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED_DATE));
                    }
                    else
                    {
                        orderModel.ApprovedDate  =   "";
                    }
                    if(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_APPROVED_QUANTITY))!=0)
                    {
                        orderModel.ApprovedQuantity  = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_APPROVED_QUANTITY));
                    }
                    else
                    {
                        orderModel.ApprovedQuantity  =   0;
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_DELIVERED_DATE))!=null)
                    {
                        orderModel.DeliveredDate  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_DELIVERED_DATE));
                    }
                    else
                    {
                        orderModel.DeliveredDate  =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_ID))!=null)
                    {
                        orderModel.OrderID  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_ID));
                    }
                    else
                    {
                        orderModel.OrderID  =   "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME))!=null)
                    {
                        orderModel.ProductName  = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME));
                    }
                    else
                    {
                        orderModel.ProductName  =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_IMAGE))!=null)
                    {
                        orderModel.Image  = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_IMAGE));
                    }
                    else
                    {
                        orderModel.Image  =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_TYPE))!=null)
                    {
                        orderModel.ProductType  = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_TYPE));
                    }
                    else
                    {
                        orderModel.ProductType  =   "";
                    }

                        if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DETAIL_ID))!=null)
                        {
                            orderModel.OrderDetailID  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DETAIL_ID));
                        }
                        else
                        {
                            orderModel.OrderDetailID  =   "";
                        }

                        orderModels.add(orderModel);
                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }

                    cursor.moveToNext();
                }
            }
            closeDatabase();
        }catch (Exception e) {
            closeDatabase();
        }
        return orderModels;
    }



    public List<OrderModel> getOrderDataPagination( String from,String pharmacyId,int row) {

        ArrayList<OrderModel> orderModels = new ArrayList<>();

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String  query=null;


            int val=1,valIs=0;
            if(from.equalsIgnoreCase("running")) {

                if(pharmacyId==null)
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_APPROVED+" = "+valIs+ " AND "+COLUMN_ORDER_IS_DELIVERED+" = "+valIs+" LIMIT "+row;
                else
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_APPROVED+" = "+valIs+" AND "+COLUMN_ORDER_IS_DELIVERED+" = "+valIs+" AND "+COLUMN_PHARMACY_ID+" ="+pharmacyId+" ORDER BY "+COLUMN_ORDER_IS_RUNNING_DATE+" DESC LIMIT "+row;
            }
            else if(from.equalsIgnoreCase("approved")){
                if(pharmacyId==null)
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_APPROVED+" = "+val+" AND "+COLUMN_ORDER_IS_DELIVERED+" = "+valIs+" LIMIT "+row;
                else
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_APPROVED+" = "+val+" AND "+COLUMN_ORDER_IS_DELIVERED+" = "+valIs+" AND "+COLUMN_PHARMACY_ID+" ="+pharmacyId+" LIMIT "+row;

            }
            else if(from.equalsIgnoreCase("delivered")){
                if(pharmacyId==null)
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_DELIVERED+" = "+val+" LIMIT "+row;
                else
                    query = " SELECT  * FROM " + TABLE_ORDERS + "  WHERE " +COLUMN_ORDER_IS_DELIVERED+" = "+val+" AND "+COLUMN_PHARMACY_ID+" ="+pharmacyId+" LIMIT "+row;

            }
            Cursor cursor = db.rawQuery(query,null);
            if (cursor!=null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    try {


                        OrderModel orderModel  =new OrderModel();

                        if(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))!=null)
                        {
                            orderModel.UserID  =  cursor.getString(cursor.getColumnIndex(COLUMN_USERID));
                        }
                        else
                        {
                            orderModel.UserID     =   "";
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID))!=null)
                        {
                            orderModel.PharmacyID    =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID));
                        }
                        else
                        {
                            orderModel.PharmacyID     =   "";
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID))!=null)
                        {
                            orderModel.ProductID  =   cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID));
                        }
                        else
                        {
                            orderModel.ProductID  =   "";
                        }
                        if(cursor.getInt(cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY))!=0)
                        {
                            orderModel.Quantity  =  Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY)));
                        }
                        else
                        {
                            orderModel.Quantity  =   0;
                        }

                        if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_DELIVERED))!=null)
                        {
                            orderModel.IsDelivered  = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_DELIVERED)));
                        }
                        else
                        {
                            orderModel.IsDelivered  =   false;
                        }

                        if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED))!=null)
                        {
                            orderModel.IsApproved  = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED)));
                        }
                        else
                        {
                            orderModel.IsApproved  =   false;
                        }

                        if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_RUNNING))!=null)
                        {
                            orderModel.IsRunning  = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_RUNNING)));
                        }
                        else
                        {
                            orderModel.IsRunning  =   false;
                        }

                        try {
                            if (cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_RUNNING_DATE)) != null) {
                                orderModel.CreatedDate = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_RUNNING_DATE));
                            } else {
                                orderModel.CreatedDate = "";
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED_BY))!=null)
                        {
                            orderModel.ApprovedBy  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED_BY));
                        }
                        else
                        {
                            orderModel.ApprovedBy  =   "";
                        }

                        if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED_DATE))!=null)
                        {
                            orderModel.ApprovedDate  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_APPROVED_DATE));
                        }
                        else
                        {
                            orderModel.ApprovedDate  =   "";
                        }
                        if(cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_APPROVED_QUANTITY))!=0)
                        {
                            orderModel.ApprovedQuantity  = cursor.getInt(cursor.getColumnIndex(COLUMN_ORDER_APPROVED_QUANTITY));
                        }
                        else
                        {
                            orderModel.ApprovedQuantity  =   0;
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_DELIVERED_DATE))!=null)
                        {
                            orderModel.DeliveredDate  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_IS_DELIVERED_DATE));
                        }
                        else
                        {
                            orderModel.DeliveredDate  =   "";
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_ID))!=null)
                        {
                            orderModel.OrderID  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_ID));
                        }
                        else
                        {
                            orderModel.OrderID  =   "";
                        }

                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME))!=null)
                        {
                            orderModel.ProductName  = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME));
                        }
                        else
                        {
                            orderModel.ProductName  =   "";
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_IMAGE))!=null)
                        {
                            orderModel.Image  = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_IMAGE));
                        }
                        else
                        {
                            orderModel.Image  =   "";
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_TYPE))!=null)
                        {
                            orderModel.ProductType  = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_TYPE));
                        }
                        else
                        {
                            orderModel.ProductType  =   "";
                        }

                        if(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DETAIL_ID))!=null)
                        {
                            orderModel.OrderDetailID  = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DETAIL_ID));
                        }
                        else
                        {
                            orderModel.OrderDetailID  =   "";
                        }

                        orderModels.add(orderModel);
                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }

                    cursor.moveToNext();
                }
            }
            closeDatabase();
        }catch (Exception e) {
            closeDatabase();
        }
        return orderModels;
    }

}
