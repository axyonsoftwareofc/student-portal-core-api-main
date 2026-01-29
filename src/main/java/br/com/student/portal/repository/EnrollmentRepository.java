package br.com.student.portal.repository;

import br.com.student.portal.entity.Enrollment;
import br.com.student.portal.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId ORDER BY e.enrollmentDate DESC")
    List<Enrollment> findByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId ORDER BY e.enrollmentDate")
    List<Enrollment> findByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId")
    Optional<Enrollment> findByStudentIdAndCourseId(
            @Param("studentId") UUID studentId,
            @Param("courseId") UUID courseId
    );

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = :status")
    List<Enrollment> findByStudentIdAndStatus(
            @Param("studentId") UUID studentId,
            @Param("status") EnrollmentStatus status
    );

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ACTIVE'")
    long countActiveEnrollmentsByCourse(@Param("courseId") UUID courseId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Enrollment e " +
            "WHERE e.student.id = :studentId AND e.course.id = :courseId AND e.status = 'ACTIVE'")
    boolean isStudentEnrolled(
            @Param("studentId") UUID studentId,
            @Param("courseId") UUID courseId
    );

    @Query("SELECT AVG(e.grade) FROM Enrollment e WHERE e.course.id = :courseId AND e.grade IS NOT NULL")
    Double getAverageGradeByCourse(@Param("courseId") UUID courseId);
}