package com.example.cryptomarket.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cryptomarket.PageTransformer;
import com.example.cryptomarket.R;
import com.example.cryptomarket.adapter.ImageSliderAdapter;
import com.example.cryptomarket.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class DashboardFragment extends Fragment {

    private CardView cardViewVideos, cardViewPrice, cardViewExchange, cardViewPricePredictor,cardViewNews;
    private FirebaseAuth mAuth;
    private Handler sliderHandler;
    private FragmentDashboardBinding binding;
    private ViewPager2.OnPageChangeCallback viewPagerCallback;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize ViewBinding
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        sliderHandler = new Handler(Looper.getMainLooper());
        mAuth = FirebaseAuth.getInstance();

        setupImageSlider();

        // Initialize the CardViews
        cardViewNews = rootView.findViewById(R.id.cardViewNews);
        cardViewVideos = rootView.findViewById(R.id.cardViewVideos);
        cardViewPrice = rootView.findViewById(R.id.cardViewPriceAlerts);
        cardViewExchange = rootView.findViewById(R.id.cardViewExchange);
        cardViewPricePredictor = rootView.findViewById(R.id.cardViewPricePredictor);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_container);

        // Set click listeners on each CardView
        cardViewVideos.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                navController.navigate(R.id.action_dashboardFragment_to_videoFragment);
            } else {
                showLoginRequiredMessage();
            }
        });

        cardViewPrice.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                navController.navigate(R.id.action_dashboard_to_priceAlertFragment);
            } else {
                showLoginRequiredMessage();
            }
        });
        cardViewNews.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                navController.navigate(R.id.action_dashboard_to_news);
            } else {
                showLoginRequiredMessage();
            }
        });

        cardViewExchange.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                navController.navigate(R.id.action_dashboardFragment_to_exchange);
            } else {
                showLoginRequiredMessage();
            }
        });

        cardViewPricePredictor.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                navController.navigate(R.id.action_dashboard_to_cryptoPredictFragment);
            } else {
                showLoginRequiredMessage();
            }
        });

        return rootView;
    }

    private void setupImageSlider() {
        List<Integer> images = Arrays.asList(R.drawable.no, R.drawable.bii, R.drawable.eth, R.drawable.sol, R.drawable.cha, R.drawable.bin, R.drawable.so);
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(requireContext(), images);
        binding.imageSliderViewPager.setAdapter(sliderAdapter);

        // Apply the custom animation transformer
        binding.imageSliderViewPager.setPageTransformer(new PageTransformer());

        // Initialize the ViewPager2 callback
        viewPagerCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable); // Remove any existing callbacks
                sliderHandler.postDelayed(sliderRunnable, 5000); // Schedule next slide after 3 seconds
            }
        };

        binding.imageSliderViewPager.registerOnPageChangeCallback(viewPagerCallback);

        // Start automatic sliding
        sliderHandler.postDelayed(sliderRunnable, 6000);
    }

    private boolean isUserLoggedIn() {
        return mAuth != null && mAuth.getCurrentUser() != null;
    }

    private void showLoginRequiredMessage() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Access Denied")
                .setMessage("You must be logged in to access this feature.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentPosition = binding.imageSliderViewPager.getCurrentItem();
            int nextPosition = currentPosition + 1;
            if (nextPosition >= binding.imageSliderViewPager.getAdapter().getItemCount()) {
                nextPosition = 0; // Loop back to the first image
            }
            binding.imageSliderViewPager.setCurrentItem(nextPosition, true);
            sliderHandler.postDelayed(this, 6000); // Schedule the next slide
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable); // Clean up handler when view is destroyed
        binding.imageSliderViewPager.unregisterOnPageChangeCallback(viewPagerCallback); // Clean up the callback
    }
}