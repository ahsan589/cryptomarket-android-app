package com.example.cryptomarket.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cryptomarket.R;
import com.example.cryptomarket.adapter.ImageSliderAdapter;
import com.example.cryptomarket.adapter.MarketAdapter;
import com.example.cryptomarket.adapter.TopMarketAdapter;
import com.example.cryptomarket.api.Apiinterface;
import com.example.cryptomarket.api.ApiUtilities;
import com.example.cryptomarket.databinding.FragmentHomeBinding;
import com.example.cryptomarket.models.CryptoCurrency;
import com.example.cryptomarket.models.MarketModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static final String TAG = "home";
    private Handler sliderHandler;
    private List<CryptoCurrency> cryptoCurrencyList = new ArrayList<>();
    private MarketAdapter marketAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupImageSlider();
        setupTabs();
        setupRecyclerView();
        fetchMarketData();

        return binding.getRoot();
    }

    private void setupImageSlider() {
        List<Integer> images = Arrays.asList(R.drawable.tt, R.drawable.bii, R.drawable.eth,
                R.drawable.sol, R.drawable.ton, R.drawable.bin, R.drawable.so);

        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(requireContext(), images);
        binding.imageSliderViewPager.setAdapter(sliderAdapter);
        binding.imageSliderViewPager.setOffscreenPageLimit(3);

        sliderHandler = new Handler(Looper.getMainLooper());
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = binding.imageSliderViewPager.getCurrentItem();
            if (currentItem == binding.imageSliderViewPager.getAdapter().getItemCount() - 1) {
                binding.imageSliderViewPager.setCurrentItem(0);
            } else {
                binding.imageSliderViewPager.setCurrentItem(currentItem + 1);
            }
            sliderHandler.postDelayed(this, 3000);
        }
    };

    private void setupTabs() {
        String[] tabTitles = {"All", "Gainers", "Losers", "MCap", "Volume"};
        for (String title : tabTitles) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(title));
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateDataDisplay(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        binding.mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        marketAdapter = new MarketAdapter(requireContext(), new ArrayList<>(), TAG);
        binding.mainRecyclerView.setAdapter(marketAdapter);
    }

    private void fetchMarketData() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiUtilities.getInstance().create(Apiinterface.class)
                .getMarketData()
                .enqueue(new Callback<MarketModel>() {
                    @Override
                    public void onResponse(Call<MarketModel> call, Response<MarketModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            cryptoCurrencyList = response.body().getData().getCryptoCurrencyList();
                            setupTopCurrencyRecyclerView();
                            updateDataDisplay(0); // Show all by default
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<MarketModel> call, Throwable t) {
                        Log.e(TAG, "API call failed: ", t);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void setupTopCurrencyRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.topCurrencyRecyclerView.setLayoutManager(layoutManager);
        binding.topCurrencyRecyclerView.setAdapter(
                new TopMarketAdapter(requireContext(), cryptoCurrencyList));
    }

    private void updateDataDisplay(int position) {
        if (cryptoCurrencyList.isEmpty()) return;

        List<CryptoCurrency> sortedList = new ArrayList<>(cryptoCurrencyList);

        switch (position) {
            case 1: // Gainers
                Collections.sort(sortedList, (c1, c2) -> Double.compare(
                        c2.getQuotes().get(0).getPercentChange24h(),
                        c1.getQuotes().get(0).getPercentChange24h()));
                break;
            case 2: // Losers
                Collections.sort(sortedList, (c1, c2) -> Double.compare(
                        c1.getQuotes().get(0).getPercentChange24h(),
                        c2.getQuotes().get(0).getPercentChange24h()));
                break;
            case 3: // Market Cap
                Collections.sort(sortedList, (c1, c2) -> Double.compare(
                        c2.getQuotes().get(0).getMarketCap(),
                        c1.getQuotes().get(0).getMarketCap()));
                break;
            case 4: // Volume
                Collections.sort(sortedList, (c1, c2) -> Double.compare(
                        c2.getQuotes().get(0).getVolume24h(),
                        c1.getQuotes().get(0).getVolume24h()));
                break;
        }

        // Show top 100 items for performance
        List<CryptoCurrency> displayList = sortedList.subList(0, Math.min(500, sortedList.size()));
        marketAdapter.updateData(displayList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sliderHandler != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
        binding = null;
    }
}