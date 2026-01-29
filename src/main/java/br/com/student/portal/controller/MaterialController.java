package br.com.student.portal.controller;

import br.com.student.portal.dto.request.MaterialRequest;
import br.com.student.portal.dto.response.MaterialResponse;
import br.com.student.portal.entity.User;
import br.com.student.portal.service.material.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@Tag(name = "Materiais", description = "Gerenciamento de materiais de estudo")
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    @Operation(summary = "Lista todos os materiais")
    public ResponseEntity<List<MaterialResponse>> getAllMaterials() {
        return ResponseEntity.ok(materialService.getAllMaterials());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca material por ID")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable UUID id) {
        return ResponseEntity.ok(materialService.getMaterialById(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Lista materiais por categoria")
    public ResponseEntity<List<MaterialResponse>> getMaterialsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(materialService.getMaterialsByCategory(category));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca materiais por termo")
    public ResponseEntity<List<MaterialResponse>> searchMaterials(@RequestParam String term) {
        return ResponseEntity.ok(materialService.searchMaterials(term));
    }

    @GetMapping("/uploader/{userId}")
    @Operation(summary = "Lista materiais de um usuário")
    public ResponseEntity<List<MaterialResponse>> getMaterialsByUploader(@PathVariable UUID userId) {
        return ResponseEntity.ok(materialService.getMaterialsByUploader(userId));
    }

    @GetMapping("/most-downloaded")
    @Operation(summary = "Lista materiais mais baixados")
    public ResponseEntity<List<MaterialResponse>> getMostDownloadedMaterials(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(materialService.getMostDownloadedMaterials(limit));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Faz upload de um material")
    public ResponseEntity<MaterialResponse> uploadMaterial(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            Authentication authentication) {

        MaterialRequest request = MaterialRequest.builder()
                .name(name)
                .description(description)
                .category(category)
                .build();

        User uploadedBy = (User) authentication.getPrincipal();
        MaterialResponse response = materialService.createMaterial(request, file, uploadedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Atualiza um material")
    public ResponseEntity<MaterialResponse> updateMaterial(
            @PathVariable UUID id,
            @RequestBody MaterialRequest request,
            Authentication authentication) {

        User requester = (User) authentication.getPrincipal();
        MaterialResponse response = materialService.updateMaterial(id, request, requester);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Remove um material")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable UUID id,
            Authentication authentication) {

        User requester = (User) authentication.getPrincipal();
        materialService.deleteMaterial(id, requester);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Faz download de um material")
    public ResponseEntity<byte[]> downloadMaterial(@PathVariable UUID id) {
        byte[] content = materialService.downloadMaterial(id);
        MaterialResponse material = materialService.getMaterialById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + material.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}