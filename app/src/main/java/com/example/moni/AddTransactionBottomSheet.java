package com.example.moni;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddTransactionBottomSheet extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_transaction, container, false);

        // Setup click listeners
        view.findViewById(R.id.layoutAddIncome).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddIncomeActivity.class));
            dismiss();
        });

        view.findViewById(R.id.layoutAddExpense).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddExpenseActivity.class));
            dismiss();
        });

        return view;
    }
}