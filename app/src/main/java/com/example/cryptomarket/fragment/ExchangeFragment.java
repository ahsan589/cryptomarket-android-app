package com.example.cryptomarket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptomarket.R;
import com.example.cryptomarket.adapter.ExchangeAdaptor;
import com.example.cryptomarket.api.ApiUtilities;
import com.example.cryptomarket.api.Apiinterface;
import com.example.cryptomarket.models.Exchange;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ExchangeFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchExchanges(view);
        return view;
    }

    private void fetchExchanges(View view) {
        Apiinterface apiService = ApiUtilities.getCoinGeckoApiInterface();
        Call<List<Exchange>> call = apiService.getExchangesData();
        call.enqueue(new Callback<List<Exchange>>() {
            @Override
            public void onResponse(@NonNull Call<List<Exchange>> call, @NonNull Response<List<Exchange>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExchangeAdaptor adapter = new ExchangeAdaptor(response.body(), getContext(), exchangeId -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("exchangeId", exchangeId);  // Pass exchangeId to the detail fragment
                        Navigation.findNavController(view).navigate(R.id.action_exchange_to_exchangeDetail, bundle);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Exchange>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
