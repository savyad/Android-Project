package com.cypress.academy.ble101_robot;

//import android.support.v7.util.DiffUtil;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DevDataDiffCallback extends DiffUtil.Callback
{
    private final List<DevData> mOldDevdataList;
    private final List<DevData> mNewDevdataList;

    public DevDataDiffCallback(List<DevData> mOldDevdataList, List<DevData> mNewDevdataList) {
        this.mOldDevdataList = mOldDevdataList;
        this.mNewDevdataList = mNewDevdataList;
    }

    public List<DevData> getmOldEmployeeList() {
        return mOldDevdataList;
    }

    public List<DevData> getmNewEmployeeList() {
        return mNewDevdataList;
    }



    @Override
    public int getOldListSize() {
        return mOldDevdataList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewDevdataList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldDevdataList.get(oldItemPosition).getMac_address() == mNewDevdataList.get(
                newItemPosition).getMac_address();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        final DevData oldEmployee = mOldDevdataList.get(oldItemPosition);
        final DevData newEmployee = mNewDevdataList.get(newItemPosition);

        return
                oldEmployee.getName().equals(newEmployee.getName());

    }
}
