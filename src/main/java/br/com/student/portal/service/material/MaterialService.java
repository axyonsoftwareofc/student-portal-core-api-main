package br.com.student.portal.service.material;

import br.com.student.portal.dto.request.MaterialRequest;
import br.com.student.portal.dto.response.MaterialResponse;
import br.com.student.portal.entity.Course;
import br.com.student.portal.entity.Material;
import br.com.student.portal.entity.User;
import br.com.student.portal.entity.enums.MaterialCategory;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
import br.com.student.portal.exception.types.ForbiddenException;
import br.com.student.portal.exception.types.InternalServerException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.CourseRepository;
import br.com.student.portal.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(UUID id) {
        log.debug("Buscando material por ID: {}", id);
        Material material = findMaterialOrThrow(id);
        return mapToResponse(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> getAllMaterials() {
        log.debug("Buscando todos os materiais");
        return materialRepository.findAllOrderByNewest().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<MaterialResponse> getAllMaterials(Pageable pageable) {
        return materialRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> getMaterialsByCategory(String category) {
        log.debug("Buscando materiais por categoria: {}", category);
        MaterialCategory categoryEnum = parseCategory(category);
        return materialRepository.findByCategory(categoryEnum).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> searchMaterials(String term) {
        log.debug("Buscando materiais com termo: {}", term);
        return materialRepository.searchByName(term).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> getMaterialsByUploader(UUID userId) {
        log.debug("Buscando materiais do usuário: {}", userId);
        return materialRepository.findByUploadedById(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> getMostDownloadedMaterials(int limit) {
        log.debug("Buscando {} materiais mais baixados", limit);
        Pageable pageable = PageRequest.of(0, limit);
        return materialRepository.findAllByOrderByDownloadsDesc(pageable).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public MaterialResponse createMaterial(MaterialRequest request, MultipartFile file, User uploadedBy) {
        log.info("Criando novo material: {}", request.getName());

        try {
            Path rootLocation = Paths.get(uploadDir);
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            String originalFilename = file.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            Path destinationFile = rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), destinationFile);

            MaterialCategory category = request.getCategory() != null
                    ? parseCategory(request.getCategory())
                    : MaterialCategory.fromFilename(originalFilename);

            Course course = null;
            if (request.getCourseId() != null) {
                course = courseRepository.findById(request.getCourseId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.COURSE_NOT_FOUND,
                                "Curso não encontrado"));
            }

            Material material = Material.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .category(category)
                    .filename(filename)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .uploadedBy(uploadedBy)
                    .course(course)
                    .downloads(0L)
                    .build();

            Material savedMaterial = materialRepository.save(material);
            log.info("Material criado com ID: {}", savedMaterial.getId());

            return mapToResponse(savedMaterial);

        } catch (IOException e) {
            log.error("Erro ao salvar arquivo: {}", e.getMessage(), e);
            throw new InternalServerException("Erro ao salvar o arquivo. Tente novamente.", e);
        }
    }

    @Transactional
    public MaterialResponse updateMaterial(UUID id, MaterialRequest request, User requester) {
        log.info("Atualizando material ID: {}", id);

        Material material = findMaterialOrThrow(id);
        validateOwnership(material, requester);

        material.setName(request.getName());
        material.setDescription(request.getDescription());

        if (request.getCategory() != null) {
            material.setCategory(parseCategory(request.getCategory()));
        }

        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.COURSE_NOT_FOUND,
                            "Curso não encontrado"));
            material.setCourse(course);
        }

        Material updatedMaterial = materialRepository.save(material);
        log.info("Material atualizado: {}", updatedMaterial.getId());

        return mapToResponse(updatedMaterial);
    }

    @Transactional
    public void deleteMaterial(UUID id, User requester) {
        log.info("Deletando material ID: {}", id);

        Material material = findMaterialOrThrow(id);

        boolean isUploader = material.getUploadedBy().getId().equals(requester.getId());
        boolean isAdmin = requester.isAdmin();

        if (!isUploader && !isAdmin) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Você não tem permissão para deletar este material.");
        }

        // Deletar arquivo físico
        try {
            Path filePath = Paths.get(uploadDir).resolve(material.getFilename());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Não foi possível deletar o arquivo físico: {}", material.getFilename());
        }

        materialRepository.delete(material);
        log.info("Material deletado: {}", id);
    }

    @Transactional
    public byte[] downloadMaterial(UUID id) {
        log.info("Download do material ID: {}", id);

        Material material = findMaterialOrThrow(id);
        Path filePath = Paths.get(uploadDir).resolve(material.getFilename());

        if (!Files.exists(filePath)) {
            throw new NotFoundException(ErrorCode.FILE_NOT_FOUND,
                    "Arquivo físico não encontrado.");
        }

        try {
            material.incrementDownloads();
            materialRepository.save(material);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Erro ao ler arquivo: {}", e.getMessage(), e);
            throw new InternalServerException("Erro ao fazer download do arquivo.", e);
        }
    }

    // ==================== Métodos Privados ====================

    private Material findMaterialOrThrow(UUID id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MATERIAL_NOT_FOUND,
                        "Material não encontrado com ID: " + id));
    }

    private MaterialCategory parseCategory(String category) {
        try {
            return MaterialCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.FIELD_INVALID_FORMAT,
                    "Categoria inválida: " + category);
        }
    }

    private void validateOwnership(Material material, User requester) {
        boolean isOwner = material.getUploadedBy().getId().equals(requester.getId());
        boolean isAdmin = requester.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Você não tem permissão para editar este material.");
        }
    }

    private MaterialResponse mapToResponse(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .name(material.getName())
                .description(material.getDescription())
                .filename(material.getFilename())
                .category(material.getCategory())
                .categoryDisplayName(material.getCategory().getDisplayName())
                .fileSize(material.getFileSize())
                .contentType(material.getContentType())
                .uploaderId(material.getUploadedBy().getId())
                .uploaderName(material.getUploadedBy().getName())
                .courseId(material.getCourse() != null ? material.getCourse().getId() : null)
                .courseName(material.getCourse() != null ? material.getCourse().getName() : null)
                .downloads(material.getDownloads())
                .uploadDate(material.getUploadDate())
                .createdAt(material.getCreatedAt())
                .build();
    }
}