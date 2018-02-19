package com.pharmacy.db.models;

import java.util.ArrayList;

/**
 * Created by PCCS-0007 on 07-Feb-18.
 */

public class PharmacyModel {



    public String UserID;
    public String UserType;
    public String PhoneNumber;
    public String DistributorID;
  //ownername
    public String Name;
    public String Email;
    public String DoorNo;
    public String Address;
    public String LandMark;
    public String City;
    public String State;
    public String Pincode;
    public String Country;
    //pharmacy name
    public String StoreName;
    public String CodeNumber;

    //no use
    public String IdProof;
    public String OwnerPhoto;

    public String Image;
    public String Licence;
    public String Billing;

    public String ImageLocalPath;
    public String LicenceLocalPath;
    public String BillingLocalPath;

    public String PharmacyID;
    public String PharmacyLocalId;
    public String CreatedTime;
    public String UpdatedTime;


    public String IsApproved;
    public String ApprovedBy;
    public String IsActive;
    public String IsDeleted;
    public String CreatedOn;
    public String CreatedBy;
    public String ModifiedOn;
    public String IsModifiedBy;
    public ArrayList<PharmacyModel> PharmacyList;
}
