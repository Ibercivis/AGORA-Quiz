package com.ibercivis.agora;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import android.util.Patterns;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Inicializar TextInputLayouts y botones
        TextInputLayout usernameSignUpTextInputLayout = findViewById(R.id.usernameSignUpTextInputLayout);
        TextInputLayout emailSignUpTextInputLayout = findViewById(R.id.emailSignUpTextInputLayout);
        TextInputLayout passwordSignUpTextInputLayout = findViewById(R.id.passwordSignUpTextInputLayout);
        TextInputLayout confirmPasswordSignUpTextInputLayout = findViewById(R.id.confirmPasswordSignUpTextInputLayout);
        Button createAccountButton = findViewById(R.id.createAccountButton);
        TextView backToLoginText = findViewById(R.id.backToLoginText);
        Button loginButton = findViewById(R.id.loginButton);

        // Manejar clic del botón de crear cuenta
        createAccountButton.setOnClickListener(view -> {
            // Obtener los valores ingresados por el usuario
            String username = usernameSignUpTextInputLayout.getEditText().getText().toString().trim();
            String email = emailSignUpTextInputLayout.getEditText().getText().toString().trim();
            String password1 = passwordSignUpTextInputLayout.getEditText().getText().toString();
            String password2 = confirmPasswordSignUpTextInputLayout.getEditText().getText().toString();

            // Validar que las entradas no estén vacías y sean correctas
            if (username.isEmpty() || !isValidEmail(email) || !arePasswordsValid(password1, password2)) {
                if (username.isEmpty()) {
                    showErrorToastAndHighlight("Please enter a username", usernameSignUpTextInputLayout);
                }
                if (!isValidEmail(email)) {
                    showErrorToastAndHighlight("Please enter a valid email address", emailSignUpTextInputLayout);
                }
                if (!arePasswordsValid(password1, password2)) {
                    showErrorToastAndHighlight("Passwords do not match", passwordSignUpTextInputLayout, confirmPasswordSignUpTextInputLayout);
                }
            } else {
                // Si todo es válido, intenta registrar al usuario
                try {
                    registerNewUser(username, email, password1, password2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Manejar clic en volver al login
        loginButton.setOnClickListener(view -> {
            // Finalizar esta actividad para volver al Login
            finish();
        });
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean arePasswordsValid(String password1, String password2) {
        return password1.equals(password2) && !password1.isEmpty();
    }

    private void showErrorToastAndHighlight(String message, TextInputLayout... layouts) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
        for (TextInputLayout layout : layouts) {
            layout.setError(message); // Esto marca el campo en rojo y muestra el mensaje de error
        }
    }

    private void registerNewUser(String username, String email, String password1, String password2) throws JSONException {
        String url = getString(R.string.base_url) + "/api/users/registration/";

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("email", email);
        jsonBody.put("password1", password1);
        jsonBody.put("password2", password2);

        String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Registro exitoso
                Toast.makeText(SignUpActivity.this, "Account created. An email to activate the account has been sent to the address provided.", Toast.LENGTH_LONG).show();
                // Navegar a LoginActivity
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Registro fallido, muestra el mensaje de error del servidor
                String errorMessage = "Registration failed"; // Mensaje por defecto
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    errorMessage = new String(error.networkResponse.data); // Obtén el mensaje de error del servidor
                }
                Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
}

