package com.pharmacy.db.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.PharmacyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PCCS-0007 on 07-Feb-18.
 */

public class PharmacyDAO  extends AbstractDAO{
    public PharmacyDAO(Context context) {
        super(context);
    }

    /*public long insertOrUpdate(PharmacyModel pharmacyModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_PHARMACY_ID,pharmacyModel.PharmacyID);
            values.put(COLUMN_PHARMACY_NAME,pharmacyModel.StoreName);
            values.put(COLUMN_PHARMACY_OWNER_NAME,pharmacyModel.Name);
            values.put(COLUMN_PHARMACY_EMAIL_ID,pharmacyModel.Email);
            values.put(COLUMN_PHARMACY_ADDRESS,pharmacyModel.Address);
            values.put(COLUMN_PHARMACY_LANDMARK,pharmacyModel.LandMark);
            values.put(COLUMN_PHARMACY_CITY,pharmacyModel.City);
            values.put(COLUMN_PHARMACY_STATE,pharmacyModel.State);
            values.put(COLUMN_PHARMACY_PINCODE,pharmacyModel.Pincode);
            values.put(COLUMN_PHARMACY_DOOR_NUMBER,pharmacyModel.DoorNo);
            values.put(COLUMN_USERID,pharmacyModel.UserID);
            values.put(COLUMN_PHARMACY_LICENCE_PHOTO,pharmacyModel.Licence);
            values.put(COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH,pharmacyModel.LicenceLocalPath);
            values.put(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO,pharmacyModel.Billing);
            values.put(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH,pharmacyModel.BillingLocalPath);
            values.put(COLUMN_PHARMACY_PHOTO,pharmacyModel.Image);
            values.put(COLUMN_PHARMACY_PHOTO_LOCAL_PATH,pharmacyModel.ImageLocalPath);
            values.put(COLUMN_PHARMACY_LOCAL_ID,pharmacyModel.PharmacyLocalId);
            values.put(COLUMN_PHARMACY_PHONE_NUMBER,pharmacyModel.PhoneNumber);
            values.put(COLUMN_PHARMACY_IS_APPROVED,pharmacyModel.IsApproved);
            values.put(COLUMN_PHARMACY_IS_APPROVED_BY,pharmacyModel.ApprovedBy);


            long id=0;

            Cursor cursor = db.query(TABLE_PHARMACY, new String[]{COLUMN_USERID}, COLUMN_USERID + " = ?", new String[]{pharmacyModel.UserID},
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                id =  db.updateWithOnConflict(TABLE_PHARMACY, values, COLUMN_USERID+" ='" + pharmacyModel.UserID + "'", null, SQLiteDatabase.CONFLICT_IGNORE);
            }
            else
            {
                id = db.insert(TABLE_PHARMACY, null, values);

            }

            Log.i("tag","value db is"+id);
            closeDatabase();
            return id;
        }catch (Exception e) {
            closeDatabase();
            return 0;
        }
    }
*/
    public long insertOrUpdateAddNewPharmacy(PharmacyModel pharmacyModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_PHARMACY_ID,pharmacyModel.PharmacyID);
            values.put(COLUMN_PHARMACY_NAME,pharmacyModel.StoreName);
            values.put(COLUMN_PHARMACY_OWNER_NAME,pharmacyModel.Name);
            values.put(COLUMN_PHARMACY_EMAIL_ID,pharmacyModel.Email);
            values.put(COLUMN_PHARMACY_ADDRESS,pharmacyModel.Address);
            values.put(COLUMN_PHARMACY_LANDMARK,pharmacyModel.LandMark);
            values.put(COLUMN_PHARMACY_CITY,pharmacyModel.City);
            values.put(COLUMN_PHARMACY_STATE,pharmacyModel.State);
            values.put(COLUMN_PHARMACY_PINCODE,pharmacyModel.Pincode);
            values.put(COLUMN_PHARMACY_DOOR_NUMBER,pharmacyModel.DoorNo);
            values.put(COLUMN_USERID,pharmacyModel.UserID);
            values.put(COLUMN_PHARMACY_LICENCE_PHOTO,pharmacyModel.Licence);
            values.put(COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH,pharmacyModel.LicenceLocalPath);
            values.put(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO,pharmacyModel.Billing);
            values.put(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH,pharmacyModel.BillingLocalPath);
            values.put(COLUMN_PHARMACY_PHOTO,pharmacyModel.Image);
            values.put(COLUMN_PHARMACY_PHOTO_LOCAL_PATH,pharmacyModel.ImageLocalPath);
            values.put(COLUMN_PHARMACY_LOCAL_ID,pharmacyModel.PharmacyLocalId);
            values.put(COLUMN_PHARMACY_PHONE_NUMBER,pharmacyModel.PhoneNumber);
            values.put(COLUMN_PHARMACY_IS_APPROVED,pharmacyModel.IsApproved);
            values.put(COLUMN_PHARMACY_IS_APPROVED_BY,pharmacyModel.ApprovedBy);


