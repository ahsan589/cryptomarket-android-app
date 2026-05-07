package com.example.cryptomarket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoFragment extends Fragment {

    private WebView videoWebView;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<VideoItem> videoList;
    private ProgressBar webViewProgressBar;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        videoWebView = view.findViewById(R.id.videoWebView);
        recyclerView = view.findViewById(R.id.videoRecyclerView);
        webViewProgressBar = view.findViewById(R.id.webViewProgressBar);

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
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        
        // Enable hardware acceleration
        videoWebView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
        
        videoWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (webViewProgressBar != null) {
                    webViewProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (webViewProgressBar != null) {
                    webViewProgressBar.setVisibility(View.GONE);
                }
                showErrorMessage("Video failed to load. Please try another video.");
            }
        });
        
        videoWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (webViewProgressBar != null && newProgress < 100) {
                    webViewProgressBar.setVisibility(View.VISIBLE);
                    webViewProgressBar.setProgress(newProgress);
                } else if (webViewProgressBar != null) {
                    webViewProgressBar.setVisibility(View.GONE);
                }
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
                .baseUrl("https://raw.githubusercontent.com/ahsan589/crypto_video/main/")
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

                    // Load the first video by default (matches React Native behavior)
                    if (!videoList.isEmpty()) {
                        playVideo(videoList.get(0));
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

    private String extractVideoId(String videoUrl) {
        // Pattern for embed URLs
        Pattern embedPattern = Pattern.compile("embed/([a-zA-Z0-9_-]+)");
        Matcher embedMatcher = embedPattern.matcher(videoUrl);
        if (embedMatcher.find()) {
            return embedMatcher.group(1);
        }
        
        // Pattern for watch URLs
        Pattern watchPattern = Pattern.compile("v=([a-zA-Z0-9_-]+)");
        Matcher watchMatcher = watchPattern.matcher(videoUrl);
        if (watchMatcher.find()) {
            return watchMatcher.group(1);
        }
        
        // Pattern for youtu.be URLs
        Pattern shortPattern = Pattern.compile("youtu\\.be/([a-zA-Z0-9_-]+)");
        Matcher shortMatcher = shortPattern.matcher(videoUrl);
        if (shortMatcher.find()) {
            return shortMatcher.group(1);
        }
        
        return "";
    }

    private void playVideo(VideoItem video) {
        if (video == null) return;
        
        String videoUrl = video.getVideoUrl();
        String videoId = extractVideoId(videoUrl);
        
        if (videoId.isEmpty()) {
            // Fallback: try to load the URL directly
            String directHtml = "<html><body style=\"margin:0;padding:0;background:black;\">" +
                    "<iframe width=\"100%\" height=\"100%\" src=\"" + videoUrl + 
                    "\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen>" +
                    "</iframe></body></html>";
            videoWebView.loadDataWithBaseURL(null, directHtml, "text/html", "utf-8", null);
        } else {
            // Use YouTube embed with proper parameters (matches React Native implementation)
            String embedUrl = "https://www.youtube.com/embed/" + videoId + "?playsinline=1&autoplay=0&controls=1&rel=0&modestbranding=1";
            
            String html = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "            background: black;\n" +
                    "        }\n" +
                    "        .video-container {\n" +
                    "            position: relative;\n" +
                    "            width: 100%;\n" +
                    "            height: 100%;\n" +
                    "            overflow: hidden;\n" +
                    "        }\n" +
                    "        iframe {\n" +
                    "            position: absolute;\n" +
                    "            top: 0;\n" +
                    "            left: 0;\n" +
                    "            width: 100%;\n" +
                    "            height: 100%;\n" +
                    "            border: 0;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"video-container\">\n" +
                    "        <iframe \n" +
                    "            src=\"" + embedUrl + "\"\n" +
                    "            frameborder=\"0\"\n" +
                    "            allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\"\n" +
                    "            allowfullscreen>\n" +
                    "        </iframe>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";
            
            videoWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        }
    }

    private void showErrorMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (videoWebView != null) {
            videoWebView.loadUrl("about:blank");
            videoWebView.destroy();
        }
    }

    // Retrofit service interface
    public interface VideoService {
        @GET("videos.json")
        Call<List<VideoItem>> getVideos();
    }
}
