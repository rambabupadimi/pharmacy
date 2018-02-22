package com.pharmacy.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pharmacy.DbConstants;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by PCCS-0007 on 07-Feb-18.
 */

public class DatabaseManager extends SQLiteOpenHelper implements DbConstants {

    private static DatabaseManager instance;
    private SQLiteDatabase mDatabase;
    private Context context;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(queryToCreateOrderTable());
        db.execSQL(queryToCreateUserTable());
        db.execSQL(queryToCreateAgentTable());
        db.execSQL(queryToCreatePharmacyTable());

        Log.i("tag","one"+queryToCreatePharmacyTable());
        Log.i("tag","one 1"+queryToCreateOrderTable());

    }



    private String queryToCreateOrderTable()
    {
        return "CREATE TABLE IF NOT EXISTS "
                + TABLE_ORDERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY autoincrement ,"

                + COLUMN_ORDER_ID + " TEXT ,"
                + COLUMN_PRODUCT_ID + " TEXT ,"
                + COLUMN_PRODUCT_NAME + " TEXT ,"
                + COLUMN_PRODUCT_IMAGE + " TEXT ,"
                + COLUMN_PRODUCT_TYPE + " TEXT ,"


                + COLUMN_PRODUCT_QUANTITY + " INTEGER DEFAULT 0 ,"

                + COLUMN_ORDER_IS_RUNNING + " TEXT , "
                + COLUMN_ORDER_IS_RUNNING_DATE + " TEXT , "

                + COLUMN_ORDER_IS_APPROVED + " TEXT , "
                + COLUMN_ORDER_IS_APPROVED_BY + " TEXT ,"
                + COLUMN_ORDER_IS_APPROVED_DATE + " TEXT , "

                + COLUMN_ORDER_IS_DELIVERED + " TEXT  , "
                + COLUMN_ORDER_IS_DELIVERED_DATE + " TEXT ,"

                + COLUMN_ORDER_DETAIL_ID + " TEXT ,"

                + COLUMN_PHARMACY_OWNER_NAME + " TEXT ,"
                + COLUMN_ORDER_APPROVED_QUANTITY + " INTEGER DEFAULT 0 ,"

                + COLUMN_USERID + " TEXT , "
                + COLUMN_PHARMACY_ID + " TEXT , "

                + COLUMN_CREATED_TIME + " TEXT ,"
                + COLUMN_UPDATED_TIME + " TEXT )";

    }

    private String queryToCreatePharmacyTable()
    {
        return "CREATE TABLE IF NOT EXISTS "
                + TABLE_PHARMACY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY autoincrement ,"
                + COLUMN_PHARMACY_ID + " TEXT ,"
                + COLUMN_PHARMACY_LOCAL_ID + " TEXT , "
                + COLUMN_PHARMACY_NAME + " TEXT,"
                + COLUMN_PHARMACY_EMAIL_ID + " TEXT,"
                + COLUMN_PHARMACY_OWNER_NAME + " TEXT,"

                + COLUMN_PHARMACY_ADDRESS + " TEXT , "
                + COLUMN_PHARMACY_LANDMARK + " TEXT , "
                + COLUMN_PHARMACY_CITY + " TEXT , "
                + COLUMN_PHARMACY_STATE + " TEXT , "
                + COLUMN_PHARMACY_PINCODE + " TEXT , "
                + COLUMN_PHARMACY_DOOR_NUMBER + " TEXT ,"

                + COLUMN_PHARMACY_IS_APPROVED + " TEXT ,"
                + COLUMN_PHARMACY_IS_APPROVED_BY + " TEXT ,"

                + COLUMN_PHARMACY_PHOTO + " TEXT ,"
                + COLUMN_PHARMACY_LICENCE_PHOTO + " TEXT , "
                + COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO + " TEXT ,"

                + COLUMN_PHARMACY_PHOTO_LOCAL_PATH + " TEXT ,"
                + COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH + " TEXT , "
                + COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH + " TEXT ,"

                + COLUMN_PHARMACY_PHONE_NUMBER + " TEXT , "

                + COLUMN_USERID + " TEXT ,"
                + COLUMN_CREATED_TIME  + " TEXT ,"
                + COLUMN_UPDATED_TIME + " TEXT )";
    }

    private String queryToCreateUserTable()
    {
        return "CREATE TABLE IF NOT EXISTS "
                + TABLE_USER + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY autoincrement ,"
                + COLUMN_USERID + " TEXT ,"
                + COLUMN_USER_PHONE_NUMBER + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_DEVICE_ID + " TEXT,"
                + COLUMN_DEVICE_UNIQUE_ID + " TEXT ,"
                + COLUMN_DISTRIBUTOR_ID   + " TEXT ,"
                + COLUMN_USER_IS_VERIFIED + " INTEGER DEFAULT 0 ,"
                + COLUMN_CREATED_TIME   + " TEXT ,"
                + COLUMN_UPDATED_TIME + " TEXT )";
    }

    private String queryToCreateAgentTable()
    {
        return "CREATE TABLE IF NOT EXISTS "
                + TABLE_AGENT + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY autoincrement ,"
                + COLUMN_AGENT_ID + " TEXT ,"
                + COLUMN_AGENT_NAME + " TEXT,"
                + COLUMN_AGENT_EMAIL + " TEXT,"
                + COLUMN_AGENT_PHONE_NUMBER + " TEXT ,"

                + COLUMN_AGENT_ADDRESS + " TEXT , "
                + COLUMN_AGENT_LANDMARK + " TEXT , "
                + COLUMN_AGENT_CITY + " TEXT , "
                + COLUMN_AGENT_STATE + " TEXT , "
                + COLUMN_AGENT_PINCODE + " TEXT , "
                + COLUMN_AGENT_DOOR_NUMBER + " TEXT ,"

                + COLUMN_AGENT_PHOTO    +" TEXT ,"
                + COLUMN_AGENT_PHOTO_LOCAL_PATH + " TEXT ,"

                + COLUMN_AGENT_ID_PROOF_PHOTO + " TEXT ,"
                + COLUMN_AGENT_ID_PROOF_PHOTO_LOCAL + " TEXT , "

                + COLUMN_AGENT_IS_APPROVED+ " TEXT , "
                + COLUMN_AGENT_IS_APPROVED_BY+ " TEXT, "

                + COLUMN_USERID + " TEXT, "
                + COLUMN_CREATED_TIME + " TEXT , "
                + COLUMN_UPDATED_TIME + " TEXT )";
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }
    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            try{
                mDatabase = instance.getWritableDatabase();
            }catch(Exception e){

            }
        }
        if (!mDatabase.isOpen())
        {
            try{
                mDatabase = instance.getWritableDatabase();
            }catch(Exception e){

            }
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0&&mDatabase.isOpen()) {
            try{
                mDatabase.close();
            }catch(Exception e){

            }
        }
        //HLog.i(TAG,"After Closing Database "+mOpenCounter.get());
    }

    public synchronized void deleteDatabase(){
        //HLog.i(TAG,"Before Deleting Database "+mOpenCounter.get());
        try{
            context.deleteDatabase(DATABASE_NAME);
            mOpenCounter.set(0);
        }catch (Exception e) {

        }
        //HLog.i(TAG,"After Deleting Database "+mOpenCounter.get());

    }



    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"message"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }


}
