package com.example.cryptomarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.cryptomarket.R;
import com.example.cryptomarket.models.VideoItem;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoItem> videoList;
    private OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(VideoItem video);
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

        // Bind data to views (matches React Native layout)
        holder.videoTitle.setText(video.getTitle());
        holder.videoTitle.setMaxLines(2);
        
        holder.videoDescription.setText(video.getDescription());
        holder.videoDescription.setMaxLines(2);
        
        // Format and show duration if available
        if (video.getDuration() != null && !video.getDuration().isEmpty()) {
            holder.videoDuration.setText(video.getDuration());
            holder.videoDuration.setVisibility(View.VISIBLE);
        } else {
            holder.videoDuration.setVisibility(View.GONE);
        }
        
        // Format and show views if available
        if (video.getViews() > 0) {
            String viewsText = formatViews(video.getViews());
            holder.videoViews.setText(viewsText);
            holder.videoViews.setVisibility(View.VISIBLE);
        } else {
            holder.videoViews.setVisibility(View.GONE);
        }

        // Load thumbnail image using Glide with better error handling
        Glide.with(holder.itemView.getContext())
                .load(video.getThumbnailUrl())
                .placeholder(R.drawable.ic_video)
                .error(R.drawable.ic_video)
                .thumbnail(0.25f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.videoThumbnail);

        // Set click listener - pass the entire video object
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVideoClick(video);
            }
        });
    }

    private String formatViews(int views) {
        if (views >= 1000000) {
            return String.format("%.1fM views", views / 1000000.0);
        } else if (views >= 1000) {
            return String.format("%.1fK views", views / 1000.0);
        } else {
            return views + " views";
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void updateList(List<VideoItem> newList) {
        videoList.clear();
        videoList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle, videoDescription, videoDuration, videoViews;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoDescription = itemView.findViewById(R.id.videoDescription);
            videoDuration = itemView.findViewById(R.id.videoDuration);
            videoViews = itemView.findViewById(R.id.videoViews);
        }
    }
}
