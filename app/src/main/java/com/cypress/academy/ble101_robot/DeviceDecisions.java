package com.cypress.academy.ble101_robot;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cypress.academy.ble101_robot.DeviceAdapter.SINGLE_DEV_DATA;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;

public class DeviceDecisions extends AppCompatActivity {

    public DevData data;
    String dev_data;
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeScanner mLEScanner;
    private static boolean mScanning;
    private static Handler mHandler;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final long SCAN_TIMEOUT = 15000;
    public Button ref,conf;
    public TextView temp,batt;
    public double temperature,battery;
    private boolean mconfig = false;
    public View view;//,view2,view3;
    public LayoutInflater layoutInflater;
    public AlertDialog alertDialog;
    public String model;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_decisions);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.no_ble, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth manager returned the adapter. If not, exit.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.no_ble, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access ");
                builder.setMessage("Please grant location access so this app can detect devices.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        final Intent intent = getIntent();
        data = (DevData) intent.getSerializableExtra(SINGLE_DEV_DATA);
        dev_data = data.getDev_data();
        //setTitle(data.getMac_address());

        mHandler = new Handler();
        layoutInflater = LayoutInflater.from(DeviceDecisions.this);



        ref = (Button)findViewById(R.id.ref);
        conf = (Button)findViewById(R.id.conf);
        temp = (TextView)findViewById(R.id.temps);
        batt = (TextView)findViewById(R.id.batts);




        ref.setOnClickListener(click);
        conf.setOnClickListener(click);
        //Log.d("data Received Mac",data.getMac_address());

        view = layoutInflater.inflate(R.layout.press_button_dialog, null);
        alertDialog = new AlertDialog.Builder(this).create();
        populate_devData(dev_data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Permission for 6.0:", "Coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("Since location access has not been granted, scanning will not work.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false,data.getMac_address());

    }
    @Override
    protected void onResume() {
        super.onResume();
        scanLeDevice(true,data.getMac_address());
    }

    private void scanLeDevice(final boolean enable,String mac) {
        if (enable) { // enable set to start scanning
            // Stops scanning after a pre-defined scan period.

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mScanning) {
                        mScanning = false;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            //noinspection deprecation
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        } else {
                            mLEScanner.stopScan(mScanCallback);
                        }
                        invalidateOptionsMenu();
                    }
                }
            }, SCAN_TIMEOUT);

            mScanning = true;
            UUID[] motorServiceArray = {PSoCBleRobotService.getMotorServiceUUID()};
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //noinspection deprecation
                mBluetoothAdapter.startLeScan(motorServiceArray, mLeScanCallback);
            } else { // New BLE scanning introduced in LOLLIPOP
                ScanSettings settings;
                List<ScanFilter> filters;
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<>();
                //ScanFilter filter = new ScanFilter.Builder().setServiceUuid(PUuid).build();
                //ScanFilter filter = new ScanFilter.Builder().setManufacturerData(89,new byte[] {}).build();
                ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(mac).build();
                filters.add(filter);
                if(mLEScanner==null)
                {
                    mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                }
                mLEScanner.startScan(filters, settings, mScanCallback);


            }
        } else { // enable set to stop scanning
            if(mScanning) {
                mScanning = false;
                if (Build.VERSION.SDK_INT < 21) {
                    //noinspection deprecation
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                } else {
                    mLEScanner.stopScan(mScanCallback);
                }
            }
        }
        invalidateOptionsMenu();
    }
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    byte[] haha = scanRecord;

                   /* if(!mBluetoothDevice.contains(device))
                    {
                        //only add new devices

                        mBluetoothDevice.add(device);
                        mBleName.add(device.getName());
                        mBleArrayAdapter.notifyDataSetChanged(); // Update the list on the screen
                    }*/

                }
            });
        }
    };


    private void populate_devData(String data)
    {
            battery = ((double)(Integer.parseInt(data.substring(6,8)+data.substring(4,6),16)))/100;

            temperature = ((double) (Integer.parseInt(data.substring(12,14)+data.substring(10,12),16)))/100;
            if(temperature>60000)
            {
                temperature = temperature-65536;
            }
            temp.setText(String.valueOf(temperature)+"\u00B0"+"c");
            batt.setText(String.valueOf(battery)+"v");
    }

    public static String SbytesToHex(SparseArray<byte[]> bytes) {
        StringBuilder builder = new StringBuilder();
        byte[] dd = bytes.valueAt(0);

        for (byte b: dd)
        {
            builder.append(String.format("%02x", b));
        }
        //System.out.println( dd.length);
        return builder.toString();

    }

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {

                    ScanRecord scanRecord = result.getScanRecord();

                    SparseArray<byte[]> dataw = scanRecord.getManufacturerSpecificData();
                    if(dataw.size()>0) {
                        populate_devData(SbytesToHex(dataw));
                    }
                    else if(mconfig)
                    {
                        alertDialog.dismiss();
                        final Intent intent = new Intent(DeviceDecisions.this, ConfigurationView.class);
                        intent.putExtra(EXTRAS_BLE_ADDRESS, data.getMac_address());
                        intent.putExtra(EXTRAS_BLE_NAME, data.getName());
                        intent.putExtra(SINGLE_DEV_DATA,data.getDev_data());
                        scanLeDevice(false,data.getMac_address());
                        mconfig=false;
                        startActivity(intent);
                    }

        }
    };

        private View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()) {

                    case R.id.ref:
                        scanLeDevice(true,data.getMac_address());
                        break;
                    case R.id.conf:
                        mconfig=true;
                        show_dialog1();
                        //scanLeDevice(false,data.getMac_address());
                        //scanLeDevice(true,data.getMac_address());
                        break;
                    default:
                        break;

                }
            }
        };


        //Dialog Box
        private void show_dialog1()
        {

            alertDialog.setTitle("ReadMe");
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Press the Config button on the device for 5 seconds until the LED flashes.");


            /*alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {



                }
            });*/


            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    //Log.d("Low Value",a);
                    //Log.d("High Value",b);
                    mconfig=false;
                    alertDialog.dismiss();
                }
            });


            //alertDialog.setView(view);
            alertDialog.show();
            //alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(false);
            scanLeDevice(false,data.getMac_address());
            scanLeDevice(true,data.getMac_address());

        }
}
