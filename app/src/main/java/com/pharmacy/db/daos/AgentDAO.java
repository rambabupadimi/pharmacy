package com.pharmacy.db.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pharmacy.db.models.AgentModel;

/**
 * Created by PCCS-0007 on 09-Feb-18.
 */

public class AgentDAO extends AbstractDAO {
    public AgentDAO(Context context) {
        super(context);
    }


    public long insertOrUpdate(AgentModel agentModel) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_AGENT_ID,agentModel.AgentID);
            values.put(COLUMN_AGENT_NAME,agentModel.Name);
            values.put(COLUMN_AGENT_EMAIL,agentModel.Email);
            values.put(COLUMN_AGENT_ADDRESS,agentModel.Address);
            values.put(COLUMN_AGENT_CITY,agentModel.City);
            values.put(COLUMN_AGENT_STATE,agentModel.State);
            values.put(COLUMN_AGENT_DOOR_NUMBER,agentModel.DoorNo);
            values.put(COLUMN_AGENT_LANDMARK,agentModel.LandMark);
              values.put(COLUMN_AGENT_PINCODE,agentModel.Pincode);
            values.put(COLUMN_CREATED_TIME,agentModel.CreatedTime);
            values.put(COLUMN_UPDATED_TIME,agentModel.UpdatedTime);
            values.put(COLUMN_USERID,agentModel.UserID);

            values.put(COLUMN_AGENT_IS_APPROVED,agentModel.IsApproved);
            values.put(COLUMN_AGENT_IS_APPROVED_BY,agentModel.ApprovedBy);

            values.put(COLUMN_AGENT_PHOTO,agentModel.Image);
            if(agentModel.ImageLocalPath!=null)
               values.put(COLUMN_AGENT_PHOTO_LOCAL_PATH,agentModel.ImageLocalPath);

            values.put(COLUMN_AGENT_ID_PROOF_PHOTO,agentModel.IdProof);
            if(agentModel.IdProofLocalPath!=null)
                values.put(COLUMN_AGENT_ID_PROOF_PHOTO_LOCAL,agentModel.IdProofLocalPath);
            long id=0;

           Cursor cursor = db.query(TABLE_AGENT, new String[]{COLUMN_USERID}, COLUMN_USERID + " = ?", new String[]{agentModel.UserID},
                    null, null, null, null);
            if (cursor.moveToFirst()) {
              id =  db.updateWithOnConflict(TABLE_AGENT, values,
                        COLUMN_USERID+" ='" + agentModel.UserID + "'",
                        null, SQLiteDatabase.CONFLICT_IGNORE);
            }
            else
            {
                id = db.insert(TABLE_AGENT, null, values);

            }

            Log.i("tag","value db is"+id);
            closeDatabase();
            return id;
        }catch (Exception e) {
            closeDatabase();
            return 0;
        }
    }



    public AgentModel getAgentData(String userid) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            AgentModel agentModel = new AgentModel();
            String  query = " SELECT  * FROM "+TABLE_AGENT+ "  WHERE "+COLUMN_USERID+ " = "+userid;
            Cursor cursor = db.rawQuery(query,null);
            if(cursor!=null && cursor.getCount()>0)
            {
                if(cursor.moveToFirst())
                {
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_NAME))!=null)
                    {
                        agentModel.Name  =  cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_NAME));
                    }
                    else
                    {
                        agentModel.Name     =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_EMAIL))!=null)
                    {
                        agentModel.Email    =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_EMAIL));
                    }
                    else
                    {
                        agentModel.Email    =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_ADDRESS))!=null)
                    {
                        agentModel.Address  =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_ADDRESS));
                    }
                    else
                    {
                        agentModel.Address  =   "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_LANDMARK))!=null)
                    {
                        agentModel.LandMark =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_LANDMARK));
                    }
                    else
                    {
                        agentModel.LandMark =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_CITY))!=null)
                    {
                        agentModel.City =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_CITY));
                    }
                    else
                    {
                        agentModel.City="";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_STATE))!=null)
                    {
                        agentModel.State    =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_STATE));
                    }
                    else
                    {
                        agentModel.State    ="";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_PINCODE))!=null)
                    {
                        agentModel.Pincode  =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_PINCODE));
                    }
                    else
                    {
                        agentModel.Pincode  =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_DOOR_NUMBER))!=null)
                    {
                        agentModel.DoorNo   =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_DOOR_NUMBER));
                    }
                    else
                    {
                        agentModel.DoorNo   =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_USERID))!=null)
                    {
                        agentModel.UserID   = cursor.getString(cursor.getColumnIndex(COLUMN_USERID));
                    }
                    else
                    {
                        agentModel.UserID   =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_PHOTO_LOCAL_PATH))!=null)
                    {
                        agentModel.ImageLocalPath   =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_PHOTO_LOCAL_PATH));
                    }
                    else
                    {
                        agentModel.ImageLocalPath   =   "";
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_PHOTO))!=null)
                    {
                        agentModel.Image    =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_PHOTO));
                    }
                    else
                    {
                        agentModel.Image    =   "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_ID_PROOF_PHOTO))!=null)
                    {
                        agentModel.IdProof  = cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_ID_PROOF_PHOTO));
                    }
                    else
                    {
                        agentModel.IdProof =    "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_ID_PROOF_PHOTO_LOCAL))!=null)
                    {
                        agentModel.IdProofLocalPath =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_ID_PROOF_PHOTO_LOCAL));
                    }
                    else
                    {
                        agentModel.IdProofLocalPath =   "";
                    }

                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_IS_APPROVED))!=null)
                    {

                        if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_IS_APPROVED)).equalsIgnoreCase("1"))
                            agentModel.IsApproved  = true;
                        else
                            agentModel.IsApproved = false;
                    }
                    else
                    {
                        agentModel.IsApproved =   false;
                    }
                    if(cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_IS_APPROVED_BY))!=null)
                    {
                        agentModel.ApprovedBy =   cursor.getString(cursor.getColumnIndex(COLUMN_AGENT_IS_APPROVED_BY));
                    }
                    else
                    {
                        agentModel.ApprovedBy =   "";
                    }




                    return agentModel;
                }
            }

            closeDatabase();
        }catch (Exception e) {
            closeDatabase();
        }
        return null;
    }


}
