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
    private TextView questionCount;
    private GameService gameService;
    private int currentGameId;
    private int currentQuestionIndex = 1;
    private int totalQuestions = 90;
    private int correctAnswersCount = 0;
    private int lastCorrectAnswersCount = 0;
    private int selectedAnswer;
    private Question currentQuestion;
    private Game game;
    private List<Question> remainingQuestions;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000; // 1 minute in milliseconds
    private int maxTimeTrialTime = 60;

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
        questionText = findViewById(R.id.questionText);
        progressBar = findViewById(R.id.progressBar);
        timerTextView = findViewById(R.id.timerTextView);

        progressBar.setMax((int) timeLeftInMillis); // Time left in milliseconds
        progressBar.setProgress((int) timeLeftInMillis); // Innitialize progress bar with time left

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
                totalQuestions = game.getQuestions().size();
                if (nextQuestionJson != null) {
                    Question nextQuestion = gson.fromJson(nextQuestionJson, Question.class);
                    updateUIWithQuestion(nextQuestion, currentQuestionIndex, correctAnswersCount);
                    startTimer(); // Initialize the timer with the remaining time
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
                            totalQuestions = game.getQuestions().size();
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
        animateQuestionIndex(currentQuestionIndex);

        answersAdapter.setAnswerState(-2, -2);

        if (countDownTimer == null) {
            startTimer();
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
                maxTimeTrialTime += 5;
                updateTimer(5000);  // Add 5 seconds to the remaining time
            } else {
                answersAdapter.setAnswerState(currentQuestion.getCorrectAnswer(), selectedAnswer);
                showIncorrectAnswer(response, currentQuestionIndex, correctAnswersCount);
                maxTimeTrialTime -= 3;
                updateTimer(-3000);  // Subtract 3 seconds from the remaining time
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finishGame() {
        // Call the finishGame endpoint to complete the game
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        if (maxTimeTrialTime < 0){ maxTimeTrialTime = 0; } // Ensure that the maxTimeTrialTime is not negative

        JSONObject params = new JSONObject();
        try {
            params.put("max_time_trial_time", maxTimeTrialTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gameService.finishGame(token, currentGameId, params, response -> {
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
            String reference = currentQuestion.getReference();
            TextView correctReferenceTextView = findViewById(R.id.correctReference);
            correctReferenceTextView.setText(reference);
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
            String reference = currentQuestion.getReference();
            TextView incorrectReferenceTextView = findViewById(R.id.incorrectReference);
            incorrectReferenceTextView.setText(reference);
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

                // Update the progress bar
                animateProgressBar((int) millisUntilFinished);
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
        // Cancel the current timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Update the time left
        timeLeftInMillis += additionalTimeInMillis;

        // Ensure that the time left is not negative
        if (timeLeftInMillis < 0) {
            timeLeftInMillis = 0;
        }

        // Update the progress bar
        animateProgressBar((int) timeLeftInMillis);

        // Start a new timer with the updated time
        startTimer();
    }

    private void endGame() {
        // Call the finishGame endpoint to complete the game
        finishGame();
    }

    private void animateProgressBar(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
        animation.setDuration(100);
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
        countDownTimer.cancel();

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
