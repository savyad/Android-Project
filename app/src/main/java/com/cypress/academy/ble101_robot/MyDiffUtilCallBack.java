package com.cypress.academy.ble101_robot;

import android.os.Bundle;
/*import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;*/

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cypress.academy.ble101_robot.RepeaterModel;

import java.util.ArrayList;
import java.util.List;

public class MyDiffUtilCallBack extends DiffUtil.Callback {
    ArrayList<RepeaterModel> newList;
    ArrayList<RepeaterModel> oldList;

    public MyDiffUtilCallBack(ArrayList<RepeaterModel> newList, ArrayList<RepeaterModel> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

        if(newList.get(newItemPosition).getMacdev().equals(oldList.get(oldItemPosition).getMacdev()))
        {
           // Log.d("itemsame","in same");
            return true;
        }
        else {
           // Log.d("itemsame", "not same");
        }
        return false;

    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        //System.out.println("in content same");

        final RepeaterModel oldRepeater = oldList.get(oldItemPosition);
        final RepeaterModel newRepeater = newList.get(newItemPosition);

        if(oldRepeater.getRssi()!=(newRepeater.getRssi()))
        {
            //Log.d("item contenets","content different");
            return false;
        }
        //Log.d("item contenets","content same");
        return true;

    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        RepeaterModel newModel = newList.get(newItemPosition);
        RepeaterModel oldModel = oldList.get(oldItemPosition);
        //System.out.println("getchange");

        Bundle diff = new Bundle();
        //if (newModel.getMacdev().equals(oldModel.getMacdev()))
        //{
            //System.out.println("getchange");

            if (newModel.getRssi()!=(oldModel.getRssi())) {
                diff.putInt("price", newModel.getRssi());
            }
                if (diff.size() == 0) {
                    return null;
                }


       // }
        return diff;
        //return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}