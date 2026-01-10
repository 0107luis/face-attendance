package com.example.studentattendanceterminal.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {
    // 模拟器访问宿主机使用 10.0.2.2，真机请改为电脑局域网IP (如 192.168.x.x)
    private static final String BASE_URL = "http://192.168.1.100:8080/api/";
    //private static final String BASE_URL = "http://47.117.xxx:8080/api/";//服务器太拉了 由于要人工智能 所以服务器扛不住
    private static Retrofit retrofit;

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static StudentService getStudentService() {
        return getRetrofit().create(StudentService.class);
    }

    public static AuthService getAuthService() {
        return getRetrofit().create(AuthService.class);
    }
}