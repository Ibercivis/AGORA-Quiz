package com.ibercivis.agora;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar TextInputLayouts y botones
        TextInputLayout usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        TextInputLayout passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        Button loginButton = findViewById(R.id.loginButton);
        TextView createAccountText = findViewById(R.id.createAccountText);
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // Manejar clic del botón de login
        loginButton.setOnClickListener(view -> {
            // Implementar lógica de login
        });

        // Manejar clic en crear cuenta
        createAccountText.setOnClickListener(view -> {
            // Navegar a la actividad de creación de cuenta
        });

        // Manejar clic en olvidé mi contraseña
        forgotPasswordText.setOnClickListener(view -> {
            // Implementar lógica para cambiar la contraseña
        });
    }
}

