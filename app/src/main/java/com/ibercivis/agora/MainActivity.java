package com.ibercivis.agora;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ibercivis.agora.databinding.ActivityMainBinding;

import org.json.JSONException;

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
                if (response.has("id")) {  // Asumiendo que la API devuelve el ID del juego en curso si existe
                    int gameId = response.getInt("id");
                    Intent intent = new Intent(this, ClassicGameActivity.class);
                    intent.putExtra("GAME_ID", gameId);
                    startActivity(intent);
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