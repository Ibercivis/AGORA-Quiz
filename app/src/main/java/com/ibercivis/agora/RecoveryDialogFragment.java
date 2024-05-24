package com.ibercivis.agora;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RecoveryDialogFragment extends DialogFragment {

    private TextInputEditText emailEditText;
    private String BASE_URL;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Ocupar toda la pantalla
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                // Aplicar transparencia al fondo del diálogo
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // Comprobar si el dispositivo está ejecutando Android 12 o posterior
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Configurar el efecto de desenfoque
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.flags |= WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
                    // Aquí puedes ajustar el nivel de desenfoque según tus necesidades
                    params.setBlurBehindRadius(50); // Ejemplo de radio de desenfoque
                    window.setAttributes(params);
                } else {
                    window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B3091968")));
                }
            }
        }
    }

    public interface RecoveryDialogListener {
        void onCloseDialog();
        void onRecoveryPassword();
    }

    private RecoveryDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Asegúrate de que el host implemente la interfaz de callback
        try {
            listener = (RecoveryDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RecoveryDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflar y establecer el layout para el diálogo
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recovery, null);

        emailEditText = dialogView.findViewById(R.id.emailEditText);
        BASE_URL = getString(R.string.base_url);

        // Configurar los botones y acciones del diálogo
        dialogView.findViewById(R.id.closeIcon).setOnClickListener(v -> {
            listener.onCloseDialog(); // Llama al método de cerrar Dialog
            dismiss();
        });
        dialogView.findViewById(R.id.recoveryButton).setOnClickListener(v -> submitChangePassword());

        builder.setView(dialogView);
        return builder.create();
    }

    private void submitChangePassword() {
        String email = emailEditText.getText().toString().trim();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "/api/users/authentication/password/reset/",
                null,
                response -> {
                    showToast("Password reset email sent successfully");
                    dismiss(); // Cierra el diálogo después de enviar el email
                },
                error -> {
                    String errorMessage = "An error occurred";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorResponse = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorObject = new JSONObject(errorResponse);
                            // Assuming the error message is within the "email" field
                            if (errorObject.has("email")) {
                                JSONArray emailErrors = errorObject.getJSONArray("email");
                                if (emailErrors.length() > 0) {
                                    errorMessage = emailErrors.getString(0);
                                }
                            }
                        } catch (UnsupportedEncodingException | JSONException e) {
                            errorMessage = "An error occurred";
                        }
                    } else {
                        errorMessage = "Failed to send password reset email";
                    }
                    showToast(errorMessage);
                }) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("email", email);
                    return jsonBody.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}
