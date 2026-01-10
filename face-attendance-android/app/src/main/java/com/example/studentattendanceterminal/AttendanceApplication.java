package com.example.studentattendanceterminal;

import android.app.Application;
import com.example.studentattendanceterminal.utils.LogUtil;

/**
 * 自定义Application类
 * 用于在应用启动时初始化各种SDK和服务
 */
public class AttendanceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化日志工具
        LogUtil.init(this);
        LogUtil.i("AttendanceApplication", "应用启动，日志系统已初始化");
        LogUtil.i("AttendanceApplication", "日志文件路径: " + LogUtil.getLogFilePath());

        // 高德地图SDK隐私合规设置（必须在应用启动时设置）
        try {
            // 设置隐私权政策是否弹窗告知用户
            com.amap.api.location.AMapLocationClient.updatePrivacyShow(this, true, true);
            // 设置隐私权政策是否取得用户同意
            com.amap.api.location.AMapLocationClient.updatePrivacyAgree(this, true);
            LogUtil.i("AttendanceApplication", "高德地图SDK隐私合规设置完成 - updatePrivacyShow: true, updatePrivacyAgree: true");
        } catch (Exception e) {
            LogUtil.e("AttendanceApplication", "高德地图SDK隐私合规设置失败: " + e.getMessage(), e);
        }

        LogUtil.i("AttendanceApplication", "应用初始化完成");

        // 记录设备信息
        LogUtil.i("AttendanceApplication", "设备信息: Android " + android.os.Build.VERSION.RELEASE + " (API " + android.os.Build.VERSION.SDK_INT + ")");
        LogUtil.i("AttendanceApplication", "设备型号: " + android.os.Build.MODEL + " - " + android.os.Build.MANUFACTURER);
    }
}
