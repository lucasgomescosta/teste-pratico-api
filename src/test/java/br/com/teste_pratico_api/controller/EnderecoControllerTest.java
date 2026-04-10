package br.com.teste_pratico_api.controller;

import br.com.teste_pratico_api.api.ApiPaths;
import br.com.teste_pratico_api.domain.dto.request.EnderecoRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.EnderecoResponseDTO;
import br.com.teste_pratico_api.exception.EnderecoNotFound;
import br.com.teste_pratico_api.repository.filter.EnderecoFilter;
import br.com.teste_pratico_api.security.JwtAuthenticationFilter;
import br.com.teste_pratico_api.security.JwtService;
import br.com.teste_pratico_api.service.EnderecoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnderecoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnderecoService enderecoService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("Deve criar endereço com sucesso")
    void deveCriarEnderecoComSucesso() throws Exception {
        EnderecoRequestDTO request = new EnderecoRequestDTO();
        request.setNmeLogradouro("Rua A");
        request.setNmeBairro("Centro");
        request.setNroCep("69300000");
        request.setNmeCidade("Boa Vista");
        request.setNmeEstado("RR");

        EnderecoResponseDTO response = new EnderecoResponseDTO();
        response.setCodEndereco(1L);
        response.setNmeLogradouro("Rua A");
        response.setNmeBairro("Centro");
        response.setNroCep("69300000");
        response.setNmeCidade("Boa Vista");
        response.setNmeEstado("RR");

        given(enderecoService.criar(any(EnderecoRequestDTO.class))).willReturn(response);

        mockMvc.perform(post(ApiPaths.ENDERECOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codEndereco").value(1))
                .andExpect(jsonPath("$.nmeLogradouro").value("Rua A"))
                .andExpect(jsonPath("$.nmeBairro").value("Centro"))
                .andExpect(jsonPath("$.nroCep").value("69300000"))
                .andExpect(jsonPath("$.nmeCidade").value("Boa Vista"))
                .andExpect(jsonPath("$.nmeEstado").value("RR"));

        verify(enderecoService).criar(any(EnderecoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve buscar endereço por id com sucesso")
    void deveBuscarEnderecoPorIdComSucesso() throws Exception {
        EnderecoResponseDTO response = new EnderecoResponseDTO();
        response.setCodEndereco(1L);
        response.setNmeLogradouro("Rua A");
        response.setNmeBairro("Centro");
        response.setNroCep("69300000");
        response.setNmeCidade("Boa Vista");
        response.setNmeEstado("RR");

        given(enderecoService.buscarPorId(1L)).willReturn(response);

        mockMvc.perform(get(ApiPaths.ENDERECOS + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codEndereco").value(1))
                .andExpect(jsonPath("$.nmeLogradouro").value("Rua A"))
                .andExpect(jsonPath("$.nmeCidade").value("Boa Vista"));

        verify(enderecoService).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve listar endereços com paginação")
    void deveListarEnderecosComPaginacao() throws Exception {
        EnderecoResponseDTO e1 = new EnderecoResponseDTO();
        e1.setCodEndereco(1L);
        e1.setNmeLogradouro("Rua A");
        e1.setNmeBairro("Centro");
        e1.setNroCep("69300000");
        e1.setNmeCidade("Boa Vista");
        e1.setNmeEstado("RR");

        EnderecoResponseDTO e2 = new EnderecoResponseDTO();
        e2.setCodEndereco(2L);
        e2.setNmeLogradouro("Rua B");
        e2.setNmeBairro("Cauamé");
        e2.setNroCep("69301000");
        e2.setNmeCidade("Boa Vista");
        e2.setNmeEstado("RR");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("nmeLogradouro").ascending());
        Page<EnderecoResponseDTO> page = new PageImpl<>(List.of(e1, e2), pageable, 2);

        given(enderecoService.listar(any(EnderecoFilter.class), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get(ApiPaths.ENDERECOS)
                        .param("nmeCidade", "boa vista")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nmeLogradouro,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codEndereco").value(1))
                .andExpect(jsonPath("$.content[0].nmeLogradouro").value("Rua A"))
                .andExpect(jsonPath("$.content[0].nroCep").value("69300000"))
                .andExpect(jsonPath("$.content[1].codEndereco").value(2))
                .andExpect(jsonPath("$.content[1].nmeLogradouro").value("Rua B"))
                .andExpect(jsonPath("$.content[1].nroCep").value("69301000"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(enderecoService).listar(any(EnderecoFilter.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar endereço com sucesso")
    void deveAtualizarEnderecoComSucesso() throws Exception {
        EnderecoRequestDTO request = new EnderecoRequestDTO();
        request.setNmeLogradouro("Rua A Atualizada");
        request.setNmeBairro("Centro");
        request.setNroCep("69300000");
        request.setNmeCidade("Boa Vista");
        request.setNmeEstado("RR");

        EnderecoResponseDTO response = new EnderecoResponseDTO();
        response.setCodEndereco(1L);
        response.setNmeLogradouro("Rua A Atualizada");
        response.setNmeBairro("Centro");
        response.setNroCep("69300000");
        response.setNmeCidade("Boa Vista");
        response.setNmeEstado("RR");

        given(enderecoService.atualizar(eq(1L), any(EnderecoRequestDTO.class))).willReturn(response);

        mockMvc.perform(put(ApiPaths.ENDERECOS + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codEndereco").value(1))
                .andExpect(jsonPath("$.nmeLogradouro").value("Rua A Atualizada"));

        verify(enderecoService).atualizar(eq(1L), any(EnderecoRequestDTO.class));
    }

    @Test
    @DisplayName("Deve excluir endereço com sucesso")
    void deveExcluirEnderecoComSucesso() throws Exception {
        doNothing().when(enderecoService).excluir(1L);

        mockMvc.perform(delete(ApiPaths.ENDERECOS + "/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(enderecoService).excluir(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar endereço inexistente")
    void deveRetornar404AoBuscarEnderecoInexistente() throws Exception {
        given(enderecoService.buscarPorId(999L))
                .willThrow(new EnderecoNotFound("Endereço não encontrado."));

        mockMvc.perform(get(ApiPaths.ENDERECOS + "/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(enderecoService).buscarPorId(999L);
    }
}
