package com.zhang.znotes.log;

import android.content.Context;

import com.zhang.znotes.MyApplication;
import com.zhang.znotes.utils.CommonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * Created by lpf on 2016/8/13.
 */
public class Log {
    private static boolean DEBUG = MyApplication.isDebug;
    private static final String NAME_LOG_FILE = "log.txt";
    private static final String NAME_BACKUP_LOG_FILE = "logbackup.txt";

    private Object lock = new Object();

    private final int KB = 1024;
    private final int M = KB * KB;
    private long maxFileSize = M * 4;

    private File logFile;
    private OutputStream os;
    private String logDirPath;

    private static Log mInstance;

    private Log() {
    }

    public static Log getInstance() {
        if (mInstance == null) {
            mInstance = new Log();
        }
        return mInstance;
    }

    public void init() {
        initLogFile();
    }

    /**
     * Send an INFO log message
     *
     * @param sClass The class object
     * @param msg    The message you would like logged.
     */
    public static void info(Class<?> sClass, String msg) {
        try {
            if (DEBUG) {
                android.util.Log.i(sClass.getSimpleName(), msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a INFO log message and log the exception
     *
     * @param sClass The class object
     * @param msg    The message you would like logged.
     * @param tr     An exception to log
     */
    public static void info(Class<?> sClass, String msg, Throwable tr) {
        try {
            if (DEBUG) {
                android.util.Log.i(sClass.getSimpleName(), msg, tr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a DEBUG log message
     *
     * @param sClass The class object
     * @param msg    The message you would like logged.
     */
    public static void debug(Class<?> sClass, String msg) {
        try {
            if (DEBUG) {
                android.util.Log.d(sClass.getSimpleName(), msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a DEBUG log message and log the exception
     *
     * @param sClass The class object
     * @param msg    The message you would like logged.
     * @param tr     An exception to log
     */
    public static void debug(Class<?> sClass, String msg, Throwable tr) {
        try {
            if (DEBUG) {
                android.util.Log.d(sClass.getSimpleName(), msg, tr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a WARN log message and log the exception
     *
     * @param sClass The class object
     * @param msg    The message you would like logged.
     */
    public static void warn(Class<?> sClass, String msg) {
        try {
            if (DEBUG) {
                android.util.Log.w(sClass.getSimpleName(), msg);
                Log.getInstance().saveLogToFile(sClass, msg, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a WARN log message and log the exception
     *
     * @param sClass The class object
     * @param msg    The message you would like logged.
     * @param tr     An exception to log
     */
    public static void warn(Class<?> sClass, String msg, Throwable tr) {
        try {
            android.util.Log.w(sClass.getSimpleName(), msg, tr);
            if (DEBUG) {
                Log.getInstance().saveLogToFile(sClass, msg, tr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send an ERROR log message
     *
     * @param sClass The class object
     * @param msg    The message you would like logged.
     */
    public static void error(Class<?> sClass, String msg) {
        try {
            android.util.Log.e(sClass.getSimpleName(), msg);
            if (DEBUG) {
                Log.getInstance().saveLogToFile(sClass, msg, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a ERROR log message and log the exception
     *
     * @param sClass The class object
     * @param msg    The message you would like logged.
     * @param tr     An exception to log
     */
    public static void error(Class<?> sClass, String msg, Throwable tr) {
        try {
            android.util.Log.e(sClass.getSimpleName(), msg, tr);
            if (DEBUG) {
                Log.getInstance().saveLogToFile(sClass, msg, tr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取异常的堆栈信息
     *
     * @param exception
     * @return
     */
    public static String getStackTrace(Throwable exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Close connection and streams
     */
    public void closeLogFile() {
        synchronized (lock) {
            try {
                if (os != null) {
                    os.close();
                }
                if (logFile != null) {
                    logFile = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Init the logger
     */
    public void initLogFile() {
        synchronized (lock) {
            try {
                Context context = MyApplication.getContext().getApplicationContext();
                if (CommonUtil.isSDCardAvailable()) {
                    logDirPath = MyApplication.getInstance().getLogCacheDir();
                } else {
                    logDirPath = context.getFilesDir().getAbsolutePath() + File.separator + MyApplication.DIR_LOG;
                }

                logFile = new File(logDirPath, NAME_LOG_FILE);
                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
                os = new FileOutputStream(logFile, true);
            } catch (Exception e) {
                System.out.println("Cannot open or create file at: " + logFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    /**
     * 将日志保存到本地
     *
     * @param sClass
     * @param message
     * @param tr
     */
    private void saveLogToFile(Class<?> sClass, String message, Throwable tr) {
        synchronized (lock) {
            try {
                if (!DEBUG) return;

                if (os != null) {
                    Date now = new Date();
                    StringBuffer logMsg = new StringBuffer(now.toString());

                    if (sClass != null) {
                        logMsg.append("[");
                        logMsg.append(sClass.getSimpleName());
                        logMsg.append("]");
                    }

                    logMsg.append(message);
                    logMsg.append("\r\n");

                    if (tr != null) {
                        logMsg.append(getStackTrace(tr));
                        logMsg.append("\r\n");
                    }

                    os.write(logMsg.toString().getBytes());
                    os.flush();

                    if (logFile.length() > maxFileSize) {
                        try {
                            String oldFileName = logDirPath + File.separator + NAME_BACKUP_LOG_FILE;
                            File oldFile = new File(oldFileName);
                            CommonUtil.deleteFile(oldFile);
                            logFile.renameTo(new File(oldFileName));
                            logFile = null;
                            // Reopen the file
                            initLogFile();
                        } catch (Exception ioe) {
                            System.out.println("Exception while renaming " + ioe);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception while logging. " + e);
                e.printStackTrace();

                try {
                    logFile = null;
                } finally {
                    initLogFile();
                }
            }
        }
    }

}
