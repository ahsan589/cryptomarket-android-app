package com.example.cryptomarket;

import android.app.Application;
import androidx.work.*;

import com.example.cryptomarket.worker.PriceAlertWorker;

import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setupWorkManager();
    }

    private void setupWorkManager() {
        WorkManager workManager = WorkManager.getInstance(this);

        PeriodicWorkRequest priceCheckWork = new PeriodicWorkRequest.Builder(PriceAlertWorker.class, 1, TimeUnit.MINUTES)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)  // Runs only when online
                        .build())
                .build();

        workManager.enqueueUniquePeriodicWork("price_check", ExistingPeriodicWorkPolicy.REPLACE, priceCheckWork);
    }
}
