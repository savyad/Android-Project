
package com.cypress.academy.ble101_robot;

import android.content.Context;
import android.content.Intent;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



public class RepeaterDeviceAdapterOld extends RecyclerView.Adapter<RepeaterDeviceAdapterOld.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;
    public String dev_data;
    //we are storing all the products in a list
    private List<RepeaterModel> devDataList;

    public static final String SINGLE_DEV_DATA = "DEV DATA";


    //getting the context and product list with constructor
    public RepeaterDeviceAdapterOld(Context mCtx, List<RepeaterModel> productList) {
        this.mCtx = mCtx;
        this.devDataList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.repeater_dev_data, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder,final int position) {
        //getting the product of the specified position
        RepeaterModel repeat = devDataList.get(position);

        //binding the data with the viewholder views
        holder.mName.setText(repeat.getMacdev());
        holder.mPrice.setText(String.valueOf(repeat.getRssi()));
        holder.imageView.setImageResource(repeat.getImageid());
        //holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(product.getImage()));


    }


    @Override
    public int getItemCount() {
        return devDataList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView mName, mPrice;
        ImageView imageView;
        public RepeaterModel repeat;

        public ProductViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.txtName);
            mPrice = itemView.findViewById(R.id.txtPrice);
            imageView = itemView.findViewById(R.id.led);
            //imageView = itemView.findViewById(R.id.imageView);


        }
    }
}