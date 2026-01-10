package com.facial.recognition.service.impl;

import com.facial.recognition.enums.RoleType;
import com.facial.recognition.exception.AccessDeniedException;
import com.facial.recognition.pojo.CourseClass;
import com.facial.recognition.pojo.Role;
import com.facial.recognition.pojo.Student;
import com.facial.recognition.pojo.Teacher;
import com.facial.recognition.pojo.User;
import com.facial.recognition.repository.CourseClassRepository;
import com.facial.recognition.repository.RoleRepository;
import com.facial.recognition.repository.StudentRepository;
import com.facial.recognition.repository.TeacherRepository;
import com.facial.recognition.repository.UserRepository;
import com.facial.recognition.service.AccessControlService;
import org.springframework.stereotype.Service;

@Service
public class AccessControlServiceImpl implements AccessControlService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseClassRepository courseClassRepository;

    public AccessControlServiceImpl(UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    StudentRepository studentRepository,
                                    TeacherRepository teacherRepository,
                                    CourseClassRepository courseClassRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseClassRepository = courseClassRepository;
    }

    @Override
    public void assertCanAccessStudentCourse(Integer requesterUserId, Long targetStudentId) {
        RoleType roleType = resolveRoleType(requesterUserId);
        if (roleType == RoleType.ADMIN) {
            return;
        }
        if (roleType == RoleType.STUDENT) {
            Student student = studentRepository.findById(targetStudentId)
                    .orElseThrow(() -> new AccessDeniedException("学生不存在或已被删除"));
            if (!student.getUserId().equals(requesterUserId)) {
                throw new AccessDeniedException("无权访问其他学生的课程信息");
            }
            return;
        }
        throw new AccessDeniedException("仅学生本人或管理员可访问该课程信息");
    }

    @Override
    public void assertCanAccessTeacherCourse(Integer requesterUserId, Long targetTeacherId) {
        RoleType roleType = resolveRoleType(requesterUserId);
        if (roleType == RoleType.ADMIN) {
            return;
        }
        if (roleType == RoleType.TEACHER) {
            Teacher teacher = teacherRepository.findById(targetTeacherId.intValue())
                    .orElseThrow(() -> new AccessDeniedException("教师不存在或已被删除"));
            if (!requesterUserId.equals(teacher.getUserId())) {
                throw new AccessDeniedException("无权查看其他教师的授课信息");
            }
            return;
        }
        throw new AccessDeniedException("仅教师本人或管理员可访问教师课程信息");
    }

    @Override
    public void assertCanAccessCourseClass(Integer requesterUserId, Long courseClassId) {
        RoleType roleType = resolveRoleType(requesterUserId);
        if (roleType == RoleType.ADMIN) {
            return;
        }
        if (roleType == RoleType.TEACHER) {
            CourseClass courseClass = courseClassRepository.findById(courseClassId)
                    .orElseThrow(() -> new AccessDeniedException("课程班级不存在或已被删除"));
            Teacher teacher = teacherRepository.findById(courseClass.getTeacherId().intValue())
                    .orElseThrow(() -> new AccessDeniedException("授课教师信息不存在"));
            if (!requesterUserId.equals(teacher.getUserId())) {
                throw new AccessDeniedException("仅课程负责人可访问该班级信息");
            }
            return;
        }
        throw new AccessDeniedException("当前角色无法访问班级信息");
    }

    @Override
    public void assertCanAccessTeacherInfo(Integer requesterUserId) {
        RoleType roleType = resolveRoleType(requesterUserId);
        if (roleType == RoleType.ADMIN || roleType == RoleType.TEACHER) {
            return;
        }
        throw new AccessDeniedException("学生无权查看教师信息");
    }
    
    @Override
    public boolean isStudent(Integer userId) {
        try {
            RoleType roleType = resolveRoleType(userId);
            return roleType == RoleType.STUDENT;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isTeacher(Integer userId) {
        try {
            RoleType roleType = resolveRoleType(userId);
            return roleType == RoleType.TEACHER;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isAdmin(Integer userId) {
        try {
            RoleType roleType = resolveRoleType(userId);
            return roleType == RoleType.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }

    private RoleType resolveRoleType(Integer userId) {
        if (userId == null) {
            throw new AccessDeniedException("缺少用户身份信息");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("用户不存在或已被禁用"));
        if (user.getRoleID() == null) {
            throw new AccessDeniedException("用户未分配角色");
        }
        Role role = roleRepository.findById(user.getRoleID())
                .orElseThrow(() -> new AccessDeniedException("角色不存在或已被删除"));
        RoleType roleType = RoleType.fromRoleName(role.getRoleName());
        if (roleType == null) {
            throw new AccessDeniedException("角色未被系统识别，无法判断权限");
        }
        return roleType;
    }
}

