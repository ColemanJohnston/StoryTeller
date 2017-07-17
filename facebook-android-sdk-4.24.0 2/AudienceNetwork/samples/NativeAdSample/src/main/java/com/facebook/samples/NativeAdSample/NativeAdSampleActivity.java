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

package com.facebook.samples.NativeAdSample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAd;
import com.facebook.samples.ads.debugsettings.DebugSettingsActivity;

public class NativeAdSampleActivity extends Activity implements AdListener {

    protected static final String TAG = NativeAdSampleActivity.class.getSimpleName();

    private TextView nativeAdStatus;
    private LinearLayout nativeAdContainer;

    private Button showNativeAdButton;
    private Button showNativeAdHScrollButton;
    private Button showNativeAdTemplateButton;
    private Button showNativeAdRecyclerButton;

    private LinearLayout adView;
    private NativeAd nativeAd;
    private AdChoicesView adChoicesView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_demo);

        nativeAdContainer = (LinearLayout) findViewById(R.id.native_ad_container);

        LayoutInflater inflater = LayoutInflater.from(this);
        adView = (LinearLayout) inflater.inflate(R.layout.ad_unit, nativeAdContainer, false);
        nativeAdContainer.addView(adView);

        nativeAdStatus = (TextView) findViewById(R.id.native_ad_status);
        showNativeAdButton = (Button) findViewById(R.id.load_native_ad_button);
        showNativeAdHScrollButton = (Button) findViewById(R.id.load_native_ad_hscroll);
        showNativeAdTemplateButton = (Button) findViewById(R.id.load_native_ad_template_button);
        showNativeAdRecyclerButton = (Button) findViewById(R.id.load_native_ad_recycler_button);

        showNativeAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nativeAdStatus.setText("Requesting an ad...");

                // Create a native ad request with a unique placement ID (generate your own on the
                // Facebook app settings). Use different ID for each ad placement in your app.
                nativeAd = new NativeAd(NativeAdSampleActivity.this, "YOUR_PLACEMENT_ID");

                // Set a listener to get notified when the ad was loaded.
                nativeAd.setAdListener(NativeAdSampleActivity.this);

                // When testing on a device, add its hashed ID to force test ads.
                // The hash ID is printed to log cat when running on a device and loading an ad.
                // AdSettings.addTestDevice("THE HASHED ID AS PRINTED TO LOG CAT");

                // Initiate a request to load an ad.
                nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
            }
        });

        showNativeAdHScrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NativeAdSampleActivity.this,
                        NativeAdHscrollActivity.class);
                NativeAdSampleActivity.this.startActivity(intent);
            }
        });

        showNativeAdTemplateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NativeAdSampleActivity.this,
                        NativeAdTemplateActivity.class);
                NativeAdSampleActivity.this.startActivity(intent);
            }
        });
        showNativeAdRecyclerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NativeAdSampleActivity.this,
                        NativeAdRecyclerActivity.class);
                NativeAdSampleActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.native_ad_sample_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.debug_settings) {
            startActivity(new Intent(getApplicationContext(), DebugSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onError(Ad ad, AdError error) {
        nativeAdStatus.setText("Ad failed to load: " + error.getErrorMessage());
    }

    @Override
    public void onAdClicked(Ad ad) {
        Toast.makeText(this, "Ad Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        Log.d(TAG, "onLoggingImpression");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (nativeAd == null || nativeAd != ad) {
            // Race condition, load() called again before last ad was displayed
            return;
        }

        // Unregister last ad
        nativeAd.unregisterView();

        nativeAdStatus.setText("");

        // Using the AdChoicesView is optional, but your native ad unit should
        // be clearly delineated from the rest of your app content. See
        // https://developers.facebook.com/docs/audience-network/guidelines/native-ads#native
        // for details. We recommend using the AdChoicesView.
        if (adChoicesView == null) {
            adChoicesView = new AdChoicesView(this, nativeAd, true);
            adView.addView(adChoicesView, 0);
        }

        inflateAd(nativeAd, adView, this);

        // Registering a touch listener to log which ad component receives the touch event.
        // We always return false from onTouch so that we don't swallow the touch event (which
        // would prevent click events from reaching the NativeAd control).
        // The touch listener could be used to do animations.
        nativeAd.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int i = view.getId();
                    if (i == R.id.native_ad_call_to_action) {
                        Log.d(TAG, "Call to action button clicked");
                    } else if (i == R.id.native_ad_media) {
                        Log.d(TAG, "Main image clicked");
                    } else {
                        Log.d(TAG, "Other ad component clicked");
                    }
                }
                return false;
            }
        });
    }

    public static void inflateAd(NativeAd nativeAd, View adView, final Context context) {
        // Create native UI using the ad metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
        nativeAdMedia.setListener(new MediaViewListener() {
            @Override
            public void onVolumeChange(MediaView mediaView, float volume) {
                Log.i(NativeAdSampleActivity.class.toString(), "MediaViewEvent: Volume " + volume);
            }

            @Override
            public void onPause(MediaView mediaView) {
                Log.i(NativeAdSampleActivity.class.toString(), "MediaViewEvent: Paused");
            }

            @Override
            public void onPlay(MediaView mediaView) {
                Log.i(NativeAdSampleActivity.class.toString(), "MediaViewEvent: Play");
            }

            @Override
            public void onFullscreenBackground(MediaView mediaView) {
                Log.i(NativeAdSampleActivity.class.toString(), "MediaViewEvent: FullscreenBackground");
            }

            @Override
            public void onFullscreenForeground(MediaView mediaView) {
                Log.i(NativeAdSampleActivity.class.toString(), "MediaViewEvent: FullscreenForeground");
            }

            @Override
            public void onExitFullscreen(MediaView mediaView) {
                Log.i(NativeAdSampleActivity.class.toString(), "MediaViewEvent: ExitFullscreen");
            }

            @Override
            public void onEnterFullscreen(MediaView mediaView) {
                Log.i(NativeAdSampleActivity.class.toString(), "MediaViewEvent: EnterFullscreen");
            }

            @Override
            public void onComplete(MediaView mediaView) {
                Log.i(NativeAdSampleActivity.class.toString(), "MediaViewEvent: Completed");
            }
        });
        nativeAdMedia.setAutoplay(AdSettings.isVideoAutoplay());
        nativeAdMedia.setAutoplayOnMobile(AdSettings.isVideoAutoplayOnMobile());
        TextView nativeAdSocialContext =
                (TextView) adView.findViewById(R.id.native_ad_social_context);
        Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

        // Setting the Text
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(View.VISIBLE);
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());

        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Downloading and setting the cover image.
        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        int bannerWidth = adCoverImage.getWidth();
        int bannerHeight = adCoverImage.getHeight();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int mediaWidth = adView.getWidth() > 0 ? adView.getWidth() : metrics.widthPixels;
        nativeAdMedia.setLayoutParams(new LinearLayout.LayoutParams(
                mediaWidth,
                Math.min(
                        (int) (((double) mediaWidth / (double) bannerWidth) * bannerHeight),
                        metrics.heightPixels / 3)));
        nativeAdMedia.setNativeAd(nativeAd);

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable.
        nativeAd.registerViewForInteraction(adView);

        // You can replace the above call with the following call to specify the clickable areas.
        // nativeAd.registerViewForInteraction(adView,
        //     Arrays.asList(nativeAdCallToAction, nativeAdMedia));
    }
}
