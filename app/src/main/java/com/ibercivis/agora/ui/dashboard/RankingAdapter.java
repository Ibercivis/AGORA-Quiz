package com.ibercivis.agora.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.ibercivis.agora.R;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<RankingItem> rankingList;
    private String BASE_URL;

    public RankingAdapter(List<RankingItem> rankingList, String BASE_URL) {
        this.rankingList = rankingList;
        this.BASE_URL = BASE_URL;
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
        holder.tvUsername.setText(currentItem.getUsername());

        // Check if the ranking is for time_trial or classic
        if (currentItem.getMaxTimeTrialTime() > 0) {
            holder.tvScore.setText(currentItem.getFormattedTime());
        } else {
            holder.tvScore.setText(currentItem.getScore() + " Pts");
        }

        RequestOptions requestOptions = new RequestOptions()
                .transform(new CircleCrop());

        if (currentItem.getProfileImageUrl() != null && !currentItem.getProfileImageUrl().isEmpty() && currentItem.getProfileImageUrl() != "null") {
            Glide.with(holder.itemView.getContext())
                    .load(BASE_URL + currentItem.getProfileImageUrl())
                    .apply(requestOptions)
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_avatar_ranking_orange);
        }
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
    private int score; // For classic
    private int maxTimeTrialTime; // For time_trial
    private String profileImageUrl;

    private String baseURL;

    public RankingItem(int rank, String username, int score, String profileImageUrl) {
        this.rank = rank;
        this.username = username;
        this.score = score;
        this.profileImageUrl = profileImageUrl;
    }

    public RankingItem(int rank, String username, int maxTimeTrialTime, String profileImageUrl, boolean isTimeTrial) {
        this.rank = rank;
        this.username = username;
        this.maxTimeTrialTime = maxTimeTrialTime;
        this.profileImageUrl = profileImageUrl;
    }

    public int getRank() {
        return rank;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getMaxTimeTrialTime() {
        return maxTimeTrialTime;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getFormattedTime() {
        int minutes = maxTimeTrialTime / 60;
        int seconds = maxTimeTrialTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

