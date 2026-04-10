package br.com.teste_pratico_api.service;

import br.com.teste_pratico_api.domain.dto.request.OcorrenciaRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaListResponseDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaResponseDTO;
import br.com.teste_pratico_api.domain.entity.Cliente;
import br.com.teste_pratico_api.domain.entity.Endereco;
import br.com.teste_pratico_api.domain.entity.Ocorrencia;
import br.com.teste_pratico_api.exception.ClienteNotFound;
import br.com.teste_pratico_api.exception.OcorrenciaNotFound;
import br.com.teste_pratico_api.repository.ClienteRepository;
import br.com.teste_pratico_api.repository.EnderecoRepository;
import br.com.teste_pratico_api.repository.FotoOcorrenciaRepository;
import br.com.teste_pratico_api.repository.OcorrenciaRepository;
import br.com.teste_pratico_api.repository.filter.OcorrenciaFilter;
import br.com.teste_pratico_api.util.MapperCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OcorrenciaServiceTest {

    @InjectMocks
    private OcorrenciaService ocorrenciaService;

    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FotoOcorrenciaRepository fotoOcorrenciaRepository;

    @Mock
    private MapperCustom mapperCustom;

    @BeforeEach
    void setup() {
        ocorrenciaService = new OcorrenciaService(
                ocorrenciaRepository,
                clienteRepository,
                enderecoRepository,
                storageService,
                modelMapper,
                fotoOcorrenciaRepository,
                mapperCustom
        );
    }

    @Test
    @DisplayName("Deve criar ocorrência com sucesso")
    void deveCriarOcorrencia() {
        OcorrenciaRequestDTO request = new OcorrenciaRequestDTO();
        request.setDtaOcorrencia(LocalDateTime.of(2026, 4, 9, 10, 30));
        request.setCodCliente(1L);
        request.setCodEndereco(1L);
        Cliente cliente = new Cliente();
        cliente.setCodCliente(1L);

        Endereco endereco = new Endereco();
        endereco.setCodEndereco(1L);

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setCodOcorrencia(1L);

        OcorrenciaResponseDTO responseDTO = new OcorrenciaResponseDTO();
        responseDTO.setCodOcorrencia(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(ocorrenciaRepository.save(any())).thenReturn(ocorrencia);
        when(modelMapper.map(any(Ocorrencia.class), eq(OcorrenciaResponseDTO.class))).thenReturn(responseDTO);

        OcorrenciaResponseDTO response = ocorrenciaService.criar(request);

        assertNotNull(response);
        verify(ocorrenciaRepository).save(any());
    }

    @Test
    @DisplayName("Deve criar ocorrência com evidências")
    void deveCriarComArquivos() throws Exception {
        OcorrenciaRequestDTO request = new OcorrenciaRequestDTO();
        request.setCodCliente(1L);
        request.setCodEndereco(1L);
        request.setDtaOcorrencia(LocalDateTime.now());

        Cliente cliente = new Cliente();
        cliente.setCodCliente(1L);

        Endereco endereco = new Endereco();
        endereco.setCodEndereco(1L);

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setCodOcorrencia(1L);

        OcorrenciaResponseDTO responseDTO = new OcorrenciaResponseDTO();
        responseDTO.setCodOcorrencia(1L);

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "foto.jpg",
                "image/jpeg",
                "imagem fake".getBytes()
        );

        when(clienteRepository.findById(request.getCodCliente())).thenReturn(Optional.of(cliente));
        when(enderecoRepository.findById(request.getCodEndereco())).thenReturn(Optional.of(endereco));
        when(ocorrenciaRepository.save(any())).thenReturn(ocorrencia);
        when(modelMapper.map(any(Ocorrencia.class), eq(OcorrenciaResponseDTO.class))).thenReturn(responseDTO);
        when(storageService.uploadFile(any())).thenReturn("url-fake");

        OcorrenciaResponseDTO response = ocorrenciaService.cadastroCompleto(request, List.of(file));

        assertNotNull(response);
        verify(storageService).uploadFile(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando cliente não existir")
    void deveLancarErroClienteNaoEncontrado() {
        OcorrenciaRequestDTO request = new OcorrenciaRequestDTO();
        request.setCodCliente(1L);

        when(clienteRepository.findById(request.getCodCliente())).thenReturn(Optional.empty());

        assertThrows(ClienteNotFound.class, () ->
                ocorrenciaService.criar(request)
        );
    }

    @Test
    @DisplayName("Deve listar ocorrências com sucesso")
    void deveListarOcorrencias() {
        OcorrenciaFilter filtro = new OcorrenciaFilter();

        Pageable pageable = Pageable.ofSize(10).withPage(0);

        List<Ocorrencia> listaOcorrencias = List.of(new Ocorrencia());
        Page<Ocorrencia> pagina = new PageImpl<>(listaOcorrencias, pageable, 1);

        when(ocorrenciaRepository.pesquisar(any(OcorrenciaFilter.class), any(Pageable.class))).thenReturn(pagina);

        Page<OcorrenciaListResponseDTO> response = ocorrenciaService.listarOcorrencias(filtro, pageable);

        assertNotNull(response);
        verify(ocorrenciaRepository).pesquisar(any(OcorrenciaFilter.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao listar ocorrências com filtro inválido")
    void deveLancarErroAoListarOcorrenciasComFiltroInvalido() {
        OcorrenciaFilter filtro = new OcorrenciaFilter();

        Pageable pageable = Pageable.ofSize(10).withPage(0);

        when(ocorrenciaRepository.pesquisar(any(), any()))
                .thenThrow(new IllegalArgumentException("Filtro inválido"));

        assertThrows(IllegalArgumentException.class, () ->
                ocorrenciaService.listarOcorrencias(filtro, pageable)
        );
    }

    @Test
    @DisplayName("Deve buscar ocorrência por ID com sucesso")
    void deveBuscarOcorrenciaPorId() {
        Long codOcorrencia = 1L;

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setCodOcorrencia(codOcorrencia);

        OcorrenciaResponseDTO responseDTO = new OcorrenciaResponseDTO();
        responseDTO.setCodOcorrencia(codOcorrencia);

        when(ocorrenciaRepository.findById(codOcorrencia))
                .thenReturn(Optional.of(ocorrencia));

        when(modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class))
                .thenReturn(responseDTO);

        OcorrenciaResponseDTO response = ocorrenciaService.buscarPorId(codOcorrencia);

        assertNotNull(response);
        assertEquals(1L, response.getCodOcorrencia());
        verify(ocorrenciaRepository).findById(codOcorrencia);
        verify(modelMapper).map(ocorrencia, OcorrenciaResponseDTO.class);
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar ocorrência por ID inexistente")
    void deveLancarErroAoBuscarOcorrenciaPorIdInexistente() {
        Long codOcorrencia = 1L;

        when(ocorrenciaRepository.findById(codOcorrencia)).thenReturn(Optional.empty());

        assertThrows(OcorrenciaNotFound.class, () ->
                ocorrenciaService.buscarPorId(codOcorrencia)
        );
    }
}
