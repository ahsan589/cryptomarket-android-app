package com.example.cryptomarket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cryptomarket.R;
import com.example.cryptomarket.adapter.VideoAdapter;
import com.example.cryptomarket.models.VideoItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {

    private WebView videoWebView;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<VideoItem> videoList;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        videoWebView = view.findViewById(R.id.videoWebView);
        recyclerView = view.findViewById(R.id.videoRecyclerView);

        // WebView setup
        setupWebView();

        // Initialize video list
        videoList = new ArrayList<>();

        // Set up RecyclerView
        setupRecyclerView();

        // Fetch videos from the server
        fetchVideosFromServer();

        return view;
    }

    private void setupWebView() {
        WebSettings webSettings = videoWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // Enable DOM storage for better compatibility
        videoWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // Handle WebView loading errors
                videoWebView.loadData("<html><body style=\"text-align:center;\"><h2>Video failed to load.</h2></body></html>", "text/html", "utf-8");
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoAdapter = new VideoAdapter(videoList, this::playVideo);
        recyclerView.setAdapter(videoAdapter);
    }

    private void fetchVideosFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/ahsan589/crypto_video/main/") // Correct base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        VideoService service = retrofit.create(VideoService.class);
        Call<List<VideoItem>> call = service.getVideos();

        call.enqueue(new Callback<List<VideoItem>>() {
            @Override
            public void onResponse(Call<List<VideoItem>> call, Response<List<VideoItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    videoList.clear();
                    videoList.addAll(response.body());
                    videoAdapter.notifyDataSetChanged();

                    // Load the first video by default
                    if (!videoList.isEmpty()) {
                        playVideo(videoList.get(0).getVideoUrl());
                    }
                } else {
                    showErrorMessage("Failed to load videos.");
                }
            }

            @Override
            public void onFailure(Call<List<VideoItem>> call, Throwable t) {
                showErrorMessage("Network error. Please try again.");
            }
        });
    }

    private void playVideo(String videoUrl) {
        String iframeVideoUrl = "<html><body style=\"margin:0;padding:0;\"><iframe width=\"100%\" height=\"100%\" src=\"" +
                videoUrl +
                "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        videoWebView.loadData(iframeVideoUrl, "text/html", "utf-8");
    }

    private void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Retrofit service interface
    public interface VideoService {
        @GET("videos.json") // Replace with your JSON file name
        Call<List<VideoItem>> getVideos();
    }
}