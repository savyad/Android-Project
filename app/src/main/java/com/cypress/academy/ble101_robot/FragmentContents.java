package com.cypress.academy.ble101_robot;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentContents.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentContents#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentContents extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public final String USER = "saved_user";
    private String mUser;
    private EditText etUser;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String sss;
    private OnFragmentInteractionListener mListener;

    private EditText vstup;
    private Bundle savedState = null;

    private  View v;
    public FragmentContents() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentContents.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentContents newInstance(String param1, String param2) {
        FragmentContents fragment = new FragmentContents();
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
        //View v=inflater.inflate(R.layout.fragment_fragment_contents, container, false);
        etUser = (EditText) v.findViewById(R.id.off_ch1);
        if(v==null)
        {

            v=inflater.inflate(R.layout.fragment_fragment_contents, container, false);
        }
        etUser = (EditText) v.findViewById(R.id.off_ch1);
        if (getArguments().getString(USER) != null){
            mUser=getArguments().getString(USER);
        }
        if (mUser != null){
            etUser.setText(mUser);
        }
        return v; //inflater.inflate(R.layout.fragment_fragment_contents, container, false);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        EditText ss =(EditText)v.findViewById(R.id.off_ch1);
         sss=ss.getText().toString();
        outState.putString("name", sss);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //setRetainInstance(true);
       // sss = savedInstanceState.getString("name");
        //EditText ss =(EditText)v.findViewById(R.id.editText);
       // ss.setText(sss);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
