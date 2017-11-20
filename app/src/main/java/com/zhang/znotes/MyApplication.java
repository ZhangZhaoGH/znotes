package com.zhang.znotes;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.zhang.znotes.log.CrashExceptionHandler;

import org.litepal.LitePal;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;


/**
 * Created by zz on 2017/2/20.
 */

public class MyApplication extends Application {
    private static Context mContext;
    /** 是否输出日志信息 **/
    public final static boolean isDebug=true;
    /**
     * 是否展示删除确认提示
     */
    public static boolean isShowDel=true;
    public final static String APP_NAME="ZNotes";
    public final static String HEAD="head";
    public final static String BGIMG="bgimg";
    public static String appVersion;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        LitePal.initialize(this);
        SQLiteDatabase db= LitePal.getDatabase();


        setBmob();

        CrashExceptionHandler exceptionHandler=CrashExceptionHandler.getInstance();
        exceptionHandler.init(mContext);

        setHotFix();
    }



    /**
     * 设置热修复初始化
     */
    private void setHotFix() {
        // initialize最好放在attachBaseContext最前面
        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            // SophixManager.getInstance().cleanPatches();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();
// queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();

    }

    /**
     * bmob初始化
     */
    private void setBmob() {
        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        BmobConfig config =new BmobConfig.Builder(this)
                ////设置appkey
                .setApplicationId("3a260ca0be714acc82995f109cc0ca94")
                ////请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                ////文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024*1024)
                ////文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
    }

    public static Context getContext(){
        return mContext;
    }

    private static MyApplication mInstance;
    public static MyApplication getInstance(){
        if (mInstance == null){
            mInstance=new MyApplication();
        }
        return mInstance;
    }
    public static final String DIR_LOG = "log"; // 日志文件目录
    public String getLogCacheDir() {
        String path = getSdcardCacheDir() + File.separator + DIR_LOG;
        File logDir = new File(path);

        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        return path;
    }

    private String getSdcardCacheDir() {
        File cacheDir = getExternalCacheDir();
        if (cacheDir == null || !cacheDir.exists()) {
            cacheDir = new File(Environment.getExternalStorageDirectory().toString()
                    + "/Android/data/" + getPackageName() + "/cache");
            cacheDir.mkdirs();
        }
        return cacheDir.getAbsolutePath();
    }
}
