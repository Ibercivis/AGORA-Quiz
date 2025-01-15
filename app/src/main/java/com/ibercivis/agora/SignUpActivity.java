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

public class SignUpActivity extends AppCompatActivity implements PrivacyPolicyDialogFragment.PrivacyPolicyDialogListener {

    private TextInputLayout usernameSignUpTextInputLayout;
    private TextInputLayout emailSignUpTextInputLayout;
    private TextInputLayout passwordSignUpTextInputLayout;
    private TextInputLayout confirmPasswordSignUpTextInputLayout;
    private String username;
    private String email;
    private String password1;
    private String password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize TextInputLayouts and buttons
        usernameSignUpTextInputLayout = findViewById(R.id.usernameSignUpTextInputLayout);
        emailSignUpTextInputLayout = findViewById(R.id.emailSignUpTextInputLayout);
        passwordSignUpTextInputLayout = findViewById(R.id.passwordSignUpTextInputLayout);
        confirmPasswordSignUpTextInputLayout = findViewById(R.id.confirmPasswordSignUpTextInputLayout);
        Button createAccountButton = findViewById(R.id.createAccountButton);
        TextView backToLoginText = findViewById(R.id.backToLoginText);
        Button loginButton = findViewById(R.id.loginButton);

        // Handle click on back to login
        createAccountButton.setOnClickListener(view -> {
            // Get the values from the TextInputLayouts
            username = usernameSignUpTextInputLayout.getEditText().getText().toString().trim();
            email = emailSignUpTextInputLayout.getEditText().getText().toString().trim();
            password1 = passwordSignUpTextInputLayout.getEditText().getText().toString();
            password2 = confirmPasswordSignUpTextInputLayout.getEditText().getText().toString();

            // Validate the input fields
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
                // Show the privacy policy dialog
                showPrivacyPolicyDialog();
            }
        });

        // Handle click on back to login
        loginButton.setOnClickListener(view -> {
            // Navigate to LoginActivity
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
            layout.setError(message); // Show the error message
        }
    }

    private void registerNewUser() throws JSONException {
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
                // Successful registration
                Toast.makeText(SignUpActivity.this, "Account created. An email to activate the account has been sent to the address provided.", Toast.LENGTH_LONG).show();
                // Navigate to LoginActivity
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Failed registration, show error message
                String errorMessage = "Registration failed";
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    errorMessage = new String(error.networkResponse.data); // Get the error message from the server
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

    private void showPrivacyPolicyDialog() {
        PrivacyPolicyDialogFragment dialog = new PrivacyPolicyDialogFragment();
        dialog.show(getSupportFragmentManager(), "PrivacyPolicyDialog");
    }

    @Override
    public void onPrivacyPolicyAccepted() {
        // Privacy policy accepted, register the new user
        try {
            registerNewUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

