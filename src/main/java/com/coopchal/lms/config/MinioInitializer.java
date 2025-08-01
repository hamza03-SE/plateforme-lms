package com.coopchal.lms.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@RequiredArgsConstructor
public class MinioInitializer {

    private final MinioClient minioClient;

    @Value("${minio.bucket.documents}")
    private String documentsBucket;

    @Value("${minio.bucket.avatars}")
    private String avatarsBucket;

    @Value("${minio.bucket.cours}")
    private String coursBucket;

    @PostConstruct
    public void init() {
        try {
            createBucketIfNotExists(documentsBucket);
            createBucketIfNotExists(avatarsBucket);
            createBucketIfNotExists(coursBucket);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création des buckets MinIO", e);
        }
    }

    private void createBucketIfNotExists(String bucketName) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            System.out.println("Bucket créé: " + bucketName);
        } else {
            System.out.println("Bucket déjà existant: " + bucketName);
        }
    }
}
