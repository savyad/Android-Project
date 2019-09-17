package com.cypress.academy.ble101_robot;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
//import androidx.core.content.LocalBroadcastManager;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;
import static com.cypress.academy.ble101_robot.Utils.makeGattUpdateIntentFilter;

public class ConfigureGateway extends AppCompatActivity {


    public BluetoothLeService mBleService;
    public BluetoothGattCharacteristic characteristic;

    public String mDeviceAddress,mDeviceName;
    private boolean mConnected = true;
    public Button save;
    public EditText ssid,pass;
    public String sid,passkey;
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
            gattservice(mBleService.getSupportedGattServices());

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_gateway);


        final Intent intent = getIntent();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mDeviceName = intent.getStringExtra(EXTRAS_BLE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);

        ssid=(EditText)findViewById(R.id.ssid);
        pass=(EditText)findViewById(R.id.pass);
        save=(Button)findViewById(R.id.save);
        save.setOnClickListener(click);
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

    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
        //mBleService.buffer="";
        //mBleService.disconnect();
        //finish();
    }
    @Override
    protected void onDestroy()
    {
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
                case R.id.save:
                    if(mBleService!=null)
                    {
                        sid = ssid.getText().toString();
                        passkey = pass.getText().toString();
                        if(!sid.isEmpty()  && !passkey.isEmpty())
                        {
                            try {
                                JSONObject obj = new JSONObject();
                                obj.put("type","gat_set");
                                obj.put("ssid",sid);
                                obj.put("pass",passkey);

                                Log.d("check",obj.toString());
                                sendbyMTUlimit(obj.toString(),characteristic);
                                Toast.makeText(ConfigureGateway.this,"Wifi settings Sent Successfully",Toast.LENGTH_SHORT).show();

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Toast.makeText(ConfigureGateway.this,"Please Enter SSID and Password to Continue",Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
                    default:
                        break;
            }
        }
    };
    public void sendbyMTUlimit(String data, BluetoothGattCharacteristic characteristic) throws Exception {
        //int Length = data.length();
        if (data.length() > 244) {
            do {
                if (data.length() > 243) {
                    //System.out.println(data.substring(0, Math.min(data.length(), 243)));
                    Log.d("243", data.substring(0, Math.min(data.length(), 243)));
                    characteristic.setValue(data.substring(0, Math.min(data.length(), 243)));
                    mBleService.writeCharacteristic(characteristic);
                    data = data.substring(243);
                    Thread.sleep(50);
                } else {
                    characteristic.setValue(data);
                    mBleService.writeCharacteristic(characteristic);
                    Log.d("last", data);
                    //System.out.println(data);
                    break;
                }

            } while (data.length() != 0);

        /* while(Length!=0)
        {
            String dataTosend1=data.substring(0, Math.min(data.length(), 243));
            int Length_dataTosend1=0;
            characteristic.setValue(data.substring(0, Math.min(data.length(), 243)));
            mBleService.writeCharacteristic(characteristic);
            data.substring(243);
            Length=data.length();
        }*/
        } else {
            characteristic.setValue(data);
            mBleService.writeCharacteristic(characteristic);

        }
    }
}
