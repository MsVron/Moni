package com.example.moni;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionManagerAdapter extends RecyclerView.Adapter<SubscriptionManagerAdapter.ViewHolder> {
    private List<SubscriptionPlan> plans;
    private Context context;
    private OnPlanActionListener listener;

    public interface OnPlanActionListener {
        void onEditPlan(SubscriptionPlan plan);
        void onDeletePlan(SubscriptionPlan plan);
    }

    public SubscriptionManagerAdapter(Context context, OnPlanActionListener listener) {
        this.context = context;
        this.listener = listener;
        this.plans = new ArrayList<>();
    }

    public void setPlans(List<SubscriptionPlan> plans) {
        this.plans = plans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subscription_plan_manager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubscriptionPlan plan = plans.get(position);

        holder.tvTitle.setText(plan.getTitle());
        holder.tvPrice.setText(String.format("$%.2f", plan.getPrice()));
        holder.tvDescription.setText(plan.getDescription());

        holder.btnEdit.setOnClickListener(v -> listener.onEditPlan(plan));
        holder.btnDelete.setOnClickListener(v -> listener.onDeletePlan(plan));
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice, tvDescription;
        MaterialButton btnEdit, btnDelete;

        ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvPlanTitle);
            tvPrice = view.findViewById(R.id.tvPlanPrice);
            tvDescription = view.findViewById(R.id.tvPlanDescription);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}