package com.ibercivis.agora;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibercivis.agora.classes.Game;
import com.ibercivis.agora.classes.Question;
import com.ibercivis.agora.databinding.ActivityMainBinding;
import com.ibercivis.agora.serializers.QuestionDeserializer;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForInProgressGame();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        //        .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void checkForInProgressGame() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        new GameService(this).checkForInProgressGame(token, response -> {
            try {
                if (response.has("game")) {  // Verifica si la respuesta contiene el objeto 'game'
                    if (response.has("next_question")) {
                        if (response.has("current_question_index")){
                            JSONObject gameJson = response.getJSONObject("game");
                            JSONObject nextQuestionJson = response.getJSONObject("next_question");
                            int currentQuestionIndex = response.getInt("current_question_index");
                            int correctAnswersCount = response.getInt("correct_answers_count");

                            // Pasar Extras a ClassicGameActivity
                            Intent intent = new Intent(this, ClassicGameActivity.class);
                            intent.putExtra("GAME_OBJECT", gameJson.toString());
                            intent.putExtra("NEXT_QUESTION", nextQuestionJson.toString());
                            intent.putExtra("CURRENT_QUESTION_INDEX", currentQuestionIndex);
                            intent.putExtra("CORRECT_ANSWERS_COUNT", correctAnswersCount);

                            startActivity(intent);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Manejar error
            }
        }, error -> handleError(error));
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}