package br.com.student.portal.repository;

import br.com.student.portal.entity.Task;
import br.com.student.portal.entity.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Buscar por curso
    @Query("SELECT t FROM Task t WHERE t.course.id = :courseId ORDER BY t.deadline")
    List<Task> findByCourseId(@Param("courseId") UUID courseId);

    // Buscar por status
    List<Task> findByStatus(TaskStatus status);

    // Buscar tarefas de um curso por status
    @Query("SELECT t FROM Task t WHERE t.course.id = :courseId AND t.status = :status ORDER BY t.deadline")
    List<Task> findByCourseIdAndStatus(
            @Param("courseId") UUID courseId,
            @Param("status") TaskStatus status
    );

    // Buscar tarefas com prazo próximo
    @Query("SELECT t FROM Task t WHERE t.deadline BETWEEN :start AND :end AND t.status = 'PENDING' ORDER BY t.deadline")
    List<Task> findUpcomingTasks(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Buscar tarefas atrasadas (pendentes com deadline passado)
    @Query("SELECT t FROM Task t WHERE t.deadline < :now AND t.status = 'PENDING'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    // Buscar por criador
    @Query("SELECT t FROM Task t WHERE t.createdBy.id = :userId ORDER BY t.createdAt DESC")
    List<Task> findByCreatedBy(@Param("userId") UUID userId);

    // Paginação por curso
    @Query("SELECT t FROM Task t WHERE t.course.id = :courseId")
    Page<Task> findByCourseId(@Param("courseId") UUID courseId, Pageable pageable);

    // Contar tarefas por status em um curso
    @Query("SELECT COUNT(t) FROM Task t WHERE t.course.id = :courseId AND t.status = :status")
    long countByCourseIdAndStatus(
            @Param("courseId") UUID courseId,
            @Param("status") TaskStatus status
    );

    // Buscar todas ordenadas por deadline
    List<Task> findAllByOrderByDeadlineAsc();
}