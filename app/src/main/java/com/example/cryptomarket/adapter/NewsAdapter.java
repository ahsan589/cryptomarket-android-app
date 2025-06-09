package com.example.cryptomarket.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptomarket.R;
import com.example.cryptomarket.models.NewsItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsItem> newsItems;
    private final OnItemClickListener listener;

    // Sentiment keywords
    private static final String[] POSITIVE_KEYWORDS = {
            "bullish", "surge", "growth", "adopt", "positive", "rise", "approve", "gain",
            "success", "breakout", "innovation", "partnership", "launch", "milestone",
            "approval", "win", "solution", "advantage", "profit", "moon", "rally",
            "breakthrough", "all-time high", "ATH", "soar", "boom", "recovery","boost","top","improve","historic"
    };

    private static final String[] NEGATIVE_KEYWORDS = {
            "bearish", "drop", "crash", "scam", "hack", "ban", "negative", "loss", "fail",
            "warning", "alert", "risk", "volatility", "fraud", "sell-off", "plunge", "dip",
            "trouble", "issue", "delay", "lawsuit", "investigation", "FUD", "bankruptcy",
            "collapse", "correction", "retreat", "sink", "decline", "dump", "downfall","slump","Low","bad","worse"
    };

    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    public NewsAdapter(List<NewsItem> newsItems, OnItemClickListener listener) {
        this.newsItems = newsItems != null ? newsItems : new ArrayList<>();
        this.listener = listener;
    }

    private static String getSentiment(NewsItem item) {
        String content = (item.getTitle() + " " + item.getBody()).toLowerCase();

        int positiveScore = 0;
        for (String keyword : POSITIVE_KEYWORDS) {
            if (content.contains(keyword)) {
                positiveScore++;
            }
        }

        int negativeScore = 0;
        for (String keyword : NEGATIVE_KEYWORDS) {
            if (content.contains(keyword)) {
                negativeScore++;
            }
        }

        int totalVotes = Integer.parseInt(item.getUpvotes()) + Integer.parseInt(item.getDownvotes());
        float voteRatio = totalVotes > 0
                ? (Float.parseFloat(item.getUpvotes()) - Float.parseFloat(item.getDownvotes())) / totalVotes
                : 0;

        float combinedScore = (positiveScore - negativeScore) * 0.7f + voteRatio * 0.3f;

        if (combinedScore > 0.3) return "positive";
        if (combinedScore < -0.3) return "negative";
        return "neutral";
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = newsItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public void updateData(List<NewsItem> newItems) {
        this.newsItems = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void appendData(List<NewsItem> newItems) {
        if (newItems != null && !newItems.isEmpty()) {
            int startPosition = newsItems.size();
            newsItems.addAll(newItems);
            notifyItemRangeInserted(startPosition, newItems.size());
        }
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView newsImage;
        private final TextView title, source, upvotes, downvotes, body, sentimentText;
        private final View sentimentDot;
        private final Context context;

        public NewsViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            newsImage = itemView.findViewById(R.id.news_image);
            title = itemView.findViewById(R.id.title);
            source = itemView.findViewById(R.id.source);
            upvotes = itemView.findViewById(R.id.upvotes);
            downvotes = itemView.findViewById(R.id.downvotes);
            body = itemView.findViewById(R.id.news_body);
            sentimentText = itemView.findViewById(R.id.sentiment_text);
            sentimentDot = itemView.findViewById(R.id.sentiment_dot);
        }

        public void bind(NewsItem item, OnItemClickListener listener) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("NewsVotes", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String newsId = item.getId();
            boolean hasVoted = sharedPreferences.getBoolean(newsId + "_voted", false);

            AtomicInteger storedUpvotes = new AtomicInteger(sharedPreferences.getInt(newsId + "_upvotes",
                    item.getUpvotes() != null ? Integer.parseInt(item.getUpvotes()) : 0));
            AtomicInteger storedDownvotes = new AtomicInteger(sharedPreferences.getInt(newsId + "_downvotes",
                    item.getDownvotes() != null ? Integer.parseInt(item.getDownvotes()) : 0));

            Glide.with(context)
                    .load(item.getImageurl())
                    .placeholder(R.drawable.appl)
                    .error(R.drawable.appl)
                    .into(newsImage);

            title.setText(item.getTitle() != null ? item.getTitle() : "");
            body.setText(item.getBody() != null ? item.getBody() : "");
            source.setText(item.getSource_info() != null && item.getSource_info().getName() != null
                    ? item.getSource_info().getName()
                    : context.getString(R.string.unknown_source));

            upvotes.setText(context.getString(R.string.upvotes_format, storedUpvotes.get()));
            downvotes.setText(context.getString(R.string.downvotes_format, storedDownvotes.get()));

            upvotes.setEnabled(!hasVoted);
            downvotes.setEnabled(!hasVoted);

            // Set sentiment with circular dot
            String sentiment = getSentiment(item);
            sentimentText.setText(sentiment.toUpperCase());

            GradientDrawable dotDrawable = (GradientDrawable) sentimentDot.getBackground();
            int colorRes = R.color.neutral;

            switch (sentiment) {
                case "positive":
                    colorRes = R.color.positive;
                    break;
                case "negative":
                    colorRes = R.color.negative;
                    break;
                case "neutral":
                default:
                    colorRes = R.color.neutral;
                    break;
            }

            dotDrawable.setColor(ContextCompat.getColor(context, colorRes));

            upvotes.setOnClickListener(v -> {
                if (!hasVoted) {
                    storedUpvotes.getAndIncrement();
                    editor.putInt(newsId + "_upvotes", storedUpvotes.get());
                    editor.putBoolean(newsId + "_voted", true);
                    editor.apply();
                    upvotes.setText(context.getString(R.string.upvotes_format, storedUpvotes.get()));
                    upvotes.setEnabled(false);
                    downvotes.setEnabled(false);
                }
            });

            downvotes.setOnClickListener(v -> {
                if (!hasVoted) {
                    storedDownvotes.getAndIncrement();
                    editor.putInt(newsId + "_downvotes", storedDownvotes.get());
                    editor.putBoolean(newsId + "_voted", true);
                    editor.apply();
                    downvotes.setText(context.getString(R.string.downvotes_format, storedDownvotes.get()));
                    upvotes.setEnabled(false);
                    downvotes.setEnabled(false);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}