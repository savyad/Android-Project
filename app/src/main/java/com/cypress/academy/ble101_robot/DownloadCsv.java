package com.cypress.academy.ble101_robot;

import android.bluetooth.BluetoothDevice;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.opencsv.CSVWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
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


    private FileNameAdapter mFileNameAdapter;
    public ArrayList filenames ;
    public ArrayAdapter<String> adapter;
    public ListView filelist;
    public Button dncb;
    public BluetoothLeService mBleService;
    public String mDeviceAddress,mDeviceName;
    public BluetoothGattCharacteristic characteristic;
    public JSONObject dnc;
    public TextView dstat;
    public int cnt=0;
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


        //LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        mFileNameAdapter=new FileNameAdapter();
        dstat=(TextView)findViewById(R.id.d_stat);
        dncb=(Button)findViewById(R.id.dnc);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
       listCsvFiles();


        filelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File s = mFileNameAdapter.getDevice(i);

                File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123/"+s.getName());//data.csv");
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
        Log.d("name",mDeviceName);

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
                cnt=cnt+1;
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
        //adapter=new ArrayAdapter<File>(DownloadCsv.this, android.R.layout.simple_list_item_1, mFileNameAdapter);
        filelist.setAdapter(mFileNameAdapter);

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
        Log.d("csv data",data);
        dstat.setText("Data Count..." + String.valueOf(cnt));
        if(data.contains("END"))
        {
           data= data.replace("END","");

            dstat.setText("DownLoad Completed...");

            File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123/"+mDeviceAddress.replaceAll(":","")+".csv");

            try
            {
                if(!file2.exists())
                {
                    if(file2.createNewFile()) {
                        FileWriter mwriter = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/files123/"+mDeviceAddress.replaceAll(":","")+".csv",false);
                        CSVWriter csvwrite = new CSVWriter(mwriter, CSVWriter.DEFAULT_SEPARATOR,
                                CSVWriter.NO_QUOTE_CHARACTER,
                                CSVWriter.NO_ESCAPE_CHARACTER, "\n");
                        String[] rec = new String[1];// = {"datetime,ch1,ch2,\n2019-11-22 23:11:22,22,11,\n2019-11-22 23:11:22,22,11"};
                        rec[0] = data;//"datetime,ch1,ch2,\n2019-11-22 23:11:22,22,11,\n2019-11-22 23:11:22,22,11";
                        csvwrite.writeNext(rec);
                        csvwrite.close();
                        mFileNameAdapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    FileWriter mwriter = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/files123/"+mDeviceAddress.replaceAll(":","")+".csv",false);
                    CSVWriter csvwrite = new CSVWriter(mwriter, CSVWriter.DEFAULT_SEPARATOR,
                            CSVWriter.NO_QUOTE_CHARACTER,
                            CSVWriter.NO_ESCAPE_CHARACTER, "\n");
                    String[] rec = new String[1];// = {"datetime,ch1,ch2,\n2019-11-22 23:11:22,22,11,\n2019-11-22 23:11:22,22,11"};
                    rec[0] = data;//"datetime,ch1,ch2,\n2019-11-22 23:11:22,22,11,\n2019-11-22 23:11:22,22,11";
                    csvwrite.writeNext(rec);
                    csvwrite.close();
                    mFileNameAdapter.notifyDataSetChanged();
                }
            }
            catch(Exception e)
            {

                e.printStackTrace();
            }

        }

    }

    private void  getFiles()
    {

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123");

        String filename;
        //Queue<File> files = new LinkedList<>();         //Linklist

       // files.addAll(Arrays.asList(path.listFiles()));  //adding all files of path in linklist
        File[] files = path.listFiles();

        for(int i=0;i<files.length;i++)
        {
            if(files[i].getName().contains(mDeviceAddress.replaceAll(":","")+".csv"))
            {
                Log.d("files names",files[i].toString());
                mFileNameAdapter.addfile(files[i]);//new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123"+files[i].getName()));
                mFileNameAdapter.notifyDataSetChanged();
            }
        }
        //while (!files.isEmpty())
       // {
            //File file = files.remove();
            //mFileNameAdapter.addfile(file);
            //mFileNameAdapter.notifyDataSetChanged();
            /*if (file.isDirectory())
            {
                files.addAll(Arrays.asList(file.listFiles()));


            }
            else if (file.getName().endsWith(".csv") || file.getName().endsWith(".xls") || file.getName().endsWith(".ppt"))
            {   //filtering files
                mFileNameAdapter.addfile(file);
                mFileNameAdapter.notifyDataSetChanged();

                *//*filename = file.getName();               // according to their extension
                filenames.add(filename);          //filenames is string ListArray
*//*
            }*/


      //  }
    }
    private View.OnClickListener click=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.dnc:

                    if(mBleService!=null)
                    {

                       try
                       {
                            dstat.setText("DownLoad Started....");
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


    private class FileNameAdapter extends BaseAdapter
    {
        private ArrayList<File> filenames;
        private LayoutInflater mInflator;

        public FileNameAdapter() {
            super();
            filenames = new ArrayList<File>();
            mInflator = DownloadCsv.this.getLayoutInflater();
        }
        public void addfile(File fname) {
          //  if(!filenames.contains(fname)) {
                filenames.add(fname);
           // }
        }
        public File getDevice(int position) {
            return filenames.get(position);
        }

        public void clear() {
            filenames.clear();
        }

        @Override
        public int getCount() {
            return filenames.size();
        }

        @Override
        public Object getItem(int i) {
            return filenames.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.fileName = (TextView) view.findViewById(R.id.row_item);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            File fnames = filenames.get(i);
            final String deviceName = fnames.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.fileName.setText(deviceName);
            else
                viewHolder.fileName.setText("No name");

            return view;


        }
    }

    static class ViewHolder {
        TextView fileName;

    }

}
