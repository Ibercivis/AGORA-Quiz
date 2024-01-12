package com.ibercivis.agora.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ibercivis.agora.LoginActivity;
import com.ibercivis.agora.MainActivity;
import com.ibercivis.agora.R;
import com.ibercivis.agora.SessionManager;
import com.ibercivis.agora.classes.UserProfile;
import com.ibercivis.agora.databinding.FragmentNotificationsBinding;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private String BASE_URL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BASE_URL = getString(R.string.base_url);

        // Cargar los datos del perfil de usuario
        loadUserProfile();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí pones la lógica que debe ocurrir cuando se haga clic en el botón
                logoutUser();
            }
        });
    }

    private void loadUserProfile() {
        SessionManager sessionManager = new SessionManager(getContext());
        String token = sessionManager.getToken();
        String username = sessionManager.getUsername();

        // Aquí, reemplaza YOUR_API_ENDPOINT con el endpoint correcto de tu API
        String url = BASE_URL + "/api/users/profile/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Aquí, reemplaza las claves JSON con las claves reales de tu respuesta de API
                        int totalPoints = response.getInt("total_points");
                        int totalGamesPlayed = response.getInt("total_games_played");
                        int totalGamesAbandoned = response.getInt("total_games_abandoned");
                        int totalCorrectAnswers = response.getInt("total_correct_answers");
                        int totalIncorrectAnswers = response.getInt("total_incorrect_answers");

                        // Actualiza la UI
                        updateUI(new UserProfile(
                                totalPoints,
                                totalGamesPlayed,
                                totalGamesAbandoned,
                                totalCorrectAnswers,
                                totalIncorrectAnswers
                        ), username);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Manejar el error
                    }
                },
                error -> {
                    // Manejar el error
                    handleError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token);
                return headers;
            }
        };

        // Añadir la solicitud al RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    private void updateUI(UserProfile userProfile, String username) {
        // Este método actualiza los TextViews con la información del perfil del usuario
        binding.tvUsername.setText(username);
        binding.tvPoints.setText(String.format(Locale.getDefault(), "%d Points", userProfile.getTotalPoints()));
        binding.ptCorrectAnswers.setText(String.valueOf(userProfile.getTotalCorrectAnswers()));
        binding.ptIncorrectAnswers.setText(String.valueOf(userProfile.getTotalIncorrectAnswers()));
        binding.ptTotalQuestions.setText(String.valueOf(userProfile.getTotalGamesPlayed()));
        binding.ptTotalGames.setText(String.valueOf(userProfile.getTotalGamesPlayed() - userProfile.getTotalGamesAbandoned()));
        binding.ptTotalPoints.setText(String.valueOf(userProfile.getTotalPoints()));
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

    private void logoutUser() {
        SessionManager sessionManager = new SessionManager(getContext());
        sessionManager.logoutUser();
        // Navegar a LoginActivity
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish(); // Finaliza la Activity que contiene este Fragment
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpiar referencias para evitar memory leaks
        binding = null;
    }
}
