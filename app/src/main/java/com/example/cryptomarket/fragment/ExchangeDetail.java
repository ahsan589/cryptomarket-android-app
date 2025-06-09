package com.example.cryptomarket.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.cryptomarket.api.ApiUtilities;
import com.example.cryptomarket.api.Apiinterface;
import com.example.cryptomarket.models.Exchange;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeDetail extends Fragment {

    private TextView exchangeName, exchangeDescription, exchangeCountry, exchangeTrustLevel, exchangeVolume24h, exchangeYearEstablished;
    private TextView trustRankValue, yearEstablishedValue, marketsValue, volumeText;
    private String exchangeId;
    private ImageView exchangeImage, facebookIcon, twitterIcon, redditIcon;
    private Button visitWebsiteButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange_detail, container, false);

        // Initialize views
        exchangeName = view.findViewById(R.id.exchangeName);
        exchangeDescription = view.findViewById(R.id.exchangeDescription);
        exchangeCountry = view.findViewById(R.id.exchangeCountry);
        exchangeTrustLevel = view.findViewById(R.id.trustRankValue);
        exchangeVolume24h = view.findViewById(R.id.volumeText);
        exchangeYearEstablished = view.findViewById(R.id.yearEstablishedValue);
        exchangeImage = view.findViewById(R.id.exchangeImage);
        visitWebsiteButton = view.findViewById(R.id.visitWebsiteButton);
        marketsValue = view.findViewById(R.id.marketsValue);
        volumeText = view.findViewById(R.id.volumeText);
        facebookIcon = view.findViewById(R.id.facebookIcon);
        twitterIcon = view.findViewById(R.id.twitterIcon);
        redditIcon = view.findViewById(R.id.redditIcon);

        // Retrieve the exchangeId passed from the ExchangeFragment
        if (getArguments() != null) {
            exchangeId = getArguments().getString("exchangeId");
        }

        // Fetch the exchange details using the exchangeId
        fetchExchangeDetail(view);

        // Set the onClickListener for the visit website button
        visitWebsiteButton.setOnClickListener(v -> openExchangeWebsite());

        // Set onClickListeners for social icons
        facebookIcon.setOnClickListener(v -> openSocialLink("facebook"));
        twitterIcon.setOnClickListener(v -> openSocialLink("twitter"));
        redditIcon.setOnClickListener(v -> openSocialLink("reddit"));

        return view;
    }

    private void fetchExchangeDetail(View view) {
        Apiinterface apiService = ApiUtilities.getCoinGeckoApiInterface();
        Call<Exchange> call = apiService.getExchangeDetails(exchangeId);

        call.enqueue(new Callback<Exchange>() {
            @Override
            public void onResponse(@NonNull Call<Exchange> call, @NonNull Response<Exchange> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Exchange exchange = response.body();

                    // Log the website URL to check its value
                    String exchangeWebsite = exchange.getUrl();
                    Log.d("ExchangeDetail", "Exchange Website: " + exchangeWebsite);

                    // Set the exchange details to the TextViews
                    exchangeName.setText(exchange.getName());
                    exchangeCountry.setText(exchange.getCountry());
                    exchangeDescription.setText(exchange.getDescription());
                    exchangeTrustLevel.setText(exchange.getTrust_score() + "/10");
                    exchangeYearEstablished.setText("" + exchange.getYear_established());
                    marketsValue.setText("" + exchange.getMarkets());

                    // Set volume section data
                    volumeText.setText("24h Volume: " + exchange.getTrade_volume_24h_btc() + " BTC");

                    // Load exchange image using Glide
                    Glide.with(getContext())
                            .load(exchange.getImage()) // Image URL from the Exchange object
                            .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image while loading
                            .into(exchangeImage);

                    // Store the exchange website URL to be used for the visit button
                    visitWebsiteButton.setTag(exchangeWebsite);  // Store the website URL as a tag

                    // Handle social links visibility
                    if (exchange.getFacebook_url() != null && !exchange.getFacebook_url().isEmpty()) {
                        facebookIcon.setVisibility(View.VISIBLE);
                        facebookIcon.setTag(exchange.getFacebook_url());
                    } else {
                        facebookIcon.setVisibility(View.GONE);
                    }

                    if (exchange.getTwitter_handle() != null && !exchange.getTwitter_handle().isEmpty()) {
                        twitterIcon.setVisibility(View.VISIBLE);
                        twitterIcon.setTag("https://twitter.com/" + exchange.getTwitter_handle());
                    } else {
                        twitterIcon.setVisibility(View.GONE);
                    }

                    if (exchange.getReddit_url() != null && !exchange.getReddit_url().isEmpty()) {
                        redditIcon.setVisibility(View.VISIBLE);
                        redditIcon.setTag(exchange.getReddit_url());
                    } else {
                        redditIcon.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Exchange> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openExchangeWebsite() {
        String websiteUrl = (String) visitWebsiteButton.getTag();  // Retrieve the website URL stored in the button tag
        if (websiteUrl != null && !websiteUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
            startActivity(intent);
        } else {
            // Provide fallback behavior or error message if no website URL is available
            Toast.makeText(getContext(), "Website not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSocialLink(String platform) {
        String url = null;
        switch (platform) {
            case "facebook":
                url = (String) facebookIcon.getTag();
                break;
            case "twitter":
                url = (String) twitterIcon.getTag();
                break;
            case "reddit":
                url = (String) redditIcon.getTag();
                break;
        }

        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), platform + " link not available", Toast.LENGTH_SHORT).show();
        }
    }
}