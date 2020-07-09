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
import android.widget.ProgressBar;
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
    private static final int EARTHQUAKE_LOADER_ID = 1;
    boolean isConnected;
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?api-key=453c106c-a6ef-456b-a096-d8c32e290b11";
    private TextView mEmptyListView;
    private ArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            LoaderManager.getInstance(this).initLoader(EARTHQUAKE_LOADER_ID, null, this);
            Log.i(LOG_TAG, ".initLoader");
        }

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        mEmptyListView = (TextView) findViewById(R.id.emptyList);
        earthquakeListView.setEmptyView(mEmptyListView);

        // Create a new {@link ArrayAdapter} of earthquakes

        mAdapter = new ArticleAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article currentArticle = (Article) parent.getItemAtPosition(position);
                Uri earthquakeUri = Uri.parse(currentArticle.getArticleWebURL());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
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

        String tags = sharedPrefs.getString(
                getString(R.string.settings_tags_key),
                getString(R.string.settings_tags_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));


        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields","thumbnail");
        uriBuilder.appendQueryParameter("tags",tags);
        //uriBuilder.appendQueryParameter("orderby",orderBy);

        return new ArticleLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {
        if (isConnected)
            mEmptyListView.setText("Eh? The earthquake list is empty?! \n\nThat's... good, right?");
        else
            mEmptyListView.setText("Eh... you should probably connect to the internet first.");

        ProgressBar progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);
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
        if(id==R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}