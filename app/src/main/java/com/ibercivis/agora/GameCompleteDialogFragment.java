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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class GameCompleteDialogFragment extends DialogFragment {

    private static final String ARG_SCORE = "score";
    private GameCompleteDialogListener listener;

    // Añadir un método estático para instanciar el diálogo con el score como argumento
    public static GameCompleteDialogFragment newInstance(int score) {
        GameCompleteDialogFragment fragment = new GameCompleteDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        fragment.setArguments(args);
        return fragment;
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Asegúrate de que el host implemente la interfaz de callback
        try {
            listener = (GameCompleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement GameCompleteDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflar y establecer el layout para el diálogo
        // Pasar null como padre es aceptable porque este layout va en el diálogo
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_complete, null);

        // Configurar los botones y acciones del diálogo
        dialogView.findViewById(R.id.btnQuit).setOnClickListener(v -> {
            listener.onCompleteGame(); // Llama al método en la actividad cuando se presiona el botón
            dismiss(); // Cierra el diálogo
        });

        // Aquí establecemos el valor del score en el TextView
        TextView tvPoints = dialogView.findViewById(R.id.tvPoints);
        int score = getArguments().getInt(ARG_SCORE, 0);
        tvPoints.setText(String.valueOf(score));

        builder.setView(dialogView);
        return builder.create();
    }

    public interface GameCompleteDialogListener {
        void onCompleteGame();
    }
}
