package com.zhang.znotes.log;

import android.content.Context;
import android.os.Looper;

import com.zhang.znotes.MyApplication;
import com.zhang.znotes.utils.CommonUtil;


/**
 * Created by lpf on 2016/8/13.
 */
public class CrashExceptionHandler  implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashExceptionHandler singletonInstance = new CrashExceptionHandler();
    private Context mContext;

    private CrashExceptionHandler() {
    }

    public static CrashExceptionHandler getInstance() {
        return singletonInstance;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
//            if (throwable != null && !Const.DEBUG) {
//                MobclickAgent.reportError(mContext, Log.getStackTrace(throwable));
//            }

            if (!handleException(throwable) && mDefaultHandler != null) {
                // 如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(thread, throwable);
            } else {
                // Sleep一会后结束程序
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.error(CrashExceptionHandler.class, e.toString());
                }
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private boolean handleException(Throwable throwable) {
        if (throwable == null) {
            return true;
        }
        Log.error(CrashExceptionHandler.class, collectCrashDeviceInfo(), throwable);
        Log.getInstance().closeLogFile();

        if (MyApplication.isDebug) {
            //使用Toast来显示异常信息
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
//                    Toast.makeText(mContext, R.string.crash_message, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }.start();
        }
        return true;
    }

    /**
     * 收集设备信息
     */
    public String collectCrashDeviceInfo() {
        StringBuilder builder = new StringBuilder();

        String appVersion = CommonUtil.getAppVersion();
        builder.append("VERSION=").append(appVersion).append('\n');
//        String imei = Util.getIMEI();

//        if (imei != null) {
//            builder.append("IMEI=").append(imei).append('\n');
//        }
        try {
            builder.append("SDK=").append(CommonUtil.getSdkVersion()).append('\n');
//            builder.append("Modle=").append(Util.getDeviceName()).append('\n');
            builder.append("Release=").append(android.os.Build.VERSION.RELEASE).append('\n');
            builder.append("Display=").append(android.os.Build.DISPLAY).append('\n');
            builder.append("Fingerprint=").append(android.os.Build.FINGERPRINT).append('\n');
            builder.append("Manufacturer=").append(android.os.Build.MANUFACTURER).append('\n');
            builder.append("Type=").append(android.os.Build.TYPE).append('\n');

        } catch (Exception e) {
            Log.debug(CrashExceptionHandler.class, e.toString());
        }
        return builder.toString();
    }

}
