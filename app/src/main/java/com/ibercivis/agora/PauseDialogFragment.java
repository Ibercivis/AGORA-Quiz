package com.ibercivis.agora;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class PauseDialogFragment extends DialogFragment {

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

    public interface PauseDialogListener {
        void onContinueGame();
        void onQuitGame();
    }

    private PauseDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Asegúrate de que el host implemente la interfaz de callback
        try {
            listener = (PauseDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PauseDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflar y establecer el layout para el diálogo
        // Pasar null como padre es aceptable porque este layout va en el diálogo
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pause, null);

        // Configurar los botones y acciones del diálogo
        dialogView.findViewById(R.id.btnContinue).setOnClickListener(v -> {
            listener.onContinueGame();
            dismiss();
        });
        dialogView.findViewById(R.id.btnQuit).setOnClickListener(v -> {
            listener.onQuitGame(); // Llama al método en la actividad cuando se presiona el botón
            dismiss(); // Cierra el diálogo
        });

        builder.setView(dialogView);
        return builder.create();
    }
}

