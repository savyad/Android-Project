package com.cypress.academy.ble101_robot;

import android.support.annotation.NonNull;

public class RepeaterModel  {

    public String macdev;
    public String rssi ;
    public int imageid;

    public RepeaterModel(String macdev, String rssi, int imageid) {
        this.macdev = macdev;
        this.rssi = rssi;
        this.imageid = imageid;
    }

    public String getMacdev() {
        return macdev;
    }

    public void setMacdev(String macdev) {
        this.macdev = macdev;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public int getImageid() {
        return imageid;
    }

    public void setImageid(int imageid) {
        this.imageid = imageid;
    }



    /*@Override
    public int compareTo(@NonNull Object o) {
        RepeaterModel compare =(RepeaterModel)o;
        if(compare.getMacdev().equals(this.macdev) && compare.getImageid()==this.imageid && compare.getRssi()==this.rssi)
        {
            return 0;
        }
        return 1;
    }

    @Override
    public RepeaterModel clone()
    {
        RepeaterModel clone;
        try {
            clone = (RepeaterModel) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); //should not happen
        }

        return clone;

    }*/
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RepeaterModel repeater = (RepeaterModel) obj;

        if (macdev .equals(repeater.macdev) ) return false;
        if (rssi!=repeater.rssi) return false;
        return imageid != 0 ? true : false;

    }

   /* @Override
    public int hashCode() {
        int result = id;
        result = result + (name != null ? name.hashCode() : 0);
        result = result + role.hashCode();
        return result;
    }*/
}
