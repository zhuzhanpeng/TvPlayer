package com.onairm.tvbaselibrary.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String uploadUrl = "http://ic_launcher.168.2.104/receive.php";

    public static String projectNmae;
    public static final String KEY_PROJECT="proname";
    public static final String KEY_TIME="time";
    public static final String KEY_EXCEPTION="exception";
    public static final String KEY_MOBILE="mobile";
    public static final String KEY_VERSION="version";
    public static final String KEY_System="system";

    public static final String TAG = "CrashHandler";
    StringBuffer sbs = new StringBuffer();
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;

    //用于格式化日期
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context,String proName) {
        projectNmae = proName;
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        // TODO Auto-generated method stub
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }


    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        final Throwable exsThrowable = ex;
        // 3.把错误的堆栈信息 获取出来
        String errorinfo = getErrorInfo(ex);
        Log.e(TAG, "handleException:"+errorinfo);
        /*new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                showDialog(mContext, getErrorInfo(ex));
                Looper.loop();
            }
        }.start();*/
        sbs.append(errorinfo);

        return true;
    }

    private void showDialog(final Context context, String error) {
        final Dialog dialog;
        AlertDialog.Builder buder = new AlertDialog.Builder(context);
        buder.setTitle("温馨提示");
        buder.setMessage(error);
        buder.setPositiveButton("上传", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "---" + sbs.toString());
                new Thread(new Runnable() {

                    public void run() {
                            sendPost(uploadUrl, sbs.toString());
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }).start();

            }
        });
        buder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        dialog = buder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(false);//设置点击屏幕其他地方，dialog不消失
        dialog.setCancelable(false);//设置点击返回键和HOme键，dialog不消失
        dialog.show();

        Log.i("PLog", "2");
    }


    //打印错误信息
    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        arg1.printStackTrace(printWriter);
        Throwable cause = arg1.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String error = writer.toString();
        return error;
    }

    /**
     * 获取手机的硬件信息
     *
     * @return
     */
    private String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("" + Build.BRAND +  Build.MODEL);
        return sb.toString();
    }
    /**
     * 操作系统信息
     *
     * @return
     */
    private String getSystemInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("" + Build.VERSION.SDK_INT+"/"+Build.VERSION.RELEASE);
        return sb.toString();
    }

    /**
     * 获取软件的版本信息
     *
     * @return
     */
    private String getVersionInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }


    public InputStream sendPost(String url, String params)
    {
        URL realurl = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        try {
            realurl = new URL(url);
            conn = (HttpURLConnection) realurl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            StringBuilder sb = new StringBuilder();

            addProName(sb);
            addTime(sb);
            addException(sb,params);
            addMobile(sb,getMobileInfo());
            addVersion(sb,getVersionInfo());
            addSystem(sb,getSystemInfo());
            int len = sb.length();
            sb.deleteCharAt(len-1);

            pw.print(sb.toString());
            pw.flush();
            pw.close();
            in = conn.getInputStream();
        } catch (Exception e){
            e.printStackTrace();
        }
        return in;
    }

    private StringBuilder addKey(StringBuilder sb,String key){
        sb.append(key)
                .append("=");
        return sb;
    }
    private StringBuilder addValue(StringBuilder sb,String value){
        sb.append(value)
                .append("&");

        return sb;
    }
    private StringBuilder addProName(StringBuilder sb){
        addKey(sb,KEY_PROJECT);
        addValue(sb,projectNmae);
        return sb;
    }
    private StringBuilder addTime(StringBuilder sb){
        addKey(sb,KEY_TIME);
        addValue(sb,formatter.format(new Date()));
        return sb;
    }
    private StringBuilder addException(StringBuilder sb,String exception){
        addKey(sb,KEY_EXCEPTION);
        addValue(sb,exception);
        return sb;
    }
    private StringBuilder addVersion(StringBuilder sb,String version){
        addKey(sb,KEY_VERSION);
        addValue(sb,version);
        return sb;
    }
    private StringBuilder addMobile(StringBuilder sb,String mobile){
        addKey(sb,KEY_MOBILE);
        addValue(sb,mobile);
        return sb;
    }
    private StringBuilder addSystem(StringBuilder sb,String system){
        addKey(sb,KEY_System);
        addValue(sb,system);
        return sb;
    }
}