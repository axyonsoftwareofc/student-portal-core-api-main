package br.com.student.portal.repository;

import br.com.student.portal.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, UUID> {

    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' ORDER BY c.name")
    List<CourseEntity> findActiveCourses();

    boolean existsByNameIgnoreCase(String name);
}