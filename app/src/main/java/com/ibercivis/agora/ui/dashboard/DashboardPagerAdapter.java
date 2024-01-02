package com.ibercivis.agora.ui.dashboard;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class DashboardPagerAdapter extends FragmentPagerAdapter {

    public DashboardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MasterFragment(); // Va al fragment referente al nivel
            case 1:
                return new UniversalFragment(); // Va al fragment referente al general
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2; // Número total de pestañas
    }
}

