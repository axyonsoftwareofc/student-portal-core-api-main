package br.com.student.portal.service;

import br.com.student.portal.dto.request.CourseRequest;
import br.com.student.portal.dto.response.CourseResponse;
import br.com.student.portal.entity.Course;
import br.com.student.portal.entity.enums.CourseStatus;
import br.com.student.portal.exception.BadRequestException;
import br.com.student.portal.exception.ObjectNotFoundException;
import br.com.student.portal.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(UUID id) {
        log.debug("Buscando curso por ID: {}", id);
        return mapToResponse(findCourseOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        log.debug("Buscando todos os cursos");
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getActiveCourses() {
        log.debug("Buscando cursos ativos");
        return courseRepository.findActiveCourses().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        log.info("Criando novo curso: {}", request.getName());

        if (courseRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Já existe um curso com o nome: " + request.getName());
        }

        //TODO:MIGRAR ESSE BUILDER PRA UM MAPPER
        var course = Course.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus() : CourseStatus.DRAFT)
                .build();

        var savedCourse = courseRepository.save(course);
        log.info("Curso criado com ID: {}", savedCourse.getId());

        return mapToResponse(savedCourse);
    }

    @Transactional
    public CourseResponse updateCourse(UUID id, CourseRequest request) {
        log.info("Atualizando curso ID: {}", id);

        var course = findCourseOrThrow(id);

        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());

        if (request.getStatus() != null) {
            course.setStatus(request.getStatus());
        }

        var updatedCourse = courseRepository.save(course);
        log.info("Curso atualizado: {}", updatedCourse.getId());

        return mapToResponse(updatedCourse);
    }

    @Transactional
    public void deleteCourse(UUID id) {
        log.info("Deletando curso ID: {}", id);
        Course course = findCourseOrThrow(id);
        courseRepository.delete(course);
        log.info("Curso deletado: {}", id);
    }

    private Course findCourseOrThrow(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Curso não encontrado com ID: " + id));
    }

    //TODO:MOVER ESSA FUNÇÃO ABAIXO PARA UM MAPPER
    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .status(course.getStatus())
                .statusDisplayName(course.getStatus().getDisplayName())
                .isActive(course.isActive())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}