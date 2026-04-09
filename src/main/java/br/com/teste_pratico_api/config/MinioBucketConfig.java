package br.com.teste_pratico_api.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração responsável por garantir a existência do bucket no MinIO.
 */
@Configuration
@RequiredArgsConstructor
public class MinioBucketConfig {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    /**
     * Executado na inicialização da aplicação.
     */
    @PostConstruct
    public void init() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucket)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucket)
                                .build()
                );

                System.out.println("✅ Bucket criado: " + bucket);
            } else {
                System.out.println("ℹ️ Bucket já existe: " + bucket);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar/criar bucket no MinIO", e);
        }
    }
}
