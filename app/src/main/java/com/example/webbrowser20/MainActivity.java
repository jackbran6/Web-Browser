package com.example.webbrowser20;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcelable;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.webbrowser20.ui.main.SectionsPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String homepage = "https://www.google.com";
    ArrayList<Website> bookmarkList = new ArrayList<>();
    ArrayList<Website> historyList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPref.init(getApplicationContext());

        loadHistoryData();
        loadBookmarkData();
        loadWebpage();

        WebView web = findViewById(R.id.webpage);
        EditText url = findViewById(R.id.searchBar);
        ImageButton goButton = findViewById(R.id.goButton);
        ImageButton bookmarkAdd = findViewById(R.id.bookmarksButton);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton historyButton = findViewById(R.id.historyButton);
        ImageButton forwardButton = findViewById(R.id.forwardButton);
        ImageButton bookmarksList = findViewById(R.id.bookmarkList);
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton refreshButton = findViewById(R.id.refreshButton);
        ImageButton changeHomepage = findViewById(R.id.newHomepage);

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                EditText searchBar = findViewById(R.id.searchBar);
                view.loadUrl(url);
                String webUrl = web.getUrl();
                searchBar.setText(webUrl);
                String title = web.getTitle();
                Website website = new Website(title, webUrl);
                historyList.add(website);
                return true;
            }
        });

        initialLoad();

        goButton.setOnClickListener((View v) -> {
            String inputText = url.getText().toString();
            if (!inputText.contains("https://") && (inputText.contains(".com") || inputText.contains(".co."))) {
                inputText = "https://" + inputText;
            }
            if (inputText.contains("http://") && (inputText.contains(".com") || inputText.contains(".co."))) {
                String x[] = inputText.split("//");
                inputText = "https://" + x[2];
            }
            if (!inputText.contains("https://") && !inputText.contains("http://") && !inputText.contains("www.") && (!inputText.contains(".com") || !inputText.contains(".co."))) {
                inputText = "https://www.google.com/search?q=" + inputText;
            }
            web.loadUrl(inputText);

            String webTitle = web.getTitle();
            String webAddress = web.getUrl();
            Website newWebsite = new Website(webTitle, webAddress);
            historyList.add(newWebsite);

        });

        url.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_GO)) {
                goButton.performClick();
                url.setText(web.getUrl());
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
            return false;
        });

        bookmarkAdd.setOnClickListener((View v) -> {
            addBookmark();
            Log.d("bookmarkPage", "Created");

            for (int i = 0; i < bookmarkList.size(); i++) {
                String test2 = bookmarkList.get(i).getTitle();
                Log.d("bookmarkCheck", "Title: " + test2);
            }
        });

        backButton.setOnClickListener((View v) -> {
            if (web.canGoBack()) {
                web.goBack();
            }

        });

        forwardButton.setOnClickListener((View v) -> {
            if (web.canGoForward()) {
                web.goForward();
            }
        });

        historyButton.setOnClickListener((View v) -> {
            saveHistoryData();
            historyScreen();
        });

        bookmarksList.setOnClickListener((View v) -> {
            saveBookmarkData();
            bookmarksScreen();
        });

        homeButton.setOnClickListener((View v) -> {
            goHome();
        });

        refreshButton.setOnClickListener((View v) ->{
            web.reload();
        });

        changeHomepage.setOnClickListener((View v) -> {
            setHomepage();
        });

    }

    private void initialLoad() {
        Intent intent = new Intent();
        String lastPage = getIntent().getStringExtra("Clicked_URL");
        WebView webPage = findViewById(R.id.webpage);
        webPage.loadUrl(lastPage);
    }

    public void saveHistoryData() {
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        SharedPref.write(SharedPref.HISTORY_LIST, json);
    }

    public void loadHistoryData() {
        Gson gson = new Gson();
        String json = SharedPref.read(SharedPref.HISTORY_LIST, null);
        Type type = new TypeToken<ArrayList<Website>>() {
        }.getType();
        historyList = gson.fromJson(json, type);

        if (historyList == null) {
            historyList = new ArrayList<>();
        }
    }

    public void saveBookmarkData() {
        Gson gson = new Gson();
        String json = gson.toJson(bookmarkList);
        SharedPref.write(SharedPref.BOOKMARK_LIST, json);
    }

    public void loadBookmarkData() {
        Gson gson = new Gson();
        String json = SharedPref.read(SharedPref.BOOKMARK_LIST, null);
        Type type = new TypeToken<ArrayList<Website>>() {
        }.getType();
        bookmarkList = gson.fromJson(json, type);

        if (bookmarkList == null) {
            bookmarkList = new ArrayList<>();
        }
    }

    public void saveWebpage() {
        WebView web = findViewById(R.id.webpage);
        String webpage = web.getUrl();
        SharedPref.write(SharedPref.CURRENT_PAGE, webpage);
    }

    public void loadWebpage() {
        String webpage = SharedPref.read(SharedPref.CURRENT_PAGE, null);

        if (webpage == null) {
            webpage = homepage;
        }

        WebView web = findViewById(R.id.webpage);
        web.loadUrl(webpage);
    }


    private void addBookmark() {
        WebView web = findViewById(R.id.webpage);
        String website = web.getUrl();
        String websiteTitle = web.getTitle();

        Website newWebsite = new Website(websiteTitle, website);

        bookmarkList.add(newWebsite);

        Toast.makeText(this, "Added to Bookmarks", Toast.LENGTH_SHORT).show();

    }

    private void historyScreen() {
        WebView web = findViewById(R.id.webpage);
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        Intent intent = new Intent(this, History.class);
        intent.putExtra("History_List", json);
        startActivity(intent);
    }

    private void bookmarksScreen() {
        WebView web = findViewById(R.id.webpage);
        Gson gson = new Gson();
        String json = gson.toJson(bookmarkList);
        Intent intent = new Intent(this, Bookmarks.class);
        intent.putExtra("Bookmark_List", json);
        startActivity(intent);
    }

    private void setHomepage() {

        Log.d("Home", "Hello World");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View myView = (inflater.inflate(R.layout.homepage_dialog, null));

        final EditText newHomepage = myView.findViewById(R.id.homepageChangeText);

        builder.setView(myView);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String newUrl = newHomepage.getText().toString();
                SharedPref.write(SharedPref.HOMEPAGE, newUrl);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.setTitle("Change Homepage?");
        dialog.show();

    }

    private void goHome() {
        String webpage = SharedPref.read(SharedPref.HOMEPAGE, null);
        WebView web = findViewById(R.id.webpage);

        if (!webpage.contains("https://") && (webpage.contains(".com") || webpage.contains(".co."))) {
            webpage = "https://" + webpage;
        }
        if (webpage.contains("http://") && (webpage.contains(".com") || webpage.contains(".co."))) {
            String x[] = webpage.split("//");
            webpage = "https://" + x[2];
        }
        if (!webpage.contains("https://") && !webpage.contains("http://") && !webpage.contains("www.") && (!webpage.contains(".com") || !webpage.contains(".co."))) {
            webpage = "https://www.google.com/search?q=" + webpage;
        }

        web.loadUrl(webpage);

        String webTitle = web.getTitle();
        String webAddress = web.getUrl();
        Website newWebsite = new Website(webTitle, webAddress);
        historyList.add(newWebsite);
    }

    @Override
    protected void onPause() {
        saveHistoryData();
        saveBookmarkData();
        saveWebpage();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}