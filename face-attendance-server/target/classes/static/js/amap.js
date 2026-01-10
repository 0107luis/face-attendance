/**
 * 高德地图工具类
 * 用于加载和初始化高德地图
 */

// 高德地图安全配置
window._AMapSecurityConfig = {
    securityJsCode: "440eb05f20d446c5e6659d60aec8a879",
};

/**
 * 初始化高德地图
 * @param {string} containerId - 地图容器的 ID
 * @param {Object} options - 地图配置选项（可选）
 * @returns {Promise} 返回地图实例
 */
function initAMap(containerId, options = {}) {
    return AMapLoader.load({
        key: "3a5839d73a5fbf89538666c9d79d9ca8", // 申请好的 Web 端开发 Key，首次调用 load 时必填
        version: "2.0", // 指定要加载的 JS API 的版本，缺省时默认为 1.4.15
        plugins: [
            "AMap.Scale",        // 比例尺
            "AMap.PlaceSearch",  // 地点搜索
            "AMap.Geocoder",     // 地理编码与逆地理编码
            "AMap.Geolocation"   // 定位插件
        ],
    })
        .then((AMap) => {
            // 如果提供了容器ID和选项，创建地图实例；否则只返回 AMap 对象
            if (containerId && options) {
                const mapOptions = {
                    zoom: options.zoom || 13, // 地图缩放级别
                    center: options.center || [116.397428, 39.90923], // 地图中心点坐标 [经度, 纬度]，默认北京
                    viewMode: options.viewMode || "3D", // 地图视图模式
                    ...options, // 合并其他配置选项
                };
                
                var map = new AMap.Map(containerId, mapOptions);
                
                // 添加比例尺组件到地图实例上
                map.addControl(new AMap.Scale());
                
                return map;
            } else {
                // 只返回 AMap 对象，不创建地图实例
                return AMap;
            }
        })
        .catch((e) => {
            console.error("高德地图加载失败:", e); // 加载错误提示
            throw e;
        });
}

// 导出到全局
window.initAMap = initAMap;

