package com.ibercivis.agora.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibercivis.agora.R;

import java.util.ArrayList;
import java.util.List;

public class MasterFragment extends Fragment {

    private RecyclerView rankingRecyclerView;
    private RankingAdapter rankingAdapter;
    private List<RankingItem> rankingItems;

    public MasterFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_master, container, false);

        // Inicializa la lista de items de ranking
        rankingItems = new ArrayList<>();
        // Aquí agrega tus items al rankingItems, por ejemplo:
        rankingItems.add(new RankingItem(4, "User 4", 9800, R.drawable.ic_avatar_ranking_green));
        rankingItems.add(new RankingItem(5, "User 5", 8000, R.drawable.ic_avatar_ranking_yellow));
        rankingItems.add(new RankingItem(6, "User 6", 7000, R.drawable.ic_avatar_ranking_orange));
        rankingItems.add(new RankingItem(7, "User 7", 6000, R.drawable.ic_avatar_ranking_green));
        rankingItems.add(new RankingItem(8, "User 8", 5000, R.drawable.ic_avatar_ranking_yellow));
        rankingItems.add(new RankingItem(9, "User 9", 4000, R.drawable.ic_avatar_ranking_orange));
        // ... más items

        // Inicializar el RecyclerView
        rankingRecyclerView = rootView.findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Inicializar el RankingAdapter con la lista de items
        rankingAdapter = new RankingAdapter(rankingItems);

        // Establecer el adaptador en el RecyclerView
        rankingRecyclerView.setAdapter(rankingAdapter);

        return rootView;
    }
}

