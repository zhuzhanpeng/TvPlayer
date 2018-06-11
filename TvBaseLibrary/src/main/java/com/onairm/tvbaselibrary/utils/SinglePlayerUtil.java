package com.onairm.tvbaselibrary.utils;

import android.util.SparseArray;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Edison on 2018/2/26.
 */

public class SinglePlayerUtil {
    private static IjkMediaPlayer mediaPlayer;
    public static boolean isFirst=false;
    public static IjkMediaPlayer getMediaPlayer(){
        if (mediaPlayer==null){
            mediaPlayer=new IjkMediaPlayer();
            isFirst=true;
        }else{
            isFirst=false;
        }

        return mediaPlayer;
    }
    public static PlayerBean getPlayerBean(int hashcode){
        return container.get(hashcode);
    }
    public static void setPlayerBean(int hashcode,PlayerBean value){
        container.put(hashcode,value);
    }

    private static SparseArray<PlayerBean> container=new SparseArray<>();


    public static int getPlayCount(){
        return container.size();
    }

    public static void release() {
        if(null != mediaPlayer){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    public static IjkMediaPlayer getPlayer(){
        return mediaPlayer;
    }
}
