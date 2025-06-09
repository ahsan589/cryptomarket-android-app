package com.example.cryptomarket.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cryptomarket.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ImageView appLogoImageView = view.findViewById(R.id.appLogoImageView);
        TextView appNameTextView = view.findViewById(R.id.appNameTextView);
        TextView appVersionTextView = view.findViewById(R.id.appVersionTextView);
        TextView appDescriptionTextView = view.findViewById(R.id.appDescriptionTextView);
        Button contactSupportButton = view.findViewById(R.id.contactSupportButton);

        // Set app details
        appLogoImageView.setImageResource(R.drawable.appl); // Ensure you have app_logo.png in res/drawable
        appNameTextView.setText("Crypto Market Analysis");
        appVersionTextView.setText("Version 1.0");
        appDescriptionTextView.setText("Crypto Market Analysis is an app designed to help users track real-time cryptocurrency prices, manage a watchlist, and analyze market trends.");

        // Open email support
        contactSupportButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:cryptoinsight4198@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Crypto Market Support");
            startActivity(Intent.createChooser(emailIntent, "Contact Support"));
        });

        return view;
    }
}
