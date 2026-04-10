package br.com.teste_pratico_api.service;

import br.com.teste_pratico_api.config.properties.MinioProperties;
import br.com.teste_pratico_api.exception.StorageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @InjectMocks
    private StorageService storageService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(minioProperties.getBucket()).thenReturn("bucket-test");
    }

    @Test
    void deveFazerUploadComSucesso() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "teste.png",
                "image/png",
                "conteudo".getBytes()
        );

        when(minioClient.bucketExists(any())).thenReturn(Boolean.valueOf(true));

        String objectName = storageService.uploadFile(file);

        assertNotNull(objectName);

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void deveGerarLinkTemporario() throws Exception {
        when(minioClient.bucketExists(any())).thenReturn(Boolean.valueOf(true));

        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("http://minio/teste-url");

        String url = storageService.gerarLinkTemporario("arquivo.png");

        assertEquals("http://minio/teste-url", url);
    }

    @Test
    void deveLancarExceptionQuandoFalharUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "teste.png",
                "image/png",
                "conteudo".getBytes()
        );

        when(minioClient.bucketExists(any())).thenReturn(Boolean.valueOf(true));
        doThrow(new RuntimeException("erro"))
                .when(minioClient)
                .putObject(any(PutObjectArgs.class));

        assertThrows(StorageException.class, () -> storageService.uploadFile(file));
    }
}