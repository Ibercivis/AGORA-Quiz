package com.ibercivis.agora;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    private String[] answers; // Aquí deberías tener los datos de tus respuestas

    public AnswersAdapter(String[] answers) {
        this.answers = answers;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        holder.tvAnswerLetter.setText(String.valueOf((char) ('A' + position))); // Esto asume que tus respuestas son secuenciales A, B, C, etc.
        holder.tvAnswerText.setText(answers[position]);
    }

    @Override
    public int getItemCount() {
        return answers.length;
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnswerLetter;
        TextView tvAnswerText;

        AnswerViewHolder(View itemView) {
            super(itemView);
            tvAnswerLetter = itemView.findViewById(R.id.tvAnswerLetter);
            tvAnswerText = itemView.findViewById(R.id.tvAnswerText);
        }
    }
}

