package com.example.cryptomarket.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptomarket.R;
import com.example.cryptomarket.adapter.PriceAlertAdapter;
import com.example.cryptomarket.api.ApiUtilities;
import com.example.cryptomarket.api.Apiinterface;
import com.example.cryptomarket.models.MarketModel;
import com.example.cryptomarket.models.CryptoCurrency;
import com.example.cryptomarket.models.Quote;
import com.example.cryptomarket.models.PriceAlert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceAlertFragment extends Fragment {
    private static final String TAG = "PriceAlertFragment";
    private SharedPreferences sharedPreferences;
    private Spinner coinSpinner, alertTypeSpinner;
    private EditText priceInput;
    private Button setAlertButton;
    private RecyclerView priceAlertRecyclerView;
    private List<CryptoCurrency> cryptoList = new ArrayList<>();
    private TextView currentPriceText;
    private PriceAlertAdapter priceAlertAdapter;
    private List<PriceAlert> alertList = new ArrayList<>();

    public PriceAlertFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_alert, container, false);

        currentPriceText = view.findViewById(R.id.currentPriceText);
        coinSpinner = view.findViewById(R.id.coinSpinner);
        alertTypeSpinner = view.findViewById(R.id.alertTypeSpinner);
        priceInput = view.findViewById(R.id.priceInput);
        setAlertButton = view.findViewById(R.id.setAlertButton);
        priceAlertRecyclerView = view.findViewById(R.id.priceAlertRecyclerView);

        sharedPreferences = requireActivity().getSharedPreferences("PriceAlerts", Context.MODE_PRIVATE);

        setAlertButton.setOnClickListener(v -> setPriceAlert());

        setupAlertTypeSpinner();
        getTopCurrencyList();
        loadSavedAlerts();
        checkPriceAlerts();

        return view;
    }

    private void setupAlertTypeSpinner() {
        List<String> alertTypes = new ArrayList<>();
        alertTypes.add("Above");
        alertTypes.add("Below");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, alertTypes);
        alertTypeSpinner.setAdapter(adapter);
    }

    private void getTopCurrencyList() {
        Apiinterface api = ApiUtilities.getCoinMarketCapApiInterface();
        Call<MarketModel> call = api.getMarketData();

        call.enqueue(new Callback<MarketModel>() {
            @Override
            public void onResponse(@NonNull Call<MarketModel> call, @NonNull Response<MarketModel> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    MarketModel marketModel = response.body();
                    cryptoList = response.body().getData().getCryptoCurrencyList();

                    List<String> coinNames = new ArrayList<>();
                    for (CryptoCurrency crypto : cryptoList) {
                        coinNames.add(crypto.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, coinNames);
                    coinSpinner.setAdapter(adapter);

                    coinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            CryptoCurrency selectedCrypto = cryptoList.get(position);
                            float currentPrice = getUsdPrice(selectedCrypto);
                            currentPriceText.setText(" $" + currentPrice);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarketModel> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch data: " + t.getMessage());
            }
        });
    }

    private void setPriceAlert() {
        if (coinSpinner.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Please select a valid coin", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedCoin = coinSpinner.getSelectedItem().toString();
        String priceText = priceInput.getText().toString();
        String alertType = alertTypeSpinner.getSelectedItem().toString();

        if (priceText.isEmpty()) {
            Toast.makeText(getContext(), "Enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float targetPrice = Float.parseFloat(priceText);
            sharedPreferences.edit().putFloat(selectedCoin, targetPrice).apply();
            sharedPreferences.edit().putString(selectedCoin + "_type", alertType).apply();

            alertList.add(new PriceAlert(selectedCoin, alertType, targetPrice));
            priceAlertAdapter.notifyDataSetChanged();

            Toast.makeText(getContext(), "Price alert set for " + selectedCoin, Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSavedAlerts() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        alertList.clear();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof Float) {
                String coinName = entry.getKey();
                float targetPrice = (Float) entry.getValue();
                String alertType = sharedPreferences.getString(coinName + "_type", "");

                alertList.add(new PriceAlert(coinName, alertType, targetPrice));
            }
        }
        priceAlertRecyclerView.setNestedScrollingEnabled(false);
        priceAlertRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        priceAlertAdapter = new PriceAlertAdapter(requireContext(), alertList); // Pass context, not sharedPreferences
        priceAlertRecyclerView.setAdapter(priceAlertAdapter);



    }

    private void checkPriceAlerts() {
        Apiinterface api = ApiUtilities.getCoinMarketCapApiInterface();
        Call<MarketModel> call = api.getMarketData();

        call.enqueue(new Callback<MarketModel>() {
            @Override
            public void onResponse(@NonNull Call<MarketModel> call, @NonNull Response<MarketModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (CryptoCurrency currency : response.body().getData().getCryptoCurrencyList()) {
                        String coinName = currency.getName();
                        float currentPrice = getUsdPrice(currency);
                        float targetPrice = sharedPreferences.getFloat(coinName, -1);
                        String alertType = sharedPreferences.getString(coinName + "_type", "");

                        if (targetPrice > 0) {
                            if ("Above".equals(alertType) && currentPrice >= targetPrice) {
                                sendNotification(coinName, currentPrice, alertType);
                            } else if ("Below".equals(alertType) && currentPrice <= targetPrice) {
                                sendNotification(coinName, currentPrice, alertType);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarketModel> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch price alerts: " + t.getMessage());
            }
        });
    }

    private float getUsdPrice(CryptoCurrency currency) {
        List<Quote> quotes = currency.getQuotes();
        if (quotes != null && !quotes.isEmpty()) {
            return (float) quotes.get(0).getPrice();
        }
        return -1;
    }

    private void sendNotification(String coinName, float currentPrice, String alertType) {
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("price_alerts", "Price Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "price_alerts")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Price Alert: " + coinName)
                .setContentText(coinName + " has gone " + alertType.toLowerCase() + " $" + currentPrice)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(1, builder.build());
    }
}
