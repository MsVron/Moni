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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;
    private Context context;

    public ExpenseAdapter(Context context) {
        this.context = context;
        this.expenseList = new ArrayList<>();
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.tvType.setText(expense.getType());
        holder.tvDate.setText(expense.getDate());
        holder.tvDescription.setText(expense.getDescription());
        holder.tvAmount.setText(expense.getCurrency() + " " + String.format("%.2f", expense.getAmount()));

        if (expense.getColor() != null) {
            holder.colorIndicator.setBackgroundColor(Color.parseColor(expense.getColor()));
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDate, tvDescription, tvAmount;
        View colorIndicator;

        ExpenseViewHolder(View view) {
            super(view);
            tvType = view.findViewById(R.id.tvType);
            tvDate = view.findViewById(R.id.tvDate);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvAmount = view.findViewById(R.id.tvAmount);
            colorIndicator = view.findViewById(R.id.colorIndicator);
        }
    }
}