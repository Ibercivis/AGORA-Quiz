package com.ibercivis.agora.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ibercivis.agora.R;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<RankingItem> rankingList;

    public RankingAdapter(List<RankingItem> rankingList) {
        this.rankingList = rankingList;
    }

    @Override
    public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RankingViewHolder holder, int position) {
        RankingItem currentItem = rankingList.get(position);
        holder.tvRank.setText(String.valueOf(currentItem.getRank()));
        holder.ivAvatar.setImageResource(currentItem.getAvatarResourceId());
        holder.tvUsername.setText(currentItem.getUsername());
        holder.tvScore.setText(currentItem.getScore() + " Pts");
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        public TextView tvRank;
        public ImageView ivAvatar;
        public TextView tvUsername;
        public TextView tvScore;

        public RankingViewHolder(View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvScore = itemView.findViewById(R.id.tvScore);
        }
    }
}

class RankingItem {
    private int rank;
    private String username;
    private int score;
    private int avatarResourceId;

    public RankingItem(int rank, String username, int score, int avatarResourceId) {
        this.rank = rank;
        this.username = username;
        this.score = score;
        this.avatarResourceId = avatarResourceId;
    }

    // Getters
    public int getRank() { return rank; }
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getAvatarResourceId() { return avatarResourceId; }
}

