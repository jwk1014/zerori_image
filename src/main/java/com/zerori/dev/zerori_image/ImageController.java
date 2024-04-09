package com.zerori.dev.zerori_image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @Value("${key}")
    private String key;

    @PostMapping(value = "/v1/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> postUpload(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestPart("folderPath") final String folderPath,
            @RequestPart("file") final MultipartFile file
    ) {
        if (!key.equals(authorization)) {
            throw new ErrResException(HttpStatus.UNAUTHORIZED, "invalid authorization " + authorization);
        }
        if (folderPath.charAt(0) != '/') {
            throw new ErrResException(HttpStatus.BAD_REQUEST, "folderPath must start /");
        }
        final String filePath = imageService.writeImageFile(folderPath, file);
        return new ResponseEntity<>(Map.of("path", filePath), HttpStatus.CREATED);
    }

    @DeleteMapping("/v1/images/delete")
    public ResponseEntity<Void> delete(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody DeleteReq req
    ) {
        if (!key.equals(authorization)) {
            throw new ErrResException(HttpStatus.UNAUTHORIZED, "invalid authorization " + authorization);
        }

        imageService.deleteImageFile(req.path);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Getter
    public static final class DeleteReq {
        @NotBlank
        private String path;
    }
}
