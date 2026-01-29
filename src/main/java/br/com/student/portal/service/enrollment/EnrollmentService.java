package br.com.student.portal.service.enrollment;

import br.com.student.portal.dto.request.EnrollmentRequest;
import br.com.student.portal.dto.response.EnrollmentResponse;
import br.com.student.portal.entity.Course;
import br.com.student.portal.entity.Enrollment;
import br.com.student.portal.entity.User;
import br.com.student.portal.entity.enums.EnrollmentStatus;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
import br.com.student.portal.exception.types.ConflictException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.CourseRepository;
import br.com.student.portal.repository.EnrollmentRepository;
import br.com.student.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(UUID id) {
        log.debug("Buscando matrícula por ID: {}", id);
        Enrollment enrollment = findEnrollmentOrThrow(id);
        return mapToResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByStudent(UUID studentId) {
        log.debug("Buscando matrículas do estudante: {}", studentId);
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(UUID courseId) {
        log.debug("Buscando matrículas do curso: {}", courseId);
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getActiveEnrollmentsByStudent(UUID studentId) {
        log.debug("Buscando matrículas ativas do estudante: {}", studentId);
        return enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ACTIVE).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(UUID studentId, UUID courseId) {
        return enrollmentRepository.isStudentEnrolled(studentId, courseId);
    }

    @Transactional
    public EnrollmentResponse createEnrollment(EnrollmentRequest request) {
        log.info("Criando matrícula - Estudante: {}, Curso: {}",
                request.getStudentId(), request.getCourseId());

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND,
                        "Estudante não encontrado"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.COURSE_NOT_FOUND,
                        "Curso não encontrado"));

        // Verificar se curso aceita matrículas
        if (!course.isEnrollable()) {
            throw new BadRequestException(ErrorCode.ENROLLMENT_CLOSED,
                    "Este curso não está aberto para matrículas.");
        }

        // Verificar se já está matriculado
        if (enrollmentRepository.isStudentEnrolled(student.getId(), course.getId())) {
            throw new ConflictException(ErrorCode.RESOURCE_ALREADY_EXISTS,
                    "Estudante já está matriculado neste curso.");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollmentDate(LocalDate.now())
                .status(EnrollmentStatus.ACTIVE)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Matrícula criada com ID: {}", savedEnrollment.getId());

        return mapToResponse(savedEnrollment);
    }

    @Transactional
    public EnrollmentResponse completeEnrollment(UUID id, BigDecimal grade) {
        log.info("Concluindo matrícula ID: {} com nota: {}", id, grade);

        Enrollment enrollment = findEnrollmentOrThrow(id);

        if (!enrollment.isActive()) {
            throw new BadRequestException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "Apenas matrículas ativas podem ser concluídas.");
        }

        enrollment.complete(grade);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        log.info("Matrícula concluída: {}", id);
        return mapToResponse(updatedEnrollment);
    }

    @Transactional
    public EnrollmentResponse dropEnrollment(UUID id) {
        log.info("Cancelando matrícula ID: {}", id);

        Enrollment enrollment = findEnrollmentOrThrow(id);

        if (!enrollment.isActive()) {
            throw new BadRequestException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "Apenas matrículas ativas podem ser canceladas.");
        }

        enrollment.drop();
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        log.info("Matrícula cancelada: {}", id);
        return mapToResponse(updatedEnrollment);
    }

    @Transactional
    public void deleteEnrollment(UUID id) {
        log.info("Deletando matrícula ID: {}", id);
        Enrollment enrollment = findEnrollmentOrThrow(id);
        enrollmentRepository.delete(enrollment);
        log.info("Matrícula deletada: {}", id);
    }

    // ==================== Métodos Privados ====================

    private Enrollment findEnrollmentOrThrow(UUID id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ENROLLMENT_NOT_FOUND,
                        "Matrícula não encontrada com ID: " + id));
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getName())
                .courseId(enrollment.getCourse().getId())
                .courseName(enrollment.getCourse().getName())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .statusDisplayName(enrollment.getStatus().getDisplayName())
                .grade(enrollment.getGrade())
                .completedAt(enrollment.getCompletedAt())
                .createdAt(enrollment.getCreatedAt())
                .build();
    }
}