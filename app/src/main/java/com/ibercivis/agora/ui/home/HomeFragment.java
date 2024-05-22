package com.ibercivis.agora.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.ibercivis.agora.ClassicGameActivity;
import com.ibercivis.agora.R;
import com.ibercivis.agora.SessionManager;
import com.ibercivis.agora.TimeTrialGameActivity;
import com.ibercivis.agora.classes.UserProfile;
import com.ibercivis.agora.databinding.FragmentHomeBinding;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String BASE_URL;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Load user profile
        BASE_URL = getString(R.string.base_url);
        loadUserProfile();

        // Configura el RecyclerView con el Adapter
        GameModeAdapter gameModeAdapter = new GameModeAdapter();
        gameModeAdapter.setOnGameModeClickListener(new GameModeAdapter.OnGameModeClickListener() {
            @Override
            public void onGameModeClick(int position) {
                if (position == 0) { // Posición 0 para el modo "Classic"
                    Intent intent = new Intent(getActivity(), ClassicGameActivity.class);
                    startActivity(intent);
                } else if (position == 1) { // Posición 0 para el modo "Time trial"
                    Intent intent = new Intent(getActivity(), TimeTrialGameActivity.class);
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

    private void loadUserProfile() {
        SessionManager sessionManager = new SessionManager(getContext());
        String token = sessionManager.getToken();
        String username = sessionManager.getUsername();

        String url = BASE_URL + "/api/users/profile/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        int totalPoints = response.getInt("total_points");
                        int totalGamesPlayed = response.getInt("total_games_played");
                        int totalGamesAbandoned = response.getInt("total_games_abandoned");
                        int totalCorrectAnswers = response.getInt("total_correct_answers");
                        int totalIncorrectAnswers = response.getInt("total_incorrect_answers");
                        String profileImageUrl = response.optString("profile_image", null);

                        updateUI(new UserProfile(
                                totalPoints,
                                totalGamesPlayed,
                                totalGamesAbandoned,
                                totalCorrectAnswers,
                                totalIncorrectAnswers,
                                profileImageUrl
                        ), username);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this.getActivity()).add(jsonObjectRequest);
    }

    private void updateUI(UserProfile userProfile, String username) {
        binding.textViewUserName.setText("Hi, " + username);
        binding.userPoints.setText(String.valueOf(userProfile.getTotalPoints()));
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());

        if (!userProfile.getProfileImageUrl().equalsIgnoreCase("null") && !userProfile.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(userProfile.getProfileImageUrl())
                    .apply(requestOptions)
                    .into(binding.imageViewUserAvatar);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_avatar_ranking_yellow)
                    .apply(requestOptions)
                    .into(binding.imageViewUserAvatar);
        }
    }

    private void handleError(VolleyError error) {
        String errorMessage;
        if (error instanceof NetworkError || error instanceof NoConnectionError) {
            errorMessage = "Cannot connect to the server. Please check your internet connection.";
        } else if (error instanceof TimeoutError) {
            errorMessage = "Request timeout. Please try again.";
        } else if (error instanceof ServerError) {
            errorMessage = "Server error. Please try again later.";
        } else if (error instanceof AuthFailureError) {
            errorMessage = "Authentication error. Please log in again.";
        } else {
            errorMessage = "An unknown error occurred. Please try again later.";
        }
        showToast(errorMessage);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberar la referencia a binding cuando la vista del fragment se destruye
        binding = null;
    }
}
