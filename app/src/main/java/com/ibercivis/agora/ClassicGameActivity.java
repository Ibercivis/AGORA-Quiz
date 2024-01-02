package com.ibercivis.agora;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClassicGameActivity extends AppCompatActivity implements PauseDialogFragment.PauseDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establece la vista para la actividad del juego "Classic"
        setContentView(R.layout.activity_classic_game);

        // Se crea el array de respuestas y se carga en el adapter
        String[] answers = {"Answer 1", "Answer 2", "Answer 3", "Answer 4"};
        AnswersAdapter answersAdapter = new AnswersAdapter(answers);

        RecyclerView answersRecyclerView = findViewById(R.id.answersRecyclerView);
        answersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        answersRecyclerView.setAdapter(answersAdapter);

        //Asignamos funcionalidad al botón de cerrar
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            // Mostrar el DialogFragment
            new PauseDialogFragment().show(getSupportFragmentManager(), "pauseDialog");
        });
    }

    @Override
    public void onContinueGame() {

    }

    @Override
    public void onQuitGame() {

    }
}
