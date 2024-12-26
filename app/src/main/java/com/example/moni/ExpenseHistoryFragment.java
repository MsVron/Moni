package com.example.moni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseHistoryFragment extends Fragment {
    private AppDatabase db;
    private SessionManager sessionManager;
    private ExpenseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_history, container, false);

        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        setupRecyclerView(view);
        loadExpenseHistory();

        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rvExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(requireContext());
        recyclerView.setAdapter(adapter);
    }

    private void loadExpenseHistory() {
        new Thread(() -> {
            List<Expense> expenseList = db.expenseDao().getAllExpenses(sessionManager.getUserId());
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setExpenseList(expenseList));
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenseHistory();
    }
}