package com.realai.realaitv;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainingPlayerAdapter extends RecyclerView.Adapter<TrainingPlayerAdapter.ViewHolder> {

    private JSONArray playerNameList;
    private Activity getActivity;

    public TrainingPlayerAdapter(Activity getActivity, JSONArray playerNameList) {
        this.getActivity = getActivity;
        this.playerNameList = playerNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_training_child, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(getActivity)
                .load("https://realaiuploadplayersvideo.s3.ap-south-1.amazonaws.com/User+Profile/user.png")
                .error(R.drawable.default_background).into(holder.playerImage);
        holder.tvPlayerName.setText(playerNameList.optJSONObject(position).optString("playerName"));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return playerNameList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView playerImage;
        TextView tvPlayerName;

        public ViewHolder(View itemView) {
            super(itemView);
            playerImage = itemView.findViewById(R.id.ci_training_player);
            tvPlayerName = itemView.findViewById(R.id.tv_training_player_name);
        }
    }
}
