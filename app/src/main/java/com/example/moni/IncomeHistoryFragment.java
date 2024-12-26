package com.example.moni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IncomeHistoryFragment extends Fragment {
    private AppDatabase db;
    private SessionManager sessionManager;
    private IncomeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_history, container, false);

        db = AppDatabase.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        setupRecyclerView(view);
        loadIncomeHistory();

        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rvIncome);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new IncomeAdapter(requireContext());
        recyclerView.setAdapter(adapter);
    }

    private void loadIncomeHistory() {
        new Thread(() -> {
            List<Income> incomeList = db.incomeDao().getAllIncome(sessionManager.getUserId());
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setIncomeList(incomeList));
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIncomeHistory();
    }
}