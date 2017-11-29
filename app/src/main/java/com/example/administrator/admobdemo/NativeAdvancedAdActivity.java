package com.example.administrator.admobdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.List;
import java.util.Locale;

/**
 * Created by Andy.Ren on 2017/5/4.
 */

public class NativeAdvancedAdActivity extends BaseActivity{

    private Button mRefresh;
    private CheckBox mRequestAppInstallAds;
    private CheckBox mRequestContentAds;
    private CheckBox mStartVideoAdsMuted;
    private Button checkBtn;
    private TextView mVideoStatus;
    private static final String TAG = "NativeAdvancedAdActivity";
    private boolean adIsOpen;
    private NativeAppInstallAdView installAdView;
    private NativeContentAdView contentAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_advanced_ad);
        checkBtn = (Button) findViewById(R.id.check_btn);
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));

        mRefresh = (Button) findViewById(R.id.btn_refresh);
        mRequestAppInstallAds = (CheckBox) findViewById(R.id.cb_appinstall);
        mRequestContentAds = (CheckBox) findViewById(R.id.cb_content);
        mStartVideoAdsMuted = (CheckBox) findViewById(R.id.cb_start_muted);
        mVideoStatus = (TextView) findViewById(R.id.tv_video_status);

        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAd(mRequestAppInstallAds.isChecked(), mRequestContentAds.isChecked());
            }
        });
        refreshAd(mRequestAppInstallAds.isChecked(), mRequestContentAds.isChecked());
    }

    /**
     * 安装广告视图
     * @param nativeAppInstallAd
     * @param adView
     */
    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                mRefresh.setEnabled(true);
                mVideoStatus.setText("Video status: Video playback has ended.");
                super.onVideoEnd();
            }
        });

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        // The MediaView will display a video asset if one is present in the ad, and the first image
        // asset otherwise.
        MediaView mediaView = (MediaView) adView.findViewById(R.id.appinstall_media);
        adView.setMediaView(mediaView);

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            mVideoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));
        } else {
            mRefresh.setEnabled(true);
            mVideoStatus.setText("Video status: Ad does not contain a video asset.");
        }

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    /**
     * 内容广告视图
     * @param nativeContentAd
     * @param adView
     */
    private void populateContentAdView(NativeContentAd nativeContentAd,
                                       NativeContentAdView adView) {
        mVideoStatus.setText("Video status: Ad does not contain a video asset.");
        mRefresh.setEnabled(true);

        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }

    /**
     * 刷新广告
     * @param requestAppInstallAds
     * @param requestContentAds
     */
    private void refreshAd(final boolean requestAppInstallAds, final boolean requestContentAds) {
        if (!requestAppInstallAds && !requestContentAds) {
            Toast.makeText(this, "At least one ad format must be checked to request an ad.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mRefresh.setEnabled(false);

//        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.native_advanved_ad_unit_id));

//        VideoOptions videoOptions = new VideoOptions.Builder()
//                .setStartMuted(mStartVideoAdsMuted.isChecked())
//                .build();
//
//        NativeAdOptions adOptions = new NativeAdOptions.Builder()
//                .setVideoOptions(videoOptions)
//                .build();
        AdLoader adLoader = new AdLoader.Builder(this,getResources().getString(R.string.native_advanved_ad_unit_id))
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                        Log.e(TAG, "onAppInstallAdLoaded");
                        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_adplaceholder);
                        installAdView = (NativeAppInstallAdView) getLayoutInflater().inflate(R.layout.ad_install, null);
                        populateAppInstallAdView(nativeAppInstallAd, installAdView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(installAdView);
                    }
                })
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                        Log.e(TAG, "onContentAdLoaded");
                        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_adplaceholder);
                        contentAdView = (NativeContentAdView) getLayoutInflater().inflate(R.layout.ad_content, null);
                        populateContentAdView(nativeContentAd, contentAdView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(contentAdView);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        super.onAdFailedToLoad(errorCode);
                        mRefresh.setEnabled(true);
                        Toast.makeText(NativeAdvancedAdActivity.this, "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show();
                        checkBtn.setVisibility(View.VISIBLE);
                        //refreshAd(requestAppInstallAds, requestContentAds);
                    }

                    @Override
                    public void onAdLeftApplication() {
                        super.onAdLeftApplication();
                        Log.e(TAG, "onAdLeftApplication");
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        Log.e(TAG, "onAdOpened");
                        adIsOpen = true;
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.e(TAG, "onAdLoaded");
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

//        if (requestAppInstallAds) {
//            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
//                @Override
//                public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
//
//                }
//            });
//        }

//        if (requestContentAds) {
//            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
//                @Override
//                public void onContentAdLoaded(NativeContentAd ad) {
//
//                }
//            });
//        }

//        builder.withNativeAdOptions(adOptions);

//        AdLoader adLoader = builder.withAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//
//            }
//        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
        mVideoStatus.setText("");
    }

    private void startShellThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!adIsOpen){
                    setShell();
                }
            }
        }).start();
    }

    private void setShell(){
        Log.e(TAG, "setShell");
        ShellUtil.CommandResult commandResult = ShellUtil.execCommand("input tap 168 452",true);
        Log.i("francis","result--->" + commandResult.responseMsg);
    }

    @Override
    public void onBackPressed() {
        adIsOpen = true;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(installAdView!=null && installAdView.isShown()){
            installAdView.destroy();
        }
        if(contentAdView!=null && contentAdView.isShown()){
            contentAdView.destroy();
        }
    }
}
