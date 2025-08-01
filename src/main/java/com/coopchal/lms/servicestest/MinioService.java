package com.coopchal.lms.servicestest;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import io.minio.GetPresignedObjectUrlArgs;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.documents}")
    private String documentsBucket;

    @Value("${minio.bucket.cours}")
    private String coursBucket;

    @Value("${minio.bucket.avatars}")
    private String avatarsBucket;

    public String uploadFichierDansBucket(String bucketName, MultipartFile fichier, String dossier) {
        try {
            String nom = dossier + "/" + UUID.randomUUID() + "_" + fichier.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(nom)
                            .stream(fichier.getInputStream(), fichier.getSize(), -1)
                            .contentType(fichier.getContentType())
                            .build()
            );

            return "/" + bucketName + "/" + nom;
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload fichier: " + e.getMessage(), e);
        }
    }


    // Méthodes spécifiques pour plus de clarté
    public String uploadCours(MultipartFile fichier, String dossier) {
        return uploadFichierDansBucket(coursBucket, fichier, dossier);
    }

    public String uploadDocument(MultipartFile fichier, String dossier) {
        return uploadFichierDansBucket(documentsBucket, fichier, dossier);
    }

    public String uploadAvatar(MultipartFile fichier, String dossier) {
        return uploadFichierDansBucket(avatarsBucket, fichier, dossier);
    }

    public void supprimerFichier(String cheminFichier) {
        try {
            String[] segments = cheminFichier.split("/");
            if (segments.length < 3) return;

            String bucket = segments[1];
            String object = cheminFichier.substring(("/" + bucket + "/").length());

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(object)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression fichier: " + e.getMessage(), e);
        }
    }

    public InputStream getFichier(String cheminFichier) {
        try {
            String[] segments = cheminFichier.split("/");
            if (segments.length < 3) {
                throw new RuntimeException("Chemin fichier invalide");
            }

            String bucket = segments[1];
            String object = cheminFichier.substring(("/" + bucket + "/").length());

            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(object)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur téléchargement fichier: " + e.getMessage(), e);
        }
    }

    public String generatePresignedUrl(String cheminFichier) throws Exception {
        String[] segments = cheminFichier.split("/");
        if (segments.length < 3) throw new RuntimeException("Chemin fichier invalide");

        String bucket = segments[1];
        String object = cheminFichier.substring(("/" + bucket + "/").length());

        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(object)
                        .expiry(10, TimeUnit.MINUTES)
                        .build()
        );
    }
}
