package com.example.moni;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HistoryPagerAdapter extends FragmentStateAdapter {

    public HistoryPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new IncomeHistoryFragment() : new ExpenseHistoryFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}