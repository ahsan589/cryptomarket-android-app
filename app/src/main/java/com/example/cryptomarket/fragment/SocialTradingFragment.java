package com.example.cryptomarket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cryptomarket.R;
import com.example.cryptomarket.adapter.PlatformAdapter;
import com.example.cryptomarket.models.PlatformModel;
import java.util.ArrayList;
import java.util.List;

public class SocialTradingFragment extends Fragment implements PlatformAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PlatformAdapter adapter;
    private List<PlatformModel> platformList;
    private WebView webView;
    private ProgressBar progressBar;
    private View emptyStateView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social_trading, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        webView = view.findViewById(R.id.SocialwebView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateView = view.findViewById(R.id.emptyStateView);

        setupRecyclerView();
        setupWebView();

        return view;
    }

    private void setupRecyclerView() {
        platformList = new ArrayList<>();

        platformList.add(new PlatformModel("TradingView", "https://www.tradingview.com/ideas/", R.drawable.tradingview));
        platformList.add(new PlatformModel("eToro", "https://www.etoro.com/discover/markets/cryptocurrencies", R.drawable.etoroo));
        platformList.add(new PlatformModel("Binance Social", "https://www.binance.com/en/feed", R.drawable.binance));
        platformList.add(new PlatformModel("CoinMarketCap Community", "https://coinmarketcap.com/community/", R.drawable.mae));
        platformList.add(new PlatformModel("Zignaly", "https://zignaly.com/#top-traders", R.drawable.man));
        platformList.add(new PlatformModel("3Commas", "https://3commas.io/signal-bot", R.drawable.comm));
        platformList.add(new PlatformModel("Shrimpy", "https://www.shrimpy.io/", R.drawable.shim));
        platformList.add(new PlatformModel("Bitcointalk", "https://bitcointalk.org/", R.drawable.bitco));
        platformList.add(new PlatformModel("CoinSpectator", "https://coinspectator.com/", R.drawable.signal));

        // Setup adapter and layout
        adapter = new PlatformAdapter(platformList, this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        // Handle empty state
        if (platformList.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupWebView() {
        // Basic WebView settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        // WebView clients
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(PlatformModel platform) {
        webView.loadUrl(platform.getUrl());
        recyclerView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroyView();
    }
}