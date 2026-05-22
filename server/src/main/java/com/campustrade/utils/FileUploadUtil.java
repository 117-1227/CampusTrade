package com.campustrade.utils;

import com.campustrade.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class FileUploadUtil {

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    private final Path uploadDir;

    public FileUploadUtil(@Value("${app.upload.path:./uploads}") String uploadPath) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + this.uploadDir, e);
        }
    }

    /** 保存文件，返回访问路径 */
    public String save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("文件大小不能超过 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("仅支持 jpg/png/webp/gif 格式");
        }

        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + ext;

        try {
            Path dest = uploadDir.resolve(filename);
            file.transferTo(dest);
            log.info("文件上传: {}", dest);
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return i > 0 ? filename.substring(i).toLowerCase() : "";
    }
}
