package com.ibercivis.agora.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.ibercivis.agora.R;
import com.ibercivis.agora.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configuración del ViewPager
        DashboardPagerAdapter dashboardPagerAdapter = new DashboardPagerAdapter(getChildFragmentManager());
        binding.viewPager.setAdapter(dashboardPagerAdapter);

        // Vincular el TabLayout con el ViewPager
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        // Configurar los títulos de las pestañas y la personalización de las vistas de las pestañas, si es necesario
        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }

        // Agregar listener para animar el fondo del tab seleccionado
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int newTabIndex = tab.getPosition();
                View newTabView = tab.getCustomView();

                if (newTabView != null) {
                    // animateTabBackground(newTabIndex);
                }

                TextView tabTextView = newTabView.findViewById(R.id.customtab_textview);
                tabTextView.setTextColor(getResources().getColor(R.color.content_secondary)); // Blanco para texto seleccionado
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTextView = tab.getCustomView().findViewById(R.id.customtab_textview);
                tabTextView.setTextColor(getResources().getColor(R.color.content_quaternary)); // Gris para texto no seleccionado
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No need to change backgrounds for reselection as it stays the same
            }
        });

        // Observar el LiveData del ViewModel
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), newText -> {
            // Aquí puedes actualizar tu UI cuando cambia el texto en LiveData
        });

        return root;
    }

    // Método para obtener la vista personalizada del tab
    private View getTabView(int position) {
        // Infla el layout personalizado
        View customTab = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        TextView tabTextView = customTab.findViewById(R.id.customtab_textview);
        switch (position) {
            case 0:
                tabTextView.setText("Master");
                tabTextView.setTextColor(getResources().getColor(R.color.content_secondary));
                break;
            case 1:
                tabTextView.setText("Universal");
                break;
        }

        return customTab;
    }

    // No necesitas el método setupTabLayout() ya que ahora estás usando el ViewPager
    // Resto de tu código...

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

