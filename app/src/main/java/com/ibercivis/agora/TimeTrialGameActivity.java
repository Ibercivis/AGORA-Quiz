package com.ibercivis.agora;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class TimeTrialGameActivity extends AppCompatActivity implements PauseDialogFragment.PauseDialogListener, GameCompleteDialogFragment.GameCompleteDialogListener {

    private RelativeLayout headerClassic, headerCorrect, headerIncorrect;
    private TextView questionText, timerTextView;
    private AnswersAdapter answersAdapter;
    private ProgressBar progressBar;
    private TextView questionCount, txtCurrentQuestionIndex;
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
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000; // 1 minuto en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetrial_game);

        gameService = new GameService(this);
        answersAdapter = new AnswersAdapter(new String[0], this::onAnswerSelected);

        RecyclerView answersRecyclerView = findViewById(R.id.answersRecyclerView);
        answersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        answersRecyclerView.setAdapter(answersAdapter);

        headerClassic = findViewById(R.id.headerClassic);
        headerCorrect = findViewById(R.id.headerCorrect);
        headerIncorrect = findViewById(R.id.headerIncorrect);
        questionCount = findViewById(R.id.questionCount);
        txtCurrentQuestionIndex = findViewById(R.id.txtCurrentQuestionIndex);
        checkImage = findViewById(R.id.checkImage);
        questionText = findViewById(R.id.questionText);
        progressBar = findViewById(R.id.progressBar);
        timerTextView = findViewById(R.id.timerTextView);

        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> showPauseDialog());

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("GAME_OBJECT")) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionDeserializer()).create();
            String gameJson = getIntent().getStringExtra("GAME_OBJECT");
            if (gameJson != null) {
                game = gson.fromJson(gameJson, Game.class);
                String nextQuestionJson = getIntent().getStringExtra("NEXT_QUESTION");
                currentQuestionIndex = getIntent().getIntExtra("CURRENT_QUESTION_INDEX", 1);
                currentGameId = game.getId();
                correctAnswersCount = getIntent().getIntExtra("CORRECT_ANSWERS_COUNT", 0);
                long timeLeftInSeconds = getIntent().getIntExtra("TIME_LEFT", 60);
                timeLeftInMillis = timeLeftInSeconds * 1000;
                if (nextQuestionJson != null) {
                    Question nextQuestion = gson.fromJson(nextQuestionJson, Question.class);
                    updateUIWithQuestion(nextQuestion, currentQuestionIndex, correctAnswersCount);
                    startTimer(); // Iniciar el temporizador con el tiempo restante
                }
            }
        } else {
            startGame();
        }
    }

    private void startGame() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        gameService.startTimeTrialGame(token, response -> {
            try {
                if (response.has("game")) {
                    if (response.has("next_question")) {
                        if (response.has("current_question_index")) {
                            Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionDeserializer()).create();
                            game = gson.fromJson(String.valueOf(response.getJSONObject("game")), Game.class);
                            currentGameId = game.getId();
                            Question nextQuestion = gson.fromJson(String.valueOf(response.getJSONObject("next_question")), Question.class);
                            int currentQuestionIndex = response.getInt("current_question_index");
                            correctAnswersCount = response.getInt("correct_answers_count");
                            updateUIWithQuestion(nextQuestion, currentQuestionIndex, correctAnswersCount);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> handleError(error));
    }

    private void updateUIWithQuestion(Question question, int currentQuestionIndex, int correctAnswersCount) {
        currentQuestion = question;
        questionText.setText(question.getQuestionText());
        answersAdapter.setAnswers(question.getAnswers().toArray(new String[0]));

        this.currentQuestionIndex = currentQuestionIndex;
        animateQuestionCount(correctAnswersCount);
        animateProgressBar(calculateProgress());
        animateQuestionIndex(currentQuestionIndex);

        answersAdapter.setAnswerState(-2, -2);

        if (countDownTimer == null) {
            startTimer();
        }
    }

    private int calculateProgress() {
        return (int) ((float) (currentQuestionIndex - 1) / totalQuestions * 100);
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
            selectedAnswer = answerIndex + 1;
            sendAnswerToServer(selectedAnswer);
        }
    }

    private void sendAnswerToServer(int selectedAnswer) {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        long timeLeftInSeconds = Math.round(timeLeftInMillis / 1000.0);

        try {
            gameService.answerTimeTrialQuestion(token, currentGameId, currentQuestion.getId(), selectedAnswer, timeLeftInSeconds, response -> {
                handleAnswerResponse(response);
            }, this::handleError);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleAnswerResponse(JSONObject response) {
        try {
            boolean correct = response.getBoolean("correct");
            int score = response.getInt("score");
            String status = response.getString("status");
            int currentQuestionIndex = response.getInt("current_question_index");
            correctAnswersCount = response.getInt("correct_answers_count");

            game.setScore(score);
            countDownTimer.cancel();

            if (correct) {
                answersAdapter.setAnswerState(currentQuestion.getCorrectAnswer(), currentQuestion.getCorrectAnswer());
                showCorrectAnswer(response, currentQuestionIndex, correctAnswersCount);
                updateTimer(5000);  // Añadir 5 segundos al tiempo restante
            } else {
                answersAdapter.setAnswerState(currentQuestion.getCorrectAnswer(), selectedAnswer);
                showIncorrectAnswer(response, currentQuestionIndex, correctAnswersCount);
                updateTimer(-3000);  // Restar 3 segundos al tiempo restante
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finishGame() {
        // Llamar al endpoint finish_game en el servidor
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        gameService.finishGame(token, currentGameId, response -> {
            showGameCompleteDialog();
        }, this::handleError);
    }

    private void loadNextQuestion(JSONObject response, int currentQuestionIndex, int correctAnswersCount) throws JSONException {
        headerCorrect.setVisibility(View.INVISIBLE);
        headerIncorrect.setVisibility(View.INVISIBLE);
        headerClassic.setVisibility(View.VISIBLE);
        Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionDeserializer()).create();
        Question nextQuestion = gson.fromJson(response.getJSONObject("next_question").toString(), Question.class);
        updateUIWithQuestion(nextQuestion, currentQuestionIndex, correctAnswersCount);
    }

    private void showCorrectAnswer(JSONObject response, int currentQuestionIndex, int correctAnswersCount) {
        headerClassic.setVisibility(View.INVISIBLE);
        headerCorrect.setVisibility(View.VISIBLE);
        try {
            boolean correct = response.getBoolean("correct");
            int score = response.getInt("score");
            String status = response.getString("status");

            if (status.equals("completed")) {
                new Handler().postDelayed(this::showGameCompleteDialog, 2000);
            } else {
                new Handler().postDelayed(() -> {
                    try {
                        loadNextQuestion(response, currentQuestionIndex, correctAnswersCount);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, 2000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showIncorrectAnswer(JSONObject response, int currentQuestionIndex, int correctAnswersCount) {
        headerClassic.setVisibility(View.INVISIBLE);
        headerIncorrect.setVisibility(View.VISIBLE);

        try {
            boolean correct = response.getBoolean("correct");
            int score = response.getInt("score");
            String status = response.getString("status");

            if (status.equals("completed")) {
                new Handler().postDelayed(this::showGameCompleteDialog, 2000);
            } else {
                new Handler().postDelayed(() -> {
                    try {
                        loadNextQuestion(response, currentQuestionIndex, correctAnswersCount);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, 2000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                countDownTimer.cancel();
                endGame();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void updateTimer(long additionalTimeInMillis) {
        // Cancelar el temporizador actual
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Actualizar el tiempo restante
        timeLeftInMillis += additionalTimeInMillis;

        // Asegurarse de que el tiempo no sea negativo
        if (timeLeftInMillis < 0) {
            timeLeftInMillis = 0;
        }

        // Iniciar un nuevo temporizador con el tiempo actualizado
        startTimer();
    }

    private void endGame() {
        // Implementa la lógica para finalizar el juego
        finishGame();
    }

    private void animateProgressBar(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void animateQuestionCount(final int currentQuestionIndex) {
        final int startValue = lastCorrectAnswersCount;
        final int endValue = currentQuestionIndex;
        lastCorrectAnswersCount = currentQuestionIndex;

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(500);

        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            questionCount.setText(String.format("%d", animatedValue));
        });

        animator.start();
    }

    private void animateQuestionIndex(final int currentQuestionIndex) {
        final int startValue = (currentQuestionIndex - 1);
        final int endValue = currentQuestionIndex;

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(500);

        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            txtCurrentQuestionIndex.setText(String.format("%d of %d questions", animatedValue, totalQuestions));
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
            Intent intent = new Intent(TimeTrialGameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, error -> {
        });
    }

    @Override
    public void onCompleteGame() {
        showToast("Game completed!");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
