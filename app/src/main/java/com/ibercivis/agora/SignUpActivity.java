package com.ibercivis.agora;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

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

        // Manejar clic del botón de crear cuenta
        createAccountButton.setOnClickListener(view -> {
            // Implementar lógica de creación de cuenta
        });

        // Manejar clic en volver al login
        backToLoginText.setOnClickListener(view -> {
            // Finalizar esta actividad para volver al Login
            finish();
        });
    }
}

