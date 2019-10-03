package com.apps.jlee.carcare.Objects;

public class Gas
{
    private int id;
    private double cost, amount, miles;
    private long dateRefilled;
    public boolean showCheckbox = false, showChecked = false;

    public Gas() {}
    public Gas(int id, double cost, double amount, double miles, long dateRefilled)
    {
        this.id = id;
        this.cost = cost;
        this.amount = amount;
        this.miles = miles;
        this.dateRefilled = dateRefilled;
    }

    public int getID()
    {
        return id;
    }

    public void setID(int id)
    {
        this.id = id;
    }

    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public double getMiles()
    {
        return miles;
    }

    public void setMiles(double miles)
    {
        this.miles = miles;
    }

    public long getDateRefilled()
    {
        return dateRefilled;
    }

    public void setDateRefilled(long dateRefilled)
    {
        this.dateRefilled = dateRefilled;
    }

    public String toString()
    {
        return "\nID: "+id+"\nCost: "+cost+"\nMiles: "+miles+"\nAmount: "+amount+"\nDate: "+dateRefilled+"\nShowCheckbox: "+showCheckbox+"\nShowChecked: "+showChecked;
    }
}
