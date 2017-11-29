package com.example.administrator.admobdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by Andy.Ren on 2017/5/4.
 */

public class BannerAdActivity extends BaseActivity{

    private static final String TAG = "BannerAdActivity";
    private AdView adView;
    private Button checkBtn;
    private boolean adIsOpen;
    private int x,y;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_ad);
        checkBtn = (Button) findViewById(R.id.check_btn);
        adView = (AdView) findViewById(R.id.adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(TAG, "onAdClosed");
            }

            /**
             * AdRequest.ERROR_CODE_INTERNAL_ERROR == 0
             * AdRequest.ERROR_CODE_INVALID_REQUEST == 1
             * AdRequest.ERROR_CODE_NETWORK_ERROR == 2
             * AdRequest.ERROR_CODE_NO_FILL == 3
             */
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e(TAG, "onAdFailedToLoad = " + i);
                checkBtn.setVisibility(View.VISIBLE);
                //loadAd();
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
                if(check()){
                    startShellThread();
                }else{
                    checkBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        loadAd();
    }

    private void startShellThread() {
        int[] location = new int[2];
        adView.getLocationOnScreen(location);
        x = location[0] + adView.getWidth()/2;
        y = location[1] + adView.getHeight()/2;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!adIsOpen){
                    setShell();
                }
            }
        }).start();
    }

    public void setShell(){
        Log.e(TAG, "setShell => " + x +" , " + y);
        ShellUtil.CommandResult commandResult = ShellUtil.execCommand("input tap " + x + " " + y,true);
        Log.i("francis","result--->" + commandResult.responseMsg);
        x = x + 50;
    }

    private void loadAd() {
        AdRequest request = new AdRequest.Builder()
                //.addTestDevice("814D383C6293AEB7C207B43DDC36A560")
                .build();
        adView.loadAd(request);
    }

    @Override
    protected void onStop() {
        adIsOpen = true;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adView!=null && adView.isShown()){
            adView.destroy();
        }
    }
}
