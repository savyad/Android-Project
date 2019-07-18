package com.cypress.academy.ble101_robot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.cypress.academy.ble101_robot.RepeaterModel;

import java.util.ArrayList;
import java.util.List;

public class MyDiffUtilCallBack extends DiffUtil.Callback {
    List<RepeaterModel> newList;
    List<RepeaterModel> oldList;

    public MyDiffUtilCallBack(List<RepeaterModel> newList, List<RepeaterModel> oldList) {
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
        return newList.get(newItemPosition).getMacdev()==(oldList.get(oldItemPosition).getMacdev()) ;

    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {

        final RepeaterModel oldRepeater = oldList.get(oldItemPosition);
        final RepeaterModel newRepeater = newList.get(newItemPosition);

        return oldRepeater.getRssi()==(newRepeater.getRssi());

    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        RepeaterModel newModel = newList.get(newItemPosition);
        RepeaterModel oldModel = oldList.get(oldItemPosition);

        Bundle diff = new Bundle();
        if(newModel.getMacdev().equals(oldModel.getMacdev()) ) {
            if (newModel.rssi != (oldModel.rssi)) {
                diff.putString("price", newModel.rssi);
            }
            if (diff.size() == 0) {
                return null;
            }
        }
        return diff;
       // return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}