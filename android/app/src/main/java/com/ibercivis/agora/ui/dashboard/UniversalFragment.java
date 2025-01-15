package com.ibercivis.agora.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.ibercivis.agora.GameService;
import com.ibercivis.agora.R;
import com.ibercivis.agora.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UniversalFragment extends Fragment {

    private RecyclerView rankingRecyclerView;
    private RankingAdapter rankingAdapter;
    private List<RankingItem> rankingItems;

    private ImageView avatarFirstPlace, avatarSecondPlace, avatarThirdPlace;
    private TextView usernameFirstPlace, usernameSecondPlace, usernameThirdPlace;
    private TextView scoreFirstPlace, scoreSecondPlace, scoreThirdPlace;
    private String BASE_URL;

    public UniversalFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_universal, container, false);

        // Inicializa la lista de items de ranking
        rankingItems = new ArrayList<>();

        // Inicializar las vistas del podio
        avatarFirstPlace = rootView.findViewById(R.id.avatarFirstPlace);
        usernameFirstPlace = rootView.findViewById(R.id.usernameFirstPlace);
        scoreFirstPlace = rootView.findViewById(R.id.scoreFirstPlace);

        avatarSecondPlace = rootView.findViewById(R.id.avatarSecondPlace);
        usernameSecondPlace = rootView.findViewById(R.id.usernameSecondPlace);
        scoreSecondPlace = rootView.findViewById(R.id.scoreSecondPlace);

        avatarThirdPlace = rootView.findViewById(R.id.avatarThirdPlace);
        usernameThirdPlace = rootView.findViewById(R.id.usernameThirdPlace);
        scoreThirdPlace = rootView.findViewById(R.id.scoreThirdPlace);

        BASE_URL = getContext().getString(R.string.base_url);

        // Inicializar el RecyclerView
        rankingRecyclerView = rootView.findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Inicializar el RankingAdapter con la lista de items
        rankingAdapter = new RankingAdapter(rankingItems, BASE_URL);

        // Establecer el adaptador en el RecyclerView
        rankingRecyclerView.setAdapter(rankingAdapter);

        // Obtener los datos del ranking del servidor
        fetchRankingData();

        return rootView;
    }

    private void fetchRankingData() {
        // Llamar al servicio para obtener los datos del ranking
        SessionManager sessionManager = new SessionManager(getContext());
        String token = sessionManager.getToken();

        GameService gameService = new GameService(getActivity());
        gameService.getRankings(token, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray classicRankings = response.getJSONArray("time_trial");
                    for (int i = 0; i < classicRankings.length(); i++) {
                        JSONObject user = classicRankings.getJSONObject(i);
                        String username = user.getString("username");
                        int score = user.getInt("max_time_trial_time");
                        String profileImageUrl = user.optString("profile_image_url", null);

                        if (i == 0) {
                            // Primer lugar
                            updatePodium(avatarFirstPlace, usernameFirstPlace, scoreFirstPlace, username, score, profileImageUrl);
                        } else if (i == 1) {
                            // Segundo lugar
                            updatePodium(avatarSecondPlace, usernameSecondPlace, scoreSecondPlace, username, score, profileImageUrl);
                        } else if (i == 2) {
                            // Tercer lugar
                            updatePodium(avatarThirdPlace, usernameThirdPlace, scoreThirdPlace, username, score, profileImageUrl);
                        } else {
                            // Resto del ranking
                            rankingItems.add(new RankingItem(i + 1, username, score, profileImageUrl,true));
                        }
                    }
                    rankingAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    private void updatePodium(ImageView avatarView, TextView usernameView, TextView scoreView, String username, int score, String profileImageUrl) {
        usernameView.setText(username);
        scoreView.setText(formatTime(score));

        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());
        if (profileImageUrl != null && !profileImageUrl.isEmpty() && profileImageUrl != "null") {
            Glide.with(this)
                    .load(BASE_URL + profileImageUrl)
                    .apply(requestOptions)
                    .into(avatarView);
        } else {
            avatarView.setImageResource(R.drawable.ic_avatar_ranking_green);
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
