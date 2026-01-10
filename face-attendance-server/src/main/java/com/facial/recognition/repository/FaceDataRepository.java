package com.facial.recognition.repository;

import com.facial.recognition.pojo.FaceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FaceDataRepository extends JpaRepository<FaceData, Long> {
    
    // 根据UserID查找人脸数据
    Optional<FaceData> findByUserId(Integer userId);
    
    // 根据创建日期范围查找人脸数据
    List<FaceData> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 根据创建日期查找人脸数据
    List<FaceData> findByCreatedDate(LocalDateTime createdDate);
    
    // 根据UserID删除人脸数据
    void deleteByUserId(Integer userId);
    
    // 自定义查询：查找指定日期之后创建的人脸数�?
    @Query("SELECT fd FROM FaceData fd WHERE fd.createdDate >= :date")
    List<FaceData> findByCreatedDateAfter(@Param("date") LocalDateTime date);
    
    // 自定义查询：查找指定日期之前创建的人脸数�?
    @Query("SELECT fd FROM FaceData fd WHERE fd.createdDate <= :date")
    List<FaceData> findByCreatedDateBefore(@Param("date") LocalDateTime date);
    
    // 自定义查询：统计人脸数据数量
    @Query("SELECT COUNT(fd) FROM FaceData fd")
    Long countAllFaceData();
    
    // 自定义查询：根据UserID检查是否存在人脸数�?
    @Query("SELECT CASE WHEN COUNT(fd) > 0 THEN true ELSE false END FROM FaceData fd WHERE fd.userId = :userId")
    boolean existsByUserId(@Param("userId") Integer userId);
}
