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

    // Add a static method to create a new instance of the dialog
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
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
            listener = (GameCompleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement GameCompleteDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for this dialog fragment
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_complete, null);

        // Configurate the buttons
        dialogView.findViewById(R.id.btnQuit).setOnClickListener(v -> {
            listener.onCompleteGame(); // Call the interface method
            dismiss(); // Close the dialog
        });

        // Set the score in the dialog
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
