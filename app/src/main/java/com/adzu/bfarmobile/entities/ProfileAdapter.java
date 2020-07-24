package com.adzu.bfarmobile.entities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adzu.bfarmobile.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private Context ctx;
    private List<FishpondRecord> recordList;


    public ProfileAdapter(Context ctx, List<FishpondRecord> recordList) {
        this.ctx = ctx;
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.list_layout3, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        FishpondRecord record = recordList.get(position);
        holder.row_date.setText(TimestampToDate.getDate(record.getTimestamp()));
        holder.row_dolevel.setText(String.valueOf(record.getDo_level()));
        holder.row_temperature.setText(String.valueOf(record.getTemperature()));
        holder.row_ph.setText(String.valueOf(record.getPh_level()));
        holder.row_salinity.setText(String.valueOf(record.getSalinity()));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }



    class ProfileViewHolder extends RecyclerView.ViewHolder {

        TextView row_date, row_ph, row_salinity, row_temperature, row_dolevel;


        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            row_date = itemView.findViewById(R.id.row_date);
            row_ph = itemView.findViewById(R.id.row_ph);
            row_salinity = itemView.findViewById(R.id.row_salinity);
            row_temperature = itemView.findViewById(R.id.row_temperature);
            row_dolevel = itemView.findViewById(R.id.row_dolevel);


        }
    }
}
