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

public class IncomeHistoryFragment extends Fragment {
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
        adapter = new IncomeAdapter(requireContext());
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
}