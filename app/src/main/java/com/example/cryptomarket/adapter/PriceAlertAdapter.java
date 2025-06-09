package com.example.cryptomarket.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cryptomarket.R;
import com.example.cryptomarket.models.PriceAlert;
import java.util.List;

public class PriceAlertAdapter extends RecyclerView.Adapter<PriceAlertAdapter.ViewHolder> {
    private Context context;
    private List<PriceAlert> alertList;
    private SharedPreferences sharedPreferences;

    public PriceAlertAdapter(Context context, List<PriceAlert> alertList) {
        this.context = context;
        this.alertList = alertList;
        this.sharedPreferences = context.getSharedPreferences("PriceAlerts", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_price_alert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PriceAlert alert = alertList.get(position);
        holder.coinName.setText(alert.getCoinName());
        holder.alertType.setText(alert.getAlertType());
        holder.targetPrice.setText("$" + alert.getTargetPrice());

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            removeAlert(alert.getCoinName(), position);
        });
    }

    private void removeAlert(String coinName, int position) {
        // Remove from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(coinName);
        editor.remove(coinName + "_type");
        editor.apply();

        // Remove from alertList and update RecyclerView
        alertList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alertList.size());

        Toast.makeText(context, "Price alert removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView coinName, alertType, targetPrice;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coinName = itemView.findViewById(R.id.coinName);
            alertType = itemView.findViewById(R.id.alertType);
            targetPrice = itemView.findViewById(R.id.targetPrice);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
