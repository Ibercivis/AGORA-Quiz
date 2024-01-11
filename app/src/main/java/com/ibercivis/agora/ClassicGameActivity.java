package com.ibercivis.agora;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibercivis.agora.classes.Game;
import com.ibercivis.agora.classes.Question;
import com.ibercivis.agora.serializers.QuestionDeserializer;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

public class ClassicGameActivity extends AppCompatActivity implements PauseDialogFragment.PauseDialogListener, GameCompleteDialogFragment.GameCompleteDialogListener {

    private RelativeLayout headerClassic, headerCorrect, headerIncorrect;
    private TextView questionText;
    private AnswersAdapter answersAdapter;
    private ProgressBar progressBar;
    private TextView questionCount;
    private TextView txtCurrentQuestionIndex;
    private ImageView checkImage;
    private GameService gameService;
    private int currentGameId;
    private int currentQuestionIndex = 1;
    private int totalQuestions = 9;
    private int correctAnswersCount = 0;
    private int lastCorrectAnswersCount = 0;
    private int selectedAnswer;
    private Question currentQuestion;
    private Game game;
    private List<Question> remainingQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establece la vista para la actividad del juego "Classic"
        setContentView(R.layout.activity_classic_game);

        gameService = new GameService(this);
        // Se crea el array de respuestas y se carga en el adapter
        String[] answers = {"Answer 1", "Answer 2", "Answer 3", "Answer 4"};
        answersAdapter = new AnswersAdapter(new String[0], this::onAnswerSelected);

        RecyclerView answersRecyclerView = findViewById(R.id.answersRecyclerView);
        answersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        answersRecyclerView.setAdapter(answersAdapter);

