package br.com.student.portal.repository;

import br.com.student.portal.entity.TaskSubmission;
import br.com.student.portal.entity.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, UUID> {

    @Query("SELECT s FROM TaskSubmission s WHERE s.task.id = :taskId ORDER BY s.submittedAt DESC")
    List<TaskSubmission> findByTaskId(@Param("taskId") UUID taskId);

    @Query("SELECT s FROM TaskSubmission s WHERE s.student.id = :studentId ORDER BY s.submittedAt DESC")
    List<TaskSubmission> findByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT s FROM TaskSubmission s WHERE s.task.id = :taskId AND s.student.id = :studentId")
    Optional<TaskSubmission> findByTaskIdAndStudentId(
            @Param("taskId") UUID taskId,
            @Param("studentId") UUID studentId
    );

    @Query("SELECT s FROM TaskSubmission s WHERE s.task.id = :taskId AND s.status = :status")
    List<TaskSubmission> findByTaskIdAndStatus(
            @Param("taskId") UUID taskId,
            @Param("status") SubmissionStatus status
    );

    @Query("SELECT COUNT(s) FROM TaskSubmission s WHERE s.task.id = :taskId")
    long countByTaskId(@Param("taskId") UUID taskId);

    @Query("SELECT COUNT(s) FROM TaskSubmission s WHERE s.task.id = :taskId AND s.status = 'GRADED'")
    long countGradedByTaskId(@Param("taskId") UUID taskId);

    @Query("SELECT AVG(s.grade) FROM TaskSubmission s WHERE s.task.id = :taskId AND s.grade IS NOT NULL")
    Double getAverageGradeByTask(@Param("taskId") UUID taskId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM TaskSubmission s " +
            "WHERE s.task.id = :taskId AND s.student.id = :studentId")
    boolean hasStudentSubmitted(
            @Param("taskId") UUID taskId,
            @Param("studentId") UUID studentId
    );
}