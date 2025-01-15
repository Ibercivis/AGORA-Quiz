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
        // Ensure that the host activity has implemented the callback interface
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
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                // Set the background of the dialog to transparent
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_privacy_policy, null);

        CheckBox acceptCheckBox = dialogView.findViewById(R.id.acceptCheckBox);
        Button acceptButton = dialogView.findViewById(R.id.acceptButton);

        // Set the privacy policy text
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
