package com.cypress.academy.ble101_robot;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

public class SensorSetup extends AppCompatActivity implements SettingFragment.onSomeEventListener {
    public ViewPager viewPager;
    public ViewPagerAdapter viewPagerAdapter;
    public EditText dd;
    public Button kk;
     public Fragment androidFragment;

public Spinner log_int,log_met,sam_rat;


    public final MyData logTime[] = new MyData[9];
    public ArrayAdapter<MyData> logTime_spinnerArrayAdapter;

    public final MyData logMethod[] = new MyData[4];
    public ArrayAdapter<MyData> logMethod_spinnerArrayAdapter;

    public final MyData sampleRate[] = new MyData[11];
    public ArrayAdapter<MyData> sampleRate_spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_setup);
        //View v;
        //androidFragment = new SettingFragment();

        // dd=(EditText)findViewById(R.id.ch1_name);
        //Log.d("data",dd.getText().toString());
       // onClick(v);

        addfirst();
        kk = (Button) findViewById(R.id.btt);
        log_int=(Spinner)findViewById(R.id.log_inter);
        log_met=(Spinner)findViewById(R.id.log_method);
        sam_rat=(Spinner)findViewById(R.id.sam_rate);
        kk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Bundle bundle = new Bundle();
                bundle.putString("text","My...My");
              //  androidFragment.setArguments(bundle);

                replaceFragment(bundle);
            }
        });

        logTime[0] = new MyData("30s", "30");
        logTime[1] = new MyData("1min", "60");
        logTime[2] = new MyData("2min", "120");
        logTime[3] = new MyData("5min", "300");
        logTime[4] = new MyData("10min", "600");
        logTime[5] = new MyData("15min", "900");
        logTime[6] = new MyData("20min", "1200");
        logTime[7] = new MyData("30min", "1800");
        logTime[8] = new MyData("1hr", "3600");


        logMethod[0] = new MyData("Max", "0");
        logMethod[1] = new MyData("Min", "1");
        logMethod[2] = new MyData("Average", "2");
        logMethod[3] = new MyData("Instant", "3");

        sampleRate[0]= new MyData("1s/S", "1");
        sampleRate[1]= new MyData("2s/S", "2");
        sampleRate[2]= new MyData("5s/S", "5");
        sampleRate[3]= new MyData("10s/S", "10");
        sampleRate[4]= new MyData("15s/S", "15");
        sampleRate[5]= new MyData("20s/S", "20");
        sampleRate[6]= new MyData("25s/S", "25");
        sampleRate[7]= new MyData("30s/S", "30");
        sampleRate[8]= new MyData("15s/S", "15");
        sampleRate[9]= new MyData("20s/S", "20");
        sampleRate[10]= new MyData("25s/S", "25");



        logTime_spinnerArrayAdapter= new ArrayAdapter<MyData>(
                SensorSetup.this,R.layout.spinner_log_trigger,logTime);
        logTime_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        log_int.setAdapter(logTime_spinnerArrayAdapter);


        logMethod_spinnerArrayAdapter= new ArrayAdapter<MyData>(
                SensorSetup.this,R.layout.spinner_log_trigger,logMethod);
        logMethod_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        log_met.setAdapter(logMethod_spinnerArrayAdapter);

        sampleRate_spinnerArrayAdapter= new ArrayAdapter<MyData>(
                SensorSetup.this,R.layout.spinner_log_trigger,sampleRate);
        sampleRate_spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_log_trigger);
        sam_rat.setAdapter(sampleRate_spinnerArrayAdapter);


        // set MyFragment Arguments
        //MyFragment myObj = new MyFragment();
      /*  LayoutInflater inflator = LayoutInflater.from(SensorSetup.this);
        View views = inflator.inflate(R.layout.fragment_fragment_contents,null);*/

        /*dd=views.findViewById(R.id.ch1_range);
        dd.setText("savya");*/

    }

    @Override
    public void someEvent(String s) {
        Fragment frag1 = getSupportFragmentManager().findFragmentByTag("f1");
        JSONObject main = new JSONObject();
        String[] json = s.split("xxc");
        try
        {
           main.put("log_inter",Integer.parseInt(logTime[log_int.getSelectedItemPosition()].getValue()));
            main.put("log_met",Integer.parseInt(logMethod[log_met.getSelectedItemPosition()].getValue()));
            main.put("sam_rat",Integer.parseInt(sampleRate[sam_rat.getSelectedItemPosition()].getValue()));

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
