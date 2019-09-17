package com.cypress.academy.ble101_robot;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

/*import androidx.core.app.Fragment;
import androidx.core.app.FragmentManager;
import androidx.core.app.FragmentTransaction;
import androidx.core.content.LocalBroadcastManager;
import androidx.core.view.ViewPager;
import android.support.v7.app.AppCompatActivity;*/
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;
import static com.cypress.academy.ble101_robot.Utils.makeGattUpdateIntentFilter;

public class SensorSetup extends AppCompatActivity implements SettingFragment.onSomeEventListener {
    public ViewPager viewPager;
    public ViewPagerAdapter viewPagerAdapter;
    public EditText dd;
     public Fragment androidFragment;
    public BluetoothGattCharacteristic characteristic;
public Spinner log_int,log_met,sam_rat;
    public BluetoothLeService mBleService;
    public String mDeviceAddress,mDeviceName;


    public final MyData logTime[] = new MyData[14];
    public ArrayAdapter<MyData> logTime_spinnerArrayAdapter;

    public final MyData logMethod[] = new MyData[4];
    public ArrayAdapter<MyData> logMethod_spinnerArrayAdapter;

    public final MyData sampleRate[] = new MyData[11];
    public ArrayAdapter<MyData> sampleRate_spinnerArrayAdapter;

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_setup);
        //View v;
        androidFragment = new SettingFragment();

        // dd=(EditText)findViewById(R.id.ch1_name);
        //Log.d("data",dd.getText().toString());
       // onClick(v);

        //addfirst();

        final Intent intent = getIntent();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mDeviceName = intent.getStringExtra(EXTRAS_BLE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);



        // set MyFragment Arguments
        //MyFragment myObj = new MyFragment();
      /*  LayoutInflater inflator = LayoutInflater.from(SensorSetup.this);
        View views = inflator.inflate(R.layout.fragment_fragment_contents,null);*/

        /*dd=views.findViewById(R.id.ch1_range);
        dd.setText("savya");*/





                /*Bundle bundle = new Bundle();
                bundle.putString("text","My...My");
              //  androidFragment.setArguments(bundle);

                replaceFragment(bundle);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addfirst();
            }
        }, 500);

    }

    @Override
    public void someEvent(String s) {
        Fragment frag1 = getSupportFragmentManager().findFragmentByTag("f1");
        JSONObject main = new JSONObject();
        String[] json = s.split("xxc");
        try
        {

            /*main.put("log_inter",Integer.parseInt(logTime[log_int.getSelectedItemPosition()].getValue()));
            main.put("log_met",Integer.parseInt(logMethod[log_met.getSelectedItemPosition()].getValue()));
            main.put("sam_rat",Integer.parseInt(sampleRate[sam_rat.getSelectedItemPosition()].getValue()));*/

            main.put("ch1",new JSONObject(json[0]));

            main.put("ch2",new JSONObject(json[1]));
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        Log.d("message from fragment",main.toString().trim().replaceAll("\n",""));
        // ((TextView)frag1.getView().findViewById(R.id.textView)).setText("Text from Fragment 2:" + s);
    }

    /*public void onClick(View v)
    {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        Fragment androidFragment = new SettingFragment();
                        replaceFragment(androidFragment);
                    }
                }


        ).start();

    }*/
    /*private void setDefaultFragment(Fragment defaultFragment)
    {
        this.replaceFragment(defaultFragment);
    }*/

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBleService != null) {
            final boolean result = mBleService.connect(mDeviceAddress);
            Log.d("aa", "Connect request result=" + result);
        }
       /* Bundle bundle = new Bundle();
        bundle.putString("text","My...My");
        androidFragment.setArguments(bundle);

        replaceFragment(bundle);*/
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
               // displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //show.setText("sasassaas");

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

    public void addfirst()
    {
        FragmentManager mang = getSupportFragmentManager();
        Fragment f1 = mang.findFragmentByTag("f1");
        if(f1==null)
        {
            f1=new SettingFragment();
        }
        FragmentTransaction transaction = mang.beginTransaction();
        transaction.add(R.id.frame, f1, "f1");
        transaction.commit();
    }


    public void replaceFragment(Bundle bd)//final Fragment destFragment)
    {


        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragmentB = fragmentManager.findFragmentByTag("f2");

        if (fragmentB == null) {
            fragmentB = new SettingFragment();
            fragmentB.setArguments(bd);
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(fragmentManager.findFragmentByTag("f1"));
        transaction.replace(R.id.frame, fragmentB, "f2");
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //setting animation for fragment transaction
        transaction.addToBackStack(null);
        transaction.commit();

                /*final FragmentManager fragmentManager = this.getSupportFragmentManager();
                // First get FragmentManager object.

                // Begin Fragment transaction.
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the layout holder with the required Fragment object.
                fragmentTransaction.replace(R.id.frame, destFragment);

                fragmentTransaction.addToBackStack(null);
                // Commit the Fragment replace action.

                fragmentTransaction.commit();*/

    }
}
