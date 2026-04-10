package br.com.teste_pratico_api.service;

import br.com.teste_pratico_api.config.properties.MinioProperties;
import br.com.teste_pratico_api.exception.StorageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
    @DisplayName("Deve fazer upload de arquivo com sucesso")
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
        assertTrue(objectName.endsWith(".png"));

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Deve gerar link temporário com sucesso")
    void deveGerarLinkTemporario() throws Exception {
        when(minioClient.bucketExists(any())).thenReturn(Boolean.valueOf(true));

        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("http://minio/teste-url");

        String url = storageService.gerarLinkTemporario("arquivo.png");

        assertEquals("http://minio/teste-url", url);
        verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    @DisplayName("Deve lançar StorageException quando upload falhar")
    void deveLancarExceptionQuandoFalharUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "teste.png",
                "image/png",
                "conteudo".getBytes()
        );

        when(minioClient.bucketExists(any())).thenReturn(Boolean.valueOf(true));
        doThrow(new StorageException("erro"))
                .when(minioClient)
                .putObject(any(PutObjectArgs.class));

        assertThrows(StorageException.class, () -> storageService.uploadFile(file));
    }

    @Test
    @DisplayName("Deve lançar StorageException quando gerar link falhar")
    void deveLancarStorageExceptionQuandoGerarLinkFalhar() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new RuntimeException("Erro ao gerar URL"));

        assertThrows(StorageException.class,
                () -> storageService.gerarLinkTemporario("arquivo.png"));
    }

    @Test
    @DisplayName("Deve lançar StorageException quando arquivo estiver vazio")
    void deveLancarStorageExceptionQuandoArquivoVazio() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "vazio.png",
                "image/png",
                new byte[0]
        );

        assertThrows(StorageException.class, () -> storageService.uploadFile(file));
        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }
}