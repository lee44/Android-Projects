package com.apps.jlee.carcare;

public class Oil
{
    private int id;
    private String oil_name;
    private double oil_amount, mileage;
    private String date;

    public Oil() {}
    public Oil(int id, String oil_name, double oil_amount, double mileage, String date)
    {
        this.id = id;
        this.oil_name = oil_name;
        this.oil_amount = oil_amount;
        this.mileage = mileage;
        this.date = date;
    }

    public int getID()
    {
        return id;
    }

    public void setID(int id)
    {
        this.id = id;
    }

    public String getOilName()
    {
        return oil_name;
    }

    public void setOilName(String oil_name)
    {
        this.oil_name = oil_name;
    }

    public double getOilAmount()
    {
        return oil_amount;
    }

    public void setOilAmount(double oil_amount)
    {
        this.oil_amount = oil_amount;
    }

    public double getMileage()
    {
        return mileage;
    }

    public void setMileage(double mileage)
    {
        this.mileage = mileage;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String toString()
    {
        return "\nID: "+id+"\nOil: "+oil_name+"\nOil Amount: "+oil_amount+"\nMileage: "+mileage+"\nDate: "+date;
    }
}
