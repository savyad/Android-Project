package com.cypress.academy.ble101_robot;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 0;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL = 1;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL = 1;

    private static final int REQUEST_ENABLE_BLE = 1;

    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeScanner mLEScanner;
    public JSONObject main = new JSONObject();
    public JSONArray main_arr = new JSONArray();
    public TextView js;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        js=(TextView)findViewById(R.id.json);
        js.setMovementMethod(new ScrollingMovementMethod());


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
        } //End of section for Android 6.0 (Marshmallow)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_WRITE_EXTERNAL);
            }
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_READ_EXTERNAL);
            }
        }


        createDataFolder();

    }
    @Override
    protected void onResume() {
        super.onResume();

        // Verify that bluetooth is enabled. If not, request permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLE);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.i("Permission for 6.0:", "Coarse location permission granted");
                }
                else
                    {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("Since location access has not been granted, scanning will not work.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {

                        }
                    });
                    builder.show();
                }
            }
        }
    } //End of section for Android 6.0 (Marshmallow)
    private void createDataFolder()
    {
        File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123");
        if(!file1.exists())
        {
            file1.mkdir();
        }
    }

    public void startMain (View view) throws Exception
    {
       /* File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123");
        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123/data.csv");


                    if(!file2.exists()) {
                        if(file2.createNewFile()) {
                            FileWriter mwriter = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/files123/data.csv",true);
                            CSVWriter csvwrite = new CSVWriter(mwriter, CSVWriter.DEFAULT_SEPARATOR,
                                    CSVWriter.NO_QUOTE_CHARACTER,
                                    CSVWriter.NO_ESCAPE_CHARACTER, "\n");
                            String[] rec = new String[1];// = {"datetime,ch1,ch2,\n2019-11-22 23:11:22,22,11,\n2019-11-22 23:11:22,22,11"};
                            rec[0] = "datetime,ch1,ch2,\n2019-11-22 23:11:22,22,11,\n2019-11-22 23:11:22,22,11";
                            csvwrite.writeNext(rec);
                            csvwrite.close();
                        }
                    }*/
          /*Log.d("Files", "Size: "+ files.length);
              for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }*/
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Intent intent = new Intent(this, ScanActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //boolean startScan = true;
        //intent.putExtra("scan_flag",startScan);
        startActivity(intent);
       // File input = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/files123/MIIGO.csv");
        //File output = new File("/x/data.json");
       // Reader in = new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/files123/MIIGO.csv");

        //  CSVReader reader = new CSVReaderBuilder(IOUtils.toBufferedReader(in))
        //         .withCSVParser(new CSVParserBuilder().withSeparator(',').build()).build();
        //new CSVReader(new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/files123/MIIGO.csv"),',');

        //CSVReader reader2 = new CSVReader(new InputStreamReader(getAssets().open(input.toString())));

       // CSVReader reader = new CSVReader(in);
       // CSVReader csvReader = new CSVReaderBuilder(in).withSkipLines(1).build();
        String[] headers;
        String[] data;
    //    headers = reader.readNext();
    //  List<String[]> myData =csvReader.readAll();
    //    Iterator<String[]> iter = myData.iterator();
     //   int size = myData.size();

      //  int i, j;

       // for(i = 0;i<headers.length;i++)
      //  {
      ///      main.put(headers[i],main_arr);
       // }
        //for(i=0;i<headers.length;i++)
        //{
           // for(i=0;i<headers.length;i++)
          //  {
              //  StringBuilder b2 = new StringBuilder();
              //  while (iter.hasNext())
              //  {
              //      String[] record = iter.next();
               //     for(j=0;j<record.length;j++)
                //    {
                       // b2.append(record[j]+",");
               //     }
                //    b2.append("\n");

                    /*JSONArray arr = (JSONArray) main.get(headers[i]);
                    arr.put(record[i]);*/

                    //System.out.println(record[0]);

                   /* for(j=0;j<headers.length;j++)
                    {
                        main.getJSONArray(headers[j]).put(record[j]);
                        System.out.println(record[j]);
                    }*/


                    //main.put(headers[0],main_arr.put(record[0]));
                    //size--;
                //}

          //  }
         //   size=myData.size();
      //  }
        //int size = headers.length;

        //  List<String[]> myData =csvReader.readAll();

        // for(i=0;i<headers.length-1;i++)
        //  {
        //     j=i;
        //Log.d("header",String.valueOf(i));
        /*for ( i = 0; i < headers.length; i++)
        {

            main.put(headers[i],main_arr);

        }*/
       /* for (i = 0; i < headers.length - 1; i++)
        {


                while ((data = csvReader.readNext()) != null)
                {

                    main.put(headers[i], main_arr);


                    System.out.print(String.valueOf(headers[0]));



                }
    }*/

       // }

        /*while ((data = csvReader.readNext()) != null)
        {

            //main.put(headers[i],main_arr);
            Log.d("header",data[0]);
        }*/

      // js.setText(b2.toString());
        // System.out.println(main.toString(4));


        /*while ((line = reader.readNext()) != null)
        {

           Log.d("header",line[0]);
        }*/

        //Log.d("header",String.valueOf(line.length));

        /*FileInputStream fis = new FileInputStream(input);

        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null)
        {
            sb.append(line);
        }*/

        //Log.d("headers",sb.toString());
        //String[] nextLine;
        //nextLine = reader2.readNext();

       /* while ((nextLine = reader.readNext()) != null)
        {

            System.out.println(nextLine[1]);
        }*/


        //List<Map<?, ?>> data = readObjectsFromCsv(input);
        //Log.d("record",data.toString());
        //writeAsJson(data, input);




       /* Reader in = new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/files123/MIIGO.csv");
        CSVParser parser = new CSVParser(in, CSVFormat.EXCEL);
        List<CSVRecord> csvRecords = parser.getRecords();
        Log.d("record",csvRecords.toString());*/

        /*String demo = "01fc5c7910B008c515a808c515a808c515a808c515a8aaaa01fb5c7910B0112233221122332211223322";
        List<String[]> datas = new ArrayList<String[]>();
        datas.add(new String[] {"datetime","ch1","ch2"});
        String[] s = demo.split("aaaa");
        for(int i=0;i<s.length;i++)
        {
            String d=s[i];
            int delay = Integer.parseInt(d.substring(0,4),16);
            long time = Long.parseLong(d.substring(4,12),16);
            String dataa = d.substring(12,d.length());

            int k=0;
            int j=4;
            while(k!=dataa.length())
            {
                int temp = Integer.parseInt(dataa.substring(k,j),16)/100;
                int humi = Integer.parseInt(dataa.substring(j,j+4),16)/100;

                k=k+8;
                j=k+4;
                datas.add(new String[] {String.valueOf(time),String.valueOf(temp),String.valueOf(humi)});
                time = time + delay;

            }

        }
        if(datas!=null)
        {
            try
            {
                File f1=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/files123/check.csv");
                FileWriter outputfile = new FileWriter(f1,false);
                CSVWriter writer = new CSVWriter(outputfile);
                writer.writeAll(datas);
                writer.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }*/



    }

    public static List<Map<?, ?>> readObjectsFromCsv(File file) throws IOException {
        CsvSchema bootstrap = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader(Map.class).with(bootstrap).readValues(file);
        return mappingIterator.readAll();
    }

    public static void writeAsJson(List<Map<?, ?>> data, File file) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        String dataa="";
        mapper.writeValue(file, dataa);
        Log.d("json",dataa);
    }
}
