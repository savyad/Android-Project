package com.cypress.academy.ble101_robot;

import android.support.annotation.NonNull;

import java.util.Objects;

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
   /* @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RepeaterModel repeater = (RepeaterModel) obj;
        System.out.println("asdasd");
        if (this.macdev.equals(repeater.macdev) ) return false;
        //if (rssi!=repeater.rssi) return false;
        return imageid != 0 ? true : true;


        *//*if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        RepeaterModel that = (RepeaterModel) obj;
        if (this.imageid != that.imageid) return false;
        if (!this.rssi.equals(that.rssi)) return false;
        if (!this.macdev.equals(that.macdev)) return false;
        return true;*//*

    }*/

    @Override
    public boolean equals(Object o) {
        System.out.println("in Equals");
        if (this == o) return true;
        if (!(o instanceof RepeaterModel)) return false;
        RepeaterModel that = (RepeaterModel) o;
        return getImageid() == that.getImageid() &&
                Objects.equals(getMacdev(), that.getMacdev()) &&
                Objects.equals(getRssi(), that.getRssi());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMacdev(), getRssi(), getImageid());
    }
/*@Override
    public int hashCode() {
        int result = Integer.valueOf(rssi);
       // result = result + (imageid != 0 ? imageid.hashCode() : 0);
        result = result + rssi.hashCode();
        System.out.println("hash");
        return result;
    }*/
}
