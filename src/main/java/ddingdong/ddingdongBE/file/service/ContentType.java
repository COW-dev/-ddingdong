package ddingdong.ddingdongBE.file.service;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum ContentType {
    JPEG("image/jpeg", "jpg", "jpeg"),
    PNG("image/png", "png"),
    GIF("image/gif", "gif"),
    PDF("application/pdf", "pdf"),
    MSWORD("application/msword", "doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    EXCEL("application/vnd.ms-excel", "xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    TEXT("text/plain", "txt"),
    HTML("text/html", "html"),
    OCTET_STREAM("application/octet-stream");

    private final String mimeType;
    private final List<String> extensions;

    ContentType(String mimeType, String... extensions) {
        this.mimeType = mimeType;
        this.extensions = Arrays.asList(extensions);
    }

    public static ContentType fromExtension(String extension) {
        String lowerExtension = extension.toLowerCase();
        for (ContentType contentType : values()) {
            if (contentType.extensions.contains(lowerExtension)) {
                return contentType;
            }
        }
        return OCTET_STREAM;
    }
}
