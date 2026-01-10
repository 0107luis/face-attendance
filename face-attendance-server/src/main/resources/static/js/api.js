// API基础配置
const API_BASE_URL = 'http://localhost:8080/api';

// API工具类
class API {
    // 获取当前登录用户ID
    static getCurrentUserId() {
        const userStr = localStorage.getItem('currentUser');
        if (userStr) {
            try {
                const user = JSON.parse(userStr);
                return user.userId || null;
            } catch (e) {
                return null;
            }
        }
        return null;
    }

    // 通用请求方法
    static async request(url, options = {}) {
        const userId = this.getCurrentUserId();
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
            },
        };

        // 添加用户身份信息到请求头
        if (userId) {
            defaultOptions.headers['X-User-Id'] = userId.toString();
        }

        const config = { ...defaultOptions, ...options };

        try {
            const response = await fetch(`${API_BASE_URL}${url}`, config);
            
            // 如果是204 No Content，直接返回
            if (response.status === 204) {
                return null;
            }
            
            // 尝试解析响应体（可能是JSON错误信息）
            let responseData = null;
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                try {
                    responseData = await response.json();
                } catch (e) {
                    // 如果解析失败，忽略
                }
            }
            
            if (!response.ok) {
                // 如果有错误响应体，提取错误信息
                const errorMessage = responseData?.message || responseData?.error || `HTTP error! status: ${response.status}`;
                const error = new Error(errorMessage);
                error.status = response.status;
                error.response = responseData;
                throw error;
            }
            
            return responseData;
        } catch (error) {
            console.error('API请求失败:', error);
            throw error;
        }
    }

    // GET请求
    static get(url) {
        return this.request(url, { method: 'GET' });
    }

    // POST请求
    static post(url, data) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data),
        });
    }

    // PUT请求
    static put(url, data) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(data),
        });
    }

    // DELETE请求
    static delete(url) {
        return this.request(url, { method: 'DELETE' });
    }
}

// 学生API
class StudentAPI {
    // 获取所有学生
    static getAll() {
        return API.get('/students');
    }

    // 根据ID获取学生
    static getById(id) {
        return API.get(`/students/${id}`);
    }

    // 根据学号获取学生
    static getByNumber(studentNumber) {
        return API.get(`/students/number/${studentNumber}`);
    }

    // 根据班级获取学生
    static getByClass(className) {
        return API.get(`/students/class/${className}`);
    }

    // 搜索学生
    static search(className) {
        return API.get(`/students/search?className=${encodeURIComponent(className)}`);
    }

    // 创建学生
    static create(student) {
        return API.post('/students', student);
    }

    // 更新学生
    static update(id, student) {
        return API.put(`/students/${id}`, student);
    }

    // 删除学生
    static delete(id) {
        return API.delete(`/students/${id}`);
    }

    // 分页获取学生
    static getPaged(page = 0, size = 10) {
        return API.get(`/students/paged?page=${page}&size=${size}`);
    }
}

// 角色API
class RoleAPI {
    // 获取所有角色
    static getAll() {
        return API.get('/roles');
    }

    // 根据ID获取角色
    static getById(id) {
        return API.get(`/roles/${id}`);
    }

    // 创建角色
    static create(role) {
        return API.post('/roles', role);
    }

    // 更新角色
    static update(id, role) {
        return API.put(`/roles/${id}`, role);
    }

    // 删除角色
    static delete(id) {
        return API.delete(`/roles/${id}`);
    }

    // 分页获取角色
    static getPaged(page = 0, size = 10) {
        return API.get(`/roles/paged?page=${page}&size=${size}`);
    }
}

// 用户API
class UserAPI {
    // 获取所有用户
    static getAll() {
        return API.get('/users');
    }

    // 分页获取用户
    static getPaged(page = 0, size = 10) {
        return API.get(`/users/paged?page=${page}&size=${size}`);
    }

    // 根据角色分页获取用户
    static getByRolePaged(roleId, page = 0, size = 10) {
        return API.get(`/users/role/${roleId}/paged?page=${page}&size=${size}`);
    }

