package com.pharmacy.db.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pharmacy.db.models.AgentModel;
import com.pharmacy.db.models.UserModel;

/**
 * Created by PCCS-0007 on 07-Feb-18.
 */

public class UserDAO extends AbstractDAO {


    public UserDAO(Context context) {
        super(context);
    }


    public long insert(UserModel userModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_USERID,userModel.UserID);
            values.put(COLUMN_USER_EMAIL,userModel.Email);
            values.put(COLUMN_USER_PHONE_NUMBER,userModel.PhoneNumber);
            values.put(COLUMN_USER_IS_VERIFIED,userModel.IsVerified);
            values.put(COLUMN_DEVICE_ID,userModel.DeviceId);
            values.put(COLUMN_DEVICE_UNIQUE_ID,userModel.DeviceUniqueId);
            values.put(COLUMN_CREATED_TIME,userModel.CreatedTime);
            values.put(COLUMN_UPDATED_TIME,userModel.UpdatedTime);
            values.put(COLUMN_DISTRIBUTOR_ID,userModel.DistributorID);

            long id = db.insert(TABLE_USER, null, values);
            closeDatabase();
            return id;
        }catch (Exception e) {
            closeDatabase();
            return 0;
        }

    }





    public UserModel getUserData(String userid) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            UserModel userModel  =new UserModel();
            String  query = " SELECT  * FROM "+TABLE_USER+ "  WHERE "+COLUMN_USERID+ " = "+userid;
            Cursor cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {
                if(cursor.moveToFirst())
                {
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))!=null)
                    {
                        userModel.UserID  =  cursor.getString(cursor.getColumnIndex(COLUMN_USERID));
                    }
                    else
                    {
                        userModel.UserID     =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE_NUMBER))!=null)
                    {
                        userModel.PhoneNumber    =   cursor.getString(cursor.getColumnIndex(COLUMN_USER_PHONE_NUMBER));
                    }
                    else
                    {
                        userModel.PhoneNumber     =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_DISTRIBUTOR_ID))!=null)
                    {
                        userModel.DistributorID  =   cursor.getString(cursor.getColumnIndex(COLUMN_DISTRIBUTOR_ID));
                    }
                    else
                    {
                       userModel.DistributorID  =   "";
                    }



                    return userModel;
                }
            }

            closeDatabase();
        }catch (Exception e) {
            closeDatabase();
        }
        return null;
    }


}