        //Localizamos las cabeceras
        headerClassic = findViewById(R.id.headerClassic);
        headerCorrect = findViewById(R.id.headerCorrect);
        headerIncorrect = findViewById(R.id.headerIncorrect);
        //Localizamos el contador de preguntas
        questionCount = findViewById(R.id.questionCount);
        txtCurrentQuestionIndex = findViewById(R.id.txtCurrentQuestionIndex);

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
            showPauseDialog();
        });

        // Comprobar si hay un objeto game pasado por la actividad anterior
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("GAME_OBJECT")) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Question.class, new QuestionDeserializer())
                    .create();
            String gameJson = getIntent().getStringExtra("GAME_OBJECT");
            if (gameJson != null) {
                game = gson.fromJson(gameJson, Game.class);
                String nextQuestionJson = getIntent().getStringExtra("NEXT_QUESTION");
                currentQuestionIndex = getIntent().getIntExtra("CURRENT_QUESTION_INDEX", 1);
                currentGameId = game.getId();
                correctAnswersCount = getIntent().getIntExtra("CORRECT_ANSWERS_COUNT", 0);
                Log.w("Game recibido del intent: ", gameJson.toString());
                Log.w("Question recibido del intent: ", nextQuestionJson.toString());
                if (nextQuestionJson != null) {
                    Question nextQuestion = gson.fromJson(nextQuestionJson, Question.class);
                    updateUIWithQuestion(nextQuestion, currentQuestionIndex, correctAnswersCount);
                }
            }
        } else {
            startGame();
        }
    }

    private void startGame() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        gameService = new GameService(this);
        gameService.startGame(token, response -> {
            try {
                if (response.has("game")) {  // Verifica si la respuesta contiene el objeto 'game'
                    if (response.has("next_question")) {
                        if (response.has("current_question_index")){
                            // Configurar gson para que utilize el deserializador personalizado para las Question
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(Question.class, new QuestionDeserializer())
                                    .create();
                            // Crear la instancia de game desde la respuesta
                            game = gson.fromJson(String.valueOf(response.getJSONObject("game")), Game.class);
                            // Setear el valor de currentGameId
                            currentGameId = game.getId();
                            // Crear la instancia de nextquestion desde la respuesta
                            Question nextQuestion = gson.fromJson(String.valueOf(response.getJSONObject("next_question")), Question.class);
                            // Crear la instancia de currentQuestionIndex desde la respuesta (que siempre debería ser = 1)
                            int currentQuestionIndex = response.getInt("current_question_index");
                            // Setear el valor de correctAnswersCount
                            correctAnswersCount = response.getInt("correct_answers_count");
                            // Actualizar la interfaz con la siguiente pregunta
                            updateUIWithQuestion(nextQuestion, currentQuestionIndex, correctAnswersCount);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Manejar error
            }
        }, error -> handleError(error));
    }

    private void updateUIWithQuestion(Question question, int currentQuestionIndex, int correctAnswersCount) {
        currentQuestion = question;
        questionText.setText(question.getQuestionText());
        answersAdapter.setAnswers(question.getAnswers().toArray(new String[0]));

        this.currentQuestionIndex = currentQuestionIndex; // Actualiza el índice actual
        //questionCount.setText(String.format("%d/%d", currentQuestionIndex, totalQuestions));
        //progressBar.setProgress(calculateProgress());
        animateQuestionCount(correctAnswersCount);
        animateProgressBar(calculateProgress());
        animateQuestionIndex(currentQuestionIndex);

        // Restablecer el estado del adaptador
        answersAdapter.setAnswerState(-2, -2);
    }

    private int calculateProgress() {
        return (int) ((float) ( currentQuestionIndex - 1 )/ totalQuestions * 100);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void onAnswerSelected(int answerIndex) {
        if (currentQuestion != null) {
            selectedAnswer = answerIndex + 1; // Asumiendo que las respuestas están indexadas desde 1
            sendAnswerToServer(selectedAnswer);
        }
    }

    private void sendAnswerToServer(int selectedAnswer) {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        try {
            gameService.answerQuestion(token, currentGameId, currentQuestion.getId(), selectedAnswer, response -> {
                handleAnswerResponse(response);
            }, this::handleError);
        } catch (JSONException e) {
            e.printStackTrace();
            // Manejar error JSON
        }
    }

    private void handleAnswerResponse(JSONObject response) {
        // Analizar la respuesta y determinar si la respuesta fue correcta o no
        // También verifica si el juego ha terminado o no
        try {
            boolean correct = response.getBoolean("correct");
            int score = response.getInt("score");
            String status = response.getString("status");
            int currentQuestionIndex = response.getInt("current_question_index");
            correctAnswersCount = response.getInt("correct_answers_count");

            // Seteamos el valor de score
            game.setScore(score);

            // Muestra la animación correspondiente basada en si la respuesta es correcta o incorrecta
            if (correct) {
                answersAdapter.setAnswerState(currentQuestion.getCorrectAnswer(), currentQuestion.getCorrectAnswer());
                showCorrectAnswer(response, currentQuestionIndex, correctAnswersCount);
            } else {
                answersAdapter.setAnswerState(currentQuestion.getCorrectAnswer(), selectedAnswer);
                showIncorrectAnswer(response, currentQuestionIndex, correctAnswersCount);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            // Manejar error JSON
        }
    }

    private void finishGame() {
        // Mostrar mensaje de finalización y navegar a MainActivity
        showToast("Game completed!");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadNextQuestion(JSONObject response, int currentQuestionIndex, int correctAnswersCount) throws JSONException {
        // Cargar la próxima pregunta del JSON y actualizar la UI
        headerCorrect.setVisibility(View.INVISIBLE);
        headerIncorrect.setVisibility(View.INVISIBLE);
        headerClassic.setVisibility(View.VISIBLE);
        // Suponiendo que el JSON contiene la próxima pregunta
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Question.class, new QuestionDeserializer())
                .create();
        Question nextQuestion = gson.fromJson(response.getJSONObject("next_question").toString(), Question.class);
        updateUIWithQuestion(nextQuestion, currentQuestionIndex, correctAnswersCount);
    }

    private void showCorrectAnswer(JSONObject response, int currentQuestionIndex, int correctAnswersCount) {
        // Hacemos invisible la cabecera de pregunta y hacemos visible la cabecera de respuesta correcta
        headerClassic.setVisibility(View.INVISIBLE);
        headerCorrect.setVisibility(View.VISIBLE);
        try {
            boolean correct = response.getBoolean("correct");
            int score = response.getInt("score");
            String status = response.getString("status");

            if (status.equals("completed")) {
                // Agrega una pequeña pausa antes de finalizar la partida
                new Handler().postDelayed(() -> showGameCompleteDialog(), 2000); // Espera 2 segundos
            } else {
                // Agrega una pequeña pausa antes de cargar la siguiente pregunta
                new Handler().postDelayed(() -> {
                    try {
                        loadNextQuestion(response, currentQuestionIndex, correctAnswersCount);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, 2000); // Espera 2 segundos
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Manejar error JSON
        }
    }

    private void showIncorrectAnswer(JSONObject response, int currentQuestionIndex, int correctAnswersCount) {
        // Hacemos invisible la cabecera de pregunta y hacemos visible la cabecera de respuesta correcta
        headerClassic.setVisibility(View.INVISIBLE);
        headerIncorrect.setVisibility(View.VISIBLE);

        try {
            boolean correct = response.getBoolean("correct");
            int score = response.getInt("score");
            String status = response.getString("status");

            if (status.equals("completed")) {
                // Agrega una pequeña pausa antes de finalizar la partida
                new Handler().postDelayed(() -> showGameCompleteDialog(), 2000); // Espera 2 segundos
            } else {
                // Agrega una pequeña pausa antes de cargar la siguiente pregunta
                new Handler().postDelayed(() -> {
                    try {
                        loadNextQuestion(response, currentQuestionIndex, correctAnswersCount);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, 2000); // Espera 2 segundos
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Manejar error JSON
        }
    }

    private void animateProgressBar(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
        animation.setDuration(500); // Duración en milisegundos
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void animateQuestionCount(final int currentQuestionIndex) {
        final int startValue = lastCorrectAnswersCount;
        final int endValue = currentQuestionIndex;
        lastCorrectAnswersCount = currentQuestionIndex;

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(500); // Duración en milisegundos

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                questionCount.setText(String.format("%d", animatedValue));
            }
        });

        animator.start();
    }

    private void animateQuestionIndex(final int currentQuestionIndex) {
        final int startValue = (currentQuestionIndex - 1);
        final int endValue = currentQuestionIndex;

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(500); // Duración en milisegundos

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                txtCurrentQuestionIndex.setText(String.format("%d of %d questions", animatedValue, totalQuestions));
            }
        });

        animator.start();
    }

    private void showPauseDialog() {
        PauseDialogFragment pauseDialogFragment = PauseDialogFragment.newInstance(game.getScore());
        pauseDialogFragment.show(getSupportFragmentManager(), "pauseDialog");
    }

    private void showGameCompleteDialog() {
        GameCompleteDialogFragment gameCompleteDialogFragment = GameCompleteDialogFragment.newInstance(game.getScore());
        gameCompleteDialogFragment.show(getSupportFragmentManager(), "gameCompleteDialog");
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

    @Override
    public void onCompleteGame() {

        // Mostrar mensaje de finalización y navegar a MainActivity
        showToast("Game completed!");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
