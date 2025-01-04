package com.example.moni;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IncomeHistoryFragment extends Fragment implements IncomeAdapter.OnIncomeClickListener {
    private AppDatabase db;
    private SessionManager sessionManager;
    private IncomeAdapter adapter;
    private AutoCompleteTextView categoryFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_history, container, false);
        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());
        setupCategoryFilter(view);
        setupRecyclerView(view);
        loadIncomeHistory(null); // null means all categories
        return view;
    }


    private void setupCategoryFilter(View view) {
        categoryFilter = view.findViewById(R.id.categoryFilterSpinner);
        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        for (IncomeCategory category : IncomeCategory.values()) {
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
            loadIncomeHistory(selectedCategory);
        });
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rvIncome);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new IncomeAdapter(requireContext(), this); // Update this line to pass the listener
        recyclerView.setAdapter(adapter);
    }

    private void loadIncomeHistory(String category) {
        new Thread(() -> {
            List<Income> incomeList;
            if (category == null) {
                incomeList = db.incomeDao().getAllIncome(sessionManager.getUserId());
            } else {
                incomeList = db.incomeDao().getIncomeByCategory(sessionManager.getUserId(), category);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setIncomeList(incomeList));
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIncomeHistory(categoryFilter.getText().toString().equals("All Categories") ?
                null : categoryFilter.getText().toString());
    }

    @Override
    public void onEditClick(Income income) {
        // Handle edit action
        Intent intent = new Intent(requireContext(), AddIncomeActivity.class);
        // Add income details to intent
        intent.putExtra("INCOME_ID", income.getId());
        intent.putExtra("AMOUNT", income.getAmount());
        intent.putExtra("CATEGORY", income.getCategory());
        intent.putExtra("SUBCATEGORY", income.getSubcategory());
        intent.putExtra("DATE", income.getDate());
        intent.putExtra("DESCRIPTION", income.getDescription());
        intent.putExtra("COLOR", income.getColor());
        intent.putExtra("CURRENCY", income.getCurrency());
        intent.putExtra("IS_RECURRING", income.isRecurring());
        intent.putExtra("RECURRING_PERIOD", income.getRecurringPeriod());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Income income) {
        // Show confirmation dialog before deleting
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Income")
                .setMessage("Are you sure you want to delete this income entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the income entry
                    new Thread(() -> {
                        db.incomeDao().delete(income);
                        // Reload the income history on the main thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Income deleted successfully", Toast.LENGTH_SHORT).show();
                                loadIncomeHistory(categoryFilter.getText().toString().equals("All Categories") ?
                                        null : categoryFilter.getText().toString());
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}