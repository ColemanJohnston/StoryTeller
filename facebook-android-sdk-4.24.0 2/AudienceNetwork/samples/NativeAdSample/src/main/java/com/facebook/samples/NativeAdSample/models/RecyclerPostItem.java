package com.facebook.samples.NativeAdSample.models;

public class RecyclerPostItem {

    private String postContent;

    public RecyclerPostItem(String postContent) {
        setPostContent(postContent);
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }
}
