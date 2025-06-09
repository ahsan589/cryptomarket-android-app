package com.example.cryptomarket.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.cryptomarket.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class CryptoPredictFragment extends Fragment {

    // 1. View declarations
    private Spinner cryptoSpinner;
    private MaterialButton predictButton;
    private TextView predictionValue, errorText, disclaimerText;
    private MaterialCardView resultCard, errorCard;
    private FrameLayout loadingOverlay;
    private EditText searchInput;

    // 2. Data storage
    private List<String> cryptoDisplayNames = new ArrayList<>();
    private List<String> cryptoIds = new ArrayList<>();
    private ArrayAdapter<String> cryptoAdapter;
    private String selectedCryptoId = "bitcoin";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crypto_predict, container, false);

        // 3. Initialize all views
        cryptoSpinner = view.findViewById(R.id.cryptoSpinner);
        predictButton = view.findViewById(R.id.predictButton);
        predictionValue = view.findViewById(R.id.predictionValue);
        resultCard = view.findViewById(R.id.resultCard);
        errorCard = view.findViewById(R.id.errorCard);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        searchInput = view.findViewById(R.id.searchInput);
        disclaimerText = view.findViewById(R.id.disclaimerText); // Critical initialization

        // 4. Setup spinner adapter
        cryptoAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cryptoDisplayNames
        );
        cryptoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cryptoSpinner.setAdapter(cryptoAdapter);

        // 5. Fetch initial crypto list
        new FetchCryptoListTask().execute();

        // 6. Setup search functionality
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCryptoList(s.toString());
            }
        });

        // 7. Spinner selection handler
        cryptoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < cryptoIds.size()) {
                    selectedCryptoId = cryptoIds.get(position);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 8. Predict button click handler
        predictButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                new PredictPriceTask().execute();
            } else {
                showError("No internet connection");
            }
        });

        return view;
    }

    // 9. Network check method
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // 10. Crypto list filtering
    private void filterCryptoList(String query) {
        List<String> filteredNames = new ArrayList<>();
        List<String> filteredIds = new ArrayList<>();

        for (int i = 0; i < cryptoDisplayNames.size(); i++) {
            if (cryptoDisplayNames.get(i).toLowerCase().contains(query.toLowerCase())) {
                filteredNames.add(cryptoDisplayNames.get(i));
                filteredIds.add(cryptoIds.get(i));
            }
        }

        cryptoAdapter.clear();
        cryptoAdapter.addAll(filteredNames);
        cryptoAdapter.notifyDataSetChanged();
        cryptoIds = new ArrayList<>(filteredIds);
    }

    // 11. AsyncTask for fetching crypto list
    private class FetchCryptoListTask extends AsyncTask<Void, Void, List<JSONObject>> {
        @Override
        protected void onPreExecute() {
            loadingOverlay.setVisibility(View.VISIBLE);
            errorCard.setVisibility(View.GONE);
        }

        @Override
        protected List<JSONObject> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            List<JSONObject> allCoins = new ArrayList<>();

            try {
                for (int page = 1; page <= 3; page++) {
                    Request request = new Request.Builder()
                            .url("https://api.coingecko.com/api/v3/coins/markets"
                                    + "?vs_currency=usd&order=market_cap_desc"
                                    + "&per_page=250&page=" + page)
                            .build();

                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        JSONArray coins = new JSONArray(response.body().string());
                        for (int i = 0; i < coins.length(); i++) {
                            allCoins.add(coins.getJSONObject(i));
                        }
                    }
                }
                return allCoins;
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<JSONObject> coins) {
            loadingOverlay.setVisibility(View.GONE);

            if (coins == null) {
                showError("Failed to load cryptocurrencies");
                return;
            }

            cryptoDisplayNames.clear();
            cryptoIds.clear();

            for (JSONObject coin : coins) {
                try {
                    String name = coin.getString("name")
                            + " (" + coin.getString("symbol").toUpperCase() + ")";
                    cryptoDisplayNames.add(name);
                    cryptoIds.add(coin.getString("id"));
                } catch (JSONException e) {
                    // Skip invalid entries
                }
            }

            cryptoAdapter.notifyDataSetChanged();
            filterCryptoList(searchInput.getText().toString());
        }
    }

    // 12. AsyncTask for price prediction
    private class PredictPriceTask extends AsyncTask<Void, Void, String> {
        private Exception exception = null;

        @Override
        protected void onPreExecute() {
            loadingOverlay.setVisibility(View.VISIBLE);
            resultCard.setVisibility(View.GONE);
            errorCard.setVisibility(View.GONE);
            disclaimerText.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(125, TimeUnit.SECONDS)
                    .writeTimeout(125, TimeUnit.SECONDS)
                    .build();

            try {
                // 13. Create JSON request body
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("coinId", selectedCryptoId);

                RequestBody body = RequestBody.create(
                        jsonBody.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                // 14. Build POST request
                Request request = new Request.Builder()
                        .url("http://192.168.32.28:5000/predict") // Replace with your server IP
                        .post(body)
                        .build();

                // 15. Execute request
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
                return null;
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            loadingOverlay.setVisibility(View.GONE);

            if (getActivity() == null) return; // Prevent fragment detachment issues

            // 16. Handle errors
            if (exception != null) {
                showError("Network error: " + exception.getMessage());
                return;
            }

            if (result == null) {
                showError("Server unavailable");
                return;
            }

            // 17. Parse response
            try {
                JSONObject json = new JSONObject(result);
                double price = json.getDouble("prediction");
                predictionValue.setText(String.format("$%.6f", price));
                animateCard(resultCard);
                disclaimerText.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                showError("Invalid server response");
            }
        }
    }

    // 18. Error display handler
    private void showError(String message) {
        if (getActivity() == null) return;
        animateCard(errorCard);
        resultCard.setVisibility(View.GONE);
        disclaimerText.setVisibility(View.GONE);
    }

    // 19. Card animation
    private void animateCard(MaterialCardView card) {
        card.setVisibility(View.VISIBLE);
        card.setAlpha(0f);
        card.animate()
                .alpha(1f)
                .setDuration(500)
                .start();
    }
}