package com.facial.recognition.util;

/**
 * 距离计算工具类
 */
public class DistanceUtil {
    // 地球半径，单位米
    private static final double EARTH_RADIUS = 6371000;

    /**
     * 计算两个经纬度之间的距离（单位：米）
     * 基于 Haversine 公式
     *
     * @param lat1 第一个点的纬度
     * @param lng1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lng2 第二个点的经度
     * @return 距离（米）
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lng1) - Math.toRadians(lng2);
        
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        
        s = s * EARTH_RADIUS;
        // 保留两位小数
        return Math.round(s * 100d) / 100d;
    }
}

