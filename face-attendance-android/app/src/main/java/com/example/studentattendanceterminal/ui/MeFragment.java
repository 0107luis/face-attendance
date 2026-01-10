package com.example.studentattendanceterminal.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.studentattendanceterminal.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.studentattendanceterminal.db.AttendanceDbHelper;
import com.example.studentattendanceterminal.models.Student;
import com.example.studentattendanceterminal.models.User;
import com.example.studentattendanceterminal.models.LoginRequest;
import com.example.studentattendanceterminal.network.ApiClient;
import com.example.studentattendanceterminal.network.AuthService;
import com.example.studentattendanceterminal.network.StudentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeFragment extends Fragment {

    private TextInputEditText etStudentNumber;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvLoginStatus;
    
    private View layoutLoginForm;
    private View layoutProfileInfo;
    private TextView tvStudentName;
    private TextView tvStudentNumberDisplay;
    private TextView tvStudentClass;
    private MaterialButton btnViewLogs;
    private MaterialButton btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etStudentNumber = view.findViewById(R.id.et_student_number);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvLoginStatus = view.findViewById(R.id.tv_login_status);
        
        layoutLoginForm = view.findViewById(R.id.layout_login_form);
        layoutProfileInfo = view.findViewById(R.id.layout_profile_info);
        tvStudentName = view.findViewById(R.id.tv_student_name);
        tvStudentNumberDisplay = view.findViewById(R.id.tv_student_number_display);
        tvStudentClass = view.findViewById(R.id.tv_student_class);
        btnViewLogs = view.findViewById(R.id.btn_view_logs);
        btnLogout = view.findViewById(R.id.btn_logout);

        updateUIState();

        btnLogin.setOnClickListener(v -> {
            String number = etStudentNumber.getText() == null ? null : etStudentNumber.getText().toString().trim();
            String password = etPassword.getText() == null ? null : etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(number)) {
                Toast.makeText(getContext(), "请输入学号或用户名", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);
            tvLoginStatus.setText("正在登录...");

            // 1. 调用登录接口
            AuthService authService = ApiClient.getAuthService();
            LoginRequest loginRequest = new LoginRequest(number, password);
            
            authService.login(loginRequest).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        // 2. 登录成功，再调用获取学生信息接口（为了拿到 studentId）
                        fetchStudentInfo(number, user);
                    } else {
                        if (isAdded()) {
                            btnLogin.setEnabled(true);
                            tvLoginStatus.setText("登录失败");
                            Toast.makeText(getContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    if (isAdded()) {
                        btnLogin.setEnabled(true);
                        // 尝试离线登录
                        tryOfflineLogin(number, password);
                    }
                }
            });
        });
        btnViewLogs.setOnClickListener(v -> {
            // 打开日志查看器
            Intent intent = new Intent(getContext(), LogViewerActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // 退出登录逻辑
            SharedPreferences prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            
            Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
            updateUIState();
        });
    }

    private void updateUIState() {
        if (!isAdded()) return;
        
        SharedPreferences prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String savedNumber = prefs.getString("student_number", null);
        String savedName = prefs.getString("student_name", "");
        String savedClass = prefs.getString("student_class", "");
        
        if (!TextUtils.isEmpty(savedNumber)) {
            // 已登录：显示个人信息，隐藏登录框
            layoutLoginForm.setVisibility(View.GONE);
            layoutProfileInfo.setVisibility(View.VISIBLE);
            
            tvStudentName.setText(TextUtils.isEmpty(savedName) ? "学生" : savedName);
            tvStudentNumberDisplay.setText("学号：" + savedNumber);
            tvStudentClass.setText("班级：" + (TextUtils.isEmpty(savedClass) ? "-" : savedClass));
        } else {
            // 未登录：显示登录框，隐藏个人信息
            layoutLoginForm.setVisibility(View.VISIBLE);
            layoutProfileInfo.setVisibility(View.GONE);
            
            tvLoginStatus.setText("未登录");
            etStudentNumber.setText("");
            etPassword.setText("");
            btnLogin.setEnabled(true);
        }
    }

    private void fetchStudentInfo(String number, User loginUser) {
        StudentService service = ApiClient.getStudentService();
        service.getByNumber(number).enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (isAdded()) btnLogin.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    Student s = response.body();
                    saveLoginInfo(loginUser, s.getStudentNumber(), s.getId(), s.getName(), s.getClassName());
                } else {
                    // 这种情况比较少见：用户存在但学生表没数据
                    // 暂时处理为：保存用户信息，ID暂时用 0 或其他标识
                    saveLoginInfo(loginUser, number, -1L, loginUser != null ? loginUser.getName() : null, null);
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                if (isAdded()) {
                    btnLogin.setEnabled(true);
                    Toast.makeText(getContext(), "获取学生详情失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveLoginInfo(User loginUser, String number, Long id, String name, String className) {
        SharedPreferences prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("student_number", number)
                .putLong("student_id", id)
                .putLong("user_id", loginUser != null && loginUser.getId() != null ? loginUser.getId() : -1L)
                .putString("student_name", name)
                .putString("student_class", className)
                .apply();
        
        if (isAdded()) {
            // 保存成功后，刷新UI状态
            updateUIState();
            Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
            
            // 登录成功跳转首页
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private void tryOfflineLogin(String number, String password) {
        // 简单的离线逻辑，假设密码是默认的 (实际生产环境离线不应明文验证密码)
        // 这里为了保持兼容性，如果本地有种子数据且密码匹配(假定)，则允许
        AttendanceDbHelper db = AttendanceDbHelper.getInstance(requireContext());
        AttendanceDbHelper.StudentSimple localS = db.findStudentByNumber(number);
        if (localS != null) {
            // 模拟密码验证：如果是 "123456" 或与学号相同则通过（仅作演示）
            if ("123456".equals(password) || number.equals(password)) {
                saveLoginInfo(null, localS.number, localS.id, localS.name, null);
                Toast.makeText(getContext(), "网络不可用，已切换为离线登录", Toast.LENGTH_SHORT).show();
            } else {
                tvLoginStatus.setText("网络错误");
                Toast.makeText(getContext(), "网络错误且本地验证失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            tvLoginStatus.setText("网络错误");
            Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_LONG).show();
        }
    }
}
