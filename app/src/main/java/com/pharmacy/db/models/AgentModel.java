package com.pharmacy.db.models;

import java.util.ArrayList;

/**
 * Created by PCCS-0007 on 09-Feb-18.
 */

public class AgentModel {




    public String AgentID;
    public String UserID;
    public String UserType;
    public String PhoneNumber;
    public String DistributorID;
    public String Name;
    public String Email;
    public String DoorNo;
    public String Address;
    public String LandMark;
    public String City;
    public String State;
    public String Pincode;
    public String Country;
    public String Image;
    public String IdProof;
    public String CreatedTime;
    public String UpdatedTime;
    public String ImageLocalPath;
    public String IdProofLocalPath;

    public String IsApproved;
    public String ApprovedBy;
    public String IsActive;
    public String IsDeleted;
    public String CreatedOn;
    public String CreatedBy;
    public String ModifiedOn;
    public String ModifiedBy;
    public ArrayList<AgentModel>  AgentList;
    public ArrayList<PharmacyModel> AgentPharmacyList;
    public ArrayList<OrderModel> RunningList;

}
