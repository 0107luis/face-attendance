package com.example.studentattendanceterminal.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattendanceterminal.R;
import com.example.studentattendanceterminal.db.AttendanceDbHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.button.MaterialButton;
import com.example.studentattendanceterminal.ui.MeFragment;

// 高德地图定位 SDK
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationPurpose;

// 日志工具
import com.example.studentattendanceterminal.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView rvRecords;
    private MaterialButton btnStartCheckin;
    private TextView tvLocation;
    private Spinner spinnerCourse;
    private RecordsAdapter adapter;
    private static final int REQ_CODE_CHECKIN = 1001;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private FusedLocationProviderClient fusedClient;
    // 高德地图定位相关
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationListener mLocationListener = null;
    
    private double currentLat = 0.0;
    private double currentLon = 0.0;
    private boolean hasLocation = false;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Context appContext;
    private static final String TAG = "HomeFragment";
    
    // 存储课程和对应的考勤任务
    private java.util.Map<Long, com.example.studentattendanceterminal.models.AttendanceTask> courseTaskMap = new java.util.HashMap<>();
    // 存储课程名称到班级ID的映射
    private java.util.Map<String, Long> courseNameToClassIdMap = new java.util.HashMap<>();
    private android.os.Handler pollingHandler;
    private static final long POLLING_INTERVAL = 30000; // 30秒轮询一次
    private Runnable pollingRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvRecords = view.findViewById(R.id.rv_records);
        btnStartCheckin = view.findViewById(R.id.btn_start_checkin);
        tvLocation = view.findViewById(R.id.tv_location);
        spinnerCourse = view.findViewById(R.id.spinner_course);

        rvRecords.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecordsAdapter(new java.util.ArrayList<>());
        rvRecords.setAdapter(adapter);

        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onCameraResult);

        // 初始化高德地图定位SDK
        initAMapLocation();

        // 加载课程到 Spinner（只加载有考勤任务的课程）
        loadCoursesToSpinner();
        // 进入页面时加载现有签到记录
        reloadRecords();
        // 启动轮询机制检测新的考勤任务
        startPollingAttendanceTasks();

        btnStartCheckin.setOnClickListener(v -> {
            // 先判断是否已登录（存在 student_id）
            Long sid = getLoggedInStudentId();
            if (sid == null) {
                showToast("请先登录学号后再签到");
                // 跳转到“我”页进行登录
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MeFragment())
                        .commit();
                return;
            }

            // 校验已选择有效课程（过滤“暂无需要签到的课程”等占位项）
            Object selected = spinnerCourse.getSelectedItem();
            if (selected == null) {
                showToast("请选择课程后再签到");
                return;
            }
            if (selected instanceof String) {
                String s = (String) selected;
                if (s.contains("暂无") || s.contains("请先登录")) {
                    showToast("暂无可签到课程");
                    return;
                }
            } else if (selected instanceof AttendanceDbHelper.Course) {
                AttendanceDbHelper.Course sc = (AttendanceDbHelper.Course) selected;
                if (sc.name == null || sc.name.trim().isEmpty()) {
                    showToast("请选择课程后再签到");
                    return;
                }
            }

            if (hasAllPermissions()) {
                proceedCheckin();
            } else {
                requestPermissions(REQUIRED_PERMISSIONS, REQ_CODE_CHECKIN);
            }
        });

        // 记录日志文件路径到日志中
        LogUtil.i(TAG, "HomeFragment初始化完成，日志文件路径: " + LogUtil.getLogFilePath());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 使用应用级 Context，避免 Fragment 生命周期导致的空指针
        appContext = context.getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 停止定位并释放资源
        if (mLocationClient != null) {
            mLocationClient.stopLocation(); // 停止定位
            mLocationClient.onDestroy(); // 销毁定位客户端
            mLocationClient = null;
        }
        // 停止轮询
        stopPollingAttendanceTasks();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 页面恢复时重新加载课程和考勤任务
        loadCoursesToSpinner();
        // 重新加载签到记录（可能用户在其他页面登录或退出登录了）
        reloadRecords();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // 页面暂停时停止轮询以节省资源
        stopPollingAttendanceTasks();
    }

    /**
     * 初始化高德地图定位SDK
     */
    private void initAMapLocation() {
        try {
            Context context = appContext != null ? appContext : requireContext().getApplicationContext();

            // 初始化定位（隐私合规已在AttendanceApplication中设置）
            mLocationClient = new AMapLocationClient(context);
            LogUtil.i(TAG, "高德地图定位客户端初始化成功");

            // 初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            
            // 设置定位场景为签到场景
            mLocationOption.setLocationPurpose(AMapLocationPurpose.SignIn);
            
            // 设置定位模式为高精度模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            
            // 获取一次定位结果
            mLocationOption.setOnceLocation(true);
            
            // 获取最近3s内精度最高的一次定位结果
            mLocationOption.setOnceLocationLatest(true);
            
            // 设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            
            // 设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            
            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            
            // 设置定位回调监听
            mLocationListener = new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation amapLocation) {
                    if (amapLocation != null) {
                        if (amapLocation.getErrorCode() == 0) {
                            // 定位成功
                            double latitude = amapLocation.getLatitude();
                            double longitude = amapLocation.getLongitude();
                            
                            // 更新位置信息
                            applyAMapLocation(latitude, longitude, amapLocation);
                            
                            // 定位成功后打开相机
                            openCamera();
                        } else {
                            // 定位失败
                            String errorInfo = "定位失败，错误码：" + amapLocation.getErrorCode() + ", 错误信息：" + amapLocation.getErrorInfo();
                            LogUtil.e(TAG, errorInfo);
                            showToast(errorInfo);
                            // 定位失败后继续打开相机
                            openCamera();
                        }
                    } else {
                        LogUtil.e(TAG, "定位结果为空");
                        showToast("定位结果为空");
                        openCamera();
                    }
                }
            };
            
            // 设置定位回调监听
            mLocationClient.setLocationListener(mLocationListener);
            
        } catch (Exception e) {
            LogUtil.e(TAG, "初始化高德定位SDK失败: " + e.getMessage(), e);
            showToast("初始化定位服务失败: " + e.getMessage());
        }
    }
    
    /**
     * 应用高德定位结果
     */
    private void applyAMapLocation(double latitude, double longitude, AMapLocation amapLocation) {
        hasLocation = true;
        currentLat = latitude;
        currentLon = longitude;
        
        if (tvLocation != null) {
            tvLocation.setText("定位：" + currentLat + ", " + currentLon);
        }
    }

    private boolean hasAllPermissions() {
        return hasCameraPermission() && hasLocationPermission();
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void proceedCheckin() {
        showToast("权限已授予，开始采集定位与照片");
        fetchLocationThenCapture();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_CHECKIN) {
            // 重新评估权限状态（考虑用户选择“近似位置”场景）
            boolean granted = hasCameraPermission() && hasLocationPermission();
            if (granted) {
                proceedCheckin();
            } else {
                showToast("需要相机与定位权限以完成签到");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void fetchLocationThenCapture() {
        if (!hasLocationPermission()) {
            showToast("定位权限未授予");
            return;
        }

        // 检查设备定位是否开启
        LocationManager lm = (LocationManager) requireContext().getSystemService(android.content.Context.LOCATION_SERVICE);
        boolean enabled = lm != null && lm.isLocationEnabled();
        if (!enabled) {
            showToast("请开启设备定位服务");
            try {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } catch (Exception ignored) {}
            return;
        }

        // 使用高德地图定位SDK进行定位
        if (mLocationClient != null) {
            try {
                // 每次签到都强制重新定位，不使用缓存
                // 先停止之前的定位
                mLocationClient.stopLocation();
                
                // 重新设置定位参数，确保不使用缓存
                if (mLocationOption != null) {
                    mLocationOption.setOnceLocation(true);
                    mLocationOption.setOnceLocationLatest(false); // 不使用缓存，强制重新定位
                    mLocationClient.setLocationOption(mLocationOption);
                }
                
                // 清除之前的位置信息
                hasLocation = false;
                currentLat = 0.0;
                currentLon = 0.0;
                
                // 启动定位
                mLocationClient.startLocation();
                showToast("正在获取位置信息...");
                LogUtil.i(TAG, "开始重新定位，不使用缓存");
            } catch (Exception e) {
                LogUtil.e(TAG, "启动高德定位失败: " + e.getMessage(), e);
                showToast("启动定位失败: " + e.getMessage());
                // 定位失败后继续打开相机
                openCamera();
            }
        } else {
            LogUtil.e(TAG, "高德定位客户端未初始化");
            showToast("定位服务未初始化");
            openCamera();
        }
    }

    private void applyLocation(@NonNull Location location) {
        hasLocation = true;
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        if (tvLocation != null) {
            tvLocation.setText("定位：" + currentLat + ", " + currentLon);
        }
    }

    /**
     * 显示日志文件信息
     */
    private void showLogInfo() {
        try {
            String logPath = LogUtil.getLogFilePath();
            String logDir = LogUtil.getLogDirPath();

            String message = "日志文件信息：\n\n" +
                    "日志目录：\n" + logDir + "\n\n" +
                    "日志文件：\n" + logPath + "\n\n" +
                    "提示：可以使用ADB命令查看日志：\n" +
                    "adb shell run-as com.example.studentattendanceterminal cat files/logs/attendance_log.txt";

            new AlertDialog.Builder(requireContext())
                .setTitle("日志信息")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .setCancelable(true)
                .show();
        } catch (Exception e) {
            LogUtil.e(TAG, "显示日志信息失败: " + e.getMessage());
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void onCameraResult(ActivityResult result) {
        if (result.getResultCode() != Activity.RESULT_OK) {
            showToast("拍照取消");
            return;
        }
        Intent data = result.getData();
        if (data == null || data.getExtras() == null) {
            showToast("未获取到照片数据");
            return;
        }
        Object extra = data.getExtras().get("data");
        if (!(extra instanceof Bitmap)) {
            showToast("照片数据格式不支持");
            return;
        }
        Bitmap bitmap = (Bitmap) extra;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
        byte[] imageBytes = bos.toByteArray();
        
        // 将图片转为 Base64 以便上传
        String base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP);

        long ts = System.currentTimeMillis();
        double lat = currentLat;
        double lon = currentLon;
        boolean geoValid = isWithinChinaBounds(lat, lon);

        // 读取选中的课程与已登录学生
        AttendanceDbHelper.Course selectedCourse = (AttendanceDbHelper.Course) spinnerCourse.getSelectedItem();
        if (selectedCourse == null) {
            showToast("请选择课程");
            return;
        }
        
        Long studentId = getLoggedInStudentId();
        if (studentId == null) {
            showToast("请先登录");
            return;
        }
        
        // 检查该课程是否有活跃的考勤任务
        Long classId = getClassIdByCourseName(selectedCourse.name);
        if (classId == null) {
            // 记录详细信息方便排查
            LogUtil.e(TAG, "无法找到课程班级映射: courseName=" + selectedCourse.name
                    + ", mapKeys=" + courseNameToClassIdMap.keySet()
                    + ", tasks=" + courseTaskMap.keySet());
            showToast("无法找到该课程的班级信息");
            return;
        }
        
        // 签到前重新获取最新的考勤任务数据，确保获取到最新的半径等信息
        com.example.studentattendanceterminal.network.ApiClient.getStudentService()
            .getAttendanceTasksByClassId(classId)
            .enqueue(new retrofit2.Callback<java.util.List<com.example.studentattendanceterminal.models.AttendanceTask>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.List<com.example.studentattendanceterminal.models.AttendanceTask>> call,
                                     retrofit2.Response<java.util.List<com.example.studentattendanceterminal.models.AttendanceTask>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        java.util.List<com.example.studentattendanceterminal.models.AttendanceTask> tasks = response.body();
                        com.example.studentattendanceterminal.models.AttendanceTask task = null;
                        for (com.example.studentattendanceterminal.models.AttendanceTask t : tasks) {
                            if (t.isActive() || "ACTIVE".equals(t.getStatus())) {
                                task = t;
                                // 更新缓存
                                courseTaskMap.put(classId, task);
                                break;
                            }
                        }
                        
                        if (task == null) {
                            showToast("该课程当前没有需要签到的考勤任务");
                            return;
                        }
                        
                        LogUtil.i(TAG, String.format("获取到最新考勤任务 - TaskID:%d, 半径:%d米, 位置:(%.6f, %.6f)", 
                                task.getTaskId(), task.getRadius() != null ? task.getRadius() : 0,
                                task.getLatitude() != null ? task.getLatitude() : 0,
                                task.getLongitude() != null ? task.getLongitude() : 0));
                        
                        Long courseId = selectedCourse.id;
                        final Long taskId = task.getTaskId();
                        validateAndUpload(task, studentId, courseId, taskId, lat, lon, ts, base64Image, imageBytes, geoValid);
                    } else {
                        // 如果获取失败，使用缓存的任务
                        com.example.studentattendanceterminal.models.AttendanceTask task = courseTaskMap.get(classId);
                        if (task == null || (!task.isActive() && !"ACTIVE".equals(task.getStatus()))) {
                            showToast("获取考勤任务失败，请重试");
                            return;
                        }
                        LogUtil.w(TAG, "获取最新任务失败，使用缓存任务，半径:" + (task.getRadius() != null ? task.getRadius() : 0) + "米");
                        Long courseId = selectedCourse.id;
                        final Long taskId = task.getTaskId();
                        validateAndUpload(task, studentId, courseId, taskId, lat, lon, ts, base64Image, imageBytes, geoValid);
                    }
                }
                
                @Override
                public void onFailure(retrofit2.Call<java.util.List<com.example.studentattendanceterminal.models.AttendanceTask>> call, Throwable t) {
                    LogUtil.e(TAG, "获取考勤任务失败: " + t.getMessage());
                    // 如果网络失败，使用缓存的任务
                    com.example.studentattendanceterminal.models.AttendanceTask task = courseTaskMap.get(classId);
                    if (task == null || (!task.isActive() && !"ACTIVE".equals(task.getStatus()))) {
                        showToast("网络错误，无法获取考勤任务");
                        return;
                    }
                    LogUtil.w(TAG, "网络错误，使用缓存任务，半径:" + (task.getRadius() != null ? task.getRadius() : 0) + "米");
                    Long courseId = selectedCourse.id;
                    final Long taskId = task.getTaskId();
                    validateAndUpload(task, studentId, courseId, taskId, lat, lon, ts, base64Image, imageBytes, geoValid);
                }
            });
        
        // 提前返回，等待异步获取任务后再执行 validateAndUpload
    }
    
    private void uploadAttendanceRecord(Long userId, Long studentId, Long courseId, Long taskId, double lat, double lon, String base64Image, long ts,
                                        java.util.function.Consumer<Boolean> onDone) {
        com.example.studentattendanceterminal.models.AttendanceDTO record = 
            new com.example.studentattendanceterminal.models.AttendanceDTO(studentId, courseId, taskId, lat, lon, base64Image, ts, "正常");
            
        com.example.studentattendanceterminal.network.ApiClient.getStudentService()
            .uploadAttendance(userId, record)
            .enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                    boolean ok = response.isSuccessful();
                    if (ok) {
                        showToast("✅ 同步到服务器成功");
                    } else {
                        LogUtil.e(TAG, "上传考勤失败: code=" + response.code());
                        showToast("❌ 上传失败: " + response.code());
                    }
                    if (onDone != null) onDone.accept(ok);
                }

                @Override
                public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                    LogUtil.e(TAG, "上传考勤网络异常: " + t.getMessage());
                    showToast("❌ 网络错误，数据已保存本地");
                    if (onDone != null) onDone.accept(false);
                }
            });
    }

    /**
     * 校验（时间+半径+人脸识别）后再上传
     */
    private void validateAndUpload(com.example.studentattendanceterminal.models.AttendanceTask task,
                                   Long studentId,
                                   Long courseId,
                                   Long taskId,
                                   double lat,
                                   double lon,
                                   long ts,
                                   String base64Image,
                                   byte[] imageBytes,
                                   boolean geoValid) {
        // 1. 时间是否在有效期内
        boolean timeOk = task != null && task.isActive() && !"EXPIRED".equalsIgnoreCase(task.getStatus());
        if (!timeOk) {
            saveLocalRecord(studentId, courseId, imageBytes, ts, lat, lon, false);
            showToast("签到失败：考勤已过期或未开始");
            return;
        }

        // 2. 距离是否在半径内
        boolean rangeOk = geoValid && isWithinTaskRadius(lat, lon, task.getLatitude(), task.getLongitude(), task.getRadius());
        if (!rangeOk) {
            saveLocalRecord(studentId, courseId, imageBytes, ts, lat, lon, false);
            double distance = haversineMeters(lat, lon, task.getLatitude() != null ? task.getLatitude() : 0, 
                                              task.getLongitude() != null ? task.getLongitude() : 0);
            String msg = String.format("签到失败：距离考勤地点%.0f米，超出%d米范围", distance, task.getRadius() != null ? task.getRadius() : 0);
            showToast(msg);
            LogUtil.e(TAG, msg);
            return;
        }

        // 3. 调用后端人脸识别（只比对当前登录用户的人脸）
        Long loginUserId = getLoggedInUserId();
        if (loginUserId == null) {
            showToast("请先登录");
            return;
        }
        
        com.example.studentattendanceterminal.models.FaceRecognitionRequest req =
                new com.example.studentattendanceterminal.models.FaceRecognitionRequest(base64Image, loginUserId.intValue());

        com.example.studentattendanceterminal.network.ApiClient.getStudentService()
                .recognizeFace(req)
                .enqueue(new retrofit2.Callback<com.example.studentattendanceterminal.models.FaceRecognitionResult>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.studentattendanceterminal.models.FaceRecognitionResult> call,
                                           retrofit2.Response<com.example.studentattendanceterminal.models.FaceRecognitionResult> response) {
                        boolean matched = false;
                        String msg = "";
                        com.example.studentattendanceterminal.models.FaceRecognitionResult body = null;
                        if (response.isSuccessful() && response.body() != null) {
                            body = response.body();
                            matched = body.isHasFace() && body.isMatched();
                            msg = body.getMessage() == null ? "" : body.getMessage();
                            
                            // 打印人脸识别调试信息
                            if (body.getDebugInfo() != null && !body.getDebugInfo().isEmpty()) {
                                LogUtil.i(TAG, "=== 人脸识别调试信息 ===");
                                LogUtil.i(TAG, body.getDebugInfo());
                                LogUtil.i(TAG, "======================");
                            }
                            
                            // 打印识别结果摘要
                            LogUtil.i(TAG, String.format("人脸识别结果 - 检测到人脸:%s, 匹配成功:%s, 相似度:%.4f, 阈值:%.2f, 消息:%s",
                                body.isHasFace(), body.isMatched(),
                                body.getSimilarity() != null ? body.getSimilarity() : 0.0,
                                body.getThreshold() != null ? body.getThreshold() : 0.0,
                                msg));
                        } else {
                            msg = "识别接口错误：" + response.code();
                            LogUtil.e(TAG, "人脸识别接口失败: code=" + response.code());
                        }

                        if (matched) {
                            // 人脸匹配成功（后端已验证是当前登录用户）
                            Long loginUserId = getLoggedInUserId();
                            showToast("人脸匹配成功，正在上传...");
                            uploadAttendanceRecord(loginUserId, studentId, courseId, taskId, lat, lon, base64Image, ts, ok -> {
                                if (ok) {
                            saveLocalRecord(studentId, courseId, imageBytes, ts, lat, lon, true);
                            showToast("签到成功");
                                } else {
                                    saveLocalRecord(studentId, courseId, imageBytes, ts, lat, lon, false);
                                }
                            reloadRecords();
                            });
                        } else {
                            saveLocalRecord(studentId, courseId, imageBytes, ts, lat, lon, false);
                            showToast(msg.isEmpty() ? "签到失败：人脸未匹配" : msg);
                            if (!msg.isEmpty()) {
                                LogUtil.e(TAG, "人脸识别未匹配: " + msg);
                            }
                            reloadRecords();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.studentattendanceterminal.models.FaceRecognitionResult> call, Throwable t) {
                        saveLocalRecord(studentId, courseId, imageBytes, ts, lat, lon, false);
                        showToast("签到失败：识别服务不可用");
                        LogUtil.e(TAG, "人脸识别接口异常: " + t.getMessage());
                        reloadRecords();
                    }
                });
    }

    private void saveLocalRecord(Long studentId, Long courseId, byte[] imageBytes, long ts, double lat, double lon, boolean success) {
        Context ctx = appContext != null ? appContext : requireContext();
        new Thread(() -> {
            AttendanceDbHelper.getInstance(ctx)
                    .insertCheckinWithCourse(ts, lat, lon, imageBytes, courseId, studentId, success);
        }).start();
    }

    private boolean isWithinTaskRadius(double lat, double lon, Double targetLat, Double targetLon, Integer radiusMeters) {
        if (targetLat == null || targetLon == null || radiusMeters == null || radiusMeters <= 0) {
            LogUtil.e(TAG, "考勤位置信息不完整: targetLat=" + targetLat + ", targetLon=" + targetLon + ", radius=" + radiusMeters);
            return false;
        }
        double distance = haversineMeters(lat, lon, targetLat, targetLon);
        LogUtil.i(TAG, String.format("距离检查 - 当前位置:(%.6f, %.6f), 目标位置:(%.6f, %.6f), 计算距离:%.2f米, 要求半径:%d米, 结果:%s",
                lat, lon, targetLat, targetLon, distance, radiusMeters, (distance <= radiusMeters ? "通过" : "不通过")));
        return distance <= radiusMeters;
    }

    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000.0; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void loadCoursesToSpinner() {
        Long studentId = getLoggedInStudentId();
        if (studentId == null) {
            // 如果未登录，显示空列表
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"请先登录"});
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCourse.setAdapter(adapter);
            return;
        }
        
        // 从服务器获取学生选修的课程
        Long userId = getLoggedInUserId();
        com.example.studentattendanceterminal.network.ApiClient.getStudentService()
            .getStudentCourses(userId, studentId)
            .enqueue(new retrofit2.Callback<java.util.List<com.example.studentattendanceterminal.models.StudentCourse>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.List<com.example.studentattendanceterminal.models.StudentCourse>> call, 
                                     retrofit2.Response<java.util.List<com.example.studentattendanceterminal.models.StudentCourse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        java.util.List<com.example.studentattendanceterminal.models.StudentCourse> serverCourses = response.body();
                        // 检查每个课程是否有活跃的考勤任务
                        checkCoursesWithTasks(serverCourses);
                    } else {
                        LogUtil.e(TAG, "获取课程列表失败: code=" + response.code() + ", msg=" + response.message());
                        // 如果网络请求失败，使用本地数据
                        loadCoursesFromLocal();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<java.util.List<com.example.studentattendanceterminal.models.StudentCourse>> call, Throwable t) {
                    LogUtil.e(TAG, "获取课程列表失败: " + t.getMessage());
                    // 网络请求失败，使用本地数据
                    loadCoursesFromLocal();
                }
            });
    }
    
    /**
     * 检查课程是否有活跃的考勤任务，只显示有考勤任务的课程
     */
    private void checkCoursesWithTasks(java.util.List<com.example.studentattendanceterminal.models.StudentCourse> courses) {
        if (courses == null || courses.isEmpty()) {
            loadCoursesFromLocal();
            return;
        }
        
        courseTaskMap.clear();
        final java.util.List<com.example.studentattendanceterminal.models.StudentCourse> coursesWithTasks = new java.util.ArrayList<>();
        final java.util.concurrent.atomic.AtomicInteger completedCount = new java.util.concurrent.atomic.AtomicInteger(0);
        final int totalCount = courses.size();
        
        if (totalCount == 0) {
            loadCoursesFromLocal();
            return;
        }
        
        for (com.example.studentattendanceterminal.models.StudentCourse course : courses) {
            if (course.getClassId() == null) {
                completedCount.incrementAndGet();
                checkAllCompleted(coursesWithTasks, completedCount, totalCount);
                continue;
            }
            
            // 检查该课程班级是否有活跃的考勤任务
            com.example.studentattendanceterminal.network.ApiClient.getStudentService()
                .getAttendanceTasksByClassId(course.getClassId())
                .enqueue(new retrofit2.Callback<java.util.List<com.example.studentattendanceterminal.models.AttendanceTask>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.List<com.example.studentattendanceterminal.models.AttendanceTask>> call,
                                         retrofit2.Response<java.util.List<com.example.studentattendanceterminal.models.AttendanceTask>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            java.util.List<com.example.studentattendanceterminal.models.AttendanceTask> tasks = response.body();
                            // 查找活跃的考勤任务
                            for (com.example.studentattendanceterminal.models.AttendanceTask task : tasks) {
                                if (task.isActive() || "ACTIVE".equals(task.getStatus())) {
                                    courseTaskMap.put(course.getClassId(), task);
                                    synchronized (coursesWithTasks) {
                                        coursesWithTasks.add(course);
                                    }
                                    break;
                                }
                            }
                        }
                        completedCount.incrementAndGet();
                        checkAllCompleted(coursesWithTasks, completedCount, totalCount);
                    }
                    
                    @Override
                    public void onFailure(retrofit2.Call<java.util.List<com.example.studentattendanceterminal.models.AttendanceTask>> call, Throwable t) {
                        LogUtil.e(TAG, "获取考勤任务失败: " + t.getMessage());
                        completedCount.incrementAndGet();
                        checkAllCompleted(coursesWithTasks, completedCount, totalCount);
                    }
                });
        }
    }
    
    /**
     * 检查所有请求是否完成，完成后更新UI
     */
    private void checkAllCompleted(java.util.List<com.example.studentattendanceterminal.models.StudentCourse> coursesWithTasks,
                                   java.util.concurrent.atomic.AtomicInteger completedCount, int totalCount) {
        if (completedCount.get() >= totalCount) {
            if (getActivity() == null) return;
            
            getActivity().runOnUiThread(() -> {
                if (coursesWithTasks.isEmpty()) {
                    // 如果没有有考勤任务的课程，显示提示
                    android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_spinner_item, 
                        new String[]{"暂无需要签到的课程"});
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCourse.setAdapter(adapter);
                } else {
                    // 更新Spinner，只显示有考勤任务的课程
                    Context ctx = appContext != null ? appContext : requireContext();
                    AttendanceDbHelper db = AttendanceDbHelper.getInstance(ctx);
                    java.util.List<AttendanceDbHelper.Course> localCourses = new java.util.ArrayList<>();
                    courseNameToClassIdMap.clear(); // 清空旧的映射
                    for (com.example.studentattendanceterminal.models.StudentCourse sc : coursesWithTasks) {
                        long courseId = db.insertCourseIfNotExists(sc.getCourseName());
                        localCourses.add(new AttendanceDbHelper.Course(courseId, sc.getCourseName()));
                        // 保存课程名称到班级ID的映射
                        if (sc.getCourseName() != null && sc.getClassId() != null) {
                            courseNameToClassIdMap.put(sc.getCourseName(), sc.getClassId());
                        }
                    }
                    android.widget.ArrayAdapter<AttendanceDbHelper.Course> adapter = 
                        new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, localCourses);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCourse.setAdapter(adapter);
                }
            });
        }
    }
    
    private void loadCoursesFromLocal() {
        Context ctx = appContext != null ? appContext : requireContext();
        AttendanceDbHelper db = AttendanceDbHelper.getInstance(ctx);
        java.util.List<AttendanceDbHelper.Course> courses = db.getAllCourses();
        android.widget.ArrayAdapter<AttendanceDbHelper.Course> adapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);
    }

    private void reloadRecords() {
        // 只有登录后才能查看签到记录
        Long studentId = getLoggedInStudentId();
        if (studentId == null) {
            // 未登录时清空记录列表
            adapter.setData(new java.util.ArrayList<>());
            return;
        }
        
        // 只加载当前登录学生的签到记录
        Context ctx = appContext != null ? appContext : requireContext();
        AttendanceDbHelper db = AttendanceDbHelper.getInstance(ctx);
        java.util.List<AttendanceDbHelper.CheckinDisplay> list = db.getCheckinDisplayListByStudentId(studentId);
        java.util.List<RecordItem> items = new java.util.ArrayList<>();
        for (AttendanceDbHelper.CheckinDisplay d : list) {
            String timeStr = formatTime(d.timestamp);
            items.add(new RecordItem(d.courseName == null ? "未选择课程" : d.courseName, d.success, timeStr));
        }
        adapter.setData(items);
    }

    private Long getLoggedInStudentId() {
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        long id = prefs.getLong("student_id", -1L);
        return id > 0 ? id : null;
    }

    private Long getLoggedInUserId() {
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        long id = prefs.getLong("user_id", -1L);
        return id > 0 ? id : null;
    }

    private boolean isWithinChinaBounds(double lat, double lon) {
        // 东经 73°33′-135°05′，北纬 3°51′-53°33′
        double minLon = 73 + 33.0/60.0;      // 73.55
        double maxLon = 135 + 5.0/60.0;      // 135.083333...
        double minLat = 3 + 51.0/60.0;       // 3.85
        double maxLat = 53 + 33.0/60.0;      // 53.55
        return (lon >= minLon && lon <= maxLon && lat >= minLat && lat <= maxLat);
    }

    private String formatTime(long ts) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new java.util.Date(ts));
    }

    private void showToast(@NonNull String msg) {
        try {
            Context ctx = appContext != null ? appContext : getContext();
            if (ctx != null) {
                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
            } else {
                LogUtil.w(TAG, "Toast context is null, msg=" + msg);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Toast error: " + e.getMessage());
        }
    }
    
    /**
     * 根据课程名称获取班级ID（从缓存映射中查找）
     */
    private Long getClassIdByCourseName(String courseName) {
        return courseNameToClassIdMap.get(courseName);
    }
    
    /**
     * 启动轮询机制，定期检查新的考勤任务
     */
    private void startPollingAttendanceTasks() {
        if (pollingHandler == null) {
            pollingHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        }
        
        stopPollingAttendanceTasks(); // 先停止之前的轮询
        
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                Long studentId = getLoggedInStudentId();
                if (studentId != null && isResumed()) {
                    // 重新加载课程列表，检查是否有新的考勤任务
                    loadCoursesToSpinner();
                    LogUtil.d(TAG, "轮询检查考勤任务");
                }
                
                // 安排下一次轮询
                if (pollingHandler != null && pollingRunnable != null) {
                    pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
                }
            }
        };
        
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
        LogUtil.i(TAG, "启动考勤任务轮询机制，间隔: " + POLLING_INTERVAL / 1000 + "秒");
    }
    
    /**
     * 停止轮询机制
     */
    private void stopPollingAttendanceTasks() {
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
            pollingRunnable = null;
            LogUtil.i(TAG, "停止考勤任务轮询机制");
        }
    }

    private List<RecordItem> sampleData() {
        List<RecordItem> list = new ArrayList<>();
        list.add(new RecordItem("高等数学", true, "2025-11-02 08:30"));
        list.add(new RecordItem("大学英语", false, "2025-11-01 09:20"));
        list.add(new RecordItem("计算机基础", true, "2025-10-30 14:05"));
        return list;
    }

    // 简易模型
    static class RecordItem {
        final String courseName;
        final boolean success;
        final String time;

        RecordItem(String courseName, boolean success, String time) {
            this.courseName = courseName;
            this.success = success;
            this.time = time;
        }
    }

    // 简易适配器
    static class RecordsAdapter extends RecyclerView.Adapter<RecordsViewHolder> {
        private List<RecordItem> data;

        RecordsAdapter(List<RecordItem> data) { this.data = data; }

        @NonNull
        @Override
        public RecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
            return new RecordsViewHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull RecordsViewHolder holder, int position) {
            RecordItem item = data.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() { return data == null ? 0 : data.size(); }

        void setData(List<RecordItem> newData) {
            this.data = newData;
            notifyDataSetChanged();
        }
    }

    static class RecordsViewHolder extends RecyclerView.ViewHolder {
        private final android.widget.TextView tvCourse;
        private final android.widget.TextView tvStatus;
        private final android.widget.TextView tvTime;

        RecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourse = itemView.findViewById(R.id.tv_course);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTime = itemView.findViewById(R.id.tv_time);
        }

        void bind(@NonNull RecordItem item) {
            tvCourse.setText(item.courseName);
            tvStatus.setText(item.success ? "成功" : "失败");
            tvTime.setText(item.time);
        }
    }
}
