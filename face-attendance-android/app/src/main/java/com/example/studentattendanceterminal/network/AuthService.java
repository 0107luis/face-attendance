package com.example.studentattendanceterminal.network;

import com.example.studentattendanceterminal.models.LoginRequest;
import com.example.studentattendanceterminal.models.Student; // 注意：登录返回的是 User，但为了简化，如果结构兼容可以使用 Student，或者新建 User 类。这里后端返回的是 User。

// 后端 User 类包含 username, realName, roleID 等。Android 端 Student 类包含 studentNumber, name, className。
// 它们结构不完全一样。登录返回的是 User 实体。
// 所以我需要定义一个 Android 端的 User 模型。

import com.example.studentattendanceterminal.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("users/login")
    Call<User> login(@Body LoginRequest request);
}