    // 根据ID获取用户
    static getById(id) {
        return API.get(`/users/${id}`);
    }

    // 创建用户
    static create(user) {
        return API.post('/users', user);
    }

    // 更新用户
    static update(id, user) {
        return API.put(`/users/${id}`, user);
    }

    // 删除用户
    static delete(id) {
        return API.delete(`/users/${id}`);
    }

    // 登录
    static login(username, password) {
        return API.post('/users/login', { username, password });
    }

    // 注册
    static register(user) {
        return API.post('/users/register', user);
    }
}

// 课程API
class CourseAPI {
    // 获取所有课程
    static getAll() {
        return API.get('/courses');
    }

    // 根据ID获取课程
    static getById(id) {
        return API.get(`/courses/${id}`);
    }

    // 分页获取课程
    static getPaged(page = 0, size = 10) {
        return API.get(`/courses/paged?page=${page}&size=${size}`);
    }

    // 创建课程
    static create(course) {
        return API.post('/courses', course);
    }

    // 更新课程
    static update(id, course) {
        return API.put(`/courses/${id}`, course);
    }

    // 删除课程
    static delete(id) {
        return API.delete(`/courses/${id}`);
    }
}

// 课程班级API
class CourseClassAPI {
    // 获取所有课程班级
    static getAll() {
        return API.get('/course-classes');
    }

    // 根据ID获取课程班级
    static getById(id) {
        return API.get(`/course-classes/${id}`);
    }

    // 创建课程班级
    static create(courseClass) {
        return API.post('/course-classes', courseClass);
    }

    // 更新课程班级
    static update(id, courseClass) {
        return API.put(`/course-classes/${id}`, courseClass);
    }

    // 删除课程班级
    static delete(id) {
        return API.delete(`/course-classes/${id}`);
    }
}

// 考勤任务API
class AttendanceTaskAPI {
    // 获取所有考勤任务
    static getAll() {
        return API.get('/attendance-tasks');
    }

    // 根据ID获取考勤任务
    static getById(id) {
        return API.get(`/attendance-tasks/${id}`);
    }

    // 创建考勤任务
    static create(task) {
        return API.post('/attendance-tasks', task);
    }

    // 更新考勤任务
    static update(id, task) {
        return API.put(`/attendance-tasks/${id}`, task);
    }

    // 删除考勤任务
    static delete(id) {
        return API.delete(`/attendance-tasks/${id}`);
    }

    // 根据班级ID获取考勤任务
    static getByCourseClassId(courseClassId) {
        return API.get(`/attendance-tasks/class/${courseClassId}`);
    }

    // 获取班级考勤任务统计
    static getClassStatistics(courseClassId) {
        return API.get(`/attendance-tasks/class/${courseClassId}/statistics`);
    }

    // 获取教师考勤任务统计
    static getTeacherStatistics(teacherId) {
        return API.get(`/attendance-tasks/teacher/${teacherId}/statistics`);
    }

    // 获取班级考勤状况（某个考勤任务对应的所有学生考勤状态）
    static getClassAttendanceStatus(taskId) {
        return API.get(`/attendance-tasks/${taskId}/attendance-status`);
    }
}

// 考勤记录API
class AttendanceRecordAPI {
    // 获取当前登录学生的考勤记录
    static getMyRecords() {
        return API.get('/attendance-records/my');
    }

    // 获取当前登录学生的考勤记录（包含缺勤补齐）
    static getMyRecordsFull() {
        return API.get('/attendance-records/my/full');
    }
}

// 管理员查看学生选课
class AdminCourseViewAPI {
    static getStudentCoursesByStudentId(studentId) {
        return API.get(`/student-course-classes/student/${studentId}/courses`);
    }
    
    static getStudentByNumber(studentNumber) {
        return StudentAPI.getByNumber(studentNumber);
    }
}

// ========== 学生选课API ==========

