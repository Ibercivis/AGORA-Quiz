package com.ibercivis.agora;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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

import java.util.List;

public class CategoryGameActivity extends AppCompatActivity implements PauseDialogFragment.PauseDialogListener, GameCompleteDialogFragment.GameCompleteDialogListener {

    private RelativeLayout headerCategory, headerCorrect, headerIncorrect;
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
        setContentView(R.layout.activity_category_game);

        gameService = new GameService(this);
        answersAdapter = new AnswersAdapter(new String[0], this::onAnswerSelected);

        RecyclerView answersRecyclerView = findViewById(R.id.answersRecyclerView);
        answersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        answersRecyclerView.setAdapter(answersAdapter);

        headerCategory = findViewById(R.id.headerClassic);
        headerCorrect = findViewById(R.id.headerCorrect);
        headerIncorrect = findViewById(R.id.headerIncorrect);
        questionCount = findViewById(R.id.questionCount);
        txtCurrentQuestionIndex = findViewById(R.id.txtCurrentQuestionIndex);
        checkImage = findViewById(R.id.checkImage);
        questionText = findViewById(R.id.questionText);
        progressBar = findViewById(R.id.progressBar);

        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> showPauseDialog());

        // Check if the activity was started with a game object
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
            // Obtain the category from the intent
            String category = getIntent().getStringExtra("CATEGORY");
            if (category != null) {
                startCategoryGame(category);
            } else {
                // Manage the case when no category is selected
                showToast("No category selected. Starting default category.");
                startCategoryGame("Climate change fundamentals");
            }
        }
    }

    private void startCategoryGame(String category) {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        gameService.startCategoryGame(token, category, response -> {
            try {
                if (response.has("game") && response.has("next_question") && response.has("current_question_index")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionDeserializer()).create();
                    game = gson.fromJson(response.getJSONObject("game").toString(), Game.class);
                    currentGameId = game.getId();
                    Question nextQuestion = gson.fromJson(response.getJSONObject("next_question").toString(), Question.class);
                    currentQuestionIndex = response.getInt("current_question_index");
                    correctAnswersCount = response.getInt("correct_answers_count");
                    updateUIWithQuestion(nextQuestion, currentQuestionIndex, correctAnswersCount);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, this::handleError);
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

        try {
            gameService.answerQuestion(token, currentGameId, currentQuestion.getId(), selectedAnswer, response -> {
                handleAnswerResponse(response);
            }, this::handleError);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleAnswerResponse(JSONObject response) {
        // Analyze the response from the server to show the correct or incorrect answer
        // Also verify if the game is completed
        try {
            boolean correct = response.getBoolean("correct");
            int score = response.getInt("score");
            String status = response.getString("status");
            int currentQuestionIndex = response.getInt("current_question_index");
            correctAnswersCount = response.getInt("correct_answers_count");

            // Score is updated in the game object
            game.setScore(score);

            // Show correct or incorrect answer
            if (correct) {
                answersAdapter.setAnswerState(currentQuestion.getCorrectAnswer(), currentQuestion.getCorrectAnswer());
                showCorrectAnswer(response, currentQuestionIndex, correctAnswersCount);
            } else {
                answersAdapter.setAnswerState(currentQuestion.getCorrectAnswer(), selectedAnswer);
                showIncorrectAnswer(response, currentQuestionIndex, correctAnswersCount);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            // Manage JSON error
        }
    }

    private void showCorrectAnswer(JSONObject response, int currentQuestionIndex, int correctAnswersCount) {
        headerCategory.setVisibility(View.INVISIBLE);
        headerCorrect.setVisibility(View.VISIBLE);

        try {
            String reference = currentQuestion.getReference();
            TextView correctReferenceTextView = findViewById(R.id.correctReference);
            correctReferenceTextView.setText(reference);
            boolean correct = response.getBoolean("correct");
            int score = response.getInt("score");
            String status = response.getString("status");

            if (status.equals("completed")) {
                // Add a small delay before finishing the game
                new Handler().postDelayed(() -> showGameCompleteDialog(), 2000); // Espera 2 segundos
            } else {
                // Add a small delay before loading the next question
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
            // Manage JSON error
        }
    }

    private void showIncorrectAnswer(JSONObject response, int currentQuestionIndex, int correctAnswersCount) {
        // Make invisible the category header and make visible the incorrect header
        headerCategory.setVisibility(View.INVISIBLE);
        headerIncorrect.setVisibility(View.VISIBLE);

        try {
            String reference = currentQuestion.getReference();
            TextView incorrectReferenceTextView = findViewById(R.id.incorrectReference);
            incorrectReferenceTextView.setText(reference);
            boolean correct = response.getBoolean("correct");
            int score = response.getInt("score");
            String status = response.getString("status");

            if (status.equals("completed")) {
                // Add a small delay before finishing the game
                new Handler().postDelayed(() -> showGameCompleteDialog(), 2000); // Espera 2 segundos
            } else {
                // Add a small delay before loading the next question
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
            // Manage JSON error
        }
    }

    private void loadNextQuestion(JSONObject response, int currentQuestionIndex, int correctAnswersCount) throws JSONException {
        headerCorrect.setVisibility(View.INVISIBLE);
        headerIncorrect.setVisibility(View.INVISIBLE);
        headerCategory.setVisibility(View.VISIBLE);
        try {
            if (response.has("next_question")) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionDeserializer()).create();
                Question nextQuestion = gson.fromJson(response.getJSONObject("next_question").toString(), Question.class);
                currentQuestionIndex = response.getInt("current_question_index");
                updateUIWithQuestion(nextQuestion, currentQuestionIndex, correctAnswersCount);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void animateProgressBar(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void animateQuestionCount(final int correctAnswersCount) {
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
    public void onBackPressed() {
        showPauseDialog();
    }

    @Override
    public void onContinueGame() {

    }

    @Override
    public void onQuitGame() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        gameService.abandonGame(token, currentGameId, response -> {
            // Navigate to MainActivity
            Intent intent = new Intent(CategoryGameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Finalyzes the current activity
        }, error -> {
            // Manage error
        });
    }

    @Override
    public void onCompleteGame() {
        // Show a message and navigate to MainActivity
        showToast("Game completed!");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}