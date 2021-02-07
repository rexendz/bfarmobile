package com.adzu.bfarmobile.entities;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adzu.bfarmobile.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private ProfileTapListener tapListener;
    private Context ctx;
    private List<FishpondRecord> recordList;
    private int selectedPos = RecyclerView.NO_POSITION;

    public interface ProfileTapListener {
        void onItemTap(int position, long timestamp, float temp, float ph, float sal, float DO);
    }


    public ProfileAdapter(Context ctx, List<FishpondRecord> recordList, ProfileTapListener tapListener) {
        this.ctx = ctx;
        this.tapListener = tapListener;
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.list_layout3, parent, false);
        return new ProfileViewHolder(view, tapListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        FishpondRecord record = recordList.get(position);
        short a = RecordData.checkTemp(record.getTemperatureCelsius());
        short b = RecordData.checkPh(record.getPh_level());
        short c = RecordData.checkSalinity(record.getSalinity());
        short d = RecordData.checkDo(record.getDo_level());
        if (a != -1)
            holder.row_temperature.setTextColor(Color.RED);
        else
            holder.row_temperature.setTextColor(Color.BLACK);
        if (b != -1)
            holder.row_ph.setTextColor(Color.RED);
        else
            holder.row_ph.setTextColor(Color.BLACK);
        if (c != -1)
            holder.row_salinity.setTextColor(Color.RED);
        else
            holder.row_salinity.setTextColor(Color.BLACK);
        if (d != -1)
            holder.row_dolevel.setTextColor(Color.RED);
        else
            holder.row_dolevel.setTextColor(Color.BLACK);

        holder.itemView.setBackgroundColor(selectedPos == position ? Color.GREEN : Color.TRANSPARENT); ;
        holder.row_date.setText(TimestampToDate.getDate(record.getTimestamp()));
        holder.row_dolevel.setText(String.format("%.2f", record.getDo_level()));
        holder.row_temperature.setText(String.format("%.2f", record.getTemperatureCelsius()));
        holder.row_ph.setText(String.valueOf(record.getPh_level()));
        holder.row_salinity.setText(String.valueOf(record.getSalinity()));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }


    class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView row_date, row_ph, row_salinity, row_temperature, row_dolevel;
        View itemView;

        ProfileTapListener tapListener;

        public ProfileViewHolder(@NonNull View itemView, ProfileTapListener tapListener) {
            super(itemView);
            this.itemView = itemView;
            this.tapListener = tapListener;
            row_date = itemView.findViewById(R.id.row_date);
            row_ph = itemView.findViewById(R.id.row_ph);
            row_salinity = itemView.findViewById(R.id.row_salinity);
            row_temperature = itemView.findViewById(R.id.row_temperature);
            row_dolevel = itemView.findViewById(R.id.row_dolevel);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedDate = dateFormat.parse(row_date.getText().toString());
                tapListener.onItemTap(getAdapterPosition(), parsedDate.getTime(), Float.parseFloat(row_temperature.getText().toString()), Float.parseFloat(row_ph.getText().toString()), Float.parseFloat(row_salinity.getText().toString()), Float.parseFloat(row_dolevel.getText().toString()));
                notifyItemChanged(selectedPos);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);
            } catch (Exception e) { //this generic but you can control another types of exception
                e.printStackTrace();
            }
        }
    }
}
