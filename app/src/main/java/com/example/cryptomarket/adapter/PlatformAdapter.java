package com.example.cryptomarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cryptomarket.R;
import com.example.cryptomarket.models.PlatformModel;
import java.util.List;

public class PlatformAdapter extends RecyclerView.Adapter<PlatformAdapter.ViewHolder> {

    private List<PlatformModel> platformList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PlatformModel platform);
    }

    public PlatformAdapter(List<PlatformModel> platformList, OnItemClickListener listener) {
        this.platformList = platformList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_platform, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlatformModel platform = platformList.get(position);
        holder.textViewName.setText(platform.getName());
        holder.imageViewIcon.setImageResource(platform.getIconRes());

        // Show/hide login status indicator


        holder.itemView.setOnClickListener(v -> listener.onItemClick(platform));
    }

    @Override
    public int getItemCount() {
        return platformList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewName;
        ImageView imageViewAuthStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.imageViewPlatformIcon);
            textViewName = itemView.findViewById(R.id.textViewPlatformName);
            imageViewAuthStatus = itemView.findViewById(R.id.imageViewAuthStatus);
        }
    }
}