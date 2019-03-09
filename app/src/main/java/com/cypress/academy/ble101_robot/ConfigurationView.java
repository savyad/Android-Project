package com.cypress.academy.ble101_robot;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;

import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;
import static com.cypress.academy.ble101_robot.Utils.makeGattUpdateIntentFilter;

public class ConfigurationView extends AppCompatActivity {

    public BluetoothLeService mBleService;
    public BluetoothGattCharacteristic characteristic;

    public String mDeviceAddress,mDeviceName;
    public Button devinfo,devset,downcsv,devcali,devgrpah;
    private boolean mConnected = true;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBleService.initialize()) {
                Log.e("TAg", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBleService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_view);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        final Intent intent = getIntent();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mDeviceName = intent.getStringExtra(EXTRAS_BLE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);

        devinfo=(Button)findViewById(R.id.dev_info);
        devset=(Button)findViewById(R.id.sen_setup);
        downcsv=(Button)findViewById(R.id.down);
        devcali=(Button)findViewById(R.id.sen_calibra);
        devgrpah=(Button)findViewById(R.id.graph);

        devinfo.setOnClickListener(click);
        devset.setOnClickListener(click);
        downcsv.setOnClickListener(click);
        devcali.setOnClickListener(click);
        devgrpah.setOnClickListener(click);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBleService != null) {
            final boolean result = mBleService.connect(mDeviceAddress);
            Log.d("aa", "Connect request result=" + result);
        }
        else if(mBleService == null && !mConnected)
        {
            finish();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBleService.buffer="";
        mBleService = null;
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                //mConnectionState.setText("Connected");
                //updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //mConnectionState.setText("DisConnected");
                finish();
                //updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                //clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //getGattServices(mBleService.getSupportedGattServices());
                //displayGattServices(mBleService.getSupportedGattServices());
                //mConnectionState.setText(String.valueOf(mWriteCharacteristic.size()));
                gattservice(mBleService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //refresh.setVisibility(View.GONE);
               // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
               // displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //Log.d("tag",intent.getStringExtra(BluetoothLeService.EXTRA_DATA));


                //mread.setText("sasassaas");

            }
        }
    };

    public void gattservice (List<BluetoothGattService> gattServices)
    {

        if(gattServices==null)
        {


            //continue;
        }


        for (BluetoothGattService gattService : gattServices)
        {
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            Log.d("service",gattService.getUuid().toString());
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {

                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                final int charaProp = gattCharacteristic.getProperties();

                if (((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                        (charaProp & BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)) > 0)
                //if(gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE)//BleUuid.write_service.equalsIgnoreCase(gattCharacteristic.getUuid().toString()))
                {
                    //mwrite.setVisibility(View.VISIBLE);
                    //sendCommand.setVisibility(View.VISIBLE);
                    //mwrite.setText("Write Service");
                    characteristic=gattCharacteristic;
                }

                else if (Utils.hasNotifyProperty(gattCharacteristic.getProperties()) != 0) {
                    if (mBleService != null) {
                        mBleService.setCharacteristicNotification(gattCharacteristic, true);
                        //mread.setText("data");
                        mBleService.readCharacteristic(gattCharacteristic);

                    }
                }

            }
        }
    }


    private View.OnClickListener click=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.dev_info:
                    if(mBleService!=null)
                    {
                        final Intent intent = new Intent(ConfigurationView.this, ControlActivity.class);

                        intent.putExtra(EXTRAS_BLE_ADDRESS,mDeviceAddress);
                        intent.putExtra(EXTRAS_BLE_NAME,mDeviceName);
                        //mBluetoothDevice.get(position).createBond();
                        // Stop scanning
                        startActivity(intent);
                    }
                    break;
                case R.id.sen_setup:
                    if(mBleService!=null)
                    {
                        final Intent intent = new Intent(ConfigurationView.this, SensorSetup.class);

                        intent.putExtra(EXTRAS_BLE_ADDRESS,mDeviceAddress);
                        intent.putExtra(EXTRAS_BLE_NAME,mDeviceName);
                        //mBluetoothDevice.get(position).createBond();
                        // Stop scanning
                        startActivity(intent);
                    }
                    break;
                case R.id.down:
                    if(mBleService!=null)
                    {
                        final Intent intent = new Intent(ConfigurationView.this, DownloadCsv.class);

                        intent.putExtra(EXTRAS_BLE_ADDRESS,mDeviceAddress);
                        intent.putExtra(EXTRAS_BLE_NAME,mDeviceName);
                        //mBluetoothDevice.get(position).createBond();
                        // Stop scanning
                        startActivity(intent);
                    }
                    break;
                case R.id.sen_calibra:
                    if(mBleService!=null)
                    {
                        final Intent intent = new Intent(ConfigurationView.this, SensorCalibrate.class);

                        intent.putExtra(EXTRAS_BLE_ADDRESS,mDeviceAddress);
                        intent.putExtra(EXTRAS_BLE_NAME,mDeviceName);
                        //mBluetoothDevice.get(position).createBond();
                        // Stop scanning
                        startActivity(intent);
                    }
                    break;
                case R.id.graph:
                    if(mBleService!=null)
                    {
                        final Intent intent = new Intent(ConfigurationView.this, DeviceSettings.class);

                        intent.putExtra(EXTRAS_BLE_ADDRESS,mDeviceAddress);
                        intent.putExtra(EXTRAS_BLE_NAME,mDeviceName);
                        //mBluetoothDevice.get(position).createBond();
                        // Stop scanning
                        startActivity(intent);
                    }
                    break;
                    default:
                        break;

            }
        }
    };

}
