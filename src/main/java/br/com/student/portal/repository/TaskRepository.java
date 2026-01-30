package br.com.student.portal.repository;

import br.com.student.portal.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    @Query("SELECT t FROM Task t WHERE t.course.id = :courseId ORDER BY t.deadline")
    List<TaskEntity> findByCourseId(@Param("courseId") UUID courseId);

    List<TaskEntity> findAllByOrderByDeadlineAsc();
}