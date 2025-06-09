package com.example.cryptomarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cryptomarket.R;
import com.example.cryptomarket.models.VideoItem;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoItem> videoList;
    private OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(String videoUrl);
    }

    public VideoAdapter(List<VideoItem> videoList, OnVideoClickListener listener) {
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem video = videoList.get(position);

        // Bind data to views
        holder.videoTitle.setText(video.getTitle());
        holder.videoDescription.setText(video.getDescription());
        holder.videoDescription.setMaxLines(3);
        holder.videoProgress.setProgress(video.getProgress());

        // Load thumbnail image using Glide
        Glide.with(holder.itemView.getContext())
                .load(video.getThumbnailUrl())
                .placeholder(R.drawable.ic_video) // Placeholder image
                .error(R.drawable.ic_video) // Error image
                .into(holder.videoThumbnail);

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video.getVideoUrl()));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail, videoIcon;
        TextView videoTitle, videoDescription;
        ProgressBar videoProgress;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoDescription = itemView.findViewById(R.id.videoDescription);
            videoProgress = itemView.findViewById(R.id.videoProgress);
        }
    }
}