class StudentCourseAPI {
    // 获取学生可选课程列表
    static getAvailableCourses(studentId) {
        return API.get(`/student-course-classes/student/${studentId}/available-courses`);
    }

    // 获取学生已选课程列表
    static getStudentCourses(studentId) {
        return API.get(`/student-course-classes/student/${studentId}/courses`);
    }

    // 获取学生已选课程历史
    static getStudentCourseHistory(studentId) {
        return API.get(`/student-course-classes/student/${studentId}`);
    }

    // 单个选课
    static enroll(studentId, classId) {
        return API.post(`/student-course-classes/enroll?studentId=${studentId}&courseClassId=${classId}`);
    }

    // 退课
    static drop(studentId, classId) {
        return API.post(`/student-course-classes/drop?studentId=${studentId}&courseClassId=${classId}`);
    }

    // 批量选课
    static batchEnroll(studentId, classIds) {
        return API.post('/student-course-classes/batch-enroll', {
            studentId: studentId,
            classIds: classIds
        });
    }

    // 检查选课时间冲突
    static checkTimeConflict(studentId, classId) {
        return API.get(`/student-course-classes/check-conflict?studentId=${studentId}&classId=${classId}`);
    }

    // 检查课程容量
    static checkCapacity(classId) {
        return API.get(`/student-course-classes/class/${classId}/capacity`);
    }

    // 获取课程推荐
    static getRecommendedCourses(studentId) {
        return API.get(`/student-course-classes/student/${studentId}/recommended-courses`);
    }
}

// 人脸数据API
class FaceDataAPI {
    // 保存或更新当前用户的人脸数据
    static save(payload) {
        return API.post('/face-data', payload);
    }

    // 获取当前用户的人脸数据
    static getMine() {
        return API.get('/face-data/my');
    }

    // 根据用户ID获取人脸数据（管理员/教师使用）
    static getByUser(userId) {
        return API.get(`/face-data/user/${userId}`);
    }
}

// 人脸识别测试 API
class FaceRecognitionAPI {
    static recognize(faceImage) {
        return API.post('/face-recognize', { faceImage });
    }
}

// 功能权限API
class FunctionAPI {
    // 获取所有功能
    static getAll() {
        return API.get('/functions');
    }

    // 获取所有激活的功能
    static getActive() {
        return API.get('/functions/active');
    }

    // 根据ID获取功能
    static getById(id) {
        return API.get(`/functions/${id}`);
    }

    // 根据功能代码获取功能
    static getByCode(functionCode) {
        return API.get(`/functions/code/${functionCode}`);
    }

    // 根据角色ID获取该角色的所有功能权限
    static getByRoleId(roleId) {
        return API.get(`/functions/role/${roleId}`);
    }
}

// 权限分配API
class PermissionAPI {
    // 获取角色的所有权限
    static getRolePermissions(roleId) {
        return API.get(`/permissions/role/${roleId}`);
    }

    // 为角色分配单个权限
    static assignPermission(roleId, functionId) {
        return API.post(`/permissions/role/${roleId}/function/${functionId}`);
    }

    // 撤销角色的单个权限
    static revokePermission(roleId, functionId) {
        return API.delete(`/permissions/role/${roleId}/function/${functionId}`);
    }

    // 批量分配权限
    static assignPermissions(roleId, functionIds) {
        return API.post(`/permissions/role/${roleId}/assign`, { functionIds });
    }

    // 批量撤销权限
    static revokePermissions(roleId, functionIds) {
        return API.post(`/permissions/role/${roleId}/revoke`, { functionIds });
    }

    // 检查角色是否有某个权限
    static hasPermission(roleId, functionId) {
        return API.get(`/permissions/role/${roleId}/function/${functionId}/check`);
    }
}

// 仪表板API
class DashboardAPI {
    // 获取统计数据
    static getStatistics() {
        return API.get('/dashboard/statistics');
    }

    // 获取最近考勤记录
    static getRecentRecords() {
        return API.get('/dashboard/recent-records');
    }
}

