package com.example.android.newsapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    private static final String LOG_TAG = ArticleLoader.class.getSimpleName();
    private final String mURL;

    public ArticleLoader(@NonNull Context context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "onStartLoading()");
        forceLoad();
    }

    @Nullable
    @Override
    public List<Article> loadInBackground() {
        Log.i(LOG_TAG, "loadInBackground()");
        if (mURL == null) {
            return null;
        }
        return UtilsActivity.fetchArticleData(mURL);
    }
}