package com.example.moni;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ExpenseHistoryFragment extends Fragment implements ExpenseAdapter.OnExpenseClickListener {
    private AppDatabase db;
    private SessionManager sessionManager;
    private ExpenseAdapter adapter;
    private AutoCompleteTextView categoryFilter; // Changed to AutoCompleteTextView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_history, container, false);
        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        setupCategoryFilter(view);
        setupRecyclerView(view);
        loadExpenseHistory(null); // Load all expenses initially
        return view;
    }

    private void setupCategoryFilter(View view) {
        categoryFilter = view.findViewById(R.id.categoryFilterSpinner); // Match the ID in layout
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
        categoryFilter.setText("All Categories", false);

        categoryFilter.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedCategory = position == 0 ? null : categories.get(position);
            loadExpenseHistory(selectedCategory);
        });
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rvExpense);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);
    }

    private void loadExpenseHistory(String category) {
        new Thread(() -> {
            List<Expense> expenseList;
            int userId = sessionManager.getUserId(); // Get the user ID

            if (category == null || category.equals("All Categories")) {
                expenseList = db.expenseDao().getAllExpenses(userId);
            } else {
                expenseList = db.expenseDao().getExpensesByCategory(userId, category);
            }

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> adapter.setExpenseList(expenseList));
            }
        }).start();
    }

    @Override
    public void onEditClick(Expense expense) {
        Intent intent = new Intent(requireContext(), AddExpenseActivity.class);
        intent.putExtra("EXPENSE_ID", expense.getId());
        intent.putExtra("EXPENSE_AMOUNT", expense.getAmount());
        intent.putExtra("EXPENSE_CATEGORY", expense.getCategory());
        intent.putExtra("EXPENSE_SUBCATEGORY", expense.getSubcategory());
        intent.putExtra("EXPENSE_DATE", expense.getDate());
        intent.putExtra("EXPENSE_DESCRIPTION", expense.getDescription());
        intent.putExtra("EXPENSE_COLOR", expense.getColor());
        intent.putExtra("EXPENSE_CURRENCY", expense.getCurrency());
        intent.putExtra("EXPENSE_IS_RECURRING", expense.isRecurring());
        intent.putExtra("EXPENSE_RECURRING_PERIOD", expense.getRecurringPeriod());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Expense expense) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        db.expenseDao().delete(expense.getId());
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                loadExpenseHistory(categoryFilter.getText().toString().equals("All Categories") ?
                                        null : categoryFilter.getText().toString());
                                Toast.makeText(requireContext(), "Expense deleted", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenseHistory(categoryFilter.getText().toString().equals("All Categories") ?
                null : categoryFilter.getText().toString());
    }
}