package com.example.cryptomarket.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptomarket.R;
import com.example.cryptomarket.adapter.SectionAdapter;
import com.example.cryptomarket.models.QuestionAnswer;
import com.example.cryptomarket.models.Section;

import java.util.ArrayList;
import java.util.List;

public class HelpAndSupportFragment extends Fragment {

    private RecyclerView sectionRecyclerView;
    private SectionAdapter sectionAdapter;
    private List<Section> sections;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_support, container, false);

        // Initialize RecyclerView
        sectionRecyclerView = view.findViewById(R.id.sectionRecyclerView);
        sectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize sections
        sections = new ArrayList<>();
        sections.add(new Section("App-Related Questions", "mobile-alt", getAppRelatedQuestions()));
        sections.add(new Section("Crypto Knowledge Base", "book-open", getCryptoKnowledgeBase()));
        sections.add(new Section("Support & Feedback", "lightbulb", getSupportAndFeedbackQuestions()));

        // Set up adapter
        sectionAdapter = new SectionAdapter(sections);
        sectionRecyclerView.setAdapter(sectionAdapter);

        // Button click listeners
        Button contactSupportButton = view.findViewById(R.id.contactSupportButton);
        Button liveChatButton = view.findViewById(R.id.liveChatButton);

        // Contact support button - open a contact support activity or navigate to a contact form
        contactSupportButton.setOnClickListener(v -> {
            // Create an email intent to contact support
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:cryptoinsight4198@gmail.com.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Crypto Market Support"); // Pre-filled subject
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, I need help with...");
            startActivity(Intent.createChooser(emailIntent, "Contact Support"));

        });


        // Live chat button - open a live chat screen or initiate a live chat
        liveChatButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "working on it", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private List<QuestionAnswer> getAppRelatedQuestions() {
        List<QuestionAnswer> data = new ArrayList<>();
        data.add(new QuestionAnswer("How to add a coin to my watchlist?", "Simply navigate to the coin details page and tap the 'Add to Watchlist' button."));
        data.add(new QuestionAnswer("How do I set up alerts?", "Go to the settings menu, select ‘Price Alerts’, and choose the coin and price threshold for the alert."));
        data.add(new QuestionAnswer("Is my data secure?", "Yes, we use industry-standard encryption and authentication methods to secure your data."));
        return data;
    }

    private List<QuestionAnswer> getCryptoKnowledgeBase() {
        List<QuestionAnswer> data = new ArrayList<>();
        data.add(new QuestionAnswer("What is cryptocurrency?", "Cryptocurrency is a digital currency that uses cryptography for security and operates on decentralized networks."));
        data.add(new QuestionAnswer("How does blockchain work?", "Blockchain is a distributed ledger that records transactions across multiple computers securely and transparently."));
        data.add(new QuestionAnswer("How do I buy cryptocurrency?", "You can buy cryptocurrency on exchanges like Binance, Coinbase, and Kraken using fiat currency or other cryptocurrencies."));
        data.add(new QuestionAnswer("What is market analysis?", "Market analysis involves studying past and current trends to predict future price movements."));
        data.add(new QuestionAnswer("Which trends should I follow?", "Traders often follow trends like Moving Averages, RSI, and MACD for signals on market movements."));
        data.add(new QuestionAnswer("What is spot trading?", "Spot trading involves buying or selling assets for immediate delivery at current market prices."));
        data.add(new QuestionAnswer("What is futures trading?", "Futures trading is a contract-based trading method where you agree to buy or sell an asset at a predetermined future date and price."));
        data.add(new QuestionAnswer("How do candlestick charts work?", "Candlestick charts display price movements over time, showing open, close, high, and low prices for each period."));
        data.add(new QuestionAnswer("What is a bullish trend?", "A bullish trend indicates rising prices and increasing investor confidence in an asset."));
        data.add(new QuestionAnswer("What is a bearish trend?", "A bearish trend signifies falling prices and declining investor confidence."));
        data.add(new QuestionAnswer("How to identify support and resistance levels?", "Support is the price level where buying pressure prevents further decline, while resistance is where selling pressure prevents further rise."));
        data.add(new QuestionAnswer("What is RSI (Relative Strength Index)?", "RSI is a momentum indicator that measures overbought and oversold conditions in the market."));
        data.add(new QuestionAnswer("What is MACD (Moving Average Convergence Divergence)?", "MACD helps identify changes in the strength, direction, momentum, and duration of a trend."));
        data.add(new QuestionAnswer("What is Bollinger Bands?", "Bollinger Bands measure volatility and indicate overbought/oversold conditions."));
        data.add(new QuestionAnswer("How do I interpret volume in crypto trading?", "Higher trading volume often confirms the strength of a trend, while lower volume can indicate weakness or trend reversals."));
        data.add(new QuestionAnswer("What is a stop-loss order?", "A stop-loss order automatically sells an asset when it reaches a predetermined price to minimize losses."));
        data.add(new QuestionAnswer("What is leverage trading?", "Leverage allows traders to borrow funds to increase their position size, amplifying both potential gains and losses."));
        data.add(new QuestionAnswer("How to avoid liquidation in futures trading?", "Avoid over-leveraging, use stop-loss orders, and maintain sufficient margin balance."));
        data.add(new QuestionAnswer("What are common crypto trading strategies?", "Common strategies include day trading, swing trading, scalping, and HODLing."));
        return data;
    }

    private List<QuestionAnswer> getSupportAndFeedbackQuestions() {
        List<QuestionAnswer> data = new ArrayList<>();
        data.add(new QuestionAnswer("Can you add a dark mode feature?", " We are considering adding a dark mode in future updates."));
        data.add(new QuestionAnswer("Can I request a new coin to be listed?", "Yes, you can submit a request via our feedback form in the settings menu."));
        data.add(new QuestionAnswer("Will there be more trading tools in the future?", "We are working on adding more advanced trading tools soon."));
        data.add(new QuestionAnswer("Can I use the app in multiple languages?", "Yes, we are working on adding more languages in future updates. Please check for language options in the settings."));
        data.add(new QuestionAnswer("What is the minimum deposit to start trading?", "The minimum deposit depends on the trading platform you are using. Please check the deposit requirements for each platform in the exchanges."));
        data.add(new QuestionAnswer("How can I delete my account?", "To delete your account, please contact support."));
        return data;
    }

}
