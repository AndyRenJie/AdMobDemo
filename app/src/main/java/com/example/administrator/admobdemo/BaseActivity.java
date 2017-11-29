package com.example.administrator.admobdemo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Created by Andy.Ren on 2017/5/4.
 */

public class BaseActivity extends AppCompatActivity{

    private static final String TAG = "BaseActivity";
    private int width,height;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        //每10s产生一次点击事件，点击的点坐标为(0.2W - 0.8W,0.2H - 0.8 H),W/H为手机分辨率的宽高.
//        timee = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    //生成点击坐标
//                    int x = (int) (width * 0.5);
//                    int y = (int) (height * 0.5);
//                    //利用ProcessBuilder执行shell命令
//                    String[] order = {
//                            "input",
//                            "tap",
//                            "" + x,
//                            "" + y
//                    };
//                    try {
//                        new ProcessBuilder(order).start();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    //线程睡眠10s
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }

    /**
     * 模拟点击某个指定坐标作用在View上
     * @param view
     * @param x
     * @param y
     */
    public void clickView(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(
                downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
        downTime+=10;
        final MotionEvent upEvent = MotionEvent.obtain(
                downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }

    /**
     * 打印点击的点的坐标
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Log.e(TAG, "onTouchEvent: "+"X at " + x + ";Y at " + y);
        return true;
    }

    public boolean check(){
        Random random = new Random();
        int temp = random.nextInt(20);//0--50
        Log.e(TAG, "check => " + temp);
        if (temp >=0 && temp <= 9){
            return true;
        }
        return false;
    }
}
