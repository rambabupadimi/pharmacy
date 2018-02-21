package com.pharmacy;

/**
 * Created by PCCS-0007 on 07-Feb-18.
 */

public interface DbConstants {
    public final String DATABASE_NAME ="pharmacy";
    public final int DATABASE_VERSION = 1;


    public final String TABLE_USER              =   "user";

    public final String COLUMN_ID               =   "id";
    public final String COLUMN_USERID           =   "user_id";
    public final String COLUMN_DEVICE_ID        =   "device_id";
    public final String COLUMN_DEVICE_UNIQUE_ID =   "device_unique_id";
    public final String COLUMN_USER_IS_VERIFIED =   "user_is_verified";
    public final String COLUMN_USER_PHONE_NUMBER=   "user_phone_number";
    public final String COLUMN_USER_EMAIL       =   "user_email";
    public final String COLUMN_USER_TOKEN       =   "user_token";
    public final String COLUMN_USER_DEVICE      =   "user_device";
    public final String COLUMN_DISTRIBUTOR_ID   =   "distributor_id";




    public final String TABLE_AGENT                 =   "agent";

    public final String COLUMN_AGENT_ID             =   "agent_id";
    public final String COLUMN_AGENT_NAME           =   "agent_name";
    public final String COLUMN_AGENT_PHONE_NUMBER   =   "agent_phone_number";
    public final String COLUMN_AGENT_EMAIL          =   "agent_email";
    public final String COLUMN_AGENT_ADDRESS        =   "agent_address";
    public final String COLUMN_AGENT_LANDMARK       =   "agent_landmark";
    public final String COLUMN_AGENT_CITY           =   "agent_city";
    public final String COLUMN_AGENT_STATE          =   "agent_state";
    public final String COLUMN_AGENT_PINCODE        =   "agent_pincode";
    public final String COLUMN_AGENT_DOOR_NUMBER    =   "agent_door_no";
    public final String COLUMN_AGENT_PHOTO          =   "agent_photo";
    public final String COLUMN_AGENT_ID_PROOF_PHOTO       =   "agent_id_proof_photo";
    public final String COLUMN_AGENT_PHOTO_LOCAL_PATH    =   "agent_photo_local_path";
    public final String COLUMN_AGENT_ID_PROOF_PHOTO_LOCAL = "agent_id_proof_photo_local_path";



    public final String TABLE_PHARMACY              =   "pharmacy";

    public final String COLUMN_PHARMACY_ID          =   "pharmacy_id";
    public final String COLUMN_PHARMACY_LOCAL_ID    =   "pharmacy_local_id";

    public final String COLUMN_PHARMACY_NAME        =   "pharmacy_name";
    public final String COLUMN_PHARMACY_OWNER_NAME  =   "pharmacy_owner_name";
    public final String COLUMN_PHARMACY_EMAIL_ID    =   "pharmacy_email_id";
    public final String COLUMN_PHARMACY_ADDRESS        =   "pharmacy_address";
    public final String COLUMN_PHARMACY_LANDMARK       =   "pharmacy_landmark";
    public final String COLUMN_PHARMACY_CITY           =   "pharmacy_city";
    public final String COLUMN_PHARMACY_STATE          =   "pharmacy_state";
    public final String COLUMN_PHARMACY_PINCODE        =   "pharmacy_pincode";
    public final String COLUMN_PHARMACY_DOOR_NUMBER    =   "pharmacy_door_no";

    public final String COLUMN_PHARMACY_PHOTO          =   "pharmacy_photo";
    public final String COLUMN_PHARMACY_LICENCE_PHOTO        =   "pharmacy_licence_photo";
    public final String COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO = "pharmacy_register_certificate_photo";
    public final String COLUMN_PHARMACY_PHOTO_LOCAL_PATH          =   "pharmacy_photo_local_path";
    public final String COLUMN_PHARMACY_LICENCE_PHOTO_LOCAL_PATH        =   "pharmacy_licence_photo_local_path";
    public final String COLUMN_PHARMACY_REGISTER_CERTIFICATE_PHOTO_LOCAL_PATH = "pharmacy_register_certificate_photo_local_path";
    public final String COLUMN_PHARMACY_PHONE_NUMBER = "pharmacy_phone_number";

    public final String COLUMN_PHARMACY_IS_APPROVED     =   "pharmacy_is_approved";
    public final String COLUMN_PHARMACY_IS_APPROVED_BY  =   "pharmacy_is_approved_by";



    public final String TABLE_ORDERS                 =   "orders";

    public final String COLUMN_ORDER_ID             =   "order_id";
    public final String COLUMN_PRODUCT_ID           =   "product_id";
    public final String COLUMN_PRODUCT_QUANTITY     =   "product_quantity";
    public final String COLUMN_PRODUCT_NAME         =   "product_name";
    public final String COLUMN_PRODUCT_IMAGE        =   "product_image";
    public final String COLUMN_PRODUCT_TYPE         =   "product_type";

    public final String COLUMN_ORDER_IS_RUNNING     =   "order_is_running";
    public final String COLUMN_ORDER_IS_APPROVED    =   "order_is_approved";
    public final String COLUMN_ORDER_IS_DELIVERED   =   "order_is_delivered";

    public final String COLUMN_ORDER_IS_RUNNING_DATE = "order_is_running_date";
    public final String COLUMN_ORDER_IS_APPROVED_BY = "order_is_approved_by";
    public final String COLUMN_ORDER_IS_DELIVERED_BY   =   "order_is_delivered_by";

    public final String COLUMN_ORDER_IS_APPROVED_DATE    =   "order_is_approved_date";
    public final String COLUMN_ORDER_IS_DELIVERED_DATE   =   "order_is_delivered_date";

    public final String COLUMN_ORDER_APPROVED_QUANTITY  =   "order_approved_quantity";
    public final String COLUMN_CREATED_TIME         =   "column_created_time";
    public final String COLUMN_UPDATED_TIME         =   "column_updated_time";

    public final String COLUMN_ORDER_DETAIL_ID      =   "order_detail_id";






}
