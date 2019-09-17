package com.cypress.academy.ble101_robot;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
//import androidx.core.content.LocalBroadcastManager;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.opencsv.*;

import com.highsoft.highcharts.core.*;
import com.highsoft.highcharts.common.hichartsclasses.*;

import org.json.JSONObject;
//import com.chilkatsoft.*;

import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_ADDRESS;
import static com.cypress.academy.ble101_robot.ScanActivity.EXTRAS_BLE_NAME;
import static com.cypress.academy.ble101_robot.Utils.makeGattUpdateIntentFilter;

public class DeviceSettings extends AppCompatActivity {


    public BluetoothLeService mBleService;
    public String mDeviceAddress,mDeviceName;

    public boolean opened=false;
    Calendar date;
    private SimpleDateFormat mSimpleDateFormat;
    private Calendar mCalendar;
    public BluetoothGattCharacteristic characteristic;
    public Boolean mConnected;
    public TextView show,showtime;
    public Button set,timepicker,datetime,clr,excsv;
    public Spinner adc_spinner,logtime_spinner,logmethod_spinner,log_trigger,stop_mode;


    public ArrayAdapter<MyData> adc_spinnerArrayAdapter;
    public ArrayAdapter<MyData> logtime_spinnerArrayAdapter;
    public ArrayAdapter<MyData> logmethod_spinnerArrayAdapter;
    public ArrayAdapter<MyData> logtrigger_spinnerArrayAdapter;
    public ArrayAdapter<MyData> stopmode_spinnerArrayAdapter;

    public String adc_value,logtime_value,logmethod_value,logtrigger_value;

    public final MyData adc[] = new MyData[10];
    public final MyData logtime[] = new MyData[16];
    public final MyData logmethod[] = new MyData[4];
    public final MyData logtrigger[] = new MyData[3];
    public final MyData stopmode[] = new MyData[2];

