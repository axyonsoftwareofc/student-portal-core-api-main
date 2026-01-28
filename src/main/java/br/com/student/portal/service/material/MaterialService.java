package br.com.student.portal.service.material;

import br.com.student.portal.dto.request.MaterialRequest;
import br.com.student.portal.dto.response.MaterialResponse;
import br.com.student.portal.entity.Material;
import br.com.student.portal.entity.User;
import br.com.student.portal.entity.enums.MaterialCategory;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
import br.com.student.portal.exception.types.ForbiddenException;
import br.com.student.portal.exception.types.InternalServerException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final Path rootLocation = Paths.get("uploads");

    public MaterialResponse getMaterialById(UUID id) {
        log.debug("Buscando material por ID: {}", id);
        Material material = findMaterialById(id);
        return mapToResponse(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> getAllMaterials() {
        log.debug("Buscando todos os materiais");
        return materialRepository.findAllOrderByNewest()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MaterialResponse> getAllMaterials(Pageable pageable) {
        return materialRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public MaterialResponse createMaterial(MaterialRequest request, MultipartFile file, User uploadedBy) {
        log.info("Criando novo material: {}", request.getName());

        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path destinationFile = rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), destinationFile);

            MaterialCategory category = parseCategory(request.getCategory());

            Material material = Material.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .category(category)
                    .filename(filename)
                    .uploadedBy(uploadedBy)
                    .uploadDate(LocalDateTime.now())
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

    public MaterialResponse updateMaterial(UUID id, MaterialRequest request, User requester) {
        log.info("Atualizando material ID: {}", id);

        Material existingMaterial = findMaterialById(id);
        validateOwnership(existingMaterial, requester, "editar");

        existingMaterial.setName(request.getName());
        existingMaterial.setDescription(request.getDescription());

        if (request.getCategory() != null) {
            existingMaterial.setCategory(parseCategory(request.getCategory()));
        }

        Material updatedMaterial = materialRepository.save(existingMaterial);
        log.info("Material atualizado: {}", updatedMaterial.getId());

        return mapToResponse(updatedMaterial);
    }

    public void deleteMaterial(UUID id, User requester) {
        log.info("Deletando material ID: {}", id);

        Material material = findMaterialById(id);

        boolean isUploader = material.getUploadedBy().getId().equals(requester.getId());
        boolean isAdmin = requester.isAdmin();

        if (!isUploader && !isAdmin) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Você não tem permissão para deletar este material.");
        }

        // Deletar arquivo físico
        try {
            Path filePath = rootLocation.resolve(material.getFilename());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Não foi possível deletar o arquivo físico: {}", material.getFilename());
        }

        materialRepository.delete(material);
        log.info("Material deletado: {}", id);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> getMaterialsByCategory(String category) {
        log.debug("Buscando materiais por categoria: {}", category);

        MaterialCategory categoryEnum = parseCategory(category);

        return materialRepository.findByCategory(categoryEnum)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> searchMaterials(String term) {
        log.debug("Buscando materiais com termo: {}", term);
        return materialRepository.searchByName(term)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> getMaterialsByUploader(UUID userId) {
        log.debug("Buscando materiais do usuário: {}", userId);
        return materialRepository.findByUploadedById(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> getMostDownloadedMaterials(int limit) {
        log.debug("Buscando {} materiais mais baixados", limit);
        Pageable pageable = PageRequest.of(0, limit);
        return materialRepository.findAllByOrderByDownloadsDesc(pageable)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void incrementDownloads(UUID id) {
        Material material = findMaterialById(id);
        material.incrementDownloads();
        materialRepository.save(material);
    }

    public byte[] downloadMaterial(UUID id) {
        log.info("Download do material ID: {}", id);

        Material material = findMaterialById(id);
        Path filePath = rootLocation.resolve(material.getFilename());

        if (!Files.exists(filePath)) {
            throw new NotFoundException(ErrorCode.MATERIAL_NOT_FOUND,
                    "Arquivo físico não encontrado.");
        }

        try {
            incrementDownloads(id);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Erro ao ler arquivo: {}", e.getMessage(), e);
            throw new InternalServerException("Erro ao fazer download do arquivo.", e);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Material findMaterialById(UUID id) {
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

    private void validateOwnership(Material material, User requester, String action) {
        if (!material.getUploadedBy().getId().equals(requester.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Apenas o autor pode " + action + " este material.");
        }
    }

    private MaterialResponse mapToResponse(Material entity) {
        return MaterialResponse.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .description(entity.getDescription())
                .filename(entity.getFilename())
                .category(entity.getCategory().name())
                .uploaderId(entity.getUploadedBy().getId().toString())
                .uploaderName(entity.getUploadedBy().getName())
                .downloads(entity.getDownloads())
                .uploadDate(entity.getUploadDate() != null ? entity.getUploadDate().toString() : "")
                .build();
    }
}