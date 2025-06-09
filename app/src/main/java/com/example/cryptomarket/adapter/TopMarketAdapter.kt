package com.example.cryptomarket.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptomarket.R
import com.example.cryptomarket.databinding.TopCurrencyLayoutBinding
import com.example.cryptomarket.fragment.HomeFragmentDirections
import com.example.cryptomarket.models.CryptoCurrency

class TopMarketAdapter(
    private val context: Context,
    private val list: MutableList<CryptoCurrency>
) : RecyclerView.Adapter<TopMarketAdapter.TopMarketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopMarketViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.top_currency_layout, parent, false)
        return TopMarketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopMarketViewHolder, position: Int) {
        val item = list[position]
        holder.binding.topCurrencyNameTextView.text = item.name

        val imageUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/${item.id}.png"
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.spinner)
            .into(holder.binding.topCurrencyImageView)

        val percentChange = item.quotes[0].percentChange24h
        if (percentChange >= 0) {
            holder.binding.topCurrencyChangeTextView.setTextColor(ContextCompat.getColor(context, R.color.teal_700))
            holder.binding.topCurrencyChangeTextView.text = "+ ${"%.02f".format(percentChange)}%"
        } else {
            holder.binding.topCurrencyChangeTextView.setTextColor(ContextCompat.getColor(context, R.color.Red))
            holder.binding.topCurrencyChangeTextView.text = "- ${"%.02f".format(-percentChange)}%"
        }

        holder.itemView.setOnClickListener { v ->
            val action = HomeFragmentDirections.actionHomeToDetailsFragment(item)
            Navigation.findNavController(v).navigate(action)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class TopMarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: TopCurrencyLayoutBinding = TopCurrencyLayoutBinding.bind(itemView)
    }
}
