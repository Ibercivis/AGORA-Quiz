package com.ibercivis.agora;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    private String[] answers;
    private OnAnswerClickListener answerClickListener;
    private int correctAnswerIndex = -2;
    private int selectedAnswerIndex = -2;

    public interface OnAnswerClickListener {
        void onAnswerClick(int position);
    }

    public AnswersAdapter(String[] answers, OnAnswerClickListener listener) {
        this.answers = answers;
        this.answerClickListener = listener;
    }

    public void setAnswers(String[] newAnswers) {
        this.answers = newAnswers;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    public void setAnswerState(int correctAnswer, int selectedAnswer) {
        this.correctAnswerIndex = correctAnswer - 1; // Convert to 0-based index
        this.selectedAnswerIndex = selectedAnswer - 1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        String answer = answers[position];
        if (answer.isEmpty()) {
            holder.itemView.setVisibility(View.GONE);
            return;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        holder.tvAnswerLetter.setText(String.valueOf((char) ('A' + position)));
        holder.tvAnswerText.setText(answer);

        holder.itemView.setOnClickListener(v -> {
            answerClickListener.onAnswerClick(position);
            // setAnswerState(correctAnswerIndex, position);
        });

        updateBackground(holder, position);
    }

    private void updateBackground(AnswerViewHolder holder, int position) {
        if (position == selectedAnswerIndex) {
            holder.answerContainer.setBackgroundResource(selectedAnswerIndex == correctAnswerIndex ?
                    R.drawable.background_answer_correct : R.drawable.background_answer_incorrect);
            holder.tvAnswerLetter.setBackgroundResource(selectedAnswerIndex == correctAnswerIndex ?
                    R.drawable.background_correct_solid : R.drawable.background_incorrect_solid);
        } else if (position == correctAnswerIndex) {
            holder.answerContainer.setBackgroundResource(R.drawable.background_answer_correct);
            holder.tvAnswerLetter.setBackgroundResource(R.drawable.background_correct_solid);
        } else {
            holder.answerContainer.setBackgroundResource(R.drawable.background_answer);
            holder.tvAnswerLetter.setBackgroundResource(R.drawable.background_circle_solid);
        }
    }

    @Override
    public int getItemCount() {
        return answers.length;
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        LinearLayout answerContainer; // The parent layout of the answer
        TextView tvAnswerLetter;
        TextView tvAnswerText;

        AnswerViewHolder(View itemView) {
            super(itemView);
            answerContainer = itemView.findViewById(R.id.answerContainer);
            tvAnswerLetter = itemView.findViewById(R.id.tvAnswerLetter);
            tvAnswerText = itemView.findViewById(R.id.tvAnswerText);
        }
    }
}

