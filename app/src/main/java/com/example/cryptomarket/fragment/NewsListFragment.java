package com.example.cryptomarket.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cryptomarket.R;
import com.example.cryptomarket.adapter.NewsAdapter;
import com.example.cryptomarket.models.NewsItem;
import com.example.cryptomarket.models.NewsResponse;
import com.example.cryptomarket.network.ApiService;
import com.example.cryptomarket.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class NewsListFragment extends Fragment implements NewsAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private LottieAnimationView loadingAnimation;
    private LottieAnimationView errorAnimation;
    private NewsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        setupViews(view);
        fetchNews();
        return view;
    }

    private void setupViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        loadingAnimation = view.findViewById(R.id.loading_animation);
        errorAnimation = view.findViewById(R.id.error_animation);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NewsAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Set click listener for error animation to retry
        errorAnimation.setOnClickListener(v -> fetchNews());
    }

    private void fetchNews() {
        loadingAnimation.setVisibility(View.VISIBLE);
        errorAnimation.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<NewsResponse> call = apiService.getNews();

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call,
                                   @NonNull Response<NewsResponse> response) {
                loadingAnimation.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsItem> newsItems = response.body().getData();
                    if (newsItems != null && !newsItems.isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.updateData(newsItems);
                    } else {
                        showError("No news available", R.raw.error);
                    }
                } else {
                    showError("Failed to load news", R.raw.error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                showError(t.getLocalizedMessage(), R.raw.error);
            }
        });
    }

    private void showError(String message, int animationRes) {
        loadingAnimation.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        errorAnimation.setAnimation(animationRes);
        errorAnimation.playAnimation();
        errorAnimation.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(NewsItem newsItem) {
        if (newsItem != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("newsItem", newsItem);

            Navigation.findNavController(requireView())
                    .navigate(R.id.action_news_to_newsDetailFragment, bundle);
        }
    }
}