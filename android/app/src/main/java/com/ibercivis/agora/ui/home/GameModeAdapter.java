package com.ibercivis.agora.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibercivis.agora.R;

public class GameModeAdapter extends RecyclerView.Adapter<GameModeAdapter.ViewHolder> {

    private String[] gameModes = {"Classic", "Time trial", "Categories", "Multiplayer"};
    private int[] gameModeIcons = {
            R.drawable.ic_classic,
            R.drawable.ic_time_trial,
            R.drawable.ic_categories,
            R.drawable.ic_multiplayer
    };

    private OnGameModeClickListener onGameModeClickListener;

    // Método para establecer el listener
    public void setOnGameModeClickListener(OnGameModeClickListener listener) {
        this.onGameModeClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return gameModes.length;
    }

    // Interfaz de callback para manejar clics en los elementos
    public interface OnGameModeClickListener {
        void onGameModeClick(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.iconGameMode.setImageResource(gameModeIcons[position]);
        holder.textGameMode.setText(gameModes[position]);

        // Establecer el OnClickListener para la vista del elemento
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onGameModeClickListener != null) {
                    onGameModeClickListener.onGameModeClick(holder.getAdapterPosition());
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconGameMode;
        TextView textGameMode;

        ViewHolder(View itemView) {
            super(itemView);
            iconGameMode = itemView.findViewById(R.id.iconGameMode);
            textGameMode = itemView.findViewById(R.id.textGameMode);
        }
    }

}

