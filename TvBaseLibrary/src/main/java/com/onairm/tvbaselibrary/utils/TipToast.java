package com.onairm.tvbaselibrary.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public final class TipToast {

    private static Toast toast = null;
    private static Object sysnObject = new Object();
    private static Handler handler = null;
    private static Context context;

    public static void init(Context context) {
        TipToast.context = context;
        handler = new Handler(context.getMainLooper());
    }

    private static void tip(final String message, int duration) {
        new ToastThread(new StringToastRunnable(context, message, duration)).start();
    }

    private static void tip(final int msgId, int duration) {
        new ToastThread(new StringIdToastRunnable(context, msgId, duration)).start();
    }

    public static void tip(final String message) {
        tip(message, Toast.LENGTH_SHORT);
    }

    public static void tip(final int msgId) {
        tip(msgId, Toast.LENGTH_SHORT);
    }

    public static void longTip(String message) {
        tip(message, Toast.LENGTH_LONG);
    }

    public static void longTip(int msgId) {
        tip(msgId, Toast.LENGTH_LONG);
    }

    public static void shortTip(String message) {
        tip(message, Toast.LENGTH_SHORT);
    }

    public static void shortTip(int msgId) {
        tip(msgId, Toast.LENGTH_SHORT);
    }

    static class ToastThread extends Thread {
        public ToastThread(Runnable runnable) {
            super(runnable);
        }
    }

    static class StringToastRunnable implements Runnable {

        Context context;
        String msg;
        int duration;

        public StringToastRunnable(Context context, String msg, int duration) {
            this.context = context;
            this.msg = msg;
            this.duration = duration;
        }

        @Override
        public void run() {

            handler.post(new Runnable() {

                @Override
                public void run() {
                    synchronized (sysnObject) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(context, msg, duration);
                        toast.show();
                    }
                }
            });
        }
    }

    static class StringIdToastRunnable implements Runnable {

        Context context;
        int msgId;
        int duration;

        public StringIdToastRunnable(Context context, int msgId, int duration) {
            this.context = context;
            this.msgId = msgId;
            this.duration = duration;
        }

        @Override
        public void run() {
            synchronized (sysnObject) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(context, msgId, duration);
                        toast.show();
                    }
                });
            }
        }
    }
}
