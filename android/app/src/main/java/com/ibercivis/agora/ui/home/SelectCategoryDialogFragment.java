package com.ibercivis.agora.ui.home;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.card.MaterialCardView;
import com.ibercivis.agora.R;

public class SelectCategoryDialogFragment extends DialogFragment {

    private OnCategorySelectedListener listener;

    public interface OnCategorySelectedListener {
        void onCategorySelected(String category);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (getParentFragment() instanceof OnCategorySelectedListener) {
                listener = (OnCategorySelectedListener) getParentFragment();
            } else {
                listener = (OnCategorySelectedListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCategorySelectedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Configurar para ocupar toda la pantalla y aplicar efecto de desenfoque
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.flags |= WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
                    params.setBlurBehindRadius(50); // Ajustar el nivel de desenfoque
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

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_category, null);

        // Configurar los botones de selección de categoría
        MaterialCardView btnClimateFundamentals = dialogView.findViewById(R.id.btnClimateFundamentals);
        MaterialCardView btnDisinformation = dialogView.findViewById(R.id.btnDisinformation);
        MaterialCardView btnCommunication = dialogView.findViewById(R.id.btnCommunication);
        MaterialCardView btnMediaLiteracy = dialogView.findViewById(R.id.btnMediaLiteracy);
        ImageView btnClose = dialogView.findViewById(R.id.btnClose);

        // Cerrar el diálogo al hacer clic en el botón de cierre
        btnClose.setOnClickListener(v -> dismiss());

        btnClimateFundamentals.setOnClickListener(v -> {
            listener.onCategorySelected("Climate change fundamentals");
            dismiss();
        });
        btnDisinformation.setOnClickListener(v -> {
            listener.onCategorySelected("Climate change disinformation");
            dismiss();
        });
        btnCommunication.setOnClickListener(v -> {
            listener.onCategorySelected("Climate change communication and narratives");
            dismiss();
        });
        btnMediaLiteracy.setOnClickListener(v -> {
            listener.onCategorySelected("Media literacy");
            dismiss();
        });

        builder.setView(dialogView);
        return builder.create();
    }
}
