package com.cypress.academy.ble101_robot;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SettingFragment extends Fragment {
    public ViewPager viewPager;
    public ViewPagerAdapter viewPagerAdapter;
    public EditText dd;
    public String ssas;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public LinearLayout contain;
    Bundle savedArgs = new Bundle();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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


        dd=(EditText)retView.findViewById(R.id.ch1_name);
        dd.setText(ssas);
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
