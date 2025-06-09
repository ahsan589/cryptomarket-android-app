package com.example.cryptomarket.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cryptomarket.adapter.MarketAdapter;
import com.example.cryptomarket.api.ApiUtilities;
import com.example.cryptomarket.api.Apiinterface;
import com.example.cryptomarket.databinding.FragmentMarketBinding;
import com.example.cryptomarket.models.CryptoCurrency;
import com.example.cryptomarket.models.MarketModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarketFragment extends Fragment {

    private FragmentMarketBinding binding;
    private List<CryptoCurrency> list;
    private MarketAdapter adapter;
    private String searchText = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMarketBinding.inflate(inflater, container, false);
        list = new ArrayList<>();
        adapter = new MarketAdapter(requireContext(), list, "MarketFragment");
        binding.currencyRecyclerView.setAdapter(adapter);

        // Perform network operation on a background thread
        new FetchMarketDataTask().execute();

        searchCoin();

        return binding.getRoot();
    }

    private void searchCoin() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText = s.toString().toLowerCase();
                updateRecyclerView();
            }
        });
    }

    private void updateRecyclerView() {
        List<CryptoCurrency> data = new ArrayList<>();
        for (CryptoCurrency item : list) {
            String coinName = item.getName().toLowerCase();
            String coinSymbol = item.getSymbol().toLowerCase();

            if (coinName.contains(searchText) || coinSymbol.contains(searchText)) {
                data.add(item);
            }
        }
        adapter.updateData(data);
    }

    private class FetchMarketDataTask extends AsyncTask<Void, Void, List<CryptoCurrency>> {

        @Override
        protected List<CryptoCurrency> doInBackground(Void... voids) {
            Apiinterface apiInterface = ApiUtilities.getInstance().create(Apiinterface.class);
            try {
                retrofit2.Response<MarketModel> response = apiInterface.getMarketData().execute();
                if (response.body() != null) {
                    return response.body().getData().getCryptoCurrencyList();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<CryptoCurrency> result) {
            list = result;
            adapter.updateData(list);
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}
