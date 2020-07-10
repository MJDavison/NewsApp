package com.example.android.newsapp;

public class Article {
    final String articleSectionName;
    final String articleTitle;
    final String articleWebURL;
    final String articleThumbnail;
    final String articleWriter;
    final long articleReleaseDateInMilliseconds;

    public Article(String sectionName, String articleTitle, String articleWebURL, String articleThumbnail, String articleWriter, long articleReleaseDateInMilliseconds) {
        this.articleSectionName = sectionName;
        this.articleTitle = articleTitle;
        this.articleWebURL = articleWebURL;
        this.articleThumbnail = articleThumbnail;
        this.articleWriter = articleWriter;
        this.articleReleaseDateInMilliseconds = articleReleaseDateInMilliseconds;
    }

    public String getArticleSectionName() {
        return articleSectionName;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleWebURL() {
        return articleWebURL;
    }

    public String getArticleWriter() {
        return articleWriter;
    }

    public long getArticleReleaseDateInMilliseconds() {
        return articleReleaseDateInMilliseconds;
    }
}
