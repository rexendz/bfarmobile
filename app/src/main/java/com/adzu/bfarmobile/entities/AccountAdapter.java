package com.adzu.bfarmobile.entities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adzu.bfarmobile.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> implements Filterable {

    private AccountListClickListener clickListener;

    public interface AccountListClickListener {
        void leftButtonClick(int position, String button, String username);
        void rightButtonClick(int position, String button, String username);

    }

    private Context ctx;
    private List<Account> account;
    private List<Account> accountFull;
    private int statusFilter = 0;

    public AccountAdapter(Context ctx, List<Account> accountList, AccountListClickListener clickListener) {
        this.clickListener = clickListener;
        this.ctx = ctx;
        this.account = accountList;
        accountFull = new ArrayList<>(accountList);
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.list_layout2, parent, false);
        return new AccountViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = this.account.get(position);
        holder.text_name.setText("Name: " + account.getFirstname() + " " + account.getMiddlename() + " " + account.getLastname());
        holder.text_username.setText("Username: " + account.getUsername());
        holder.account_activated_by.setText(account.getActivated_by());
        holder.account_deactivated_by.setText(account.getDeactivated_by());
        holder.account_madeadmin_by.setText(account.getMade_admin_by());
        holder.account_removedadmin_by.setText(account.getRemoved_admin_by());
        holder.account_fla.setText("FLA #: " + account.getFla_number());
        holder.account_sim1.setText(account.getSim1());
        holder.account_sim2.setText(account.getSim2());

        if(account.getActivated_by().equals("NONE"))
            holder.l1.setVisibility(View.GONE);
        else
            holder.l1.setVisibility(View.VISIBLE);
        if(account.getDeactivated_by().equals("NONE"))
            holder.l2.setVisibility(View.GONE);
        else
            holder.l2.setVisibility(View.VISIBLE);
        if(account.getMade_admin_by().equals("NONE"))
            holder.l3.setVisibility(View.GONE);
        else
            holder.l3.setVisibility(View.VISIBLE);
        if(account.getRemoved_admin_by().equals("NONE"))
            holder.l4.setVisibility(View.GONE);
        else
            holder.l4.setVisibility(View.VISIBLE);
        if(account.getFla_number() < 0)
            holder.account_fla.setVisibility(View.GONE);
        else
            holder.account_fla.setVisibility(View.VISIBLE);
        if(account.getSim1().equals("NONE"))
            holder.l6.setVisibility(View.GONE);
        else
            holder.l6.setVisibility(View.VISIBLE);
        if(account.getSim2().equals("NONE"))
            holder.l7.setVisibility(View.GONE);
        else
            holder.l7.setVisibility(View.VISIBLE);

        if(account.isActivated()){
            holder.text_status1.setText("APPROVED");
            holder.text_status1.setBackgroundResource(R.color.colorActive);
            holder.button_left.setText("DEACTIVATE ACCOUNT");

            if(account.isAdmin()) {
                holder.text_status2.setVisibility(View.VISIBLE);
                holder.button_right.setText("REMOVE AS ADMIN");
            }
            else {
                holder.text_status2.setVisibility(View.INVISIBLE);
                holder.button_right.setText("MAKE ADMIN");
            }
        }
        else{
            holder.text_status2.setVisibility(View.INVISIBLE);
            holder.text_status1.setText("PENDING");
            holder.text_status1.setBackgroundResource(R.color.colorExpired);
            holder.button_left.setText("REMOVE REQUEST");
            holder.button_right.setText("ACTIVATE ACCOUNT");
        }

        if(account.isOperator())
            holder.text_status3.setVisibility(View.VISIBLE);
        else
            holder.text_status3.setVisibility(View.INVISIBLE);


    }

    @Override
    public int getItemCount() {
        return this.account.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Account> filteredList = new ArrayList<>();
            if(statusFilter == 0){
                filteredList.addAll(accountFull);
            } else {
                for(Account item : accountFull) {
                    switch(statusFilter){
                        case 1:
                            if(item.isActivated())
                                filteredList.add(item);
                            break;
                        case 2:
                            if(!item.isActivated())
                                filteredList.add(item);
                            break;
                        case 3:
                            if(item.isAdmin())
                                filteredList.add(item);
                            break;
                        case 4:
                            if(item.isOperator())
                                filteredList.add(item);
                            break;
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            account.clear();
            account.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text_name;
        TextView text_username;
        TextView text_status1; // Approved | Pending
        TextView text_status2; // Admin
        TextView text_status3; // Operator
        Button button_left; // Deactivate | Remove Request
        Button button_right; // Admin | Activate
        AccountListClickListener clickListener;

        TextView account_activated_by, account_madeadmin_by, account_removedadmin_by, account_deactivated_by, account_fla, account_sim1, account_sim2;
        ExpandableLayout expandableLayout;

        LinearLayout l1, l2, l3, l4, l6, l7;

        public AccountViewHolder(@NonNull View itemView, AccountListClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;

            text_name = itemView.findViewById(R.id.acc_name);
            text_username = itemView.findViewById(R.id.acc_username);
            text_status1 = itemView.findViewById(R.id.acc_status);
            text_status2 = itemView.findViewById(R.id.acc_status2);
            text_status3 = itemView.findViewById(R.id.acc_status3);
            button_left = itemView.findViewById(R.id.button_left);
            button_right = itemView.findViewById(R.id.button_right);

            account_activated_by = itemView.findViewById(R.id.account_activated_by);
            account_deactivated_by = itemView.findViewById(R.id.account_deactivated_by);
            account_madeadmin_by = itemView.findViewById(R.id.account_madeadmin_by);
            account_removedadmin_by = itemView.findViewById(R.id.account_removedadmin_by);
            account_fla = itemView.findViewById(R.id.account_fla);
            account_sim1 = itemView.findViewById(R.id.account_sim1);
            account_sim2 = itemView.findViewById(R.id.account_sim2);

            l1 = itemView.findViewById(R.id.activated_container);
            l2 = itemView.findViewById(R.id.deactivated_container);
            l3 = itemView.findViewById(R.id.madeadmin_container);
            l4 = itemView.findViewById(R.id.removedadmin_container);
            l6 = itemView.findViewById(R.id.sim1_container);
            l7 = itemView.findViewById(R.id.sim2_container);

            expandableLayout = itemView.findViewById(R.id.expandable_layout1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!expandableLayout.isExpanded())
                        expandableLayout.expand();
                    else
                        expandableLayout.collapse();
                }
            });

            button_left.setOnClickListener(this);
            button_right.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (view.getId() == button_left.getId())
                clickListener.leftButtonClick(getAdapterPosition(), button_left.getText().toString(), text_username.getText().toString().substring(10));

            if (view.getId() == button_right.getId())
                clickListener.rightButtonClick(getAdapterPosition(), button_right.getText().toString(), text_username.getText().toString().substring(10));

        }

    }

    public void setFilter(int i){
        this.statusFilter = i;
    }
}
