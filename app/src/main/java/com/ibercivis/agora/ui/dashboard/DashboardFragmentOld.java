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

public class DashboardFragmentOld extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configuración del TabLayout
        setupTabLayout();

        // Observar el LiveData del ViewModel
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), newText -> {
            // Aquí puedes actualizar tu UI cuando cambia el texto en LiveData
        });

        return root;
    }

    private void setupTabLayout() {
        TabLayout tabLayout = binding.tabLayout;
        String[] tabTitles = new String[]{"Master", "Universal"};

        for (String tabTitle : tabTitles) {
            TabLayout.Tab tab = tabLayout.newTab();
            // Infla el layout personalizado
            View customTab = LayoutInflater.from(tabLayout.getContext()).inflate(R.layout.custom_tab, null);

            // Encuentra el TextView dentro del layout personalizado y establece el texto
            TextView tabTextView = customTab.findViewById(R.id.customtab_textview);
            tabTextView.setTextColor(getResources().getColor(R.color.content_quaternary)); // Gris para texto no seleccionado
            tabTextView.setText(tabTitle);

            // No más configuración de fondo aquí, ya que se define en custom_tab.xml
            tab.setCustomView(customTab);
            tabLayout.addTab(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().setSelected(true);
                TextView selectedText = tab.getCustomView().findViewById(R.id.customtab_textview);
                selectedText.setTextColor(getResources().getColor(R.color.content_secondary));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().setSelected(false);
                TextView selectedText = tab.getCustomView().findViewById(R.id.customtab_textview);
                selectedText.setTextColor(getResources().getColor(R.color.content_quaternary));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No need to change backgrounds for reselection as it stays the same
            }
        });

        // Establecer el fondo inicial para la pestaña seleccionada y no seleccionada
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
        TextView levelText = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.customtab_textview);
        levelText.setTextColor(getResources().getColor(R.color.content_secondary));
        tabLayout.getTabAt(1).getCustomView().setSelected(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
