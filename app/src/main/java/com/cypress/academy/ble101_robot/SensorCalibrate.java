package com.cypress.academy.ble101_robot;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;
import static com.cypress.academy.ble101_robot.Utils.makeGattUpdateIntentFilter;

public class SensorCalibrate extends AppCompatActivity {

    public BluetoothLeService mBleService;
    public BluetoothGattCharacteristic characteristic;

    public Button cali_ch1,cali_ch2,calib_ch1,calib_ch2;
    public LayoutInflater layoutInflater;
     public AlertDialog alertDialog,alertDialog2,alertDialog3;
     public String a,b;

     public EditText off_ch1,gai_ch1,off_ch2,gai_ch2;

     public double real_low=0;
    public double real_high=0;
    public double offset=0;
    public double gain=0;

    public  EditText ch1_low,ch1_high;
    public TextView set_low,low_t,set_low3,low_t3;
    public Button ref,ref3;
    public View view,view2,view3;
    public String mDeviceAddress,mDeviceName;
    public JSONObject refs;
    public Boolean cali_show=false;

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
        setContentView(R.layout.activity_sensor_calibrate);

        final Intent intent = getIntent();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection,BIND_AUTO_CREATE);
        mDeviceName = intent.getStringExtra(EXTRAS_BLE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);


        refs=new JSONObject();
        layoutInflater = LayoutInflater.from(SensorCalibrate.this);

        view = layoutInflater.inflate(R.layout.dialog_1_layout, null);

        view2 = layoutInflater.inflate(R.layout.dailog_2_layout,null);
        view3 = layoutInflater.inflate(R.layout.dialog_3_layout,null);

       alertDialog = new AlertDialog.Builder(this).create();
       alertDialog2 = new AlertDialog.Builder(this).create();//AlertDialog to set Low
        alertDialog3 = new AlertDialog.Builder(this).create();

        cali_ch1=(Button) findViewById(R.id.set_cali_range_ch1);
            cali_ch2=(Button) findViewById(R.id.set_cali_range_ch2);

         off_ch1 = (EditText)findViewById(R.id.off_ch1);
        gai_ch1 = (EditText)findViewById(R.id.gain_ch1);

        off_ch2 = (EditText)findViewById(R.id.off_ch2);
        gai_ch2 = (EditText)findViewById(R.id.gain_ch2);


        ch1_low = (EditText)view.findViewById(R.id.low_val);
        ch1_high = (EditText)view.findViewById(R.id.high_val);


        set_low=(TextView)view2.findViewById(R.id.set_low);
        ref=(Button)view2.findViewById(R.id.ref);
        low_t=(TextView)view2.findViewById(R.id.low_text);


        set_low3=(TextView)view3.findViewById(R.id.set_low);
        ref3=(Button)view3.findViewById(R.id.ref);
        low_t3=(TextView)view3.findViewById(R.id.low_text);


        calib_ch1=(Button)findViewById(R.id.calibrate_ch1);
        calib_ch2=(Button)findViewById(R.id.calibrate_ch2);

        cali_ch1.setOnClickListener(click);
        cali_ch2.setOnClickListener(click);
        ref.setOnClickListener(click);
        ref3.setOnClickListener(click);

        calib_ch1.setOnClickListener(click);
        calib_ch2.setOnClickListener(click);

        ch1_low.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*if(charSequence.length()!=0)
                {
                 alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(true);
            }*/
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()==0)
                {
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            else
                {
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(true);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });

        ch1_high.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

                if(charSequence.toString().trim().length()==0)
                {
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
                else
                {
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(true);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
        if(mBleService.buffer!=null)
        {
            mBleService.buffer = "";
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
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
                set_data(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //mread.setText("sasassaas");

            }
        }
    };


    private void show_dialog1()
    {
        alertDialog.setTitle("Set Range");
        alertDialog.setCancelable(false);
        //alertDialog.setMessage("Your Message Here");


        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Next", new DialogInterface.OnClickListener() {
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


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
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
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setEnabled(false);


    }

    private void show_dialog2()
    {

        alertDialog2.setTitle("Range : "+a+" ~ "+b);
        alertDialog2.setCancelable(false);
        //alertDialog.setMessage("Your Message Here");
        set_low.setText("Set the Sensor at "+a );

        alertDialog2.setButton(AlertDialog.BUTTON_POSITIVE, "Set Low", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {


                real_low = Double.valueOf(low_t.getText().toString());
                offset = Double.valueOf(a) - real_low;
                show_dialog3();
                /*Log.d("Low Value",a);
                Log.d("High Value",b);
                */

            }
        });


        alertDialog2.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
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

        alertDialog3.setButton(AlertDialog.BUTTON_POSITIVE, "Set High", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {


                real_high = (double)Double.valueOf(low_t3.getText().toString());
                gain = Math.round(((    (Double.valueOf(b)) -(Double.valueOf(a))  )/(real_high-real_low))*10000.0)/10000.0;
                if(!cali_show)
                {
                    off_ch1.setText(String.valueOf(offset));
                    gai_ch1.setText(String.valueOf(gain));
                }
                else
                {
                    off_ch2.setText(String.valueOf(offset));
                    gai_ch2.setText(String.valueOf(gain));
                }


                alertDialog3.dismiss();

                /*Log.d("Low Value",a);
                Log.d("High Value",b);
                */


            }
        });


        alertDialog3.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
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

    public void set_data(String data)
    {
        Log.d("data",data);
        if(data.contains("sen") && data.contains("end"))
        {
            double ch1=0;
            if(alertDialog3.isShowing())
            {
                try
                {
                    JSONObject obj = new JSONObject(data);
                    if(!cali_show)
                    {
                        ch1 = obj.getDouble("ch1");
                    }
                    else
                    {
                        ch1 = obj.getDouble("ch2");
                    }


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                low_t3.setText(String.valueOf(ch1));
                mBleService.buffer="";
            }
            else if(alertDialog2.isShowing())
            {
                try
                {
                    JSONObject obj = new JSONObject(data);
                    if(!cali_show)
                    {
                        ch1 = obj.getDouble("ch1");
                    }
                    else
                    {
                        ch1 = obj.getDouble("ch2");
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                low_t.setText(String.valueOf(ch1));
                mBleService.buffer="";
            }

        }
    }

    private void get_sensorData() throws Exception
    {
        if(mBleService!=null)
        {
            refs.put("type","sens");
            sendbyMTUlimit(refs.toString(),characteristic);
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
    private View.OnClickListener click=new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            switch(view.getId())
            {
                case R.id.set_cali_range_ch1:
                    cali_show=false;
                    show_dialog1();
                    break;
                case R.id.set_cali_range_ch2:
                    cali_show=true;
                    show_dialog1();
                    break;
                case R.id.calibrate_ch1:
                    if( isEmpty(off_ch1) && isEmpty(gai_ch1))//(off_ch1.getText().toString()==null) && (gai_ch1.getText().toString()==null))
                    {
                        Toast.makeText(SensorCalibrate.this, "Set Calibration Setting First!",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                    {

                    }
                    break;
                case R.id.calibrate_ch2:
                    if( isEmpty(off_ch2) && isEmpty(gai_ch2))//(off_ch2.getText().toString()==null) && (gai_ch2.getText().toString()==null))
                    {
                        Toast.makeText(SensorCalibrate.this, "Set Calibration Setting First!",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.ref:
                    try
                    {
                        get_sensorData();

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    };


    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }


}
