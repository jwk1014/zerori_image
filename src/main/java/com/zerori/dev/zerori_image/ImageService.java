package com.zerori.dev.zerori_image;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final Tika tika = new Tika();

    @Value("${baseDir}")
    private String baseDirStr;

    public String writeImageFile(final String dirPathStr, final MultipartFile multipartFile) {
        final String ext;
        try {
            ext = getImageExtension(multipartFile.getBytes());
        } catch (IOException e) {
            throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected ( multipartFile.getBytes() IOException " + e.getMessage() + ")");
        }

        final String fileName = UUID.randomUUID().toString().replaceAll("-", "") + Instant.now().getEpochSecond();

        final Path path = Paths.get(baseDirStr + dirPathStr + "/" + fileName + ext);
        if (!Files.exists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (UnsupportedOperationException e) {
                throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected ( Files.createDirectories UnsupportedOperationException " + e.getMessage() + ")");
            } catch (FileAlreadyExistsException ignored) {
            } catch (IOException e) {
                throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "fail to upload file ( Files.createDirectories IOException " + e.getMessage() + ")");
            }
        } else if (Files.exists(path)) {
            throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "fail to upload file");
        }

        try {
            Files.copy(multipartFile.getInputStream(), path);
        } catch (FileAlreadyExistsException e) {
            throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "fail to upload file ( Files.copy FileAlreadyExistsException " + e.getMessage() + ")");
        } catch (DirectoryNotEmptyException e) {
            throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "fail to upload file ( Files.copy DirectoryNotEmptyException " + e.getMessage() + ")");
        } catch (UnsupportedOperationException e) {
            throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected ( Files.copy UnsupportedOperationException " + e.getMessage() + ")");
        } catch (IOException e) {
            throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "fail to upload file ( Files.copy IOException " + e.getMessage() + ")");
        }

        return dirPathStr + "/" + fileName + ext;
    }

    public void deleteImageFile(final String filePathStr) {
        final Path path = Paths.get(baseDirStr + filePathStr);
        if (!Files.exists(path)) {
            throw new ErrResException(HttpStatus.BAD_REQUEST, "not exists file");
        }
        try {
            Files.delete(path);
        } catch (NoSuchFileException e) {
            throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "fail to delete file ( NoSuchFileException " + e.getMessage() + ")");
        } catch (DirectoryNotEmptyException e) {
            throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "fail to delete file ( DirectoryNotEmptyException " + e.getMessage() + ")");
        } catch (IOException e) {
            throw new ErrResException(HttpStatus.INTERNAL_SERVER_ERROR, "fail to delete file ( IOException " + e.getMessage() + ")");
        }
    }

    private String getImageExtension(final byte[] fileBytes) {
        final String mimeType = tika.detect(fileBytes);
        switch (mimeType) {
            case MimeTypeUtils.IMAGE_GIF_VALUE:
                return ".gif";
            case MimeTypeUtils.IMAGE_JPEG_VALUE:
                return ".jpg";
            case MimeTypeUtils.IMAGE_PNG_VALUE:
                return ".png";
            default:
                break;
        }

        throw new ErrResException(HttpStatus.BAD_REQUEST, "getImageExtension unexpected mimeType = " + mimeType);
    }
}
