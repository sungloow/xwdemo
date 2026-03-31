package com.citycheckin.controller;

import com.citycheckin.common.Result;
import com.citycheckin.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Tag(name = "10-文件上传", description = "图片上传，返回可访问的图片 URL")
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_SIZE = 10 * 1024 * 1024L; // 10 MB

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.url-prefix:/uploads}")
    private String uploadUrlPrefix;

    /** 启动时解析为绝对路径，并确保根目录存在，避免 transferTo 使用 Tomcat 工作目录 */
    @PostConstruct
    public void init() {
        File root = new File(uploadDir).getAbsoluteFile();
        uploadDir = root.getAbsolutePath();
        if (!root.exists()) {
            root.mkdirs();
        }
    }

    @Operation(summary = "上传图片", description = "支持 jpg/png/gif/webp，单文件不超过 10MB，返回图片访问 URL")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file,
                                      HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要上传的文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException(400, "仅支持 jpg/png/gif/webp 格式图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(400, "图片大小不能超过 10MB");
        }

        // 按日期分目录存储，文件名使用 UUID 防止冲突
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg";
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        File destDir = new File(uploadDir, dateDir);
        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new BusinessException(500, "创建上传目录失败");
        }

        try {
            file.transferTo(new File(destDir, fileName));
        } catch (IOException e) {
            throw new BusinessException(500, "文件保存失败：" + e.getMessage());
        }

        // 拼接可访问的 URL
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort();
        String url = baseUrl + uploadUrlPrefix + "/" + dateDir + "/" + fileName;
        return Result.ok(url);
    }
}
