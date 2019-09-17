package com.cypress.academy.ble101_robot;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Belal on 10/18/2017.
 */


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;
    public String dev_data;
    //we are storing all the products in a list
    private List<DevData> devDataList;

    public static final String SINGLE_DEV_DATA = "DEV DATA";


    //getting the context and product list with constructor
    public DeviceAdapter(Context mCtx, List<DevData> productList) {
        this.mCtx = mCtx;
        this.devDataList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.device_holer_card, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder,final int position) {
        //getting the product of the specified position
        DevData devData = devDataList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(devData.getMac_address());
        holder.textViewShortDesc.setText(devData.getName());
        holder.textViewRating.setText(String.valueOf(devData.getRssi()));
        holder.textViewPrice.setText(devData.getDev_data());

        //holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(product.getImage()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DevData dev = devDataList.get(position);

                dev_data = dev.getDev_data();
                String id = dev_data.substring(0,4);
                switch(id)
                {
                    case "83bc":
                        final Intent intent = new Intent(mCtx, DeviceDecisions.class);
                        intent.putExtra(SINGLE_DEV_DATA,dev);
                        mCtx.startActivity(intent);
                        break;
                    case "80bc":
                        final Intent intent2 = new Intent(mCtx, DeviceThermoDoor.class);
                        intent2.putExtra(SINGLE_DEV_DATA,dev);
                        mCtx.startActivity(intent2);
                        break;
                    case "8012":
                        final Intent intent3 = new Intent(mCtx, DeviceThermoDoor.class);
                        intent3.putExtra(SINGLE_DEV_DATA,dev);
                        mCtx.startActivity(intent3);
                        break;
                    case "8011":
                        final Intent intent4 = new Intent(mCtx, DeviceDecisions.class);
                        intent4.putExtra(SINGLE_DEV_DATA,dev);
                        mCtx.startActivity(intent4);
                        break;
                    case "8013":
                        final Intent intent5 = new Intent(mCtx,DeviceThermoDoor.class);
                        intent5.putExtra(SINGLE_DEV_DATA,dev);
                        mCtx.startActivity(intent5);
                        default:
                            break;

                }
                if(dev.getManuid()==88)
                {
                    final Intent intent4 = new Intent(mCtx, RepeaterAdvertise.class);
                    intent4.putExtra(SINGLE_DEV_DATA,dev);
                    mCtx.startActivity(intent4);
                }
                if(dev.getManuid()==23038)
                {
                    final Intent intent4 = new Intent(mCtx, DfuActivityMain.class);
                    intent4.putExtra(SINGLE_DEV_DATA,dev);
                    mCtx.startActivity(intent4);
                }
               /* else if(dev.getManuid()==87)
                {
                    final Intent intent3 = new Intent(mCtx, DeviceThermoDoor.class);
                    intent3.putExtra(SINGLE_DEV_DATA,dev);
                    mCtx.startActivity(intent3);
                }*/
                /*final Intent intent = new Intent(mCtx, DeviceDecisions.class);
                intent.putExtra(SINGLE_DEV_DATA,dev);


                mCtx.startActivity(intent);*/
                Toast.makeText(view.getContext(), dev.getDev_data(),Toast.LENGTH_SHORT).show();

            }
        });


    }


    @Override
    public int getItemCount() {
        return devDataList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView imageView;
        public DevData dev;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            //imageView = itemView.findViewById(R.id.imageView);


        }
    }
}