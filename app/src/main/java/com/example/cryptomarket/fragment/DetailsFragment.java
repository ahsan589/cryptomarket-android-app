package com.example.cryptomarket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cryptomarket.R;
import com.example.cryptomarket.adapter.NewsAdapter;
import com.example.cryptomarket.databinding.FragmentDetailsBinding;
import com.example.cryptomarket.models.CryptoCurrency;
import com.example.cryptomarket.models.NewsItem;
import com.example.cryptomarket.models.NewsResponse;
import com.example.cryptomarket.network.ApiService;
import com.example.cryptomarket.network.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment implements NewsAdapter.OnItemClickListener {

    private FragmentDetailsBinding binding; // View binding
    private CryptoCurrency data; // CryptoCurrency data passed from the previous fragment
    private ArrayList<String> watchlist; // List of coins in the watchlist
    private boolean watchlistIsChecked; // Flag to check if the coin is in the watchlist
    private NewsAdapter newsAdapter; // Adapter for the news RecyclerView
    private List<NewsItem> newsItems = new ArrayList<>(); // List of news items
    private Long lastTimestamp = null; // Timestamp for pagination
    private boolean isLoading = false; // Flag to prevent duplicate API calls

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentDetailsBinding.inflate(inflater, container, false);

        try {
            // Get the data passed using Safe Args
            if (getArguments() != null) {
                data = DetailsFragmentArgs.fromBundle(getArguments()).getCryptoCurrency();
            }

            // Check if data is null
            if (data == null) {
                Log.e("DetailsFragment", "CryptoCurrency data is null");
                requireActivity().onBackPressed(); // Go back if data is null
                return binding.getRoot();
            }

            // Initialize the news RecyclerView
            setupNewsRecyclerView();

            // Set up scroll listener for pagination
            setupScrollListener();

            // Load the initial chart
            loadChart(data);

            // Set up the details view
            setUpDetails(data);

            // Add to watchlist
            addToWatchlist(data);

            // Fetch initial news
            fetchInitialNews(data.getSymbol());

        } catch (Exception e) {
            Log.e("DetailsFragment", "Error in onCreateView: " + e.getMessage(), e);
        }

        // Return the root view from the binding
        return binding.getRoot();
    }

    private void setupNewsRecyclerView() {
        // Set up the RecyclerView with a LinearLayoutManager
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new NewsAdapter(newsItems, this);
        binding.recyclerView.setAdapter(newsAdapter);
    }

    private void setupScrollListener() {
        // Add a scroll listener to detect when the user reaches the bottom of the list
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null &&
                        layoutManager.findLastVisibleItemPosition() == newsItems.size() - 1 &&
                        !isLoading &&
                        lastTimestamp != null) {

                    // Fetch more news when the user reaches the bottom
                    fetchMoreNews(data.getSymbol());
                }
            }
        });
    }

    private void fetchInitialNews(String symbol) {
        // Clear existing news items and reset the timestamp
        newsItems.clear();
        lastTimestamp = null;
        fetchMoreNews(symbol);
    }

    private void fetchMoreNews(String symbol) {
        // Set loading state to prevent duplicate API calls
        isLoading = true;

        // Create an instance of the API service
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Make the API call with the lastTimestamp for pagination
        Call<NewsResponse> call = apiService.getNewsByCoin(symbol, lastTimestamp);

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                // Reset loading state
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    // Filter news items by symbol
                    List<NewsItem> newItems = filterNewsBySymbol(response.body().getData(), symbol);

                    if (!newItems.isEmpty()) {
                        // Append new items to the list
                        newsAdapter.appendData(newItems);

                        // Update the lastTimestamp for the next pagination request
                        lastTimestamp = (long) newItems.get(newItems.size() - 1).getPublished_on();
                    }

                    // Show an error message if no news items are found
                    if (newsItems.isEmpty()) {
                        showNewsError("No related news found");
                    }
                } else {
                    showNewsError("Failed to load news");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                // Reset loading state and show an error message
                isLoading = false;
                showNewsError("Network error: " + t.getMessage());
            }
        });
    }

    private List<NewsItem> filterNewsBySymbol(List<NewsItem> allNews, String symbol) {
        // Filter news items by symbol
        List<NewsItem> filteredNews = new ArrayList<>();
        for (NewsItem item : allNews) {
            if (item.getTitle().toLowerCase().contains(symbol.toLowerCase())) {
                filteredNews.add(item);
            }
        }
        return filteredNews;
    }

    private void showNewsError(String message) {
        // Show error message if news loading fails
        binding.newsErrorText.setText(message);
        binding.newsErrorText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(NewsItem newsItem) {
        // Handle click on a news item
        if (newsItem != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("newsItem", newsItem);

            View view = getView();
            if (view != null) {
                Log.d("DetailsFragment", "Navigating with NewsItem: " + newsItem);
                Navigation.findNavController(view)
                        .navigate(R.id.action_detailsFragment_to_newsDetailFragment, bundle);
            }
        }
    }

    private void addToWatchlist(CryptoCurrency data) {
        // Add or remove the coin from the watchlist
        readData();
        watchlistIsChecked = watchlist.contains(data.getSymbol());
        updateWatchlistButton();

        binding.addWatchlistButton.setOnClickListener(v -> {
            if (!watchlistIsChecked) {
                if (!watchlist.contains(data.getSymbol())) {
                    watchlist.add(data.getSymbol());
                }
                storeData();
            } else {
                watchlist.remove(data.getSymbol());
                storeData();
            }
            watchlistIsChecked = !watchlistIsChecked;
            updateWatchlistButton();
        });
    }

    private void updateWatchlistButton() {
        // Update the watchlist button UI
        ImageView watchlistIcon = binding.addWatchlistButton.findViewById(R.id.watchlistIcon);
        TextView watchlistText = binding.addWatchlistButton.findViewById(R.id.watchlistText);

        if (watchlistIcon != null && watchlistText != null) {
            if (watchlistIsChecked) {
                watchlistIcon.setImageResource(R.drawable.watchl);
                watchlistText.setText("Remove from Watchlist");
            } else {
                watchlistIcon.setImageResource(R.drawable.watchli);
                watchlistText.setText("Add to Watchlist");
            }
        }
    }

    private void storeData() {
        // Save the watchlist to SharedPreferences
        Context context = requireContext();
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("watchlist", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(watchlist);
        editor.putString("watchlist", json);
        editor.apply();
    }

    private void readData() {
        // Read the watchlist from SharedPreferences
        Context context = requireContext();
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("watchlist", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("watchlist", null);
        java.lang.reflect.Type type = new TypeToken<ArrayList<String>>() {}.getType();
        watchlist = gson.fromJson(json, type);

        if (watchlist == null) {
            watchlist = new ArrayList<>();
        }
    }

    private void loadChart(CryptoCurrency data) {
        // Load the TradingView chart
        binding.detaillChartWebView.getSettings().setJavaScriptEnabled(true);
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        binding.detaillChartWebView.loadUrl(
                "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + data.getSymbol()
                        + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
        );
    }

    private void setUpDetails(CryptoCurrency data) {
        // Set up the UI with coin details
        binding.detailSymbolTextView.setText(data.getSymbol());

        // Load the coin image using Glide
        String coinImageUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/" + data.getId() + ".png";
        Glide.with(requireContext())
                .load(coinImageUrl)
                .apply(new RequestOptions().placeholder(R.drawable.spinner))
                .into(binding.detailImageView);

        binding.title.setText(data.getName());

        // Set price, market cap, and volume
        binding.detailPriceTextView.setText(String.format("$%.04f", data.getQuotes().get(0).getPrice()));
        binding.marketCapTextView.setText(String.format("$%.02fM", data.getQuotes().get(0).getMarketCap() / 1_000_000));
        binding.volumeTextView.setText(String.format("$%.02fM", data.getQuotes().get(0).getVolume24h() / 1_000_000));

        // Set 24h change
        double percentChange = data.getQuotes().get(0).getPercentChange24h();
        if (percentChange >= 0) {
            binding.detailChangeTextView.setTextColor(requireContext().getResources().getColor(R.color.teal_700, null));
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_up);
            binding.detailChangeTextView.setText("24h Change: +" + String.format("%.02f", percentChange) + "%");
        } else {
            binding.detailChangeTextView.setTextColor(requireContext().getResources().getColor(R.color.Red, null));
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_down);
            binding.detailChangeTextView.setText("24h Change: -" + String.format("%.02f", -percentChange) + "%");
        }

        // Set news section header
        binding.newsHeader.setText("Related News for " + data.getSymbol());
    }
}