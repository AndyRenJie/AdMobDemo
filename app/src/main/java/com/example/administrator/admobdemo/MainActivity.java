package com.example.administrator.admobdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.banner_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BannerAdActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.native_express_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NativeExpressAdActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.native_advanced_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NativeAdvancedAdActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.interstitial_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,InterstitialAdActivity.class);
                startActivity(intent);
            }
        });
        //MobileAds.initialize(this,"ca-app-pub-1736034326014887~3176278252");
        //MobileAds.initialize(this, getResources().getString(R.string.banner_ad_unit_id));
    }
}
