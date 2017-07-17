/**
 * Copyright (c) 2004-present, Facebook, Inc. All rights reserved.
 * <p>
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 * <p>
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.facebook.samples.NativeAdSample.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.facebook.samples.NativeAdSample.R;
import com.facebook.samples.NativeAdSample.models.RecyclerPostItem;

import java.util.ArrayList;
import java.util.List;

public class NativeAdRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecyclerPostItem> mPostItems;
    private List<NativeAd> mAdItems;
    private NativeAdsManager mNativeAdsManager;

    private int adDisplayFrequency = 5;
    private static int POST_TYPE = 0;
    private static int AD_TYPE = 1;

    public NativeAdRecyclerAdapter(List<RecyclerPostItem> postItems, NativeAdsManager
            nativeAdsManager) {
        mNativeAdsManager = nativeAdsManager;
        mPostItems = postItems;
        mAdItems = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .recycler_ad_item, parent, false);
            return new AdHolder(inflatedView);
        } else {
            View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .recycler_post_item, parent, false);
            return new PostHolder(inflatedView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position % adDisplayFrequency == 0 ? AD_TYPE : POST_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == AD_TYPE) {
            NativeAd ad;

            if (mAdItems.size() > position / adDisplayFrequency) {
                ad = mAdItems.get(position / adDisplayFrequency);
            } else {
                ad = mNativeAdsManager.nextNativeAd();
                mAdItems.add(ad);
            }

            if (ad != null) {
                AdHolder adHolder = (AdHolder) holder;
                adHolder.tvAdTitle.setText(ad.getAdTitle());
                adHolder.tvAdBody.setText(ad.getAdBody());
                adHolder.tvAdSocialContext.setText(ad.getAdSocialContext());
                adHolder.mvAdMedia.setNativeAd(ad);
                adHolder.btnAdCallToAction.setText(ad.getAdCallToAction());
                NativeAd.Image adIcon = ad.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, adHolder.ivAdIcon);
                ad.registerViewForInteraction(adHolder.itemView);
            }
        } else {
            PostHolder postHolder = (PostHolder) holder;

            //Calculate where the next postItem index is by subtracting ads we've shown.
            int index = position - (position / adDisplayFrequency) - 1;

            RecyclerPostItem postItem = mPostItems.get(index);
            postHolder.tvPostContent.setText(postItem.getPostContent());
        }
    }

    @Override
    public int getItemCount() {
        return mPostItems.size() + mAdItems.size();
    }

    private static class PostHolder extends RecyclerView.ViewHolder {
        TextView tvPostContent;

        PostHolder(View view) {
            super(view);

            tvPostContent = (TextView) view.findViewById(R.id.tvPostContent);
        }
    }

    private static class AdHolder extends RecyclerView.ViewHolder {
        MediaView mvAdMedia;
        ImageView ivAdIcon;
        TextView tvAdTitle;
        TextView tvAdBody;
        TextView tvAdSocialContext;
        Button btnAdCallToAction;

        AdHolder(View view) {
            super(view);

            mvAdMedia = (MediaView) view.findViewById(R.id.native_ad_media);
            tvAdTitle = (TextView) view.findViewById(R.id.native_ad_title);
            tvAdBody = (TextView) view.findViewById(R.id.native_ad_body);
            tvAdSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
            btnAdCallToAction = (Button) view.findViewById(R.id.native_ad_call_to_action);
            ivAdIcon = (ImageView) view.findViewById(R.id.native_ad_icon);
        }
    }
}
