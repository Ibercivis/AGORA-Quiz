package com.ibercivis.agora;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements RecoveryDialogFragment.RecoveryDialogListener {
    // Creamos una instancia de sessionManager
    SessionManager sessionManager;
    // Inicializar TextInputLayouts y botones
    TextInputLayout usernameTextInputLayout;
    TextInputLayout passwordTextInputLayout;
    Button loginButton;
    Button signUpButton;
    TextView createAccountText;
    TextView forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verifica si el usuario ya está logeado
        sessionManager = new SessionManager(LoginActivity.this);
        if (sessionManager.getIsLogged()) {
            // Usuario ya logeado, redirigir a MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finaliza LoginActivity para que el usuario no regrese a esta pantalla
            return; // Importante para evitar que el resto del código de onCreate se ejecute
        }

        setContentView(R.layout.activity_login);

        // Asignamos identificadores a las vistas
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        signUpButton = findViewById(R.id.createAccountButton);


        // Manejar clic del botón de login
        loginButton.setOnClickListener(view -> {
            // Obtener los valores ingresados por el usuario
            String username = usernameTextInputLayout.getEditText().getText().toString().trim();
            String password = passwordTextInputLayout.getEditText().getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (username.isEmpty()) {
                    usernameTextInputLayout.setError("Username cannot be empty");
                    if (vibrator != null) {
                        vibrator.vibrate(100); // 100 milisegundos
                    }
                }

                if (password.isEmpty()) {
                    passwordTextInputLayout.setError("Password cannot be empty");
                    if (vibrator != null) {
                        vibrator.vibrate(100); // 100 milisegundos
                    }
                }

            } else {
                try {
                    loginUser(username, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        // Manejar clic en crear cuenta
        signUpButton.setOnClickListener(view -> {
            // Navegar a la actividad de creación de cuenta
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Manejar clic en olvidé mi contraseña
        forgotPasswordText.setOnClickListener(view -> {
            // Implementar lógica para cambiar la contraseña
            showRecoveryDialog();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        // Asegúrate de que los campos estén vacíos cada vez que se muestra la actividad
        if (usernameTextInputLayout.getEditText() != null) {
            usernameTextInputLayout.getEditText().setText("");
        }
        if (passwordTextInputLayout.getEditText() != null) {
            passwordTextInputLayout.getEditText().setText("");
        }
    }

    private void loginUser(String username, String password) throws JSONException {
        String url = getString(R.string.base_url) + "/api/users/authentication/login/";

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("password", password);

        String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Autenticación exitosa
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String token = jsonResponse.getString("key");

                    // Guarda el nombre de usuario y el token en SessionManager
                    sessionManager.createLoginSession(username, token);

                    // Navegar a MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Finaliza LoginActivity
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Autenticación fallida, mostrar mensaje de error
                String errorMessage = "Login failed"; // Mensaje por defecto
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    errorMessage = new String(error.networkResponse.data); // Obtén el mensaje de error del servidor
                }
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                error.printStackTrace();
                Log.e("LoginError", "Error: " + error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody == null ? null : requestBody.getBytes();
            }
        };

        queue.add(stringRequest);
    }

    private void showRecoveryDialog() {
        RecoveryDialogFragment recoveryDialogFragment = new RecoveryDialogFragment();
        recoveryDialogFragment.show(getSupportFragmentManager(), "pauseDialog");
    }

    @Override
    public void onCloseDialog() {

    }

    @Override
    public void onRecoveryPassword() {

    }
}

