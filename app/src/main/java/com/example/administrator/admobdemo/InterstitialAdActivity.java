package com.example.administrator.admobdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Andy.Ren on 2017/5/4.
 */

public class InterstitialAdActivity extends BaseActivity{

    private static final String TAG = "InterstitialAdActivity";
    private InterstitialAd interstitialAd;
    private boolean adIsOpen;
    private Button checkBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad);
        checkBtn = (Button) findViewById(R.id.check_btn);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(TAG, "onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e(TAG, "onAdFailedToLoad => " + i);
                checkBtn.setVisibility(View.VISIBLE);
                //loadAd();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.e(TAG, "onAdLeftApplication");
                adIsOpen = true;
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.e(TAG, "onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e(TAG, "onAdLoaded");
                interstitialAd.show();
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
        Log.e(TAG, "setShell");
        ShellUtil.CommandResult commandResult = ShellUtil.execCommand("input tap 168 252",true);
        Log.i("francis","result--->" + commandResult.responseMsg);
    }

    private void loadAd() {
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onBackPressed() {
        adIsOpen = true;
        super.onBackPressed();
    }
}
