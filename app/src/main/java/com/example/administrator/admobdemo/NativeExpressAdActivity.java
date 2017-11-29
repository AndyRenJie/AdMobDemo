package com.example.administrator.admobdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;

/**
 * Created by Andy.Ren on 2017/5/4.
 */

public class NativeExpressAdActivity extends BaseActivity{

    private NativeExpressAdView expressAdView;
    private Button checkBtn;
    private static final String TAG = "NativeExpressAdActivity";
    private boolean adIsOpen;
    private int x,y;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_express_ad);
        checkBtn = (Button) findViewById(R.id.check_btn);
        expressAdView = (NativeExpressAdView) findViewById(R.id.nativeAdView);
        expressAdView.setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build());
        final VideoController controller = expressAdView.getVideoController();
        controller.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                super.onVideoEnd();
                Log.e(TAG, "onVideoEnd");
            }
        });
        expressAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(TAG, "Native onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e(TAG, "Native onAdFailedToLoad => " + i);
                checkBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.e(TAG, "Native onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.e(TAG, "Native onAdOpened");
                adIsOpen = true;
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(controller.hasVideoContent()){
                    Log.e(TAG, "onAdLoaded => has an video ads");
                }else{
                    Log.e(TAG, "onAdLoaded => has not an video ads");
                }
                if(check()){
                    startShellThread();
                }else{
                    checkBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        loadAd();
    }

    private void loadAd() {
        //expressAdView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
        expressAdView.loadAd(new AdRequest.Builder().build());
    }

    private void startShellThread() {
        int[] location = new int[2];
        expressAdView.getLocationOnScreen(location);
        x = location[0] + expressAdView.getWidth()/2;
        y = location[1] + expressAdView.getHeight()-expressAdView.getHeight()/6;
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
        Log.e(TAG, "setShell => " + x +" , " + y);
        ShellUtil.CommandResult commandResult = ShellUtil.execCommand("input tap " + x + " " + y,true);
        Log.i("francis","result--->" + commandResult.responseMsg);
    }


    @Override
    protected void onStop() {
        adIsOpen = true;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(expressAdView!=null && expressAdView.isShown()){
            expressAdView.destroy();
        }
    }
}
