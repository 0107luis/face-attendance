package com.example.studentattendanceterminal.network;

import com.example.studentattendanceterminal.models.AttendanceDTO;
import com.example.studentattendanceterminal.models.FaceRecognitionRequest;
import com.example.studentattendanceterminal.models.FaceRecognitionResult;
import com.example.studentattendanceterminal.models.Student;
import com.example.studentattendanceterminal.models.StudentCourse;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StudentService {
    // 验证学生身份（根据学号获取信息）
    @GET("students/number/{studentNumber}")
    Call<Student> getByNumber(@Path("studentNumber") String studentNumber);

    // 上传考勤记录
    @POST("attendance/record")
    Call<ResponseBody> uploadAttendance(@retrofit2.http.Header("X-User-Id") Long userId,
                                        @Body AttendanceDTO record);

    // 人脸识别（调用后端识别并返回匹配结果）
    @POST("face-recognize")
    Call<FaceRecognitionResult> recognizeFace(@Body FaceRecognitionRequest request);
    
    // 获取学生选修的课程列表
    @GET("student-course-classes/student/{studentId}/courses")
    Call<List<StudentCourse>> getStudentCourses(@retrofit2.http.Header("X-User-Id") Long userId,
                                                @Path("studentId") Long studentId);
    
    // 根据班级ID获取考勤任务
    @GET("attendance-tasks/class/{courseClassId}")
    Call<List<com.example.studentattendanceterminal.models.AttendanceTask>> getAttendanceTasksByClassId(@Path("courseClassId") Long courseClassId);
    
    // 获取当前活跃的考勤任务
    @GET("attendance-tasks/active")
    Call<List<com.example.studentattendanceterminal.models.AttendanceTask>> getActiveTasks();
}
