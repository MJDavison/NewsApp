package com.example.android.newsapp;

public class Article {
    final String sectionName;
    final String articleTitle;
    final String articleWebURL;
    final String articleThumbnail;
    final String articleWriter;
    final String articleReleaseDate;

    public Article(String sectionName, String articleTitle, String articleWebURL, String articleThumbnail, String articleWriter, String articleReleaseDate) {
        this.sectionName = sectionName;
        this.articleTitle = articleTitle;
        this.articleWebURL = articleWebURL;
        this.articleThumbnail = articleThumbnail;
        this.articleWriter = articleWriter;
        this.articleReleaseDate = articleReleaseDate;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleWebURL() {
        return articleWebURL;
    }

    public String getArticleThumbnail() {
        return articleThumbnail;
    }

    public String getArticleWriter(){
        return articleWriter;
    }

    public String getArticleReleaseDate() {
        return articleReleaseDate;
    }
}
