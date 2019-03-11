package com.cypress.academy.ble101_robot;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.CharacterCodingException;

public class SettingFragment extends Fragment
{
    public ViewPager viewPager;
    public ViewPagerAdapter viewPagerAdapter;
    public EditText dd;
    public String ssas;
    public Button snd;

    public EditText ch1_name,ch1_rang,ch1_offse,ch1_gain;
    public CheckBox ch1_1_enb,ch1_2_enb,ch1_3_enb,ch1_4_enb;
    public EditText ch1_1_setpoint,ch1_2_setpoint,ch1_3_setpoint,ch1_4_setpoint;
    public Spinner ch1_1_judge,ch1_2_judge,ch1_3_judge,ch1_4_judge;
    public Switch ch1_1_hyst,ch1_2_hyst,ch1_3_hyst,ch1_4_hyst;
    public TextView ch1_H,ch1_L,ch1_HH,ch1_LL;

    public EditText ch2_name,ch2_rang,ch2_offse,ch2_gain;
    public CheckBox ch2_1_enb,ch2_2_enb,ch2_3_enb,ch2_4_enb;
    public EditText ch2_1_setpoint,ch2_2_setpoint,ch2_3_setpoint,ch2_4_setpoint;
    public Spinner ch2_1_judge,ch2_2_judge,ch2_3_judge,ch2_4_judge;
    public Switch ch2_1_hyst,ch2_2_hyst,ch2_3_hyst,ch2_4_hyst;
    public TextView ch2_H,ch2_L,ch2_HH,ch2_LL;




    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    public LinearLayout contain;
    Bundle savedArgs = new Bundle();



    public interface onSomeEventListener
    {

        public void someEvent(String s);
    }

