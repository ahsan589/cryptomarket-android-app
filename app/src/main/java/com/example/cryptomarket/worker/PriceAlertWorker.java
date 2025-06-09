package com.example.cryptomarket.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cryptomarket.R;
import com.example.cryptomarket.api.ApiUtilities;
import com.example.cryptomarket.api.Apiinterface;
import com.example.cryptomarket.models.CryptoCurrency;
import com.example.cryptomarket.models.MarketModel;
import com.example.cryptomarket.models.Quote;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceAlertWorker extends Worker {
    private static final String TAG = "PriceAlertWorker";

    public PriceAlertWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);

        if (!notificationsEnabled) {
            return Result.success(); // Do nothing if notifications are disabled
        }

        checkPriceAlerts();
        return Result.success();
    }

    private void checkPriceAlerts() {
        Apiinterface api = ApiUtilities.getCoinMarketCapApiInterface();
        Call<MarketModel> call = api.getMarketData();

        call.enqueue(new Callback<MarketModel>() {
            @Override
            public void onResponse(@NonNull Call<MarketModel> call, @NonNull Response<MarketModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CryptoCurrency> cryptoList = response.body().getData().getCryptoCurrencyList();
                    for (CryptoCurrency currency : cryptoList) {
                        checkAndSendNotification(currency);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MarketModel> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch price alerts: " + t.getMessage());
            }
        });
    }

    private void checkAndSendNotification(CryptoCurrency currency) {
        Context context = getApplicationContext();
        float currentPrice = getUsdPrice(currency);
        String coinName = currency.getName();

        float targetPrice = context.getSharedPreferences("PriceAlerts", Context.MODE_PRIVATE).getFloat(coinName, -1);
        String alertType = context.getSharedPreferences("PriceAlerts", Context.MODE_PRIVATE).getString(coinName + "_type", "");

        if (targetPrice > 0) {
            if ("Above".equals(alertType) && currentPrice >= targetPrice) {
                sendNotification(context, coinName, currentPrice, alertType);
            } else if ("Below".equals(alertType) && currentPrice <= targetPrice) {
                sendNotification(context, coinName, currentPrice, alertType);
            }
        }
    }

    private float getUsdPrice(CryptoCurrency currency) {
        List<Quote> quotes = currency.getQuotes();
        if (quotes != null && !quotes.isEmpty()) {
            return (float) quotes.get(0).getPrice();
        }
        return -1;
    }

    private void sendNotification(Context context, String coinName, float currentPrice, String alertType) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("price_alerts", "Price Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "price_alerts")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Price Alert: " + coinName)
                .setContentText(coinName + " has gone " + alertType.toLowerCase() + " $" + currentPrice)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
