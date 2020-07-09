package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ArticleAdapter extends ArrayAdapter<Article> {
    public ArticleAdapter(@NonNull Context context, int resource, ArrayList<Article> articles) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Article currentArticle = getItem(position);

        // this.sectionName = sectionName;
        //        this.articleTitle = articleTitle;
        //        this.articleWebURL = articleWebURL;
        //        this.articleThumbnail = articleThumbnail;
        ImageView imageThumbnail = listItemView.findViewById(R.id.imageThumbnail);
        TextView textArticleTitle = listItemView.findViewById(R.id.text_ArticleTitle);
        TextView textDatePosted = listItemView.findViewById(R.id.text_ArticleReleaseDate);
        TextView textArticleContributor = listItemView.findViewById(R.id.text_ArticleContributor);

        //imageThumbnail = currentArticle.getArticleThumbnail();
        textArticleTitle.setText(currentArticle.getArticleTitle());
        String[] time = currentArticle.getArticleReleaseDate().split("T");
        textDatePosted.setText(time[0] +"\n" + time[1]);
        textArticleContributor.setText(currentArticle.getArticleWriter());
        return listItemView;
    }
}
