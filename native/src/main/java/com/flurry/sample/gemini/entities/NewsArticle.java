package com.flurry.sample.gemini.entities;

public class NewsArticle {
    private String mArticleTitle;
    private String mArticleContent;
    private String mArticleCreator;
    private int mArticleImageResourceId;

    public String getArticleTitle() {
        return mArticleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        mArticleTitle = articleTitle;
    }

    public String getArticleContent() {
        return mArticleContent;
    }

    public void setArticleContent(String articleContent) {
        mArticleContent = articleContent;
    }

    public int getArticleImageResourceId() {
        return mArticleImageResourceId;
    }

    public void setArticleImageResourceId(int articleImageResourceId) {
        mArticleImageResourceId = articleImageResourceId;
    }

    public String getArticleCreator() {
        return mArticleCreator;
    }

    public void setArticleCreator(String articleCreator) {
        mArticleCreator = articleCreator;
    }
}
