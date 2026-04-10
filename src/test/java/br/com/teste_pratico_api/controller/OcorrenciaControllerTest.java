package br.com.teste_pratico_api.controller;

import br.com.teste_pratico_api.api.ApiPaths;
import br.com.teste_pratico_api.domain.dto.request.OcorrenciaRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaListResponseDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaResponseDTO;
import br.com.teste_pratico_api.domain.enums.StatusOcorrencia;
import br.com.teste_pratico_api.exception.GlobalExceptionHandler;
import br.com.teste_pratico_api.exception.OcorrenciaNotFound;
import br.com.teste_pratico_api.repository.filter.OcorrenciaFilter;
import br.com.teste_pratico_api.security.JwtAuthenticationFilter;
import br.com.teste_pratico_api.security.JwtService;
import br.com.teste_pratico_api.service.OcorrenciaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OcorrenciaController.class)
@AutoConfigureMockMvc(addFilters = false)
class OcorrenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OcorrenciaService ocorrenciaService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("Deve criar ocorrência com sucesso")
    void deveCriarOcorrenciaComSucesso() throws Exception {
        OcorrenciaRequestDTO request = new OcorrenciaRequestDTO();
        request.setCodCliente(1L);
        request.setCodEndereco(2L);
        request.setDtaOcorrencia(LocalDateTime.of(2026, 4, 9, 10, 30));

        OcorrenciaResponseDTO response = new OcorrenciaResponseDTO();
        response.setCodOcorrencia(1L);
        response.setCodCliente(1L);
        response.setNmeCliente("Lucas Gomes");
        response.setCodEndereco(2L);
        response.setNmeCidade("Boa Vista");
        response.setNmeEstado("RR");
        response.setDtaOcorrencia(LocalDateTime.of(2026, 4, 9, 10, 30));
        response.setStaOcorrencia(StatusOcorrencia.ATIVA);

        given(ocorrenciaService.criar(any(OcorrenciaRequestDTO.class))).willReturn(response);

        mockMvc.perform(post(ApiPaths.OCORRENCIAS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codOcorrencia").value(1))
                .andExpect(jsonPath("$.codCliente").value(1))
                .andExpect(jsonPath("$.codEndereco").value(2))
                .andExpect(jsonPath("$.nmeCliente").value("Lucas Gomes"))
                .andExpect(jsonPath("$.nmeCidade").value("Boa Vista"))
                .andExpect(jsonPath("$.staOcorrencia").value("ATIVA"));

        verify(ocorrenciaService).criar(any(OcorrenciaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve buscar ocorrência por id com sucesso")
    void deveBuscarOcorrenciaPorIdComSucesso() throws Exception {
        OcorrenciaResponseDTO response = new OcorrenciaResponseDTO();
        response.setCodOcorrencia(1L);
        response.setCodCliente(1L);
        response.setNmeCliente("Lucas Gomes");
        response.setCodEndereco(2L);
        response.setNmeCidade("Boa Vista");
        response.setNmeEstado("RR");
        response.setDtaOcorrencia(LocalDateTime.of(2026, 4, 9, 10, 30));
        response.setStaOcorrencia(StatusOcorrencia.ATIVA);

        given(ocorrenciaService.buscarPorId(1L)).willReturn(response);

        mockMvc.perform(get(ApiPaths.OCORRENCIAS + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codOcorrencia").value(1))
                .andExpect(jsonPath("$.codCliente").value(1))
                .andExpect(jsonPath("$.nmeCliente").value("Lucas Gomes"))
                .andExpect(jsonPath("$.nmeCidade").value("Boa Vista"))
                .andExpect(jsonPath("$.staOcorrencia").value("ATIVA"));

        verify(ocorrenciaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve listar ocorrências com paginação e filtros")
    void deveListarOcorrenciasComPaginacaoEFiltros() throws Exception {
        OcorrenciaListResponseDTO o1 = new OcorrenciaListResponseDTO();
        o1.setCodOcorrencia(1L);
        o1.setDtaOcorrencia(LocalDateTime.of(2026, 4, 9, 10, 30));
        o1.setStaOcorrencia(StatusOcorrencia.ATIVA);
        o1.setCodCliente(1L);
        o1.setNmeCliente("Lucas Gomes");
        o1.setNroCpf("12345678900");
        o1.setCodEndereco(2L);
        o1.setNmeLogradouro("Rua A");
        o1.setNmeBairro("Centro");
        o1.setNroCep("69300000");
        o1.setNmeCidade("Boa Vista");
        o1.setNmeEstado("RR");
        o1.setLinksEvidencias(List.of("http://localhost/evidencia1"));

        OcorrenciaListResponseDTO o2 = new OcorrenciaListResponseDTO();
        o2.setCodOcorrencia(2L);
        o2.setDtaOcorrencia(LocalDateTime.of(2026, 4, 10, 8, 15));
        o2.setStaOcorrencia(StatusOcorrencia.FINALIZADA);
        o2.setCodCliente(2L);
        o2.setNmeCliente("Maria Silva");
        o2.setNroCpf("98765432100");
        o2.setCodEndereco(3L);
        o2.setNmeLogradouro("Rua B");
        o2.setNmeBairro("Cauamé");
        o2.setNroCep("69301000");
        o2.setNmeCidade("Boa Vista");
        o2.setNmeEstado("RR");
        o2.setLinksEvidencias(List.of("http://localhost/evidencia2"));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("dtaOcorrencia").descending());
        Page<OcorrenciaListResponseDTO> page = new PageImpl<>(List.of(o1, o2), pageable, 2);

        given(ocorrenciaService.listarOcorrencias(any(OcorrenciaFilter.class), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get(ApiPaths.OCORRENCIAS)
                        .param("nmeCliente", "lucas")
                        .param("nroCpf", "12345678900")
                        .param("dtaOcorrencia", "2026-04-09")
                        .param("nmeCidade", "Boa Vista")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dtaOcorrencia,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codOcorrencia").value(1))
                .andExpect(jsonPath("$.content[0].nmeCliente").value("Lucas Gomes"))
                .andExpect(jsonPath("$.content[0].nroCpf").value("12345678900"))
                .andExpect(jsonPath("$.content[0].nmeCidade").value("Boa Vista"))
                .andExpect(jsonPath("$.content[0].linksEvidencias[0]").value("http://localhost/evidencia1"))
                .andExpect(jsonPath("$.content[1].codOcorrencia").value(2))
                .andExpect(jsonPath("$.content[1].staOcorrencia").value("FINALIZADA"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(ocorrenciaService).listarOcorrencias(any(OcorrenciaFilter.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar ocorrência com sucesso")
    void deveAtualizarOcorrenciaComSucesso() throws Exception {
        OcorrenciaRequestDTO request = new OcorrenciaRequestDTO();
        request.setCodCliente(1L);
        request.setCodEndereco(2L);
        request.setDtaOcorrencia(LocalDateTime.of(2026, 4, 9, 11, 0));

        OcorrenciaResponseDTO response = new OcorrenciaResponseDTO();
        response.setCodOcorrencia(1L);
        response.setCodCliente(1L);
        response.setNmeCliente("Lucas Gomes");
        response.setCodEndereco(2L);
        response.setNmeCidade("Boa Vista");
        response.setNmeEstado("RR");
        response.setDtaOcorrencia(LocalDateTime.of(2026, 4, 9, 11, 0));
        response.setStaOcorrencia(StatusOcorrencia.ATIVA);

        given(ocorrenciaService.atualizar(eq(1L), any(OcorrenciaRequestDTO.class))).willReturn(response);

        mockMvc.perform(put(ApiPaths.OCORRENCIAS + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codOcorrencia").value(1))
                .andExpect(jsonPath("$.nmeCliente").value("Lucas Gomes"))
                .andExpect(jsonPath("$.staOcorrencia").value("ATIVA"));

        verify(ocorrenciaService).atualizar(eq(1L), any(OcorrenciaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve excluir ocorrência com sucesso")
    void deveExcluirOcorrenciaComSucesso() throws Exception {
        doNothing().when(ocorrenciaService).excluir(1L);

        mockMvc.perform(delete(ApiPaths.OCORRENCIAS + "/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(ocorrenciaService).excluir(1L);
    }

    @Test
    @DisplayName("Deve finalizar ocorrência com sucesso")
    void deveFinalizarOcorrenciaComSucesso() throws Exception {
        OcorrenciaResponseDTO response = new OcorrenciaResponseDTO();
        response.setCodOcorrencia(1L);
        response.setCodCliente(1L);
        response.setNmeCliente("Lucas Gomes");
        response.setCodEndereco(2L);
        response.setNmeCidade("Boa Vista");
        response.setNmeEstado("RR");
        response.setDtaOcorrencia(LocalDateTime.of(2026, 4, 9, 10, 30));
        response.setStaOcorrencia(StatusOcorrencia.FINALIZADA);

        given(ocorrenciaService.finalizar(1L)).willReturn(response);

        mockMvc.perform(patch(ApiPaths.OCORRENCIAS + "/{id}/finalizar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codOcorrencia").value(1))
                .andExpect(jsonPath("$.staOcorrencia").value("FINALIZADA"));

        verify(ocorrenciaService).finalizar(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar ocorrência inexistente")
    void deveRetornar404AoBuscarOcorrenciaInexistente() throws Exception {
        given(ocorrenciaService.buscarPorId(999L))
                .willThrow(new OcorrenciaNotFound("Ocorrência não encontrada."));

        mockMvc.perform(get(ApiPaths.OCORRENCIAS + "/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(ocorrenciaService).buscarPorId(999L);
    }

    @Test
    @DisplayName("Deve criar ocorrência com cadastro completo e upload de evidências")
    void deveCriarCadastroCompletoAndUmaEvidenciaComSucesso() throws Exception {

        OcorrenciaRequestDTO requestDto = criarRequestPadrao();

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDto)
        );

        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "foto1.png",
                MediaType.IMAGE_PNG_VALUE,
                "conteudo-imagem-1".getBytes()
        );

        OcorrenciaResponseDTO response = new OcorrenciaResponseDTO();
        response.setCodOcorrencia(1L);

        given(ocorrenciaService.cadastroCompleto(any(OcorrenciaRequestDTO.class), anyList()))
                .willReturn(response);

        mockMvc.perform(multipart(ApiPaths.OCORRENCIAS + "/cadastro-completo")
                        .file(requestPart)
                        .file(file1)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codOcorrencia").value(1));
    }

    @Test
    @DisplayName("Deve adicionar evidências à ocorrência com sucesso")
    void deveAdicionarEvidenciasComSucesso() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "foto1.png",
                MediaType.IMAGE_PNG_VALUE,
                "conteudo-imagem-1".getBytes()
        );

        doNothing().when(ocorrenciaService).adicionarEvidencias(eq(1L), anyList());

        mockMvc.perform(multipart(ApiPaths.OCORRENCIAS + "/{id}/evidencias", 1L)
                        .file(file1)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNoContent());

        verify(ocorrenciaService).adicionarEvidencias(eq(1L), anyList());
    }

    @Test
    @DisplayName("Deve criar ocorrência com cadastro completo e múltiplas evidências")
    void deveCriarCadastroCompletoAndMultiplasEvidenciasComSucesso() throws Exception {
        OcorrenciaRequestDTO requestDto = criarRequestPadrao();

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDto)
        );

        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "foto1.png",
                MediaType.IMAGE_PNG_VALUE,
                "imagem-1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "foto2.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imagem-2".getBytes()
        );

        OcorrenciaResponseDTO response = new OcorrenciaResponseDTO();
        response.setCodOcorrencia(1L);
        response.setCodCliente(1L);
        response.setCodEndereco(2L);
        response.setNmeCliente("Lucas Gomes");
        response.setNmeCidade("Boa Vista");
        response.setNmeEstado("RR");
        response.setDtaOcorrencia(LocalDateTime.of(2026, 4, 10, 10, 0));
        response.setStaOcorrencia(StatusOcorrencia.ATIVA);

        given(ocorrenciaService.cadastroCompleto(any(OcorrenciaRequestDTO.class), anyList()))
                .willReturn(response);

        mockMvc.perform(multipart(ApiPaths.OCORRENCIAS + "/cadastro-completo")
                        .file(requestPart)
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codOcorrencia").value(1))
                .andExpect(jsonPath("$.codCliente").value(1))
                .andExpect(jsonPath("$.codEndereco").value(2))
                .andExpect(jsonPath("$.nmeCliente").value("Lucas Gomes"))
                .andExpect(jsonPath("$.staOcorrencia").value("ATIVA"));

        verify(ocorrenciaService).cadastroCompleto(any(OcorrenciaRequestDTO.class), anyList());
    }

    @Test
    @DisplayName("Deve retornar 400 quando não enviar arquivos no cadastro completo")
    void deveRetornar400QuandoNaoEnviarArquivosNoCadastroCompleto() throws Exception {
        OcorrenciaRequestDTO requestDto = criarRequestPadrao();

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDto)
        );

        mockMvc.perform(multipart(ApiPaths.OCORRENCIAS + "/cadastro-completo")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        verify(ocorrenciaService, never()).cadastroCompleto(any(OcorrenciaRequestDTO.class), anyList());
    }

    @Test
    @DisplayName("Deve adicionar múltiplas evidências à ocorrência com sucesso")
    void deveAdicionarMultiplasEvidenciasComSucesso() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "foto1.png",
                MediaType.IMAGE_PNG_VALUE,
                "conteudo-imagem-1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "foto2.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "conteudo-imagem-2".getBytes()
        );

        doNothing().when(ocorrenciaService).adicionarEvidencias(eq(1L), anyList());

        mockMvc.perform(multipart(ApiPaths.OCORRENCIAS + "/{id}/evidencias", 1L)
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNoContent());

        verify(ocorrenciaService).adicionarEvidencias(eq(1L), anyList());
    }

    @Test
    @DisplayName("Deve retornar 400 quando não enviar arquivos para evidências")
    void deveRetornar400QuandoNaoEnviarArquivosParaEvidencias() throws Exception {
        mockMvc.perform(multipart(ApiPaths.OCORRENCIAS + "/{id}/evidencias", 1L)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        verify(ocorrenciaService, never()).adicionarEvidencias(eq(1L), anyList());
    }

    @Test
    @DisplayName("Deve aceitar imagem simulada via MockMultipartFile")
    void deveAceitarImagemSimuladaViaMockMultipartFile() throws Exception {
        MockMultipartFile imagem = new MockMultipartFile(
                "files",
                "evidencia-realista.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10}
        );

        doNothing().when(ocorrenciaService).adicionarEvidencias(eq(10L), anyList());

        mockMvc.perform(multipart(ApiPaths.OCORRENCIAS + "/{id}/evidencias", 10L)
                        .file(imagem)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNoContent());

        verify(ocorrenciaService).adicionarEvidencias(eq(10L), anyList());
    }


    private OcorrenciaRequestDTO criarRequestPadrao() {
        OcorrenciaRequestDTO dto = new OcorrenciaRequestDTO();
        dto.setCodCliente(1L);
        dto.setCodEndereco(2L);
        dto.setDtaOcorrencia(LocalDateTime.now());
        return dto;
    }
}
