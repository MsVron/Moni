package com.example.moni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ExpenseHistoryFragment extends Fragment {
    private AppDatabase db;
    private SessionManager sessionManager;
    private ExpenseAdapter adapter;
    private AutoCompleteTextView categoryFilter; // Updated to AutoCompleteTextView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_history, container, false);

        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        setupCategoryFilter(view);
        setupRecyclerView(view);
        loadExpenseHistory(null); // null means all categories

        return view;
    }

    private void setupCategoryFilter(View view) {
        categoryFilter = view.findViewById(R.id.categoryFilterSpinner); // Update the ID in your layout
        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        for (ExpenseCategory category : ExpenseCategory.values()) {
            categories.add(category.getCategoryName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
        );

        categoryFilter.setAdapter(adapter);
        categoryFilter.setText("All Categories", false); // Default selection

        categoryFilter.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedCategory = position == 0 ? null : categories.get(position);
            loadExpenseHistory(selectedCategory);
        });
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rvExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(requireContext());
        recyclerView.setAdapter(adapter);
    }

    private void loadExpenseHistory(String category) {
        new Thread(() -> {
            List<Expense> expenseList;
            if (category == null) {
                expenseList = db.expenseDao().getAllExpenses(sessionManager.getUserId());
            } else {
                expenseList = db.expenseDao().getExpensesByCategory(sessionManager.getUserId(), category);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setExpenseList(expenseList));
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenseHistory(null); // Reload all categories when the fragment resumes
    }
}
