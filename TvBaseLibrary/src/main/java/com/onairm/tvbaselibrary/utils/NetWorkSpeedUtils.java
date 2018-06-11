package com.onairm.tvbaselibrary.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class NetWorkSpeedUtils {
    private Context context;
    private Handler mHandler;
  
    private long lastTotalRxBytes = 0;  
    private long lastTimeStamp = 0;  
    private Timer mTimer;
    public NetWorkSpeedUtils(Context context, Handler mHandler){  
        this.context = context;  
        this.mHandler = mHandler;  
    }  
  
    TimerTask task = new TimerTask() {
        @Override  
        public void run() {  
            showNetSpeed();  
        }  
    };  
  
    public void startShowNetSpeed(){  
        lastTotalRxBytes = getTotalRxBytes();  
        lastTimeStamp = System.currentTimeMillis();  
        mTimer = new Timer();
        mTimer.schedule(task, 1000, 1000); // 1s后启动任务，每2s执行一次
  
    }  
  
    private long getTotalRxBytes() {  
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
    }  
  
    private void showNetSpeed() {  
        long nowTotalRxBytes = getTotalRxBytes();  
        long nowTimeStamp = System.currentTimeMillis();  
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换  
        long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 % (nowTimeStamp - lastTimeStamp));//毫秒转换  
  
        lastTimeStamp = nowTimeStamp;  
        lastTotalRxBytes = nowTotalRxBytes;  

        if(null==mHandler){
            return;
        }
        Message msg = mHandler.obtainMessage();
        msg.what = 100;  
        msg.obj = String.valueOf(speed)/* + "." + String.valueOf(speed2)*/ + "k/s";
        mHandler.sendMessage(msg);//更新界面  
    }
    public void stopSpeed(){
        mTimer.cancel();
    }
} 