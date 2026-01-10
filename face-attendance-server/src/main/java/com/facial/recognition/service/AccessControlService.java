package com.facial.recognition.service;

/**
 * 统一的访问控制校验服务
 */
public interface AccessControlService {

    /**
     * 学生访问与自己相关的课程/课表时的权限校验
     */
    void assertCanAccessStudentCourse(Integer requesterUserId, Long targetStudentId);

    /**
     * 教师访问自己授课班级或课程时的权限校验
     */
    void assertCanAccessTeacherCourse(Integer requesterUserId, Long targetTeacherId);

    /**
     * 教师/管理员访问指定班级（用于课程、学生名单、考勤等）
     */
    void assertCanAccessCourseClass(Integer requesterUserId, Long courseClassId);
    
    /**
     * 检查是否可以访问教师信息（学生不允许，教师和管理员允许）
     */
    void assertCanAccessTeacherInfo(Integer requesterUserId);
    
    /**
     * 检查是否为学生角色
     */
    boolean isStudent(Integer userId);
    
    /**
     * 检查是否为教师角色
     */
    boolean isTeacher(Integer userId);
    
    /**
     * 检查是否为管理员角色
     */
    boolean isAdmin(Integer userId);
}

