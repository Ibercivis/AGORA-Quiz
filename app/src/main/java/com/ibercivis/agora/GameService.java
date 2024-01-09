package com.ibercivis.agora;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ibercivis.agora.classes.Game;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GameService {
    private RequestQueue requestQueue;
    private String BASE_URL;


    public GameService(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
        this.BASE_URL = context.getString(R.string.base_url);
    }

    //Iniciar una partida
    public void startGame(String token, final Response.Listener<Game> listener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/api/games/start/",
                null,
                response -> {
                    // Analizar la respuesta y convertirla en una instancia de Game
                    Game game = parseGameFromResponse(response);
                    listener.onResponse(game);
                },
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private Game parseGameFromResponse(JSONObject response) {
        // Analiza el JSON para crear y devolver una instancia de Game
        Gson gson = new Gson();
        Game game = gson.fromJson(response.toString(), Game.class);
        return game;
    }

    //Responder una pregunta
    public void answerQuestion(String token, int gameId, int questionId, int answer, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("question_id", questionId);
        requestBody.put("answer", answer);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/api/games/" + gameId + "/answer/",
                requestBody,
                listener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    // Comprobar si hay una partida en progreso
    public void checkForInProgressGame(String token, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                BASE_URL + "/api/games/resume/",
                null,
                listener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    //Abandonar una partida
    public void abandonGame(String token, int gameId, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/api/games/" + gameId + "/abandon/",
                null,
                listener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

}

