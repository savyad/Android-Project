package com.cypress.academy.ble101_robot;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReapeaterDeviceAdapter extends RecyclerView.Adapter<ReapeaterDeviceAdapter.CryptoViewHolder> {

    private ArrayList<RepeaterModel> data;

    public class CryptoViewHolder extends RecyclerView.ViewHolder {

        private TextView mName, mPrice;

        public CryptoViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.txtName);
            mPrice = itemView.findViewById(R.id.txtPrice);
        }
    }

    public ReapeaterDeviceAdapter(ArrayList<RepeaterModel> data) {
        this.data = data;
    }

    @Override
    public CryptoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.repeater_dev_data,parent, false);
        return new CryptoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CryptoViewHolder holder, int position) {
        holder.mName.setText(data.get(position).macdev);
        holder.mPrice.setText(String.valueOf(data.get(position).rssi));
    }

    @Override
    public void onBindViewHolder(CryptoViewHolder holder, int position, List<Object> payloads) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Bundle o = (Bundle) payloads.get(0);

            for (String key : o.keySet()) {
                if (key.equals("price")) {
                    holder.mName.setText(data.get(position).macdev);
                    holder.mPrice.setText(String.valueOf(data.get(position).rssi));
                    //holder.mPrice.setTextColor(Color.GREEN);
                    //this.notifyItemChanged(position);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<RepeaterModel> getData() {
        return data;
    }

    public void setData(ArrayList<RepeaterModel> newData) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallBack(newData, data));
        data.clear();
        this.data.addAll(newData);
        diffResult.dispatchUpdatesTo(this);

        //this.notifyDataSetChanged();
    }


}