            long id=0;

            Cursor cursor = db.query(TABLE_PHARMACY, new String[]{COLUMN_PHARMACY_LOCAL_ID}, COLUMN_PHARMACY_LOCAL_ID + " = ?", new String[]{pharmacyModel.PharmacyLocalId},
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                id =  db.updateWithOnConflict(TABLE_PHARMACY, values, COLUMN_PHARMACY_LOCAL_ID+" ='" + pharmacyModel.PharmacyLocalId + "'", null, SQLiteDatabase.CONFLICT_IGNORE);
            }
            else
            {
                id = db.insert(TABLE_PHARMACY, null, values);

            }

            Log.i("tag","value db is"+id);
            closeDatabase();
            return id;
        }catch (Exception e) {
            closeDatabase();
            return 0;
        }
    }

    public ArrayList<PharmacyModel> getListOfPharmacys(String userid) {
        ArrayList<PharmacyModel> pharmacyModelList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String  query = " SELECT  * FROM "+TABLE_PHARMACY+ "  WHERE "+COLUMN_USERID+ " = "+userid;
            Cursor cursor = db.rawQuery(query,null);

            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToFirst())
                {
                    do {

                        PharmacyModel pharmacyModel = new PharmacyModel();

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_NAME)) != null) {
                            pharmacyModel.StoreName = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_NAME));
                        } else {
                            pharmacyModel.StoreName = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_OWNER_NAME)) != null) {
                            pharmacyModel.Name = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_OWNER_NAME));
                        } else {
                            pharmacyModel.Name = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_EMAIL_ID)) != null) {
                            pharmacyModel.Email = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_EMAIL_ID));
                        } else {
                            pharmacyModel.Email = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ADDRESS)) != null) {
                            pharmacyModel.Address = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ADDRESS));
                        } else {
                            pharmacyModel.Address = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LANDMARK)) != null) {
                            pharmacyModel.LandMark = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LANDMARK));
                        } else {
                            pharmacyModel.LandMark = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_CITY)) != null) {
                            pharmacyModel.City = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_CITY));
                        } else {
                            pharmacyModel.City = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_STATE)) != null) {
                            pharmacyModel.State = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_STATE));
                        } else {
                            pharmacyModel.State = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PINCODE)) != null) {
                            pharmacyModel.Pincode = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PINCODE));
                        } else {
                            pharmacyModel.Pincode = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_DOOR_NUMBER)) != null) {
                            pharmacyModel.DoorNo = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_DOOR_NUMBER));
                        } else {
                            pharmacyModel.DoorNo = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_USERID)) != null) {
                            pharmacyModel.UserID = (cursor.getString(cursor.getColumnIndex(COLUMN_USERID)));
                        } else {
                            pharmacyModel.UserID = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO)) != null) {
                            pharmacyModel.Licence = (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO)));
                        } else {
                            pharmacyModel.Licence = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH)) != null) {
                            pharmacyModel.LicenceLocalPath = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH));
                        } else {
                            pharmacyModel.LicenceLocalPath = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO)) != null) {
                            pharmacyModel.Billing = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO));
                        } else {
                            pharmacyModel.Billing = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH)) != null) {
                            pharmacyModel.BillingLocalPath = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH));
                        } else {
                            pharmacyModel.BillingLocalPath = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO)) != null) {
                            pharmacyModel.Image = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO));
                        } else {
                            pharmacyModel.Image = "";
                        }


                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO_LOCAL_PATH)) != null) {
                            pharmacyModel.ImageLocalPath = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO_LOCAL_PATH));
                        } else {
                            pharmacyModel.ImageLocalPath = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LOCAL_ID)) != null) {
                            pharmacyModel.PharmacyLocalId = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LOCAL_ID));
                        } else {
                            pharmacyModel.PharmacyLocalId = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHONE_NUMBER)) != null) {
                            pharmacyModel.PhoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHONE_NUMBER));
                        } else {
                            pharmacyModel.PhoneNumber = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID)) != null) {
                            pharmacyModel.PharmacyID = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID));
                        } else {
                            pharmacyModel.PharmacyID = "";
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED))!=null)
                        {

                            Log.i("tag","cursor value"+cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED)));
                            if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED)).equalsIgnoreCase("1"))
                               pharmacyModel.IsApproved  = true;
                            else
                               pharmacyModel.IsApproved = false;
                        }
                        else
                        {
                            pharmacyModel.IsApproved  =   false;
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED_BY))!=null)
                        {
                            pharmacyModel.ApprovedBy  =cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED_BY));
                        }
                        else
                        {
                            pharmacyModel.ApprovedBy  =   "";
                        }



                        pharmacyModelList.add(pharmacyModel);
                    }while (cursor.moveToNext());
                }
            }

            closeDatabase();
        }catch (Exception e) {
            closeDatabase();
        }
        return pharmacyModelList;
    }
