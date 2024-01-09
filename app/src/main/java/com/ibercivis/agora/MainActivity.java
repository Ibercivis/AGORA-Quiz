package com.ibercivis.agora;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
                        JSONObject gameJson = response.getJSONObject("game");

                        // Usar Gson para convertir el objeto JSON en un objeto Game
                        //Gson gson = new GsonBuilder()
                        //        .registerTypeAdapter(Question.class, new QuestionDeserializer())
                        //        .create();
                        //Game game = gson.fromJson(gameJson.toString(), Game.class);

                        // Question nextQuestion = null;
                        JSONObject nextQuestionJson = response.getJSONObject("next_question");
                        //nextQuestion = gson.fromJson(nextQuestionJson.toString(), Question.class);

                        // Pasar el objeto Game a ClassicGameActivity
                        Intent intent = new Intent(this, ClassicGameActivity.class);
                        intent.putExtra("GAME_OBJECT", gameJson.toString());  // Convertir el objeto Game a JSON para pasarlo como String
                        intent.putExtra("NEXT_QUESTION", nextQuestionJson.toString());

                        startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Manejar error
            }
        }, error -> {
            // Manejar error, podría ser que no haya juego en curso o un error de red
        });
    }

}