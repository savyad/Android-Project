package com.cypress.academy.ble101_robot;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;
import static com.cypress.academy.ble101_robot.Utils.makeGattUpdateIntentFilter;

public class DownloadCsv extends AppCompatActivity {


    public ArrayList filenames ;
    public ArrayAdapter<String> adapter;
    public ListView filelist;
    public Button dncb;
    public BluetoothLeService mBleService;
    public String mDeviceAddress,mDeviceName;
    public BluetoothGattCharacteristic characteristic;
    public JSONObject dnc;

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
        setContentView(R.layout.activity_download_csv);
        filelist =  findViewById(R.id.listcsv);
        dnc=new JSONObject();
        final Intent intent = getIntent();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection,BIND_AUTO_CREATE);
        mDeviceName = intent.getStringExtra(EXTRAS_BLE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);


        dncb=(Button)findViewById(R.id.dnc);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        listCsvFiles();
        filelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123/data.csv");
                if (file2.exists())
                {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    Uri uri =FileProvider.getUriForFile(DownloadCsv.this, "com.cypress.academy.ble101_robot.FileProvider", file2);
                    //Uri.fromFile(file2);


                    intent.setDataAndType(uri, "text/csv");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

                    try
                    {
                        startActivity(intent);
                    }
                    catch(ActivityNotFoundException e)
                    {
                        Toast.makeText(DownloadCsv.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        dncb.setOnClickListener(click);

    }
    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBleService != null) {
            final boolean result = mBleService.connect(mDeviceAddress);
            Log.d("aa", "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mBleService.buffer!=null)
        {
            mBleService.buffer = "";
        }
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBleService = null;

    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //mConnected = true;
                //mConnectionState.setText("Connected");
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
                //refresh.setVisibility(View.GONE);
                // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                // displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //Log.d("tag",intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

                readCsv(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
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
                    if (mBleService != null)
                    {
                        mBleService.setCharacteristicNotification(gattCharacteristic, true);
                        //mread.setText("data");
                        mBleService.readCharacteristic(gattCharacteristic);

                    }
                }

            }
        }
    }




    private  void listCsvFiles()
    {


         //filelist is ListView widget
        filenames = new ArrayList<>();
        getFiles();         //  files funtions
        adapter=new ArrayAdapter<>(DownloadCsv.this, android.R.layout.simple_list_item_1, filenames);
        filelist.setAdapter(adapter);

    }

    public void sendDumreq() throws Exception
    {

        dnc.put("type","dnc");
        if (mBleService != null)
        {

            sendbyMTUlimit(dnc.toString(),characteristic);

        }
    }

    public void sendbyMTUlimit(String data,BluetoothGattCharacteristic characteristic) throws Exception
    {
        //int Length = data.length();
        if(data.length()>244)
        {
            do
            {
                if(data.length()>243)
                {
                    //System.out.println(data.substring(0, Math.min(data.length(), 243)));
                    Log.d("243",data.substring(0,Math.min(data.length(),243)));
                    characteristic.setValue(data.substring(0,Math.min(data.length(),243)));
                    mBleService.writeCharacteristic(characteristic);
                    data=data.substring(243);
                    Thread.sleep(50);
                }
                else
                {
                    characteristic.setValue(data);
                    mBleService.writeCharacteristic(characteristic);
                    Log.d("last",data);
                    //System.out.println(data);
                    break;
                }

            }while(data.length()!=0);

        /* while(Length!=0)
        {
            String dataTosend1=data.substring(0, Math.min(data.length(), 243));
            int Length_dataTosend1=0;
            characteristic.setValue(data.substring(0, Math.min(data.length(), 243)));
            mBleService.writeCharacteristic(characteristic);
            data.substring(243);
            Length=data.length();
        }*/
        }
        else
        {
            characteristic.setValue(data);
            mBleService.writeCharacteristic(characteristic);

        }
    }

    public void readCsv(String data)
    {
       //data=data.replaceAll("\\r",System.lineSeparator());
        System.out.print(data);

    }

    private void  getFiles()
    {

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123");

        String filename;
        Queue<File> files = new LinkedList<>();         //Linklist

        files.addAll(Arrays.asList(path.listFiles()));  //adding all files of path in linklist

        while (!files.isEmpty())
        {
            File file = files.remove();
            if (file.isDirectory())
            {
                files.addAll(Arrays.asList(file.listFiles()));


            }
            else if (file.getName().endsWith(".csv") || file.getName().endsWith(".xls") || file.getName().endsWith(".ppt"))
            {   //filtering files
                filename = file.getName();               // according to their extension
                filenames.add(filename);          //filenames is string ListArray

            }


        }
    }
    private View.OnClickListener click=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.dnc:

                    if(mBleService!=null)
                    {
                       try{
                           dnc.put("type","dnc");
                           sendbyMTUlimit(dnc.toString(),characteristic);
                       } catch(Exception e)
                       {

                           e.printStackTrace();

                       }

                    }
                    break;

                default:
                    break;

            }
        }
    };
}
