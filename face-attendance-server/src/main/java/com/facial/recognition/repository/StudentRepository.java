
package com.facial.recognition.repository;

import com.facial.recognition.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    // 根据学号查找学生
    Optional<Student> findByStudentNumber(String studentNumber);
    
    // 根据UserID查找学生
    Optional<Student> findByUserId(Integer userId);
    
    // 根据所属班级查找学生
    List<Student> findByClassName(String className);
    
    // 根据UserID删除学生
    void deleteByUserId(Integer userId);
    
    // 根据学号删除学生
    void deleteByStudentNumber(String studentNumber);
    
    // 自定义查询：根据班级名称模糊查询
    @Query("SELECT s FROM Student s WHERE s.className LIKE %:className%")
    List<Student> findByClassNameContaining(@Param("className") String className);
}

