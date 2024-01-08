package com.ibercivis.agora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.ibercivis.agora.classes.Game;
import com.ibercivis.agora.classes.Question;

public class ClassicGameActivity extends AppCompatActivity implements PauseDialogFragment.PauseDialogListener {

    private TextView questionText;
    private AnswersAdapter answersAdapter;
    private ProgressBar progressBar;
    private TextView questionCount;
    private ImageView checkImage;
    private GameService gameService;
    private int currentGameId;

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

        //Localizamos el contador de preguntas
        questionCount = findViewById(R.id.questionCount);

        //Localizamos la imagen que determina si la pregunta anterior ha sido correcta o incorrecta
        checkImage = findViewById(R.id.checkImage);

        //Localizamos el texto de la pregunta
        questionText = findViewById(R.id.questionText);

        //Localizamos la progressBar
        progressBar = findViewById(R.id.progressBar);

        //Asignamos funcionalidad al botón de cerrar
        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            // Mostrar el DialogFragment
            new PauseDialogFragment().show(getSupportFragmentManager(), "pauseDialog");
        });

        // Comprobar si hay un ID de juego pasado por la actividad anterior
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("GAME_ID")) {
            currentGameId = extras.getInt("GAME_ID");
            // Aquí se debe llamar a un método para reanudar el juego anterior
        } else {
            // Si no hay ID, se comienza un nuevo juego
            startGame();
        }
    }

    private void startGame() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        gameService = new GameService(this);
        gameService.startGame(token, new Response.Listener<Game>() {
            @Override
            public void onResponse(Game game) {
                if (game != null && !game.getQuestions().isEmpty()) {
                    updateUIWithQuestion(game.getQuestions().get(0));
                } else {
                    // Mostrar algún mensaje de error o manejar partidas sin preguntas
                }
            }
        }, error -> {
            // Manejar error
        });
    }

    private void updateUIWithQuestion(Question question) {
        questionText.setText(question.getQuestionText());
        answersAdapter.setAnswers(question.getAnswers().toArray(new String[0]));
        progressBar.setProgress(0);  // Actualizar según la lógica de tu juego
        questionCount.setText("1/5");  // Actualizar según la lógica de tu juego
        checkImage.setVisibility(View.INVISIBLE);  // Ocultar hasta que se responda la pregunta
    }

    @Override
    public void onContinueGame() {

    }

    @Override
    public void onQuitGame() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        gameService.abandonGame(token, currentGameId, response -> {
            // Navegar de vuelta a MainActivity una vez que el juego se haya abandonado con éxito
            Intent intent = new Intent(ClassicGameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Finalizar esta actividad para que el usuario no pueda volver a ella presionando atrás
        }, error -> {
            // Manejar el error, posiblemente mostrar un mensaje al usuario
        });
    }
}
