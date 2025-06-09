package com.example.cryptomarket.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cryptomarket.R
import com.example.cryptomarket.databinding.CurrencyItemLayoutBinding
import com.example.cryptomarket.fragment.HomeFragmentDirections
import com.example.cryptomarket.fragment.MarketFragmentDirections
import com.example.cryptomarket.fragment.WatchFragmentDirections
import com.example.cryptomarket.models.CryptoCurrency
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale


class MarketAdapter @JvmOverloads constructor(
    private val context: Context,
    private var list: MutableList<CryptoCurrency>,
    private val type: String, // Optional delete listener
) :
    RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {
    private val fullList: MutableList<CryptoCurrency> =
        ArrayList(list) // Copy original list for filtering

    class MarketViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var binding: CurrencyItemLayoutBinding = CurrencyItemLayoutBinding.bind(view!!)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.currency_item_layout, parent, false)
        return MarketViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        val item = list[position]

        // Set currency name, symbol, and price
        holder.binding.currencyNameTextView.text = item.name
        holder.binding.currencySymbolTextView.text = item.symbol
        holder.binding.currencyPriceTextView.text = String.format("$%.02f", item.quotes[0].price)

        // Set percent change and color
        val percentChange = item.quotes[0].percentChange24h
        if (percentChange >= 0) {
            holder.binding.currencyChangeTextView.setTextColor(
                context.resources.getColor(
                    R.color.teal_700,
                    null
                )
            )
            holder.binding.currencyChangeTextView.text =
                "+ " + String.format("%.02f", percentChange) + "%"
        } else {
            holder.binding.currencyChangeTextView.setTextColor(
                context.resources.getColor(
                    R.color.Red,
                    null
                )
            )
            holder.binding.currencyChangeTextView.text =
                "- " + String.format("%.02f", -percentChange) + "%"
        }

        // Handle item click
        holder.itemView.setOnClickListener { view: View ->
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                // Show alert dialog if user is not logged in
                AlertDialog.Builder(view.context)
                    .setTitle("Access Denied")
                    .setMessage("You must be logged in to access this feature.")
                    .setPositiveButton(
                        "OK"
                    ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                    .show()
            } else {
                // Navigate based on the fragment type
                when (type) {
                    "MarketFragment" -> findNavController(view).navigate(
                        MarketFragmentDirections.actionMarketToDetailsFragment(
                            item
                        )
                    )

                    "watchlist" -> findNavController(view).navigate(
                        WatchFragmentDirections.actionWatchToDetailsFragment(
                            item
                        )
                    )
                    "home" -> findNavController(view).navigate(
                        HomeFragmentDirections.actionHomeToDetailsFragment(
                            item
                        )
                    )

                }
            }
        }

        // Load coin image
        val coinImageUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/" + item.id + ".png"
        Glide.with(context)
            .load(coinImageUrl)
            .apply(RequestOptions().placeholder(R.drawable.spinner))
            .into(holder.binding.currencyImageView)

        // Load chart image
        val chartImageUrl =
            "https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + item.id + ".png"
        Glide.with(context)
            .load(chartImageUrl)
            .apply(RequestOptions().placeholder(R.drawable.spinner))
            .into(holder.binding.currencyChartImageView)

        // Show delete button only in WatchFragment


        // Logging for debugging
        Log.d("MarketAdapter", "Loading coin image for " + item.id + " from " + coinImageUrl)
        Log.d("MarketAdapter", "Loading chart image for " + item.id + " from " + chartImageUrl)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // Update the adapter's data

    fun updateData(newData: List<CryptoCurrency?>) {
        this.list = newData as MutableList<CryptoCurrency>
        notifyDataSetChanged()
    }
    // Filter the list based on search text
    fun filterList(text: String) {
        if (text.isEmpty()) {
            list = fullList
        } else {
            val query = text.lowercase(Locale.getDefault())
            list = ArrayList()
            for (item in fullList) {
                if (item.name.lowercase(Locale.getDefault())
                        .contains(query) || item.symbol.lowercase(
                        Locale.getDefault()
                    ).contains(query)
                ) {
                    list.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }
}