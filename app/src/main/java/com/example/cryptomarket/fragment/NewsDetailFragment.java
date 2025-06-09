package com.example.cryptomarket.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.cryptomarket.R;
import com.example.cryptomarket.models.NewsItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsDetailFragment extends Fragment {

    private ImageView newsImage, sourceLogo;
    private TextView newsTitle, newsBody, sourceName, newsCategories, newsTags, newsDate, upvoteCount, downvoteCount;
    private Button readMoreButton, fullArticleButton;
    private boolean expanded = false;
    private NewsItem newsItem;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "NewsPrefs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setupViews(view);
        loadNewsData();
        return view;
    }

    private void setupViews(View view) {
        newsImage = view.findViewById(R.id.news_image);
        newsTitle = view.findViewById(R.id.news_title);
        newsBody = view.findViewById(R.id.news_body);
        sourceLogo = view.findViewById(R.id.source_logo);
        sourceName = view.findViewById(R.id.source_name);
        newsCategories = view.findViewById(R.id.news_categories);
        newsTags = view.findViewById(R.id.news_tags);
        newsDate = view.findViewById(R.id.news_date);
        readMoreButton = view.findViewById(R.id.read_more_button);
        fullArticleButton = view.findViewById(R.id.full_article_button);
    }

    private void loadNewsData() {
        if (getArguments() != null) {
            newsItem = getArguments().getParcelable("newsItem");
            if (newsItem != null) {
                Glide.with(requireContext()).load(newsItem.getImageurl()).into(newsImage);
                newsTitle.setText(newsItem.getTitle());
                newsBody.setText(newsItem.getBody());
                newsBody.setMovementMethod(new ScrollingMovementMethod()); // Enable scrolling
                sourceName.setText(newsItem.getSource());
                Glide.with(requireContext()).load(newsItem.getSource_info().getImg()).into(sourceLogo);
                newsCategories.setText(formatList(newsItem.getCategories()));
                newsTags.setText(formatList(newsItem.getTags()));
                newsDate.setText("Published on: " + newsItem.getPublished_on());

                String date = (newsItem.getPublished_on() != 0)
                        ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(newsItem.getPublished_on() * 1000L))
                        : "No date available";


                // Expand/collapse body text
                readMoreButton.setOnClickListener(v -> {
                    expanded = !expanded;
                    if (expanded) {
                        newsBody.setMaxLines(Integer.MAX_VALUE); // Show full text
                        readMoreButton.setText("Show Less"); // Change button text
                    } else {
                        newsBody.setMaxLines(5); // Limit to 5 lines
                        readMoreButton.setText("Read More"); // Change button text
                    }
                });

                // Open full article
                fullArticleButton.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getUrl()));
                    startActivity(browserIntent);
                });
            } else {
                Toast.makeText(requireContext(), "News details not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String formatList(String data) {
        if (data == null || data.isEmpty()) return "N/A";
        return data.replace("|", ", ");
    }
}
