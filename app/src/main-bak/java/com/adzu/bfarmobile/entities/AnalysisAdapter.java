package com.adzu.bfarmobile.entities;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adzu.bfarmobile.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ProfileViewHolder> {

    private Context ctx;
    private List<DataAnalysis> dataAnalysis;

    public AnalysisAdapter(Context ctx, List<DataAnalysis> dataAnalysis) {
        this.ctx = ctx;
        this.dataAnalysis = dataAnalysis;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.list_layout4, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        DataAnalysis analysis = dataAnalysis.get(position);
        holder.warning.setText(analysis.getWarning());
        holder.suggestion.setText(analysis.getSuggestion());
    }

    @Override
    public int getItemCount() {
    return dataAnalysis.size();
    }



    class ProfileViewHolder extends RecyclerView.ViewHolder {

        TextView warning, suggestion;
        ExpandableLayout expandableLayout;


        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            warning = itemView.findViewById(R.id.data_warning);
            suggestion = itemView.findViewById(R.id.data_suggestion);
            expandableLayout = itemView.findViewById(R.id.expandable_layout4);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!expandableLayout.isExpanded())
                        expandableLayout.expand();
                    else
                        expandableLayout.collapse();
                }
            });



        }
    }
}