/*

    public PharmacyModel getPharmacyData(String userid) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String  query = " SELECT  * FROM "+TABLE_PHARMACY+ "  WHERE "+COLUMN_USERID+ " = "+userid;
            Cursor cursor = db.rawQuery(query,null);

            PharmacyModel pharmacyModel = new PharmacyModel();
            if(cursor!=null && cursor.getCount()>0)
            {

                if(cursor.moveToFirst())
                {



                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_NAME)) != null) {
                            pharmacyModel.StoreName = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_NAME));
                        } else {
                            pharmacyModel.StoreName = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_OWNER_NAME)) != null) {
                            pharmacyModel.Name = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_OWNER_NAME));
                        } else {
                            pharmacyModel.Name = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_EMAIL_ID)) != null) {
                            pharmacyModel.Email = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_EMAIL_ID));
                        } else {
                            pharmacyModel.Email = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ADDRESS)) != null) {
                            pharmacyModel.Address = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ADDRESS));
                        } else {
                            pharmacyModel.Address = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LANDMARK)) != null) {
                            pharmacyModel.LandMark = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LANDMARK));
                        } else {
                            pharmacyModel.LandMark = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_CITY)) != null) {
                            pharmacyModel.City = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_CITY));
                        } else {
                            pharmacyModel.City = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_STATE)) != null) {
                            pharmacyModel.State = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_STATE));
                        } else {
                            pharmacyModel.State = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PINCODE)) != null) {
                            pharmacyModel.Pincode = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PINCODE));
                        } else {
                            pharmacyModel.Pincode = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_DOOR_NUMBER)) != null) {
                            pharmacyModel.DoorNo = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_DOOR_NUMBER));
                        } else {
                            pharmacyModel.DoorNo = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_USERID)) != null) {
                            pharmacyModel.UserID = (cursor.getString(cursor.getColumnIndex(COLUMN_USERID)));
                        } else {
                            pharmacyModel.UserID = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO)) != null) {
                            pharmacyModel.Licence = (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO)));
                        } else {
                            pharmacyModel.Licence = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH)) != null) {
                            pharmacyModel.LicenceLocalPath = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH));
                        } else {
                            pharmacyModel.LicenceLocalPath = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO)) != null) {
                            pharmacyModel.Billing = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO));
                        } else {
                            pharmacyModel.Billing = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH)) != null) {
                            pharmacyModel.BillingLocalPath = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH));
                        } else {
                            pharmacyModel.BillingLocalPath = "";
                        }

                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO)) != null) {
                            pharmacyModel.Image = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO));
                        } else {
                            pharmacyModel.Image = "";
                        }


                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO_LOCAL_PATH)) != null) {
                            pharmacyModel.ImageLocalPath = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO_LOCAL_PATH));
                        } else {
                            pharmacyModel.ImageLocalPath = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LOCAL_ID)) != null) {
                            pharmacyModel.PharmacyLocalId = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LOCAL_ID));
                        } else {
                            pharmacyModel.PharmacyLocalId = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHONE_NUMBER)) != null) {
                            pharmacyModel.PhoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHONE_NUMBER));
                        } else {
                            pharmacyModel.PhoneNumber = "";
                        }
                        if (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID)) != null) {
                            pharmacyModel.PharmacyID = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID));
                        } else {
                            pharmacyModel.PharmacyID = "";
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED))!=null)
                        {
                            Log.i("tag","cursor value"+cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED)));
                            if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED)).equalsIgnoreCase("1"))
                                pharmacyModel.IsApproved  = true;
                            else
                                pharmacyModel.IsApproved = false;
                        }
                        else
                        {
                            pharmacyModel.IsApproved  =   false;
                        }
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED_BY))!=null)
                        {
                            pharmacyModel.ApprovedBy  =cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED_BY));
                        }
                        else
                        {
                            pharmacyModel.ApprovedBy  =   "";
                        }
                       return pharmacyModel;

                }
            }

            closeDatabase();
        }catch (Exception e) {
            closeDatabase();
        }
        return null;
    }
*/

    public PharmacyModel getPharmacyDataByPharmacyID(String pharmacyLocalId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            PharmacyModel pharmacyModel = new PharmacyModel();
            String  query = " SELECT  * FROM "+TABLE_PHARMACY+ "  WHERE "+COLUMN_PHARMACY_LOCAL_ID+ " = '"+pharmacyLocalId+"' ";
            Cursor cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {
                if(cursor.moveToFirst())
                {
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_NAME))!=null)
                    {
                        pharmacyModel.StoreName = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_NAME));
                    }
                    else
                    {
                        pharmacyModel.StoreName = "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_OWNER_NAME))!=null)
                    {
                        pharmacyModel.Name  = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_OWNER_NAME));
                    }
                    else
                    {
                        pharmacyModel.Name = "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_EMAIL_ID))!=null)
                    {
                        pharmacyModel.Email = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_EMAIL_ID));
                    }
                    else
                    {
                        pharmacyModel.Email = "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ADDRESS))!=null)
                    {
                        pharmacyModel.Address   =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ADDRESS));
                    }
                    else
                    {
                        pharmacyModel.Address   =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LANDMARK))!=null)
                    {
                        pharmacyModel.LandMark  =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LANDMARK));
                    }
                    else
                    {
                        pharmacyModel.LandMark  =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_CITY))!=null)
                    {
                        pharmacyModel.City  =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_CITY));
                    }
                    else
                    {
                        pharmacyModel.City  =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_STATE))!=null)
                    {
                        pharmacyModel.State =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_STATE));
                    }
                    else
                    {
                        pharmacyModel.State =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PINCODE))!=null)
                    {
                        pharmacyModel.Pincode =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PINCODE));
                    }
                    else
                    {
                        pharmacyModel.Pincode   =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_DOOR_NUMBER))!=null)
                    {
                        pharmacyModel.DoorNo    =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_DOOR_NUMBER));
                    }
                    else
                    {
                        pharmacyModel.DoorNo    =   "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))!=null)
                    {
                        pharmacyModel.UserID    =   (cursor.getString(cursor.getColumnIndex(COLUMN_USERID)));
                    }
                    else
                    {
                        pharmacyModel.UserID    ="";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO))!=null)
                    {
                        pharmacyModel.Licence   =   (cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO)));
                    }
                    else
                    {
                        pharmacyModel.Licence   =   "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH))!=null)
                    {
                        pharmacyModel.LicenceLocalPath  =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH));
                    }
                    else
                    {
                        pharmacyModel.LicenceLocalPath = "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO))!=null)
                    {
                        pharmacyModel.Billing  = cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO));
                    }
                    else
                    {
                        pharmacyModel.Billing = "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH))!=null)
                    {
                        pharmacyModel.BillingLocalPath  =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH));
                    }
                    else
                    {
                        pharmacyModel.BillingLocalPath  =   "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO))!=null)
                    {
                        pharmacyModel.Image =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO));
                    }
                    else
                    {
                        pharmacyModel.Image =   "";
                    }


                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO_LOCAL_PATH))!=null)
                    {
                        pharmacyModel.ImageLocalPath    =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHOTO_LOCAL_PATH));
                    }
                    else
                    {
                        pharmacyModel.ImageLocalPath    =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LOCAL_ID))!=null)
                    {
                        pharmacyModel.PharmacyLocalId    =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_LOCAL_ID));
                    }
                    else
                    {
                        pharmacyModel.PharmacyLocalId    =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHONE_NUMBER))!=null)
                    {
                        pharmacyModel.PhoneNumber    =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_PHONE_NUMBER));
                    }
                    else
                    {
                        pharmacyModel.PhoneNumber    =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID))!=null)
                    {
                        pharmacyModel.PharmacyID    =   cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_ID));
                    }
                    else
                    {
                        pharmacyModel.PharmacyID    =   "";
                    }


                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED))!=null)
                    {
                        pharmacyModel.IsApproved  = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED)));
                    }
                    else
                    {
                        pharmacyModel.IsApproved  =   false;
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED_BY))!=null)
                    {
                        Log.i("tag","cursor value"+cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED)));
                        if(cursor.getString(cursor.getColumnIndex(COLUMN_PHARMACY_IS_APPROVED)).equalsIgnoreCase("1"))
                            pharmacyModel.IsApproved  = true;
                        else
                            pharmacyModel.IsApproved = false;
                    }
                    else
                    {
                        pharmacyModel.ApprovedBy  =   "";
                    }

                    return pharmacyModel;
                }
            }

            closeDatabase();
        }catch (Exception e) {
            closeDatabase();
        }
        return null;
    }



}
