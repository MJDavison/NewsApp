package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Article>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int ARTICLE_LOADER_ID = 1;
    boolean isConnected;
    private static final String API_KEY = "453c106c-a6ef-456b-a096-d8c32e290b11";
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?";
    private TextView mEmptyListView;
    private ArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyListView = findViewById(R.id.emptyList);

        ConnectivityManager cm =
                (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork =
                cm.getActiveNetworkInfo();

        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            LoaderManager loaderManager = LoaderManager.getInstance(this);

            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
            Log.i(LOG_TAG, ".initLoader");
        } else {
            disableLoadingBar();
            mEmptyListView.setText(R.string.no_internet_connection);
        }

        // Find a reference to the {@link ListView} in the layout
        ListView articleListView = findViewById(R.id.list);
        articleListView.setEmptyView(mEmptyListView);

        // Create a new {@link ArrayAdapter} of articles

        mAdapter = new ArticleAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article currentArticle = (Article) parent.getItemAtPosition(position);
                Uri articleUri = Uri.parse(currentArticle.getArticleWebURL());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                startActivity(webIntent);
            }
        });
    }

    //https://content.guardianapis.com/search?api-key=test
    //https://content.guardianapis.com/search?api-key=test&show-tags=contributor&show-fields=thumbnail&tag=environment/recycling
    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader()");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String section = sharedPrefs.getString(
                getString(R.string.preferences_section_key),
                getString(R.string.preferences_section_default));

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("section", section.toLowerCase());
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("api-key", API_KEY);


        return new ArticleLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {
        disableLoadingBar();
        mEmptyListView.setText(R.string.no_articles_avaliable);


        mAdapter.clear();
        Log.i(LOG_TAG, "onLoadFinished()");

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        Log.i(LOG_TAG, "onLoadReset()");
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_preferences) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void disableLoadingBar() {
        View progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);
    }
}