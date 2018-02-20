package com.pharmacy.db.models;

/**
 * Created by PCCS-0007 on 09-Feb-18.
 */

public class OrderModel {

    public String DistributorID;
    public String PharmacyID;
    public String UserID;
    public String ProductID;
    public int Quantity;
    public String Description;


    public boolean IsRunning;
    public String OrderID;
    public boolean IsApproved;
    public int ApprovedQuantity;
    public String ApprovedBy;
    public String ApprovedDate;
    public boolean IsDelivered;
    public String DeliveredDate;
    public String CreatedDate;

    public String ProductName;
    public String Image;
    public String ProductType;

    public String OrderDetailID;
}
