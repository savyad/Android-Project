package com.cypress.academy.ble101_robot;
/** BLE UUID Strings */
public class BleUuid {
    // 180A Device Information
    public static final String SERVICE_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_SERIAL_NUMBEAR_STRING = "00002a25-0000-1000-8000-00805f9b34fb";

    // 1802 Immediate Alert
    public static final String SERVICE_IMMEDIATE_ALERT = "00001802-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_ALERT_LEVEL = "00002a06-0000-1000-8000-00805f9b34fb";

    // 180F Battery Service
    public static final String SERVICE_BATTERY_SERVICE = "0000180F-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";


    public static final String gatt_service ="6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String write_service = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String read_service = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

}