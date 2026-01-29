package br.com.student.portal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaterialCategory {
    PDF("PDF", "Documento PDF", "application/pdf"),
    VIDEO("Vídeo", "Arquivo de vídeo", "video/*"),
    ARTICLE("Artigo", "Artigo ou texto", "text/*"),
    PRESENTATION("Apresentação", "Slides de apresentação", "application/vnd.ms-powerpoint"),
    DOCUMENT("Documento", "Documento de texto", "application/msword"),
    SPREADSHEET("Planilha", "Planilha eletrônica", "application/vnd.ms-excel"),
    IMAGE("Imagem", "Arquivo de imagem", "image/*"),
    AUDIO("Áudio", "Arquivo de áudio", "audio/*"),
    COMPRESSED("Compactado", "Arquivo compactado", "application/zip"),
    OTHER("Outro", "Outros tipos de arquivo", "application/octet-stream");

    private final String displayName;
    private final String description;
    private final String mimeType;

    public static MaterialCategory fromFilename(String filename) {
        if (filename == null) return OTHER;

        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return PDF;
        if (lower.matches(".*\\.(mp4|avi|mkv|mov|wmv)$")) return VIDEO;
        if (lower.matches(".*\\.(ppt|pptx|odp)$")) return PRESENTATION;
        if (lower.matches(".*\\.(doc|docx|odt|rtf)$")) return DOCUMENT;
        if (lower.matches(".*\\.(xls|xlsx|ods|csv)$")) return SPREADSHEET;
        if (lower.matches(".*\\.(jpg|jpeg|png|gif|bmp|svg|webp)$")) return IMAGE;
        if (lower.matches(".*\\.(mp3|wav|ogg|flac|aac)$")) return AUDIO;
        if (lower.matches(".*\\.(zip|rar|7z|tar|gz)$")) return COMPRESSED;
        if (lower.matches(".*\\.(txt|md|html)$")) return ARTICLE;

        return OTHER;
    }
}