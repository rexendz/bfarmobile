package com.adzu.bfarmobile.entities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.adzu.bfarmobile.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class OperatorAdapter extends RecyclerView.Adapter<OperatorAdapter.OperatorViewHolder> implements Filterable {

    private OperatorTapListener tapListener;

    private Context ctx;
    private List<FishpondOperator> fishpondOperator;
    private List<FishpondOperator> fishpondOperatorFull;
    private int filterGroup = 0;
    private int statusFilter = 0;

    public interface OperatorTapListener{
        void onItemTap(int position, long fla);
    }

    public OperatorAdapter(Context ctx, List<FishpondOperator> operatorList, OperatorTapListener tapListener) {
        this.ctx = ctx;
        this.fishpondOperator = operatorList;
        this.tapListener = tapListener;
        fishpondOperatorFull = new ArrayList<>(operatorList);
    }

    @NonNull
    @Override
    public OperatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.list_layout, parent, false);
        return new OperatorViewHolder(view, tapListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OperatorViewHolder holder, int position) {
        FishpondOperator operator = fishpondOperator.get(position);
        holder.text_name.setText("Name: " + operator.getFullName());
        holder.text_flanum.setText("FLA #: " + operator.getFla_number());
        holder.text_address.setText("Address: " + operator.getAddress());
        holder.text_opnum.setText("Operator #: " + operator.getSim1());
        if(operator.isIsActive()){
            holder.text_status.setText("Active");
            holder.text_status.setBackgroundResource(R.color.colorActive);
        }
        else{
            holder.text_status.setText("Expired");
            holder.text_status.setBackgroundResource(R.color.colorExpired);
        }
    }

    @Override
    public int getItemCount() {
        return fishpondOperator.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<FishpondOperator> filteredList = new ArrayList<>();

            if((charSequence == null || charSequence.length() == 0 || filterGroup == 0) && statusFilter == 0){
                filteredList.addAll(fishpondOperatorFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(FishpondOperator item : fishpondOperatorFull) {
                    switch(filterGroup){
                        case 0:
                            if ((statusFilter == 1 && item.isIsActive()) || (statusFilter == 2 && !item.isIsActive()))
                                filteredList.add(item);
                        case 1:
                            if ((item.getBarangay().toLowerCase().contains(filterPattern)) && (statusFilter == 0 || (statusFilter == 1 && item.isIsActive()) || (statusFilter == 2 && !item.isIsActive())))
                                filteredList.add(item);
                            break;
                        case 2:
                            if ((String.valueOf(item.getFla_number()).contains(filterPattern)) && (statusFilter == 0 || (statusFilter == 1 && item.isIsActive()) || (statusFilter == 2 && !item.isIsActive())))
                                filteredList.add(item);
                            break;
                        case 3:
                            if ((item.getFullName().toLowerCase().contains(filterPattern)) && (statusFilter == 0 || (statusFilter == 1 && item.isIsActive()) || (statusFilter == 2 && !item.isIsActive())))
                                filteredList.add(item);
                            break;
                        case 4:
                            if ((item.getSim1().contains(filterPattern)) && (statusFilter == 0 || (statusFilter == 1 && item.isIsActive()) || (statusFilter == 2 && !item.isIsActive())))
                                filteredList.add(item);
                            break;
                        case 5:
                            if ((item.getCityProvince().toLowerCase().contains(filterPattern)) && (statusFilter == 0 || (statusFilter == 1 && item.isIsActive()) || (statusFilter == 2 && !item.isIsActive())))
                                filteredList.add(item);
                            break;
                        default:
                            if(statusFilter == 0)
                                filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            fishpondOperator.clear();
            fishpondOperator.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class OperatorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView text_name;
        TextView text_opnum;
        TextView text_flanum;
        TextView text_address;
        TextView text_status;

        OperatorTapListener operatorTapListener;

        public OperatorViewHolder(@NonNull View itemView, OperatorTapListener tapListener) {
            super(itemView);
            this.operatorTapListener = tapListener;
            text_name = itemView.findViewById(R.id.op_name);
            text_status = itemView.findViewById(R.id.op_status);
            text_address = itemView.findViewById(R.id.op_address);
            text_flanum = itemView.findViewById(R.id.op_flanum);
            text_opnum = itemView.findViewById(R.id.op_simnum);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            operatorTapListener.onItemTap(getAdapterPosition(), Long.parseLong(text_flanum.getText().toString().substring(7)));
        }
    }

    public void setFilter(int i, int j){
        this.filterGroup = i;
        this.statusFilter = j;
    }
}
