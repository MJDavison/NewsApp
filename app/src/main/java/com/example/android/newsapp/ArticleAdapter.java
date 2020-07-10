package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArticleAdapter extends ArrayAdapter<Article> {
    Article currentArticle;

    public ArticleAdapter(@NonNull Context context, int resource, ArrayList<Article> articles) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        currentArticle = getItem(position);

        assert currentArticle != null;
        new DownloadImageFromInternet((ImageView) listItemView.findViewById(R.id.imageThumbnail))
                .execute(currentArticle.articleThumbnail);


        TextView textArticleTitle = listItemView.findViewById(R.id.text_ArticleTitle);
        TextView textDatePosted = listItemView.findViewById(R.id.text_ArticleReleaseDate);
        TextView textArticleContributor = listItemView.findViewById(R.id.text_ArticleContributor);
        TextView textArticleSection = listItemView.findViewById(R.id.text_ArticleSection);


        textArticleTitle.setText(currentArticle.getArticleTitle());
        textArticleContributor.setText(currentArticle.getArticleWriter());
        long epochTime = currentArticle.getArticleReleaseDateInMilliseconds();
        Date date = new Date(epochTime);
        textDatePosted.setText(formatDate(date));
        textArticleSection.setText(currentArticle.getArticleSectionName());


        return listItemView;
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy");
        return dateFormatter.format(date);
    }


    private String formatSeconds(Date dateObject) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("mm");
        return timeFormatter.format(dateObject);
    }

    private String formatHours(Date dateObject) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH");
        return timeFormatter.format(dateObject);
    }

    private static class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getLocalizedMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }


    }
}
