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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cypress.academy.ble101_robot.DeviceAdapter.SINGLE_DEV_DATA;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;

public class RepeaterAdvertise extends AppCompatActivity {
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeScanner mLEScanner;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static boolean mScanning;
    private static Handler mHandler;
    private static final long SCAN_TIMEOUT = 15000;
    public DevData data;

    RecyclerView recyclerView;
    ReapeaterDeviceAdapter reapeaterDeviceAdapter;
    private ArrayList<RepeaterModel> modelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeater_advertise);
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
        recyclerView = (RecyclerView) findViewById(R.id.devdata);
        //dummyData();
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reapeaterDeviceAdapter = new ReapeaterDeviceAdapter(modelArrayList);

        recyclerView.setAdapter(reapeaterDeviceAdapter);


        mHandler = new Handler();
        final Intent intent = getIntent();
        data = (DevData) intent.getSerializableExtra(SINGLE_DEV_DATA);
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
                //populate_devData(SbytesToHex(dataw));
                String data = SbytesToHex(dataw);
                ArrayList<RepeaterModel> repeatermodels = new ArrayList<>();

               /* for (RepeaterModel model : modelArrayList) {
                    repeatermodels.add(model.clone());
                }*/
                int rssi = Integer.valueOf(data.substring(12,14),16)-256;
                repeatermodels.add(new RepeaterModel(data.substring(0,12),String.valueOf(rssi),0));
                System.out.println(rssi +" "+ data.substring(0,12));
                //reapeaterDeviceAdapter= new ReapeaterDeviceAdapter(repeatermodels);
                reapeaterDeviceAdapter.setData(repeatermodels);
                //recyclerView.setAdapter(reapeaterDeviceAdapter);

            }
            else if(false)
            {
                //alertDialog.dismiss();
                final Intent intent = new Intent(RepeaterAdvertise.this, ConfigurationView.class);
                intent.putExtra(EXTRAS_BLE_ADDRESS, data.getMac_address());
                intent.putExtra(EXTRAS_BLE_NAME, data.getName());
                intent.putExtra(SINGLE_DEV_DATA,data.getDev_data());
                scanLeDevice(false,data.getMac_address());
                //mconfig=false;
                startActivity(intent);
            }

        }
    };
    /*private void dummyData() {

        modelArrayList.add(new RepeaterModel("fffasdaaads",  8000,12));
        modelArrayList.add(new RepeaterModel( "asdvvdvasv", 600,32));
        modelArrayList.add(new RepeaterModel("asdfevevewff", 250,33));
        modelArrayList.add(new RepeaterModel("asdasfasdfasd", 1000,11));
    }*/
}
