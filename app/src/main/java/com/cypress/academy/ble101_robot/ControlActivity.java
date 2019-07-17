package com.cypress.academy.ble101_robot;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vicmikhailau.maskededittext.MaskedFormatter;

import org.json.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;

import static android.widget.Toast.makeText;

public class ControlActivity extends AppCompatActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public JSONObject ref;

    public static final String EXTRAS_BLE_NAME = "BLE_NAME";
    public static final String EXTRAS_BLE_ADDRESS = "BLE_ADDRESS";
    public static final String BLE_SERVICE_VAR = "BLE_SERVICE";


    private Calendar mCalendar;
    private SimpleDateFormat mSimpleDateFormat;
    public MaskedFormatter formatter;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private String arr = "";

    private BluetoothGattCharacteristic characteristic, mNotifyCharacteristic;

    public AlertDialog.Builder builder1;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    List<BluetoothGattCharacteristic> bluetoothGattCharacteristic = new ArrayList<BluetoothGattCharacteristic>();
    Queue<BluetoothGattCharacteristic> mWriteCharacteristic = new LinkedList<BluetoothGattCharacteristic>();


    public BluetoothLeService mBleService;
    public String mDeviceName;
    public String mDeviceAddress;
    public TextView mConnectionState, mwrite, model, loggings, logginge, stop_mode, cali_on, status, min, max, avg, min_till, max_till, avg_till;
    public TextView loggingd, tri_info, batt_volt, cali_due, read_cnt, supp_p;
    Switch logging_on_off, ad_sleep;
    public Button calibrate, send;
    public EditText dev_name, op_name, adv;

    public Boolean mchangedByUser = true;
    private boolean mConnected = false;
    Spinner log_trigger, recordM, unitms;
    public Spinner log_int,log_met,sam_rat;
    public Handler handler;

    ProgressBar refresh;
    public Button disconnect, sendCommand, clear_buffer, setting_page;
    public EditText sendEdit, mread;


    public final MyData logtrigger[] = new MyData[3];
    public ArrayAdapter<MyData> logtrigger_spinnerArrayAdapter;

    public final MyData recor_mode[] = new MyData[2];
    public ArrayAdapter<MyData> recordmode_spinnerArrayAdapter;


    public final MyData logTime[] = new MyData[14];
    public ArrayAdapter<MyData> logTime_spinnerArrayAdapter;

    public final MyData logMethod[] = new MyData[4];
    public ArrayAdapter<MyData> logMethod_spinnerArrayAdapter;

    public final MyData sampleRate[] = new MyData[11];
    public ArrayAdapter<MyData> sampleRate_spinnerArrayAdapter;


    //mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);
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
        setContentView(R.layout.activity_device_control);
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_BLE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);
        //init=true;
        ref = new JSONObject();
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.getDefault());
        //mConnectionState = (TextView) findViewById(R.id.textView);

        builder1 = new AlertDialog.Builder(ControlActivity.this);
        builder1.setMessage("Please Check the Advertise Interval format.It should be in 24Hr Format");
        builder1.setCancelable(true);
        builder1.setTitle("Warning");
        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        dev_name = (EditText) findViewById(R.id.dev_name);
        op_name = (EditText) findViewById(R.id.op_name);

        loggings = (TextView) findViewById(R.id.loggings);
        logginge = (TextView) findViewById(R.id.logginge);
        //loggingd = (TextView) findViewById(R.id.loggingd);
        tri_info = (TextView) findViewById(R.id.tri_info);
        batt_volt = (TextView) findViewById(R.id.batt_volt);
        //  stop_mode = (TextView) findViewById(R.id.stop_mode);
        cali_on = (TextView) findViewById(R.id.cali_on);
        cali_due = (TextView) findViewById(R.id.cali_due);
        read_cnt = (TextView) findViewById(R.id.read_cnt);
        supp_p = (TextView) findViewById(R.id.supp);
        log_trigger = (Spinner) findViewById(R.id.log_trigger);
        recordM = (Spinner) findViewById(R.id.rec_mode);
        logging_on_off = (Switch) findViewById(R.id.logging_on_off);
        //ad_sleep = (Switch) findViewById(R.id.ad_sleep);

        send = (Button) findViewById(R.id.send_setting);
        //adv=(EditText)findViewById(R.id.ad_inter);



        refresh = (ProgressBar) findViewById(R.id.refresh);
        refresh.setVisibility(View.GONE);

        //sendEdit = (EditText) findViewById(R.id.textView3);

        if (mDeviceName == null) {
            //((TextView) findViewById(R.id.textView2)).setText("Unknown_device");
            setTitle("Unknown_device");
            // mConnectionState.setText(mDeviceAddress.replace(":",""));
        } else {
            //((TextView) findViewById(R.id.textView2)).setText(mDeviceName);
            setTitle(mDeviceName);
            //  mConnectionState.setText(mDeviceAddress.replace(":",""));
        }
        //System.out.print(mDeviceAddress);

        formatter = new MaskedFormatter("##:##:##");

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        logtrigger[0] = new MyData("Mag", "0");
        logtrigger[1] = new MyData("Delay", "1");
        logtrigger[2] = new MyData("Date/Time", "2");

        recor_mode[0] = new MyData("OneTime", "0");
        recor_mode[1] = new MyData("EndLess", "1");

        recordmode_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                ControlActivity.this, R.layout.spinner_log_trigger, recor_mode);
        recordmode_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        recordM.setAdapter(recordmode_spinnerArrayAdapter);




        logtrigger_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                ControlActivity.this, R.layout.spinner_log_trigger, logtrigger);
        logtrigger_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        log_trigger.setAdapter(logtrigger_spinnerArrayAdapter);

        log_trigger.setSelection(0, false);
        log_trigger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                MyData d = logtrigger[position];

                //Get selected value of key
                String logtrigger_value = d.getValue();
                String key = d.getSpinnerText();
                if (key.equals("Delay") && mchangedByUser) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(ControlActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            tri_info.setText(selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }

                if (key.equals("Mag")) {

                }
                if (key.equals("Date/Time") && mchangedByUser) {
                    showDateTimePicker();
                }
                mchangedByUser = true;

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        log_int=(Spinner)findViewById(R.id.log_inter);
        log_met=(Spinner)findViewById(R.id.log_method);
        sam_rat=(Spinner)findViewById(R.id.sam_rate);




        logTime[0] = new MyData("5s", "5");
        logTime[1] = new MyData("10s", "10");
        logTime[2] = new MyData("15s", "15");
        logTime[3] = new MyData("20s", "20");
        logTime[4] = new MyData("25s", "25");
        logTime[5] = new MyData("30s", "30");
        logTime[6] = new MyData("1min", "60");
        logTime[7] = new MyData("2min", "120");
        logTime[8] = new MyData("5min", "300");
        logTime[9] = new MyData("10min", "600");
        logTime[10] = new MyData("15min", "900");
        logTime[11] = new MyData("20min", "1200");
        logTime[12] = new MyData("30min", "1800");
        logTime[13] = new MyData("1hr", "3600");


        logMethod[0] = new MyData("Min", "1");
        logMethod[1] = new MyData("Max", "2");
        logMethod[2] = new MyData("Average", "3");
        logMethod[3] = new MyData("Instant", "4");

        sampleRate[0]= new MyData("1s/S", "1");
        sampleRate[1]= new MyData("2s/S", "2");
        sampleRate[2]= new MyData("5s/S", "5");
        sampleRate[3]= new MyData("10s/S", "10");
        sampleRate[4]= new MyData("15s/S", "15");
        sampleRate[5]= new MyData("20s/S", "20");
        sampleRate[6]= new MyData("25s/S", "25");
        sampleRate[7]= new MyData("30s/S", "30");
        sampleRate[8]= new MyData("35s/S", "35");
        sampleRate[9]= new MyData("40s/S", "40");
        sampleRate[10]= new MyData("50s/S", "50");



        logTime_spinnerArrayAdapter= new ArrayAdapter<MyData>(
                ControlActivity.this,R.layout.spinner_log_trigger,logTime);
        logTime_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        log_int.setAdapter(logTime_spinnerArrayAdapter);


        logMethod_spinnerArrayAdapter= new ArrayAdapter<MyData>(
                ControlActivity.this,R.layout.spinner_log_trigger,logMethod);
        logMethod_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        log_met.setAdapter(logMethod_spinnerArrayAdapter);

        sampleRate_spinnerArrayAdapter= new ArrayAdapter<MyData>(
                ControlActivity.this,R.layout.spinner_log_trigger,sampleRate);
        sampleRate_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        sam_rat.setAdapter(sampleRate_spinnerArrayAdapter);

        send.setOnClickListener(click);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshData();
            }
        }, 500);

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
                refresh.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //Log.d("tag",intent.getStringExtra(BluetoothLeService.EXTRA_DATA));


                //mread.setText("sasassaas");

            }
        }
    };

    @Override
    protected void onResume() {
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
        if (mBleService.buffer != null) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.Disconnect:
                mBleService.disconnect();
                break;
            case R.id.clr_buff:
                if (mBleService != null) {
                    if (mBleService.buffer != null) {
                        mBleService.buffer = "";

                        //mread.setText("");
                    }
                }
                break;
            case R.id.refresh:
                refreshData();
                break;
            case R.id.sync_rtc:
                if(mBleService!=null) {
                    try {
                        long unixTime = System.currentTimeMillis() / 1000L;
                        JSONObject ref = new JSONObject();
                        ref.put("type", "rtc_set");
                        ref.put("time", unixTime);
                        sendbyMTUlimit(ref.toString(), characteristic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                default:
                    break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // mConnectionState.setText("Connected");
            }
        });
    }

    private void getGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }


    }

    public void gattservice(List<BluetoothGattService> gattServices) {

        if (gattServices == null) {
            //continue;
        }

        //for(int i=0;i<gattServices.size();i++)
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            Log.d("service", gattService.getUuid().toString());
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                final int charaProp = gattCharacteristic.getProperties();

                if (((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                        (charaProp & BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)) > 0)
                //if(gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE)//BleUuid.write_service.equalsIgnoreCase(gattCharacteristic.getUuid().toString()))
                {
                    //mwrite.setVisibility(View.VISIBLE);
                    //sendCommand.setVisibility(View.VISIBLE);
                    //mwrite.setText("Write Service");
                    characteristic = gattCharacteristic;
                }
               /*else if(Utils.hasReadProperty(gattCharacteristic.getProperties())!=0)
               {
                   if (mBleService != null) {
                       mBleService.readCharacteristic(gattCharacteristic);
                       mread.setText("data");
                   }
               }*/
                else if (Utils.hasNotifyProperty(gattCharacteristic.getProperties()) != 0) {
                    if (mBleService != null) {
                        mBleService.setCharacteristicNotification(gattCharacteristic, true);
                        //mread.setText("data");
                        mBleService.readCharacteristic(gattCharacteristic);

                    }
                }
               /*if(((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) |
                       (charaProp & BluetoothGattCharacteristic.PROPERTY_READ)) > 0)
               {
                   if (mNotifyCharacteristic != null) {
                       mBleService.setCharacteristicNotification(
                               gattCharacteristic, false);
                       mNotifyCharacteristic = null;
                   }
                   mBleService.readCharacteristic(characteristic);
               }
               if (((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) |
                       (charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY)) > 0) {
                   mNotifyCharacteristic = characteristic;
                   mBleService.setCharacteristicNotification(
                           characteristic, true);
               }*/
            }
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        for (BluetoothGattService service : gattServices) {
            Log.i("serv", "Service UUID = " + service.getUuid());

            bluetoothGattCharacteristic = service.getCharacteristics();

            for (BluetoothGattCharacteristic character : bluetoothGattCharacteristic) {
                Log.i("char", "Service Character UUID = " + character.getUuid());

                // Add your **preferred characteristic** in a Queue
                if (character.getUuid().equals("6e400002-b5a3-f393-e0a9-e50e24dcca9e") || character.getUuid().equals("6e400003-b5a3-f393-e0a9-e50e24dcca9e")) {
                    mWriteCharacteristic.add(character);
                    //mConnectionState.setText(character.getUuid().toString());
                }
            }
        }


    }

    private void displayData(String data) {
        //mread.setText("sdasdasd");
        Log.d("tag", data);
        //char last = data.charAt(data.length() - 1);
        //char lastc='}';

        if (data != null && data.contains("basic") && data.contains("enddata") && data.contains("}")) {

            try {
                JSONObject read_json = new JSONObject(data);
                System.out.println(read_json.toString());
                log_trigger.setSelection(0, false);
                //String prop = read_json.getString("property");
                //String models = read_json.getString("model_no");
                String dev_nmv = read_json.getString("dev_name");
                String op_namev = read_json.getString("op_nm");
                String supp = read_json.getString("sup_p");
                int log_stat = read_json.getInt("log_stat");
                int rec_m = read_json.getInt("rec_mode");
                int log_triv = read_json.getInt("log_tri");
                //String delayv = read_json.getString("delay");
                int delayv = read_json.getInt("delay");
                long date_tmv = read_json.getLong("date_tm");

                Boolean logg_onoffV = read_json.getBoolean("log");
                //Boolean ad_sleepV = read_json.getBoolean("ad_slp");

                //int ad_interV = read_json.getInt("ad_int");
                 int log_inter = read_json.getInt("log_inter");
                int log_mets = read_json.getInt("log_met");
                int sam_rats = read_json.getInt("sam_rat");

                long loggingst = read_json.getLong("loggings");
                long loggingen = read_json.getLong("logginge");
                //long loggingdn = read_json.getLong("loggingd");
                int batt_voltv = read_json.getInt("bt_vl");

                //String stopmode = read_json.getString("stop_mode");
                long calibration = read_json.getLong("cali_on");
                long cali_duev = read_json.getLong("cali_due");
                int no_readV = read_json.getInt("nr");


                dev_name.setText(dev_nmv);
                loggings.setText(unixTodatetime(loggingst));

                // loggingd.setText(unixTodatetime(loggingen));
                op_name.setText(op_namev);
                supp_p.setText(supp);

                setLogtriggervals(log_triv, delayv, date_tmv);


                if (log_stat == 0) {
                    logginge.setText("Ended " + unixTodatetime(loggingen));
                } else {
                    logginge.setText("Still Logging");
                }

                batt_volt.setText(String.valueOf((double) batt_voltv / 1000));
                recordM.setSelection(rec_m);
                cali_on.setText(unixTodatetime(calibration));
                cali_due.setText(unixTodatetime(cali_duev));
                read_cnt.setText(String.valueOf(no_readV));
                logging_on_off.setChecked(logg_onoffV);
                //ad_sleep.setChecked(ad_sleepV);
                //unitms.setSelection(getIndex(unitms, unit, String.valueOf(ad_interV)));
                //adv.setText(Secstohmsad_int(ad_interV));
                log_int.setSelection(getIndex(log_int,logTime,String.valueOf(log_inter)));
                log_met.setSelection(getIndex(log_met,logMethod,String.valueOf(log_mets)));
                sam_rat.setSelection(getIndex(sam_rat,sampleRate,String.valueOf(sam_rats)));



                mBleService.buffer = "";


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this,"something went wrong in communication",Toast.LENGTH_LONG).show();
                mBleService.buffer = "";
            }

            //mread.setText(data);
        } else {
            //mBleService.buffer="";
        }
    }

    public String unixTodatetime(Long time) {
        Date date = new java.util.Date(time * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public long dateToUnixstamp(String time) {
        long timestamp = 15;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = format.parse(time);
            timestamp = date.getTime() / 1000;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }


    private void refreshData()
    {
        if (mBleService != null) {
            System.out.println("ref");

            refresh.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (mBleService.buffer == null) {
                send_refresh_req(characteristic, "{\\\"type\\\":\\\"basic\\\"}");
                // Log.d("refresh","basic");
            } else {
                mBleService.buffer = "";
                send_refresh_req(characteristic, "{\\\"type\\\":\\\"basic\\\"}");
                //Log.d("refresh","basic");
            }

        }
    }
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId() /*to get clicked view id**/) {
                /*case R.id.button2:
                    mBleService.disconnect();
                    //finish();
                    // do something when the corky is clicked

                    break;*/
               /* case R.id.button3:
                    //mread.setText("barobr");
                    if(mBleService!=null)
                    {
                        try
                        {
                            sendbyMTUlimit(sendEdit.getText().toString(), characteristic);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;*/
                /*case R.id.clear_buffer:
                    if(mBleService!=null)
                    {
                        if(mBleService.buffer!=null)
                        {
                            mBleService.buffer="";

                            //mread.setText("");
                        }
                    }
                    break;*/
                /*case R.id.setting:
                    if(mBleService!=null)
                    {
                        opensetting();
                    }*/
                case R.id.send_setting:

                    //Log.d("value",String.valueOf(log_trigger.getSelectedItemPosition()));
                    String devName = dev_name.getText().toString();
                    String Op = op_name.getText().toString();
                    int logtri = log_trigger.getSelectedItemPosition();
                    int rec_mod = recordM.getSelectedItemPosition();
                    String tringfo = tri_info.getText().toString();
                    //Boolean logging =false;
                    //Boolean adinSl=false;
                    //String adinter = adv.getText().toString();
                    JSONObject to_send = new JSONObject();
                    try {
                        to_send.put("type", "basic1");
                        to_send.put("dev_nm", devName);
                        to_send.put("op_nm", Op);
                        to_send.put("rec_mode", rec_mod);
                        to_send.put("log_tri", logtri);

                        if (logtri == 1) {
                            //hmsToSecintDelay(tringfo);
                            to_send.put("delay", hmsToSecintDelay(tringfo));


                        } else if (logtri == 2) {
                            to_send.put("date_tm", dateToUnixstamp(tringfo));
                        }

                        if (logging_on_off.isChecked()) {
                            to_send.put("log", true);

                        } else if (!logging_on_off.isChecked()) {
                            to_send.put("log", false);

                        }

                        /*if (ad_sleep.isChecked()) {
                            to_send.put("ad_slp", true);

                        } else if (!ad_sleep.isChecked()) {
                            to_send.put("ad_slp", false);

                        }*/
                        //String unmaskedString = formatter.formatString(adinter).getUnMaskedString();

                        // to_send.put("ad_int",hmsToSecintad_int(adinter));//formatter.formatString(adinter).getUnMaskedString());
                        to_send.put("log_inter", Integer.parseInt(logTime[log_int.getSelectedItemPosition()].getValue()));
                        to_send.put("log_met", Integer.parseInt(logMethod[log_met.getSelectedItemPosition()].getValue()));
                        to_send.put("sam_rat", Integer.parseInt(sampleRate[sam_rat.getSelectedItemPosition()].getValue()));

                        if (mBleService != null) {
                            try {
                                System.out.println(to_send.toString());
                                sendbyMTUlimit(to_send.toString(), characteristic);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        /*if(checktime(adinter))
                        {
                            if (mBleService != null) {
                                try {
                                    sendbyMTUlimit(to_send.toString(), characteristic);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }*/
                        //Log.d("json_to_send",to_send.toString());

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                default:
                    break;
            }
        }
    };

    public void opensetting() {
        final Intent intent = new Intent(ControlActivity.this, DeviceSettings.class);
        intent.putExtra(EXTRAS_BLE_ADDRESS, mDeviceAddress);
        intent.putExtra(EXTRAS_BLE_NAME, mDeviceName);
        startActivity(intent);
    }

    private void setLogtriggervals(int log_triv, int delayv, long date_tmv) {
        mchangedByUser = false;
        switch (log_triv) {
            case 0:
                log_trigger.setSelection(getIndex(log_trigger, logtrigger, String.valueOf(log_triv)), false);
                break;
            case 1:
                tri_info.setText(SecstohmsDelay(delayv));
                //SecstohmsDelay(delayv);
                log_trigger.setSelection(getIndex(log_trigger, logtrigger, String.valueOf(log_triv)));
                break;
            case 2:
                tri_info.setText(unixTodatetime(date_tmv));
                log_trigger.setSelection(getIndex(log_trigger, logtrigger, String.valueOf(log_triv)));
                break;
            default:
                mchangedByUser = true;
                break;
        }

    }


    public void send_refresh_req(BluetoothGattCharacteristic charac, String data) {
        try {
            ref.put("type", "basic");
            charac.setValue(ref.toString());
            mBleService.writeCharacteristic(charac);
        } catch (Exception e) {

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

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        //date = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        DatePickerDialog datepick = new DatePickerDialog(ControlActivity.this, mDateDataSet, mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        datepick.getDatePicker().setMinDate(mCalendar.getTimeInMillis());
        datepick.show();
    }

    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(ControlActivity.this, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true).show();
        }
    };

    /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            tri_info.setText(mSimpleDateFormat.format(mCalendar.getTime()));
        }
    };

    private boolean checktime(String maskedT) {

        //int timeinsecs = Integer.valueOf(maskedT);
        if (maskedT != null) {
            String hms[] = maskedT.split(":");

            if (((Integer.valueOf(hms[0]) >= 0) && (Integer.valueOf(hms[0]) <= 23)) && ((Integer.valueOf(hms[1]) >= 0) && (Integer.valueOf(hms[1]) <= 59)) && ((Integer.valueOf(hms[2]) >= 0) && (Integer.valueOf(hms[2]) <= 59))) {
                return true;
            } else {
                return false;

            }
        } else {
            return false;
        }
    }

    public int hmsToSecintDelay(String time) {
        int temp = 0;
        if (time != null) {
            String ss[] = time.split(":");

            int hour = Integer.parseInt(ss[0]);
            int minute = Integer.parseInt(ss[1]);

            temp = (60 * minute) + (3600 * hour);

        }
        return temp;
    }


    public String SecstohmsDelay(int sec) {
        Date d = new Date(sec * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = df.format(d);
        return time;
    }

    public int hmsToSecintad_int(String time) {
        int temp = 0;
        if (time != null) {
            String ss[] = time.split(":");

            int hour = Integer.parseInt(ss[0]);
            int minute = Integer.parseInt(ss[1]);
            int seconds = Integer.parseInt(ss[2]);

            temp = seconds + (60 * minute) + (3600 * hour);

        }
        return temp;
    }


    public String Secstohmsad_int(int sec) {
        Date d = new Date(sec * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = df.format(d);
        return time;
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


}
