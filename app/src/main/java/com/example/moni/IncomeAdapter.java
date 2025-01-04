package com.example.moni;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
    private List<Income> incomeList;
    private Context context;
    private OnIncomeClickListener listener;  // Added listener

    public IncomeAdapter(Context context, OnIncomeClickListener listener) {  // Updated constructor
        this.context = context;
        this.listener = listener;
        this.incomeList = new ArrayList<>();
    }

    public void setIncomeList(List<Income> incomeList) {
        this.incomeList = incomeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = incomeList.get(position);

        holder.tvType.setText(income.getCategory());
        holder.tvSubcategory.setText(income.getSubcategory());
        holder.tvDate.setText(income.getDate());
        holder.tvDescription.setText(income.getDescription());
        holder.tvAmount.setText(income.getCurrency() + " " + String.format("%.2f", income.getAmount()));

        // Handle recurring income visibility
        if (income.isRecurring()) {
            holder.tvRecurring.setVisibility(View.VISIBLE);
            holder.tvRecurring.setText("Recurring: " + income.getRecurringPeriod());
        } else {
            holder.tvRecurring.setVisibility(View.GONE);
        }

        // Set color indicator if available
        if (income.getColor() != null) {
            holder.colorIndicator.setBackgroundColor(Color.parseColor(income.getColor()));
        }

        // Set listeners for edit and delete buttons
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(income);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(income);
            }
        });
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvSubcategory, tvDate, tvDescription, tvAmount, tvRecurring;
        View colorIndicator;
        ImageButton btnEdit, btnDelete;

        IncomeViewHolder(View view) {
            super(view);
            tvType = view.findViewById(R.id.tvType);
            tvSubcategory = view.findViewById(R.id.tvSubcategory);
            tvDate = view.findViewById(R.id.tvDate);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvAmount = view.findViewById(R.id.tvAmount);
            tvRecurring = view.findViewById(R.id.tvRecurring);
            colorIndicator = view.findViewById(R.id.colorIndicator);
            btnEdit = view.findViewById(R.id.btnEdit);  // Bind edit button
            btnDelete = view.findViewById(R.id.btnDelete);  // Bind delete button
        }
    }

    public interface OnIncomeClickListener {  // Listener interface
        void onEditClick(Income income);
        void onDeleteClick(Income income);
    }
}
