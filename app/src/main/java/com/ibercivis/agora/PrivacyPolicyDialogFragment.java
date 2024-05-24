package com.ibercivis.agora;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class PrivacyPolicyDialogFragment extends DialogFragment {

    public interface PrivacyPolicyDialogListener {
        void onPrivacyPolicyAccepted();
    }

    private PrivacyPolicyDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Asegúrate de que el host implemente la interfaz de callback
        try {
            listener = (PrivacyPolicyDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PrivacyPolicyDialogListener");
        }
    }

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflar y establecer el layout para el diálogo
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_privacy_policy, null);

        CheckBox acceptCheckBox = dialogView.findViewById(R.id.acceptCheckBox);
        Button acceptButton = dialogView.findViewById(R.id.acceptButton);

        // Configurar el TextView para mostrar la política de privacidad
        TextView privacyPolicyTextView = dialogView.findViewById(R.id.privacyPolicyTextView);
        privacyPolicyTextView.setText(Html.fromHtml(getString(R.string.privacy_policy)));

        dialogView.findViewById(R.id.closeIcon).setOnClickListener(v -> {
            dismiss();
        });

        acceptCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            acceptButton.setEnabled(isChecked);
        });

        acceptButton.setOnClickListener(v -> {
            listener.onPrivacyPolicyAccepted();
            dismiss();
        });

        builder.setView(dialogView);
        return builder.create();
    }
}
