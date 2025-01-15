package com.ibercivis.agora;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibercivis.agora.classes.FaqItem;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {

    private List<FaqItem> faqList;

    public FaqAdapter(List<FaqItem> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        FaqItem faqItem = faqList.get(position);
        holder.tvQuestion.setText(faqItem.getQuestion());
        holder.tvAnswer.setText(faqItem.getAnswer());
        holder.itemView.setOnClickListener(v -> {
            boolean isVisible = holder.tvAnswer.getVisibility() == View.VISIBLE;
            holder.layoutQuestion.setBackgroundResource(!isVisible ? R.drawable.background_question : 0);
            holder.tvQuestion.setTextColor(!isVisible ? holder.tvQuestion.getContext().getColor(R.color.background_primary): holder.tvQuestion.getContext().getColor(R.color.content_primary));
            holder.tvAnswer.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public static class FaqViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuestion, tvAnswer;
        LinearLayout layoutQuestion;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            layoutQuestion = itemView.findViewById(R.id.layout_question);
        }
    }
}
