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

public class PauseDialogFragment extends DialogFragment {

    private static final String ARG_SCORE = "score";
    private PauseDialogListener listener;

    // Add a static method to create a new instance of the dialog
    public static PauseDialogFragment newInstance(int score) {
        PauseDialogFragment fragment = new PauseDialogFragment();
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

                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // Check if the device is running Android 12 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Apply the blur effect
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.flags |= WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
                    params.setBlurBehindRadius(50);
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
        // Ensure that the host activity has implemented the callback interface
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

        // Inflate the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pause, null);

        // Configure the buttons
        dialogView.findViewById(R.id.btnContinue).setOnClickListener(v -> {
            listener.onContinueGame();
            dismiss();
        });
        dialogView.findViewById(R.id.btnQuit).setOnClickListener(v -> {
            listener.onQuitGame(); // Call the listener method
            dismiss(); // Close the dialog
        });

        // Display the score
        TextView tvPoints = dialogView.findViewById(R.id.tvPoints);
        int score = getArguments().getInt(ARG_SCORE, 0);
        tvPoints.setText(String.valueOf(score));

        builder.setView(dialogView);
        return builder.create();
    }

    public interface PauseDialogListener {
        void onContinueGame();
        void onQuitGame();
    }
}

