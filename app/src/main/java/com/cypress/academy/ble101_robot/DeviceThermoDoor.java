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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cypress.academy.ble101_robot.DeviceAdapter.SINGLE_DEV_DATA;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;

public class DeviceThermoDoor extends AppCompatActivity {

    public DevData data;
    String dev_data;
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeScanner mLEScanner;
    private static boolean mScanning;
    private static Handler mHandler;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final long SCAN_TIMEOUT = 15000;
    public Button ref,conf,calib_ch1,ref3,refs;
    public double temperature,battery;
    public int doorval;
    public TextView temp,batt;
    public Boolean valid_val=true;
    public Boolean cali_show=false;

    private boolean mconfig = false;
    private boolean mcalibrate = false;

    public View view,view2,view3;
    public LayoutInflater layoutInflater;
    public AlertDialog alertDialog,alertDialog2,alertDialog3,alertDialogconf,alertdialogcalibrate;
    //Spinner log_trigger;
    public String a,b;
    public EditText ch1_low,ch1_high;
    public TextView set_low,low_t,set_low3,low_t3;
    public double real_low=0;
    public double real_high=0;
    public double offset=0;
    public double gain=0;
    ImageView doorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_flow);
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

        ref = (Button)findViewById(R.id.ref);
        conf = (Button)findViewById(R.id.conf);

        final Intent intent = getIntent();
        data = (DevData) intent.getSerializableExtra(SINGLE_DEV_DATA);
        dev_data = data.getDev_data();
        mHandler = new Handler();
        layoutInflater = LayoutInflater.from(DeviceThermoDoor.this);
        doorImage=findViewById(R.id.doorimage);


        setTitle(data.getMac_address());
        ref.setOnClickListener(click);
        conf.setOnClickListener(click);
        temp = (TextView)findViewById(R.id.temps);
        batt = (TextView)findViewById(R.id.batts);
        //Log.d("data Received Mac",data.getMac_address());

        view = layoutInflater.inflate(R.layout.press_button_dialog, null);
        alertDialogconf = new AlertDialog.Builder(this).create();

        layoutInflater = LayoutInflater.from(DeviceThermoDoor.this);

        view = layoutInflater.inflate(R.layout.dialog_1_layout, null);

        view2 = layoutInflater.inflate(R.layout.dailog_2_layout,null);
        view3 = layoutInflater.inflate(R.layout.dialog_3_layout,null);

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog2 = new AlertDialog.Builder(this).create();//AlertDialog to set Low
        alertDialog3 = new AlertDialog.Builder(this).create();
        alertdialogcalibrate = new AlertDialog.Builder(this).create();


        ch1_low = (EditText)view.findViewById(R.id.low_val);
        ch1_high = (EditText)view.findViewById(R.id.high_val);

        populate_devData(dev_data);
        calib_ch1=(Button)findViewById(R.id.cali);
        calib_ch1.setOnClickListener(click);

        set_low=(TextView)view2.findViewById(R.id.set_low);
        refs=(Button)view2.findViewById(R.id.ref);
        low_t=(TextView)view2.findViewById(R.id.low_text);


        set_low3=(TextView)view3.findViewById(R.id.set_low);
        ref3=(Button)view3.findViewById(R.id.ref);
        low_t3=(TextView)view3.findViewById(R.id.low_text);
        refs.setOnClickListener(click);
        ref3.setOnClickListener(click);
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
        doorval =  Integer.parseInt(data.substring(14,18),16);
        if(temperature>60000)
        {
            temperature = temperature-65536;
        }
        //Log.d("door val",String.valueOf(doorval));

        temp.setText(String.valueOf(temperature)+"\u00B0"+"c");
        batt.setText(String.valueOf(battery)+"v");
        if(alertDialog2.isShowing()) {
            low_t.setText(String.valueOf(temperature));
        }
        else if(alertDialog3.isShowing())
        {
            low_t3.setText(String.valueOf(temperature));
        }
        switch(doorval)
        {
            case 0:
                doorImage.setImageResource(R.drawable.dclose);
                break;
            case 1:
                doorImage.setImageResource(R.drawable.dopen);
                break;
            default:
                break;
        }

    }

    public static String SbytesToHex(SparseArray<byte[]> bytes)
    {
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
                //set_data();

            }
            else if(mconfig)
            {
                alertDialog.dismiss();
                final Intent intent = new Intent(DeviceThermoDoor.this, ConfigurationView.class);
                intent.putExtra(EXTRAS_BLE_ADDRESS, data.getMac_address());
                intent.putExtra(EXTRAS_BLE_NAME, data.getName());
                intent.putExtra(SINGLE_DEV_DATA,data.getDev_data());
                scanLeDevice(false,data.getMac_address());
                mconfig=false;
                startActivity(intent);
            }
            else if(mcalibrate)
            {
                alertdialogcalibrate.dismiss();
                final Intent intent = new Intent(DeviceThermoDoor.this, SensorCalibrate.class);
                intent.putExtra(EXTRAS_BLE_ADDRESS, data.getMac_address());
                intent.putExtra(EXTRAS_BLE_NAME, data.getName());
                intent.putExtra(SINGLE_DEV_DATA,data.getDev_data());
                intent.putExtra("Offset",String.valueOf(offset));
                intent.putExtra("Gain",String.valueOf(gain));
                scanLeDevice(false,data.getMac_address());
                mcalibrate=false;
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
                case R.id.cali:
                    cali_show=false;
                    mcalibrate=true;
                    show_dialog11();
                    default:
                    break;

            }
        }
    };


    //Dialog Box
    private void show_dialog1()
    {

        alertDialogconf.setTitle("ReadMe");
        alertDialogconf.setCancelable(false);
        alertDialogconf.setMessage("Press the Config button on the device for 5 seconds until the LED turns on.");


            /*alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {



                }
            });*/


        alertDialogconf.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Log.d("Low Value",a);
                //Log.d("High Value",b);
                mconfig=false;
                alertDialogconf.dismiss();
            }
        });


        //alertDialog.setView(view);
        alertDialogconf.show();
        //alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(false);
        scanLeDevice(false,data.getMac_address());
        scanLeDevice(true,data.getMac_address());

    }

    private void show_dialog11()
    {
        alertDialog.setTitle("Set Range");
        alertDialog.setCancelable(false);
        //alertDialog.setMessage("Your Message Here");


        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                a= ch1_low.getText().toString();
                b= ch1_high.getText().toString();
                //alertDialog.dismiss();
                ch1_low.setText("");
                ch1_high.setText("");

                show_dialog2();
                /*Log.d("Low Value",a);
                Log.d("High Value",b);
                */

            }
        });


        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Log.d("Low Value",a);
                //Log.d("High Value",b);
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.show();
       // alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(false);


    }

    private void show_dialog2()
    {

        alertDialog2.setTitle("Range : "+a+" ~ "+b);
        alertDialog2.setCancelable(false);
        //alertDialog.setMessage("Your Message Here");
        set_low.setText("Set the Sensor at "+a );

        alertDialog2.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Set Low", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                try
                {
                    Double num= Double.parseDouble(low_t.getText().toString());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    valid_val=false;
                }
                if(valid_val) {
                    real_low = Double.valueOf(low_t.getText().toString());
                    offset = Double.valueOf(a) - real_low;
                    show_dialog3();
                }
                else
                {
                    Toast.makeText(DeviceThermoDoor.this, "There's Some Problem with the Current Sensor Reading",
                            Toast.LENGTH_LONG).show();
                }
                /*Log.d("Low Value",a);
                Log.d("High Value",b);
                */

            }
        });


        alertDialog2.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Log.d("Low Value",a);
                //Log.d("High Value",b);
                alertDialog2.dismiss();
            }
        });

        alertDialog2.setView(view2);
        alertDialog2.show();
        //alertDialog2.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(false);

    }

    private void show_dialog3()
    {

        alertDialog3.setTitle("Range : "+a+" ~ "+b);
        alertDialog3.setCancelable(false);
        //alertDialog.setMessage("Your Message Here");
        set_low3.setText("Set the Sensor at "+b );

        alertDialog3.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Set High", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                try
                {
                    Double num= Double.parseDouble(low_t3.getText().toString());
                }
                catch(Exception e)
                {
                    valid_val=false;
                }
                if(valid_val) {
                    real_high = (double) Double.valueOf(low_t3.getText().toString());
                    gain = Math.round((((Double.valueOf(b)) - (Double.valueOf(a))) / (real_high - real_low))); //* 10000.0) / 10000.0;
                    if (!cali_show) {
                        Log.d("real_low",String.valueOf(real_low));
                        Log.d("real_high",String.valueOf(real_high));

                        Log.d("offset",String.valueOf(offset));
                        Log.d("gain",String.valueOf(gain));
                        showdialogtosavecalibrate();
                        // off_ch1.setText(String.valueOf(offset));
                        //gai_ch1.setText(String.valueOf(gain));
                    } else {
                        //off_ch2.setText(String.valueOf(offset));
                        //gai_ch2.setText(String.valueOf(gain));
                    }


                    alertDialog3.dismiss();
                }
                else
                {
                    Toast.makeText(DeviceThermoDoor.this, "There's Some Problem with the Current Sensor Reading",
                            Toast.LENGTH_LONG).show();
                }
                /*Log.d("Low Value",a);
                Log.d("High Value",b);
                */


            }
        });


        alertDialog3.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Log.d("Low Value",a);
                //Log.d("High Value",b);
                alertDialog3.dismiss();
            }
        });

        alertDialog3.setView(view3);
        alertDialog3.show();
        //alertDialog2.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void showdialogtosavecalibrate()
    {

        alertdialogcalibrate.setTitle("ReadMe");
        alertdialogcalibrate.setCancelable(false);
        alertdialogcalibrate.setMessage("Press the config button to save the calibration settings to the device");


            /*alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {



                }
            });*/


        alertdialogcalibrate.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Log.d("Low Value",a);
                //Log.d("High Value",b);
                mcalibrate=false;
                alertdialogcalibrate.dismiss();
            }
        });


        //alertDialog.setView(view);
        alertdialogcalibrate.show();
        //alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(false);
        scanLeDevice(false,data.getMac_address());
        scanLeDevice(true,data.getMac_address());
    }
}
