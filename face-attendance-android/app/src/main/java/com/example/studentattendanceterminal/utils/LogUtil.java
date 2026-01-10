package com.example.studentattendanceterminal.utils;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志工具类
 * 同时输出到Logcat和文件，方便跟踪和调试
 */
public class LogUtil {
    private static final String TAG = "AttendanceApp";
    private static final String LOG_FILE_NAME = "attendance_log.txt";
    private static final int MAX_LOG_SIZE = 5 * 1024 * 1024; // 5MB
    private static Context appContext;
    private static File logFile;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

    /**
     * 初始化日志工具
     * @param context 应用上下文
     */
    public static void init(Context context) {
        appContext = context.getApplicationContext();
        try {
            // 日志文件保存在应用的私有目录
            File logDir = new File(appContext.getFilesDir(), "logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            logFile = new File(logDir, LOG_FILE_NAME);

            // 如果日志文件过大，重命名并创建新文件
            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
                String backupName = "attendance_log_" + System.currentTimeMillis() + ".txt";
                File backupFile = new File(logDir, backupName);
                logFile.renameTo(backupFile);
                logFile = new File(logDir, LOG_FILE_NAME);
            }
        } catch (Exception e) {
            Log.e(TAG, "初始化日志工具失败", e);
        }
    }

    /**
     * 获取日志文件路径
     * @return 日志文件的完整路径
     */
    public static String getLogFilePath() {
        if (logFile != null && logFile.exists()) {
            return logFile.getAbsolutePath();
        }
        return "日志文件未初始化";
    }

    /**
     * 获取日志文件目录路径
     * @return 日志文件目录的完整路径
     */
    public static String getLogDirPath() {
        if (appContext != null) {
            File logDir = new File(appContext.getFilesDir(), "logs");
            return logDir.getAbsolutePath();
        }
        return "日志目录未初始化";
    }

    /**
     * 写入日志到文件
     */
    private static synchronized void writeToFile(String level, String tag, String message, Throwable throwable) {
        if (logFile == null || appContext == null) {
            return;
        }

        try (FileWriter writer = new FileWriter(logFile, true)) {
            String timestamp = dateFormat.format(new Date());
            String logEntry = String.format("[%s] [%s] [%s] %s\n", timestamp, level, tag, message);
            writer.append(logEntry);

            if (throwable != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                writer.append(sw.toString()).append("\n");
            }
            writer.flush();
        } catch (IOException e) {
            Log.e(TAG, "写入日志文件失败", e);
        }
    }

    /**
     * Debug级别日志
     */
    public static void d(String tag, String message) {
        Log.d(tag, message);
        writeToFile("DEBUG", tag, message, null);
    }

    /**
     * Info级别日志
     */
    public static void i(String tag, String message) {
        Log.i(tag, message);
        writeToFile("INFO", tag, message, null);
    }

    /**
     * Warning级别日志
     */
    public static void w(String tag, String message) {
        Log.w(tag, message);
        writeToFile("WARN", tag, message, null);
    }

    /**
     * Warning级别日志（带异常）
     */
    public static void w(String tag, String message, Throwable throwable) {
        Log.w(tag, message, throwable);
        writeToFile("WARN", tag, message, throwable);
    }

    /**
     * Error级别日志
     */
    public static void e(String tag, String message) {
        Log.e(tag, message);
        writeToFile("ERROR", tag, message, null);
    }

    /**
     * Error级别日志（带异常）
     */
    public static void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
        writeToFile("ERROR", tag, message, throwable);
    }

    /**
     * 记录异常信息
     */
    public static void exception(String tag, String message, Exception e) {
        e(tag, message, e);
    }
}
