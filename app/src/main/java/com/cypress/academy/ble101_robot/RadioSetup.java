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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;
import static com.cypress.academy.ble101_robot.Utils.makeGattUpdateIntentFilter;

public class RadioSetup extends AppCompatActivity {

    public Switch ad_sleep;
    public final MyData unit[] = new MyData[11];
    public final MyData txchannel_array[] = new MyData[10];
    public final MyData trpower_array[] = new MyData[3];



    public Spinner unitms,tx_ch,transpower;
    public ArrayAdapter<MyData> unit_spinnerArrayAdapter,txch_spinnerArrayAdapter,tpower_spinnerArrayAdapter;
    public BluetoothGattCharacteristic characteristic;
    public BluetoothLeService mBleService;
    public String mDeviceAddress,mDeviceName;
    public Button save,cancel;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBleService.initialize()) {
                Log.e("TAg", "Unable to initialize Bluetooth");
                finish();
            }
            mBleService.connect(mDeviceAddress);
            gattservice(mBleService.getSupportedGattServices());
            // Automatically connects to the device upon successful start-up initialization.
            //mBleService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_setup);
        setTitle("Radio Setup");

        final Intent intent = getIntent();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mDeviceName = intent.getStringExtra(EXTRAS_BLE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);

        save=(Button)findViewById(R.id.save);
        cancel = (Button)findViewById(R.id.cancel);

        ad_sleep = (Switch) findViewById(R.id.ad_sleep);
        unitms = (Spinner)findViewById(R.id.unit);

        tx_ch = (Spinner)findViewById(R.id.tx_ch);
        transpower = (Spinner)findViewById(R.id.trans_power);

        unit[0] = new MyData("100 ms", "100");
        unit[1] = new MyData("200 ms", "200");
        unit[2] = new MyData("300 ms", "300");
        unit[3] = new MyData("500 ms", "500");
        unit[4] = new MyData("1 s", "1000");
        unit[5] = new MyData("2 s", "2000");
        unit[6] = new MyData("3 s", "3000");
        unit[7] = new MyData("5 s", "5000");
        unit[8] = new MyData("7 s", "7000");
        unit[9] = new MyData("9 s", "9000");
        unit[10] = new MyData("10 s", "10000");

        unit_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                RadioSetup.this, R.layout.spinner_log_trigger, unit);
        unit_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        unitms.setAdapter(unit_spinnerArrayAdapter);

        txchannel_array[0] = new MyData("T1","0");
        txchannel_array[1] = new MyData("T2","1");
        txchannel_array[2] = new MyData("T3","2");
        txchannel_array[3] = new MyData("T4","3");
        txchannel_array[4] = new MyData("T5","4");
        txchannel_array[5] = new MyData("T6","5");
        txchannel_array[6] = new MyData("T7","6");
        txchannel_array[7] = new MyData("T8","7");
        txchannel_array[8] = new MyData("T9","8");
        txchannel_array[9] = new MyData("T10","9");

        txch_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                RadioSetup.this, R.layout.spinner_log_trigger, txchannel_array);
        txch_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        tx_ch.setAdapter(txch_spinnerArrayAdapter);

        trpower_array[0] = new MyData("Low","0");
        trpower_array[1] = new MyData("Mid","4");
        trpower_array[2] = new MyData("High","8");

        tpower_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                RadioSetup.this, R.layout.spinner_log_trigger, trpower_array);
        tpower_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        transpower.setAdapter(tpower_spinnerArrayAdapter);

        save.setOnClickListener(click);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //mConnected = true;
                //show.setText("Connected");
                //updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //mConnected = false;
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
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //show.setText("sasassaas");

            }
        }
    };

    private void displayData(String data)
    {
        if(data!=null && data.contains("rad_b") )
        {
            try {
                    JSONObject obj = new JSONObject(data);
                    int tx = obj.getInt("tx_ch");
                    int tr = obj.getInt("tr_pwr");
                    boolean adslp = obj.getBoolean("ad_slp");
                    int adint = obj.getInt("ad_int");

                    tx_ch.setSelection(getIndex(tx_ch,txchannel_array,String.valueOf(tx)));
                    transpower.setSelection(getIndex(transpower,trpower_array,String.valueOf(tr)));
                    ad_sleep.setChecked(adslp);
                    unitms.setSelection(getIndex(unitms,unit,String.valueOf(adint)));



            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
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
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBleService.buffer="";
        mBleService = null;
    }

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
    private View.OnClickListener click =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                switch(view.getId())
                {
                    case R.id.save:
                        try {
                            sendRadioSetupData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.cancel:

                        break;
                }
        }
    };

    private void sendRadioSetupData() throws Exception
    {
        JSONObject obj_tosend = new JSONObject();
        obj_tosend.put("type","rad_stp");
        obj_tosend.put("tx_ch", Integer.parseInt(txchannel_array[tx_ch.getSelectedItemPosition()].getValue()));
        obj_tosend.put("tr_pwr",Integer.parseInt(trpower_array[transpower.getSelectedItemPosition()].getValue()));
        obj_tosend.put("ad_slp", ad_sleep.isChecked());
        obj_tosend.put("ad_int", Integer.parseInt(unit[unitms.getSelectedItemPosition()].getValue()));
        obj_tosend.put("dataf",0);
        obj_tosend.put("EndData","endata");

        if(mBleService!=null)
        {
            sendbyMTUlimit(obj_tosend.toString(), characteristic);
            System.out.println(obj_tosend.toString());
        }
    }
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
    private int getIndex(Spinner spinner, MyData dummy[], String myString) {

        for (int i = 0; i < dummy.length; i++) {
            MyData d = dummy[i];
            if (d.getValue().equals(myString)) {
                return i;
            }
        }


        return -1;
    }

}