    onSomeEventListener someEventListener;


    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            ssas = getArguments().getString("text");
        }


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Please note the third parameter should be false, otherwise a java.lang.IllegalStateException maybe thrown.
        //final FragmentContents f1 = new FragmentContents();
        //final Fragment2Contents f2 = new Fragment2Contents();

        View retView = inflater.inflate(R.layout.fragment_fragment_contents, container, false);
        //contain = (LinearLayout) retView.findViewById(R.id.fragment_container);


        //dd=(EditText)retView.findViewById(R.id.off_ch1);
        snd=(Button)retView.findViewById(R.id.save_bt);

        ch1_name=(EditText)retView.findViewById(R.id.off_ch1);
        ch1_rang=(EditText)retView.findViewById(R.id.ch1_range);
        ch1_offse=(EditText)retView.findViewById(R.id.ch1_offset);
        ch1_gain=(EditText)retView.findViewById(R.id.ch1_gain);

        ch1_1_enb=(CheckBox)retView.findViewById(R.id.ch1_1_enb);
        ch1_2_enb=(CheckBox)retView.findViewById(R.id.ch1_2_enb);
        ch1_3_enb=(CheckBox)retView.findViewById(R.id.ch1_3_enb);
        ch1_4_enb=(CheckBox)retView.findViewById(R.id.ch1_4_enb);

        ch1_1_setpoint=(EditText)retView.findViewById(R.id.ch1_1_setpoint);
        ch1_2_setpoint=(EditText)retView.findViewById(R.id.ch1_2_setpoint);
        ch1_3_setpoint=(EditText)retView.findViewById(R.id.ch1_3_setpoint);
        ch1_4_setpoint=(EditText)retView.findViewById(R.id.ch1_4_setpoint);

        ch1_1_judge=(Spinner) retView.findViewById(R.id.ch1_1_judge);
        ch1_2_judge=(Spinner)retView.findViewById(R.id.ch1_2_judge);
        ch1_3_judge=(Spinner)retView.findViewById(R.id.ch1_3_judge);
        ch1_4_judge=(Spinner)retView.findViewById(R.id.ch1_4_judge);

        ch1_1_hyst=(Switch)retView.findViewById(R.id.ch1_1_hyst);
        ch1_2_hyst=(Switch)retView.findViewById(R.id.ch1_2_hyst);
        ch1_3_hyst=(Switch)retView.findViewById(R.id.ch1_3_hyst);
        ch1_4_hyst=(Switch)retView.findViewById(R.id.ch1_4_hyst);

        ch1_H=(TextView)retView.findViewById(R.id.ch1_H);
        ch1_L=(TextView)retView.findViewById(R.id.ch1_L);
        ch1_HH=(TextView)retView.findViewById(R.id.ch1_HH);
        ch1_LL=(TextView)retView.findViewById(R.id.ch1_LL);

        ch2_name=(EditText)retView.findViewById(R.id.off_ch2);
        ch2_rang=(EditText)retView.findViewById(R.id.ch2_range);
        ch2_offse=(EditText)retView.findViewById(R.id.ch2_offset);
        ch2_gain=(EditText)retView.findViewById(R.id.ch2_gain);

        ch2_1_enb=(CheckBox)retView.findViewById(R.id.ch2_1_enb);
        ch2_2_enb=(CheckBox)retView.findViewById(R.id.ch2_2_enb);
        ch2_3_enb=(CheckBox)retView.findViewById(R.id.ch2_3_enb);
        ch2_4_enb=(CheckBox)retView.findViewById(R.id.ch2_4_enb);

        ch2_1_setpoint=(EditText)retView.findViewById(R.id.ch2_1_setpoint);
        ch2_2_setpoint=(EditText)retView.findViewById(R.id.ch2_2_setpoint);
        ch2_3_setpoint=(EditText)retView.findViewById(R.id.ch2_3_setpoint);
        ch2_4_setpoint=(EditText)retView.findViewById(R.id.ch2_4_setpoint);

        ch2_1_judge=(Spinner) retView.findViewById(R.id.ch2_1_judge);
        ch2_2_judge=(Spinner)retView.findViewById(R.id.ch2_2_judge);
        ch2_3_judge=(Spinner)retView.findViewById(R.id.ch2_3_judge);
        ch2_4_judge=(Spinner)retView.findViewById(R.id.ch2_4_judge);

        ch2_1_hyst=(Switch)retView.findViewById(R.id.ch2_1_hyst);
        ch2_2_hyst=(Switch)retView.findViewById(R.id.ch2_2_hyst);
        ch2_3_hyst=(Switch)retView.findViewById(R.id.ch2_3_hyst);
        ch2_4_hyst=(Switch)retView.findViewById(R.id.ch2_4_hyst);

        ch2_H=(TextView)retView.findViewById(R.id.ch2_H);
        ch2_L=(TextView)retView.findViewById(R.id.ch2_L);
        ch2_HH=(TextView)retView.findViewById(R.id.ch2_HH);
        ch2_LL=(TextView)retView.findViewById(R.id.ch2_LL);

        //dd.setText(ssas);


        snd.setOnClickListener(click);

        //Log.d("data",dd.getText().toString());
        /*TabLayout tabLayout = (TabLayout) retView.findViewById(R.id.tab);

        tabLayout.addTab(tabLayout.newTab().setText("ch1"));
        tabLayout.addTab(tabLayout.newTab().setText("ch2"));
        */
        //Log.d("counts123",String.valueOf(tabLayout.getTabCount()));

       /* if (savedInstanceState.getString("saved_user") != null)
        {
            savedArgs.putString(f1.USER,savedInstanceState.getString("saved_user"));
        }*/


       /* tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if (tab.getPosition() == 0) {
                    if(getFragmentManager().findFragmentByTag("ch1")==null) {
                        if(savedArgs!=null)
                        {
                            f1.setArguments(savedArgs);
                        }
                        replaceFragment(f1, "ch1");

                    }
                } else if (tab.getPosition() == 1) {
                    if(getFragmentManager().findFragmentByTag("ch2")==null) {
                        replaceFragment(new Fragment2Contents(), "ch2");
                    }
                } else {
                    //replaceFragment(new GameFragment());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });*/


        return retView;
    }
    public void replaceFragment(Fragment destFragment,String tag)
    {
        // First get FragmentManager object.
        FragmentManager fragmentManager = this.getFragmentManager();

        // Begin Fragment transaction.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the layout holder with the required Fragment object.
        fragmentTransaction.replace(R.id.fragment_container, destFragment,tag);

        //fragmentTransaction.addToBackStack(null);
        // Commit the Fragment replace action.
        fragmentTransaction.commit();
    }




    private View.OnClickListener click =new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId())
            {
                case R.id.save_bt:



                    JSONObject main = new JSONObject();
                    JSONObject obj_ch1 = new JSONObject();
                    JSONObject obj_ch2 = new JSONObject();
                    JSONArray events = new JSONArray();
                    JSONObject temp = new JSONObject();
                    try
                    {
                        //object 1 inintialization i.e ch1
                        obj_ch1.put("nm",ch1_name.getText().toString());
                        obj_ch1.put("mu",ch1_name.getText().toString());
                        obj_ch1.put("range",ch1_rang.getText().toString());
                        obj_ch1.put("ofs",ch1_offse.getText().toString());
                        obj_ch1.put("gn",ch1_gain.getText().toString());

                        temp.put("enb",ch1_1_enb.isChecked());
                        temp.put("typ",ch1_H.getText());
                        temp.put("stp",Double.valueOf(ch1_1_setpoint.getText().toString()));
                        temp.put("jud",Integer.valueOf("200"));//unit[unitms.getSelectedItemPosition()].getValue()
                        temp.put("hst",ch1_1_hyst.isChecked());

                        events.put(temp);
                        temp=new JSONObject();

                        temp.put("enb",ch1_2_enb.isChecked());
                        temp.put("typ",ch1_L.getText());
                        temp.put("stp",Double.valueOf(ch1_2_setpoint.getText().toString()));
                        temp.put("jud",Integer.valueOf("200"));//unit[unitms.getSelectedItemPosition()].getValue()
                        temp.put("hst",ch1_2_hyst.isChecked());

                        events.put(temp);

                        temp=new JSONObject();

                        temp.put("enb",ch1_3_enb.isChecked());
                        temp.put("typ",ch1_HH.getText());
                        temp.put("stp",Double.valueOf(ch1_3_setpoint.getText().toString()));
                        temp.put("jud",Integer.valueOf("200"));//unit[unitms.getSelectedItemPosition()].getValue()
                        temp.put("hst",ch1_3_hyst.isChecked());

                        events.put(temp);

                        temp=new JSONObject();

                        temp.put("enb",ch1_4_enb.isChecked());
                        temp.put("typ",ch1_LL.getText());
                        temp.put("stp",Double.valueOf(ch1_4_setpoint.getText().toString()));
                        temp.put("jud",Integer.valueOf("200"));//unit[unitms.getSelectedItemPosition()].getValue()
                        temp.put("hst",ch1_4_hyst.isChecked());

                        events.put(temp);
                        obj_ch1.put("events",events);
                        events = new JSONArray();
                        temp= new JSONObject();
                        //*******////

                        //obj2 initialization i.e ch2/////


                        obj_ch2.put("nm",ch2_name.getText().toString());
                        obj_ch2.put("mu",ch2_name.getText().toString());
                        obj_ch2.put("range",ch2_rang.getText().toString());
                        obj_ch2.put("ofs",ch2_offse.getText().toString());
                        obj_ch2.put("gn",ch2_gain.getText().toString());

                        temp.put("enb",ch2_1_enb.isChecked());
                        temp.put("typ",ch2_H.getText());
                        temp.put("stp",Double.valueOf(ch2_1_setpoint.getText().toString()));
                        temp.put("jud",Integer.valueOf("200"));//unit[unitms.getSelectedItemPosition()].getValue()
                        temp.put("hst",ch2_1_hyst.isChecked());

                        events.put(temp);
                        temp=new JSONObject();

                        temp.put("enb",ch2_2_enb.isChecked());
                        temp.put("typ",ch2_L.getText());
                        temp.put("stp",Double.valueOf(ch2_2_setpoint.getText().toString()));
                        temp.put("jud",Integer.valueOf("200"));//unit[unitms.getSelectedItemPosition()].getValue()
                        temp.put("hst",ch2_2_hyst.isChecked());

                        events.put(temp);

                        temp=new JSONObject();

                        temp.put("enb",ch2_3_enb.isChecked());
                        temp.put("typ",ch2_HH.getText());
                        temp.put("stp",Double.valueOf(ch2_3_setpoint.getText().toString()));
                        temp.put("jud",Integer.valueOf("200"));//unit[unitms.getSelectedItemPosition()].getValue()
                        temp.put("hst",ch2_3_hyst.isChecked());

                        events.put(temp);

                        temp=new JSONObject();

                        temp.put("enb",ch2_4_enb.isChecked());
                        temp.put("typ",ch2_LL.getText());
                        temp.put("stp",Double.valueOf(ch2_4_setpoint.getText().toString()));
                        temp.put("jud",Integer.valueOf("200"));//unit[unitms.getSelectedItemPosition()].getValue()
                        temp.put("hst",ch2_4_hyst.isChecked());

                        events.put(temp);
                        obj_ch2.put("events",events);



                       /* main.put("ch1",obj_ch1);
                        main.put("ch2",obj_ch2);*/
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    someEventListener.someEvent(obj_ch1.toString()+"xxc"+obj_ch2.toString());
                    break;
            }

        }
    };

/*
    private SectionsPageAdapter mSectionsPageAdapter;
    public ViewPager mViewPager;
    //TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);
        //TabLayout tt = (TabLayout)view.findViewById(R.id.tab);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("Temperature"));
        //View tabContent = LayoutInflater.from().inflate(R.layout.tab1fragment, null);


        ///TabLayout.Tab tab =tt.getTabAt(0);
        //tab.setCustomView(R.layout.tab1fragment);
        //tt.addTab(tab);
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
*/

}
