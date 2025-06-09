package com.example.cryptomarket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptomarket.R;
import com.example.cryptomarket.models.Exchange;

import java.util.List;

public class ExchangeAdaptor extends RecyclerView.Adapter<ExchangeAdaptor.ExchangeViewHolder> {

    private List<Exchange> exchanges;
    private Context context;
    private OnExchangeClickListener onExchangeClickListener;

    public interface OnExchangeClickListener {
        void onExchangeClick(String exchangeId);
    }

    public ExchangeAdaptor(List<Exchange> exchanges, Context context, OnExchangeClickListener onExchangeClickListener) {
        this.exchanges = exchanges;
        this.context = context;
        this.onExchangeClickListener = onExchangeClickListener;
    }

    @NonNull
    @Override
    public ExchangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exchange, parent, false);
        return new ExchangeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExchangeViewHolder holder, int position) {
        Exchange exchange = exchanges.get(position);

        holder.nameTextView.setText(exchange.getName());
        holder.trustLevelTextView.setText("Trust: " + exchange.getTrust_score() + "/10");
        holder.volumeTextView.setText("Volume (BTC): " + exchange.getTrade_volume_24h_btc());

        Glide.with(context)
                .load(exchange.getImage())
                .override(30, 30)
                .centerCrop()
                .circleCrop()
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> onExchangeClickListener.onExchangeClick(exchange.getId()));
    }

    @Override
    public int getItemCount() {
        return exchanges.size();
    }

    public static class ExchangeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, trustLevelTextView, volumeTextView;
        ImageView imageView;

        public ExchangeViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.exchange_name);
            trustLevelTextView = itemView.findViewById(R.id.exchange_trust_level);
            volumeTextView = itemView.findViewById(R.id.exchange_volume);
            imageView = itemView.findViewById(R.id.exchange_image);
        }
    }
}
