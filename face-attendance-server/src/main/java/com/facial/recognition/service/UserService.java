package com.facial.recognition.service;

import com.facial.recognition.dto.LoginResponseDTO;
import com.facial.recognition.dto.RegisterDTO;
import com.facial.recognition.pojo.Course;
import com.facial.recognition.pojo.CourseClass;
import com.facial.recognition.pojo.Role;
import com.facial.recognition.pojo.Student;
import com.facial.recognition.pojo.Teacher;
import com.facial.recognition.pojo.User;
import com.facial.recognition.repository.AttendanceRecordRepository;
import com.facial.recognition.repository.CourseClassRepository;
import com.facial.recognition.repository.CourseRepository;
import com.facial.recognition.repository.FaceDataRepository;
import com.facial.recognition.repository.RoleRepository;
import com.facial.recognition.repository.StudentCourseClassRepository;
import com.facial.recognition.repository.StudentRepository;
import com.facial.recognition.repository.TeacherRepository;
import com.facial.recognition.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseClassRepository courseClassRepository;
    
    @Autowired
    private StudentCourseClassRepository studentCourseClassRepository;
    
    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;
    
    @Autowired
    private FaceDataRepository faceDataRepository;

    @Override
    public User add(User user) {
        User userPojo = new User();
        BeanUtils.copyProperties(user, userPojo);
        return userRepository.save(userPojo);
    }

    @Override
    public List<User> findAll() {
        // 管理员置顶，其余按创建时间倒序
        return userRepository.findAllOrdered();
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<User> findAllPaged(org.springframework.data.domain.Pageable pageable) {
        // 管理员置顶，其余按创建时间倒序
        return userRepository.findAllOrdered(pageable);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        if (user.isPresent()) {
            return user;
        }
        // 尝试通过学号查找
        return studentRepository.findByStudentNumber(username)
                .map(Student::getUserId)
                .flatMap(userRepository::findById);
    }
    
    @Override
    public List<User> findByRoleId(Integer roleId) {
        return userRepository.findByRoleId(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<User> findByRoleIdPaged(Integer roleId, org.springframework.data.domain.Pageable pageable) {
        return userRepository.findByRoleId(roleId, pageable);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // 检查用户是否存在
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        
        // 级联删除学生相关数据
        Optional<Student> student = studentRepository.findByUserId(id);
        if (student.isPresent()) {
            Long studentId = student.get().getStudentId();
            
            // 1. 删除考勤记录
            List<com.facial.recognition.pojo.AttendanceRecord> records = attendanceRecordRepository.findByStudentId(studentId);
            attendanceRecordRepository.deleteAll(records);
            
            // 2. 删除选课记录
            List<com.facial.recognition.pojo.StudentCourseClass> enrollments = studentCourseClassRepository.findByStudentId(studentId);
            studentCourseClassRepository.deleteAll(enrollments);
            
            // 3. 删除人脸数据
            faceDataRepository.deleteByUserId(id);
            
            // 4. 删除学生记录
            studentRepository.deleteById(studentId);
        }
        
        // 级联删除教师相关数据
        Optional<Teacher> teacher = teacherRepository.findByUserId(id);
        if (teacher.isPresent()) {
            // 删除人脸数据
            faceDataRepository.deleteByUserId(id);
            
            // 删除教师记录
            teacherRepository.deleteById(teacher.get().getTeacherId());
        }
        
        // 删除用户记录
        userRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public LoginResponseDTO register(RegisterDTO registerDTO) {
        // 验证角色ID (2=教师, 3=学生)
        Integer roleId = registerDTO.getRoleId();
        if (roleId == null || (roleId != 2 && roleId != 3)) {
            writeLog("注册失败: 无效的角色ID=" + roleId);
            throw new IllegalArgumentException("无效的角色，请选择教师或学生");
        }
        
        // 检查用户名是否已存在
        if (userRepository.findByUserName(registerDTO.getUsername()).isPresent()) {
            writeLog("注册失败: 用户名已存在=" + registerDTO.getUsername());
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 生成学号或工号
        String generatedNumber;
        if (roleId == 3) {
            // 学生角色，生成学号
            generatedNumber = generateStudentNumber();
        } else {
            // 教师角色，生成工号
            generatedNumber = generateJobNumber();
        }
        
        // 创建用户
        User user = new User();
        user.setUserName(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setRealName(registerDTO.getRealName());
        user.setPhoneNumber(registerDTO.getPhoneNumber());
        user.setEmail(registerDTO.getEmail());
        user.setRoleID(roleId);
        user.setIsActive(true);
        user.setCreatedDate(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        writeLog("用户注册成功: UserID=" + savedUser.getUserID() + ", Username=" + savedUser.getUserName() + ", RoleID=" + roleId);
        
        // 根据角色创建对应的学生或教师记录
        if (roleId == 3) {
            // 创建学生记录
            Student student = new Student();
            student.setStudentNumber(generatedNumber);
            student.setStudentName(registerDTO.getRealName());
            student.setUserId(savedUser.getUserID());
            // 设置班级
            if (registerDTO.getClassName() != null && !registerDTO.getClassName().isEmpty()) {
                student.setClassName(registerDTO.getClassName());
            }
            studentRepository.save(student);
            writeLog("学生记录创建成功: StudentNumber=" + generatedNumber + ", ClassName=" + registerDTO.getClassName() + ", UserID=" + savedUser.getUserID());
        } else {
            // 创建教师记录
            Teacher teacher = new Teacher();
            teacher.setUserId(savedUser.getUserID());
            teacherRepository.save(teacher);
            writeLog("教师记录创建成功: JobNumber=" + generatedNumber + ", UserID=" + savedUser.getUserID());
            
            // 如果选择了课程，创建课程班级
            if (registerDTO.getCourseId() != null) {
                createCourseClassForTeacher(teacher, registerDTO.getCourseId(), registerDTO.getRealName());
            }
        }
        
        // 构建并返回登录响应
        return buildLoginResponse(savedUser, generatedNumber, roleId);
    }
    
    @Override
    public LoginResponseDTO getLoginResponse(User user) {
        String number = null;
        Integer roleId = user.getRoleID();
        if (roleId == null) {
            roleId = 0;
        }
        if (roleId == 3) {
            // 学生角色，获取学号
            Optional<Student> student = studentRepository.findByUserId(user.getUserID());
            if (student.isPresent()) {
                number = student.get().getStudentNumber();
            }
        } else if (roleId == 2) {
            // 教师角色，使用用户名作为工号(T开头的)
            if (user.getUserName() != null && user.getUserName().startsWith("T")) {
                number = user.getUserName();
            }
        }
        return buildLoginResponse(user, number, roleId);
    }
    
    private LoginResponseDTO buildLoginResponse(User user, String number, Integer roleId) {
        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(user.getUserID());
        response.setUsername(user.getUserName());
        response.setRealName(user.getRealName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setRoleId(roleId);
        response.setIsActive(user.getIsActive());
        
        // 获取角色名称
        if (roleId != null && roleId > 0) {
            Optional<Role> role = roleRepository.findById(roleId);
            role.ifPresent(r -> response.setRoleName(r.getRoleName()));
        }
        
        if (roleId != null && roleId == 3) {
            // 学生角色
            response.setStudentNumber(number);
            Optional<Student> student = studentRepository.findByUserId(user.getUserID());
            student.ifPresent(s -> {
                response.setClassName(s.getClassName());
                response.setStudentId(s.getStudentId());
            });
        } else if (roleId != null && roleId == 2) {
            // 教师角色
            response.setJobNumber(number);
            Optional<Teacher> teacher = teacherRepository.findByUserId(user.getUserID());
            if (teacher.isPresent()) {
                response.setDepartment(teacher.get().getDepartment());
                response.setJobTitle(teacher.get().getJobTitle());
            }
        }
        
        return response;
    }
    
    @Override
    public String generateStudentNumber() {
        // 学号格式: 年份(4位) + 序号(4位) = 8位
        // 例如: 20250001, 20250002
        int year = LocalDate.now().getYear();
        String prefix = String.valueOf(year);
        
        // 查找当前年份最大的学号
        List<Student> allStudents = studentRepository.findAll();
        int maxSeq = 0;
        
        for (Student student : allStudents) {
            String sn = student.getStudentNumber();
            if (sn != null && sn.startsWith(prefix) && sn.length() == 8) {
                try {
                    int seq = Integer.parseInt(sn.substring(4));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException e) {
                    // 忽略格式不正确的学号
                }
            }
        }
        
        // 同时检查User表中是否有以该年份开头的用户名(学号)
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            String un = user.getUserName();
            if (un != null && un.startsWith(prefix) && un.length() == 8 && !un.startsWith("T")) {
                try {
                    int seq = Integer.parseInt(un.substring(4));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException e) {
                    // 忽略格式不正确的用户名
                }
            }
        }
        
        String newNumber = prefix + String.format("%04d", maxSeq + 1);
        writeLog("生成学号: " + newNumber);
        return newNumber;
    }
    
    @Override
    public String generateJobNumber() {
        // 工号格式: T + 年份后两位(2位) + 序号(5位) = 8位
        // 例如: T2500001, T2500002
        int year = LocalDate.now().getYear() % 100; // 取年份后两位
        String prefix = "T" + String.format("%02d", year);
        
        // 查找当前年份最大的工号
        List<User> allUsers = userRepository.findAll();
        int maxSeq = 0;
        
        for (User user : allUsers) {
            String un = user.getUserName();
            if (un != null && un.startsWith(prefix) && un.length() == 8) {
                try {
                    int seq = Integer.parseInt(un.substring(3));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException e) {
                    // 忽略格式不正确的用户名
                }
            }
        }
        
        String newNumber = prefix + String.format("%05d", maxSeq + 1);
        writeLog("生成工号: " + newNumber);
        return newNumber;
    }
    
    private void createCourseClassForTeacher(Teacher teacher, Long courseId, String teacherName) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            CourseClass courseClass = new CourseClass();
            courseClass.setClassName(course.getCourseName() + "-" + teacherName + "班");
            courseClass.setCourseId(courseId);
            courseClass.setTeacherId(teacher.getTeacherId().longValue());
            courseClass.setSemester(course.getSemester());
            courseClassRepository.save(courseClass);
            writeLog("课程班级创建成功: ClassName=" + courseClass.getClassName() + ", TeacherID=" + teacher.getTeacherId());
        }
    }
    
    private void writeLog(String message) {
        try {
            String logDir = "log";
            File dir = new File(logDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String logFile = logDir + File.separator + dateStr + ".log";
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.println("[" + timestamp + "] " + message);
            }
        } catch (IOException e) {
            logger.error("写入日志文件失败: " + e.getMessage());
        }
    }
}


