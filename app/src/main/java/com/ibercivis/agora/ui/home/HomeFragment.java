package com.ibercivis.agora.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ibercivis.agora.ClassicGameActivity;
import com.ibercivis.agora.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configura el RecyclerView con el Adapter
        GameModeAdapter gameModeAdapter = new GameModeAdapter();
        gameModeAdapter.setOnGameModeClickListener(new GameModeAdapter.OnGameModeClickListener() {
            @Override
            public void onGameModeClick(int position) {
                if (position == 0) { // Posición 0 para el modo "Classic"
                    Intent intent = new Intent(getActivity(), ClassicGameActivity.class);
                    startActivity(intent);
                }
            }
        });
        binding.recyclerViewGameModes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewGameModes.setAdapter(gameModeAdapter);

        // Observa los cambios en el ViewModel si los hay
        homeViewModel.getText().observe(getViewLifecycleOwner(), newText -> {
            // Actualiza la UI si es necesario
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberar la referencia a binding cuando la vista del fragment se destruye
        binding = null;
    }
}
