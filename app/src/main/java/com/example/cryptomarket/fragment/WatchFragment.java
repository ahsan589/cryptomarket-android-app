package com.example.cryptomarket.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cryptomarket.adapter.MarketAdapter;
import com.example.cryptomarket.api.ApiUtilities;
import com.example.cryptomarket.api.Apiinterface;
import com.example.cryptomarket.databinding.FragmentWatchBinding;
import com.example.cryptomarket.models.CryptoCurrency;
import com.example.cryptomarket.models.MarketModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class WatchFragment extends Fragment {

    private FragmentWatchBinding binding;
    private List<String> watchlist = new ArrayList<>();
    private List<CryptoCurrency> watchListItem = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWatchBinding.inflate(inflater, container, false);
        loadData();
        fetchMarketData();
        return binding.getRoot();
    }



    private void updateWatchlist() {
        fetchMarketData();
        checkEmptyState();
    }

    private void checkEmptyState() {
        if (watchlist.isEmpty()) {
            binding.emptyTextView.setVisibility(View.VISIBLE);
            binding.watchlistRecyclerView.setVisibility(View.GONE);
        } else {
            binding.emptyTextView.setVisibility(View.GONE);
            binding.watchlistRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void storeData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("watchlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("watchlist", new Gson().toJson(watchlist));
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("watchlist", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("watchlist", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        watchlist = new Gson().fromJson(json, type);

        if (watchlist == null) {
            watchlist = new ArrayList<>();
        }
    }

    private void fetchMarketData() {
        Apiinterface apiInterface = ApiUtilities.getInstance().create(Apiinterface.class);
        apiInterface.getMarketData().enqueue(new Callback<MarketModel>() {
            @Override
            public void onResponse(@NonNull Call<MarketModel> call, @NonNull Response<MarketModel> response) {
                if (response.body() != null) {
                    watchListItem.clear();
                    for (String watchData : watchlist) {
                        for (CryptoCurrency item : response.body().getData().getCryptoCurrencyList()) {
                            if (watchData.equals(item.getSymbol())) {
                                watchListItem.add(item);
                            }
                        }
                    }
                    binding.progressBar.setVisibility(View.GONE);
                    binding.watchlistRecyclerView.setAdapter(new MarketAdapter(
                            requireContext(),
                            watchListItem,
                            "watchlist"// Pass the fragment as the delete listener
                    ));
                    checkEmptyState();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarketModel> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                // Handle failure
            }
        });
    }
}