    public TextView tt;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBleService.initialize()) {
                Log.e("TAg", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //mBleService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        setContentView(R.layout.activity_device_settings);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mDeviceName = intent.getStringExtra(EXTRAS_BLE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);
        //clr=(Button)findViewById(R.id.button2) ;
        //clr.setOnClickListener(click);



        excsv=(Button)findViewById(R.id.ex_csv);
        excsv.setOnClickListener(click);


        HIChartView chartView = (HIChartView) findViewById(R.id.shw);


        HIRanges ranges = new HIRanges();

        chartView.plugins = new ArrayList<>();
        chartView.plugins.add("data");

        HIData data = new HIData();
        String CSV="";//"datetime,ch1,ch2\n2019-2-14 17:19:00,12,23\n2019-2-14 17:19:00,12,23";//,\n2019-2-14 17:55:19,15,20,\n2019-2-14 17:55:23,16,17";
        String json  ="";
        int num_series=0;
        try
        {

            json = show_graph();
            JSONObject datad = new JSONObject(json);
            CSV = datad.getString("data");
            num_series = datad.getInt("num_of_headers");
            Log.d("numbers",String.valueOf(num_series));
        }catch(Exception e)
        {
           e.printStackTrace();
        }


        ArrayList<HIYAxis> y_axis = new ArrayList<HIYAxis>(num_series);
        ArrayList<HISeries> n_series = new ArrayList<HISeries>(num_series);

        for(int i=0;i<num_series;i++)
        {
            HIYAxis hiyAxis1 = new HIYAxis();
            hiyAxis1.setOpposite(true);
            hiyAxis1.setTitle(new HITitle());
            hiyAxis1.getTitle().setText("");
           // hiyAxis1.getTitle().setAlign("right");*/
            y_axis.add(hiyAxis1);
        }


        data.setItemDelimiter(",");
        data.setLineDelimiter("\n");
        //data.setDateFormat("hh:mm:ss");
        //System.out.println(CSV);
        data.setCsv(CSV);


        HIOptions options = new HIOptions();
        HICsv csv=new HICsv();


        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        exporting.setCsv(csv);
        HIChart chart = new HIChart();
        chart.setZoomType("x");
        chart.setType("line");
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setText("Miigo_BLE");

        options.setTitle(title);



       /* HIYAxis hiyAxis1 = new HIYAxis();
        hiyAxis1.setOpposite(true);
        //hiyAxis1.setLabels(new HILabels());
        //hiyAxis1.getLabels().setFormat("{value} C");
       // hiyAxis1.setTitle(new HITitle());
        //hiyAxis1.getTitle().setText("C");

        HIYAxis hiyAxis2 = new HIYAxis();
        hiyAxis2.setOpposite(true);
        //hiyAxis2.setLabels(new HILabels());
       // hiyAxis2.getLabels().setFormat("{value} %Rf");
        hiyAxis2.setTitle(new HITitle());
        hiyAxis2.getTitle().setText("%Rf");*/

        //options.setYAxis(new ArrayList<>(Arrays.asList(hiyAxis1, hiyAxis2)));

        for(int i =0;i<num_series;i++)
        {
            if(i==num_series-1) {
                HISeries series = new HILine();
                //series.setName("Humi");
                series.setYAxis(num_series-1);
                n_series.add(series);
            }
            else
            {
                HISeries series = new HILine();
                series.setYAxis(i);

                //series.setName("Temp");
                n_series.add(series);
            }
            /*else
                {
                    HISeries series = new HILine();
                    series.setYAxis(i);
                    //series.setName("Temp");
                    n_series.add(series);
            }*/
        }


        /*HISeries series1 = new HILine();
        series1.setName("Temp");

        HISeries series2 = new HILine();
        series2.setName("Humi");
        series2.setYAxis(1);*/

        HIXAxis xaxis =  new HIXAxis();
        xaxis.setType("datetime");
        xaxis.setTickLength(50);
        xaxis.setGridLineWidth(1);
        /*xaxis.setDateTimeLabelFormats(new HIDateTimeLabelFormats());
        xaxis.getDateTimeLabelFormats().setMonth(new HIMonth());
        xaxis.getDateTimeLabelFormats().getMonth().setMain("%m");

        xaxis.getDateTimeLabelFormats().setYear(new HIYear());
        xaxis.getDateTimeLabelFormats().getYear().setMain("%Y");
        HIDay hiDay = new HIDay();
        hiDay.setMain("%d");
        xaxis.getDateTimeLabelFormats().setDay(hiDay);*/
        //xaxis.setTickPixelInterval(150);
        options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));

        options.setData(data);
        options.setSeries(n_series);
        options.setYAxis(y_axis);
        // options.setSeries(new ArrayList<>(Arrays.asList(series1, series2)));

        //options.set
        options.setExporting(exporting);
        options.setCredits(new HICredits());
        options.getCredits().setEnabled(false);
        chartView.setOptions(options);
        /*
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.getDefault());
        setContentView(R.layout.activity_device_settings);
        mDeviceName = intent.getStringExtra(EXTRAS_BLE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_BLE_ADDRESS);

        show =(TextView) findViewById(R.id.show);
        set =(Button)findViewById(R.id.setvalue);

        showtime=(TextView)findViewById(R.id.show_time);
        timepicker = (Button)findViewById(R.id.timepicker);

        datetime= (Button)findViewById(R.id.datetime);


        set.setOnClickListener(click);
        timepicker.setOnClickListener(click);
        datetime.setOnClickListener(click);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

         adc_spinner = (Spinner) findViewById(R.id.adc_spinner);

         logtime_spinner = (Spinner) findViewById(R.id.logtime_spinner);
         logmethod_spinner = (Spinner) findViewById(R.id.log_method);
         log_trigger = (Spinner) findViewById(R.id.log_trigger);
         stop_mode=(Spinner)findViewById(R.id.stop_mode);


        adc[0] = new MyData("5 Sec", "5");
        adc[1] = new MyData("10 Sec", "10");
        adc[2] = new MyData("30 Sec", "30");
        adc[3] = new MyData("1 Min", "60");
        adc[4] = new MyData("2 Min", "120");
        adc[5] = new MyData("5 Min", "300");
        adc[6] = new MyData("10 Min", "600");
        adc[7] = new MyData("15 Min", "900");
        adc[8] = new MyData("30 Min", "1800");
        adc[9] = new MyData("1 Hr", "3600");


        logtime[0] = new MyData("1 Min", "60");
        logtime[1] = new MyData("2 Min", "120");
        logtime[2] = new MyData("5 Min", "300");
        logtime[3] = new MyData("10 Min", "600");
        logtime[4] = new MyData("15 Min", "900");
        logtime[5] = new MyData("30 Min", "1800");
        logtime[6] = new MyData("1 Hr", "3600");
        logtime[7] = new MyData("2 Hr", "7200");
        logtime[8] = new MyData("3 Hr", "10800");
        logtime[9] = new MyData("4 Hr", "14400");
        logtime[10] = new MyData("8 Hr", "28800");
        logtime[11] = new MyData("13 Hr", "46800");
        logtime[12] = new MyData("15 Hr", "54000");
        logtime[13] = new MyData("17 Hr", "61200");
        logtime[14] = new MyData("20 Hr", "72000");
        logtime[15] = new MyData("24 Hr", "86400");


        logmethod[0] = new MyData("Instant", "0");
        logmethod[1] = new MyData("Minimum", "1");
        logmethod[2] = new MyData("Maximum", "2");
        logmethod[3] = new MyData("Average", "3");


        logtrigger[0] = new MyData("Mag", "0");
        logtrigger[1] = new MyData("Delay", "1");
        logtrigger[2] = new MyData("Date/Time", "2");

        stopmode[0]= new MyData("OneTime","0");
        stopmode[1]=new MyData("OverWrite","1");


        //adc time dropdown
         adc_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                this,R.layout.spinner_adc_item,adc);
        adc_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_adc_item);
        adc_spinner.setAdapter(adc_spinnerArrayAdapter);

        //logtime dropdown
        logtime_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                this,R.layout.spinner_logtime_item,logtime);
        logtime_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_logtime_item);
        logtime_spinner.setAdapter(logtime_spinnerArrayAdapter);

        //logmethod dropdown
         logmethod_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                this,R.layout.spinner_log_method,logmethod);
        logmethod_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_method);
        logmethod_spinner.setAdapter(logmethod_spinnerArrayAdapter);

        //log_trigger
        logtrigger_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                this,R.layout.spinner_log_trigger,logtrigger);
        logtrigger_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        log_trigger.setAdapter(logtrigger_spinnerArrayAdapter);

        //stop_mode
        stopmode_spinnerArrayAdapter = new ArrayAdapter<MyData>(
                this,R.layout.spinner_log_trigger,stopmode);
        stopmode_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        stop_mode.setAdapter(stopmode_spinnerArrayAdapter);

        adc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                MyData d = adc[position];

                //Get selected value of key
                 adc_value= d.getValue();
                String key = d.getSpinnerText();
                Log.d("value_adc",adc_value);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        log_trigger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                MyData d = logtrigger[position];

                //Get selected value of key
                logtrigger_value= d.getValue();
                String key = d.getSpinnerText();
                if(key.equals("Delay"))
                {
                    showtime.setVisibility(View.VISIBLE);
                    timepicker.setVisibility(View.VISIBLE);


                }
                if(key.equals("Mag"))
                {
                    showtime.setVisibility(View.INVISIBLE);
                    timepicker.setVisibility(View.INVISIBLE);
                }
                if(key.equals("Date/Time"))
                {
                    showtime.setVisibility(View.VISIBLE);
                    timepicker.setVisibility(View.INVISIBLE);
                    datetime.setVisibility(View.VISIBLE);
                }
                Log.d("log_trigger",adc_value);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


*/

       // tt=(TextView) findViewById(R.id.shw);
        //tt.setMovementMethod(new ScrollingMovementMethod());
    }

    private String show_graph() throws Exception
    {
        Reader in = new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/files123/"+mDeviceAddress.replaceAll(":","")+".csv");

        CSVReader reader = new CSVReader(in);
        CSVReader csvReader = new CSVReaderBuilder(in).build();
        String[] headers;
        String[] data;
        //headers = reader.readNext();
        //List<String[]> myData =csvReader.readAll();
        //Iterator<String[]> iter = myData.iterator();
        //int size = myData.size();
        String to_send="";
        int i, j;


        JSONObject csv_details = new JSONObject();
        StringBuilder b2 = new StringBuilder();
        int len=0;
        int flf=0;
        while((data=reader.readNext())!=null)
        {
            //len=data.length;
            for(j=0;j<data.length;j++)
            {
                //len=data.length;
                if(flf==0 )
                {
                    len=data.length;

                   /* Log.d("must be 3",String.valueOf(j));
                    b2.append(data[j]+",");*/
                }
                 if(j==data.length-1)
                {

                    b2.append(data[j]);
                }

                 else
                {
                    b2.append(data[j]+",");
                }

            }
            flf=1;
            b2.append("\n");
        }
        /*while (iter.hasNext())
        {
            String[] record = iter.next();
            for(j=0;j<record.length;j++)
            {
                b2.append(record[j]+",");
            }
            b2.append("\n");


        }*/
        Log.d("lengthv of ss",String.valueOf(len));
        csv_details.put("data",b2.toString());
        csv_details.put("num_of_headers",len-1);
        //Log.d("json",csv_details.toString());

        return csv_details.toString();//b2.toString();//b2.toString().substring(0, b2.toString().length() - 2);
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
    public void displayData(String data)
    {
        if(data!=null)
        {
            tt.setText(data);
        }
    }
    /*public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        //date = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        DatePickerDialog datepick=new DatePickerDialog(DeviceSettings.this, mDateDataSet, mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        datepick.getDatePicker().setMinDate(mCalendar.getTimeInMillis());
        datepick.show();
    }*/

    private View.OnClickListener click=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId() ) {
                /*case R.id.setvalue:
                    //mBleService.disconnect();
                    logtime_spinner.setSelection(getIndex(logtime_spinner,logtime,"3600"));
                    //finish();
                    // do something when the corky is clicked
                break;*/
               /* case R.id.timepicker:
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(DeviceSettings.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            showtime.setText( selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                    break;*/
                /*case R.id.datetime:
                    showDateTimePicker();
                    break;*/
                /*case  R.id.button2:
                    tt.setText("");*/

                case R.id.ex_csv:
                    try
                    {
                        //String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                        //CSVWriter writer = new CSVWriter(new FileWriter(csv));
                        String dataa="ch1,ch2,time,\n11,22,2019-02-01 11:22:22,\n11,22,2019-02-01 11:22:22,\n11,22,2019-02-01 11:22:22";

                        /*CSVReader reader = new CSVReader(new StringReader(dataa),','); //Important for reading through csv
                        String[] employeeDetails = null;
                        //Reader read =new StringReader(dataa);
                        List<String[]> records = reader.readAll();


                        for (String[] record : records) {
                            System.out.println("Name : " + record[0]);
                            System.out.println("Email : " + record[1]);
                            System.out.println("Phone : " + record[2]);
                            //System.out.println("Country : " + record[3]);
                            System.out.println("---------------------------");
                        }*/

                        try (Reader in = new StringReader(dataa);)
                        {
                            CSV csv = new CSV(true, ',', in);
                            int nrows = 0;
                            while (csv.hasNext())
                            {
                                List<String> fields = csv.next();
                                for (int i = 0 ; i < fields.size() ; i++) {
                                    System.out.printf("%-3d: %s%n", (i+1), fields.get(i));
                                }
                                System.out.println();
                                nrows++;
                            }
                        }
                        /* while((employeeDetails = reader.readNext())!=null)
                        {
                            //Printing to the console
                            System.out.println(Arrays.toString(employeeDetails));
                        }*/


                        /*
                        String json_data="{\\r\\n  \\\"data\\\":[12,12,12,12,121,212],\\r\\n  \\\"dataq\\\":[11,22,3,3,1,5]\\r\\n  \\r\\n}";
                        JSONObject mainjson = new JSONObject(json_data);
                        JSONArray data=mainjson.getJSONArray("data");
                        JSONArray dataq=mainjson.getJSONArray("dataq");

                        int[] datav = new int[data.length()];
                        int[] dataqv = new int[dataq.length()];

                        for (int i = 0; i < data.length(); ++i)
                        {
                            datav[i] = data.optInt(i);
                        }

                        for (int i = 0; i < dataq.length(); ++i)
                        {
                            dataqv[i] = dataq.optInt(i);
                        }


                        List<String[]> data1 = new ArrayList<String[]>();
                        data1.add(new String[] {"India", "New Delhi"});
                        data1.add(new String[] {"United States", "Washington D.C"});
                        data1.add(new String[] {"Germany", "Berlin"});

                        writer.writeAll(data1);

                        writer.close();*/
                        //System.out.println(reader);

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
               default:
                    break;
            }
        }
    };
    /*private int getIndex(Spinner spinner,MyData dummy[], String myString){

        for(int i=0;i<16;i++)
        {
            MyData  d = logtime[i];
            if(d.getValue().equals("3600"))
            {
                return i;
            }
        }



        return -1;
    }*/

   /* private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(DeviceSettings.this, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true).show();
        }
    };*/

    /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
    /*private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            showtime.setText(mSimpleDateFormat.format(mCalendar.getTime()));
        }
    };*/

}
