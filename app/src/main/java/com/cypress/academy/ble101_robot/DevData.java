package com.cypress.academy.ble101_robot;

import java.io.Serializable;

public class DevData implements Serializable
{
    private int rssi;
    private String mac_address;
    private String name;
    private String dev_data;
    private int manuid;

    public DevData(int rssi, String mac_address, String name, String dev_data, int manuid) {
        this.rssi = rssi;
        this.mac_address = mac_address;
        this.name = name;
        this.dev_data = dev_data;
        this.manuid = manuid;
    }

    public int getRssi() {
        return rssi;
    }

    public String getMac_address() {
        return mac_address;
    }

    public String getName() {
        return name;
    }

    public String getDev_data() {
        return dev_data;
    }


    public int getManuid() {
        return manuid;
    }

    public void setManuid(int manuid) {
        this.manuid = manuid;
    }
}
