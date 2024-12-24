package com.example.moni;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
    private List<Income> incomeList;
    private Context context;

    public IncomeAdapter(Context context) {
        this.context = context;
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
        holder.tvType.setText(income.getType());
        holder.tvDate.setText(income.getDate());
        holder.tvDescription.setText(income.getDescription());
        holder.tvAmount.setText(income.getCurrency() + " " + String.format("%.2f", income.getAmount()));

        if (income.getColor() != null) {
            holder.colorIndicator.setBackgroundColor(Color.parseColor(income.getColor()));
        }
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDate, tvDescription, tvAmount;
        View colorIndicator;

        IncomeViewHolder(View view) {
            super(view);
            tvType = view.findViewById(R.id.tvType);
            tvDate = view.findViewById(R.id.tvDate);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvAmount = view.findViewById(R.id.tvAmount);
            colorIndicator = view.findViewById(R.id.colorIndicator);
        }
    }
}