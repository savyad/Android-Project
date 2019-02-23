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
import android.widget.EditText;

public  class SensorSetup extends AppCompatActivity {
    public ViewPager viewPager;
    public ViewPagerAdapter viewPagerAdapter;
    public EditText dd;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_setup);
        //View v;
        Fragment androidFragment = new SettingFragment();

        // dd=(EditText)findViewById(R.id.ch1_name);
        //Log.d("data",dd.getText().toString());
       // onClick(v);

        Bundle bundle = new Bundle();
        bundle.putString("text","My...My");
        androidFragment.setArguments(bundle);

        replaceFragment(androidFragment);

        // set MyFragment Arguments
        //MyFragment myObj = new MyFragment();





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


    public void replaceFragment(final Fragment destFragment)
    {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                // First get FragmentManager object.


                // Begin Fragment transaction.
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the layout holder with the required Fragment object.
                fragmentTransaction.replace(R.id.frame, destFragment);

                // Commit the Fragment replace action.
                fragmentTransaction.commit();

            }
        }).start();
    }
}
