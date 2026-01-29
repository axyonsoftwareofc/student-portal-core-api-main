package br.com.student.portal.repository;

import br.com.student.portal.entity.Course;
import br.com.student.portal.entity.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    List<Course> findByStatus(CourseStatus status);

    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' ORDER BY c.name")
    List<Course> findActiveCourses();

    @Query("SELECT c FROM Course c WHERE c.status IN ('SCHEDULED', 'ACTIVE') ORDER BY c.startDate")
    List<Course> findEnrollableCourses();

    @Query("SELECT c FROM Course c WHERE c.startDate <= :date AND (c.endDate IS NULL OR c.endDate >= :date) AND c.status = 'ACTIVE'")
    List<Course> findCurrentlyRunning(@Param("date") LocalDate date);

    @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Course> searchByTerm(@Param("term") String term);

    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.endDate < :date AND c.status = 'ACTIVE'")
    List<Course> findCoursesToComplete(@Param("date") LocalDate date);

    boolean existsByNameIgnoreCase(String name);
}