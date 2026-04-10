package br.com.teste_pratico_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.teste_pratico_api.api.ApiPaths;
import br.com.teste_pratico_api.domain.dto.request.ClienteRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.ClienteResponseDTO;
import br.com.teste_pratico_api.exception.ClienteNotFound;
import br.com.teste_pratico_api.exception.GlobalExceptionHandler;
import br.com.teste_pratico_api.repository.filter.ClienteFilter;
import br.com.teste_pratico_api.security.JwtAuthenticationFilter;
import br.com.teste_pratico_api.security.JwtService;
import br.com.teste_pratico_api.service.ClienteService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void deveCriarClienteComSucesso() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNmeCliente("Lucas Gomes");
        request.setDtaNascimento(LocalDate.of(1995, 1, 10));
        request.setNroCpf("12345678900");

        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setCodCliente(1L);
        response.setNmeCliente("Lucas Gomes");
        response.setDtaNascimento(LocalDate.of(1995, 1, 10));
        response.setNroCpf("12345678900");
        response.setDtaCriacao(LocalDateTime.of(2026, 4, 9, 10, 0));

        given(clienteService.criar(any(ClienteRequestDTO.class))).willReturn(response);

        mockMvc.perform(post(ApiPaths.CLIENTES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codCliente").value(1))
                .andExpect(jsonPath("$.nmeCliente").value("Lucas Gomes"))
                .andExpect(jsonPath("$.nroCpf").value("12345678900"))
                .andExpect(jsonPath("$.dtaNascimento").value("1995-01-10"));

        verify(clienteService).criar(any(ClienteRequestDTO.class));
    }

    @Test
    @DisplayName("Deve buscar cliente por id com sucesso")
    void deveBuscarClientePorIdComSucesso() throws Exception {
        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setCodCliente(1L);
        response.setNmeCliente("Lucas Gomes");
        response.setDtaNascimento(LocalDate.of(1995, 1, 10));
        response.setNroCpf("12345678900");
        response.setDtaCriacao(LocalDateTime.of(2026, 4, 9, 10, 0));

        given(clienteService.buscarPorId(1L)).willReturn(response);

        mockMvc.perform(get(ApiPaths.CLIENTES + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codCliente").value(1))
                .andExpect(jsonPath("$.nmeCliente").value("Lucas Gomes"))
                .andExpect(jsonPath("$.nroCpf").value("12345678900"));

        verify(clienteService).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve listar clientes com paginação")
    void deveListarClientesComPaginacao() throws Exception {
        ClienteResponseDTO c1 = new ClienteResponseDTO();
        c1.setCodCliente(1L);
        c1.setNmeCliente("Lucas Gomes");
        c1.setDtaNascimento(LocalDate.of(1995, 1, 10));
        c1.setNroCpf("12345678900");
        c1.setDtaCriacao(LocalDateTime.of(2026, 4, 9, 10, 0));

        ClienteResponseDTO c2 = new ClienteResponseDTO();
        c2.setCodCliente(2L);
        c2.setNmeCliente("Maria Silva");
        c2.setDtaNascimento(LocalDate.of(1990, 5, 20));
        c2.setNroCpf("98765432100");
        c2.setDtaCriacao(LocalDateTime.of(2026, 4, 9, 11, 0));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("nmeCliente").ascending());
        Page<ClienteResponseDTO> page = new PageImpl<>(List.of(c1, c2), pageable, 2);

        given(clienteService.listar(any(ClienteFilter.class), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get(ApiPaths.CLIENTES)
                        .param("nmeCliente", "lucas")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nmeCliente,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codCliente").value(1))
                .andExpect(jsonPath("$.content[0].nmeCliente").value("Lucas Gomes"))
                .andExpect(jsonPath("$.content[0].nroCpf").value("12345678900"))
                .andExpect(jsonPath("$.content[1].codCliente").value(2))
                .andExpect(jsonPath("$.content[1].nmeCliente").value("Maria Silva"))
                .andExpect(jsonPath("$.content[1].nroCpf").value("98765432100"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(clienteService).listar(any(ClienteFilter.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setNmeCliente("Lucas Gomes Atualizado");
        request.setDtaNascimento(LocalDate.of(1995, 1, 10));
        request.setNroCpf("12345678900");

        ClienteResponseDTO response = new ClienteResponseDTO();
        response.setCodCliente(1L);
        response.setNmeCliente("Lucas Gomes Atualizado");
        response.setDtaNascimento(LocalDate.of(1995, 1, 10));
        response.setNroCpf("12345678900");
        response.setDtaCriacao(LocalDateTime.of(2026, 4, 9, 10, 0));

        given(clienteService.atualizar(eq(1L), any(ClienteRequestDTO.class))).willReturn(response);

        mockMvc.perform(put(ApiPaths.CLIENTES + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codCliente").value(1))
                .andExpect(jsonPath("$.nmeCliente").value("Lucas Gomes Atualizado"));

        verify(clienteService).atualizar(eq(1L), any(ClienteRequestDTO.class));
    }

    @Test
    @DisplayName("Deve excluir cliente com sucesso")
    void deveExcluirClienteComSucesso() throws Exception {
        doNothing().when(clienteService).excluir(1L);

        mockMvc.perform(delete(ApiPaths.CLIENTES + "/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(clienteService).excluir(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar cliente inexistente")
    void deveRetornar404AoBuscarClienteInexistente() throws Exception {
        given(clienteService.buscarPorId(999L))
                .willThrow(new ClienteNotFound("Cliente não encontrado."));

        mockMvc.perform(get(ApiPaths.CLIENTES + "/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(clienteService).buscarPorId(999L);
    }
}