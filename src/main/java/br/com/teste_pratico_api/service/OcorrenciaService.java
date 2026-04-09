package br.com.teste_pratico_api.service;

import br.com.teste_pratico_api.domain.dto.request.OcorrenciaRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaListResponseDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaResponseDTO;
import br.com.teste_pratico_api.domain.entity.Cliente;
import br.com.teste_pratico_api.domain.entity.Endereco;
import br.com.teste_pratico_api.domain.entity.FotoOcorrencia;
import br.com.teste_pratico_api.domain.entity.Ocorrencia;
import br.com.teste_pratico_api.domain.enums.StatusOcorrencia;
import br.com.teste_pratico_api.excetion.*;
import br.com.teste_pratico_api.repository.ClienteRepository;
import br.com.teste_pratico_api.repository.EnderecoRepository;
import br.com.teste_pratico_api.repository.FotoOcorrenciaRepository;
import br.com.teste_pratico_api.repository.OcorrenciaRepository;
import br.com.teste_pratico_api.repository.filter.OcorrenciaFilter;
import br.com.teste_pratico_api.util.MapperCustom;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final StorageService storageService;
    private final ModelMapper modelMapper;
    private final FotoOcorrenciaRepository fotoOcorrenciaRepository;
    private final MapperCustom mapperCustom;

    public OcorrenciaResponseDTO criar(OcorrenciaRequestDTO request) {
        Cliente cliente = obterClientePorId(request.getCodCliente());
        Endereco endereco = obterEnderecoPorId(request.getCodEndereco());

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setCliente(cliente);
        ocorrencia.setEndereco(endereco);
        ocorrencia.setDtaOcorrencia(request.getDtaOcorrencia());
        ocorrencia.setStaOcorrencia(StatusOcorrencia.ATIVA);

        ocorrencia = ocorrenciaRepository.save(ocorrencia);
        return modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class);
    }

    public OcorrenciaResponseDTO buscarPorId(Long id) {
        Ocorrencia ocorrencia = obterOcorrenciaPorId(id);
        return modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class);
    }

    public OcorrenciaResponseDTO atualizar(Long id, OcorrenciaRequestDTO request) {
        Ocorrencia ocorrencia = obterOcorrenciaPorId(id);
        validarOcorrenciaNaoFinalizada(ocorrencia);

        Cliente cliente = obterClientePorId(request.getCodCliente());
        Endereco endereco = obterEnderecoPorId(request.getCodEndereco());

        ocorrencia.setCliente(cliente);
        ocorrencia.setEndereco(endereco);
        ocorrencia.setDtaOcorrencia(request.getDtaOcorrencia());

        ocorrencia = ocorrenciaRepository.save(ocorrencia);
        return modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class);
    }

    private void validarOcorrenciaNaoFinalizada(Ocorrencia ocorrencia) {
        if (StatusOcorrencia.FINALIZADA.equals(ocorrencia.getStaOcorrencia())) {
            throw new BusinessException("Ocorrência finalizada não pode ser alterada.");
        }
    }

    public void excluir(Long id) {
        Ocorrencia ocorrencia = obterOcorrenciaPorId(id);
        validarOcorrenciaNaoFinalizada(ocorrencia);

        ocorrenciaRepository.delete(ocorrencia);
    }

    private Ocorrencia obterOcorrenciaPorId(Long id) {
        return ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new OcorrenciaNotFound("Ocorrência não encontrada."));
    }

    private Cliente obterClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFound("Cliente não encontrado."));
    }

    private Endereco obterEnderecoPorId(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNotFound("Endereço não encontrado."));
    }

    @Transactional
    public OcorrenciaResponseDTO cadastroCompleto(OcorrenciaRequestDTO request, List<MultipartFile> files) {
        Cliente cliente = clienteRepository.findById(request.getCodCliente())
                .orElseThrow(() -> new ClienteNotFound("Cliente não encontrado."));

        Endereco endereco = enderecoRepository.findById(request.getCodEndereco())
                .orElseThrow(() -> new EnderecoNotFound("Endereço não encontrado."));

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setCliente(cliente);
        ocorrencia.setEndereco(endereco);
        ocorrencia.setDtaOcorrencia(request.getDtaOcorrencia());
        ocorrencia.setStaOcorrencia(StatusOcorrencia.ATIVA);

        ocorrencia = ocorrenciaRepository.save(ocorrencia);

        salvarFotos(ocorrencia, files);

        return modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class);
    }

    private void salvarFotos(Ocorrencia ocorrencia, List<MultipartFile> files) {
        validarOcorrenciaNaoFinalizada(ocorrencia);

        if (files == null || files.isEmpty()) {
            return;
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            if (!file.getContentType().startsWith("image/")) {
                throw new BusinessException("Arquivo deve ser uma imagem.");
            }

            if (file.getSize() > 5_000_000) {
                throw new BusinessException("Arquivo excede 5MB.");
            }

            String objectName  = storageService.uploadFile(file);

            FotoOcorrencia fotoOcorrencia = new FotoOcorrencia();
            fotoOcorrencia.setOcorrencia(ocorrencia);
            fotoOcorrencia.setDtaCriacao(LocalDateTime.now());
            fotoOcorrencia.setDscPathBucket(objectName);
            fotoOcorrencia.setDscHash(calcularHash(file));

            ocorrencia.addFoto(fotoOcorrencia);
        }
    }

    private String calcularHash(MultipartFile file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(file.getBytes());

            StringBuilder hashHex = new StringBuilder();
            for (byte b : hashBytes) {
                hashHex.append(String.format("%02x", b));
            }

            return hashHex.toString();
        } catch (Exception e) {
            throw new StorageException("Erro ao calcular hash do arquivo.", e);
        }
    }

    public Page<OcorrenciaListResponseDTO> listarOcorrencias(OcorrenciaFilter filter, Pageable pageable) {
        return ocorrenciaRepository.pesquisar(filter, pageable)
                    .map(mapperCustom::toListResponseDTO);
    }


    @Transactional
    public OcorrenciaResponseDTO finalizar(Long id) {
        Ocorrencia ocorrencia = obterOcorrenciaPorId(id);

        if (StatusOcorrencia.FINALIZADA.equals(ocorrencia.getStaOcorrencia())) {
            throw new BusinessException("A ocorrência já está finalizada.");
        }

        ocorrencia.setStaOcorrencia(StatusOcorrencia.FINALIZADA);
        ocorrencia = ocorrenciaRepository.save(ocorrencia);
        return modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class);
    }

    @Transactional
    public void adicionarEvidencias(Long id, List<MultipartFile> files) {
        Ocorrencia ocorrencia = obterOcorrenciaPorId(id);

        validarOcorrenciaNaoFinalizada(ocorrencia);

        salvarFotos(ocorrencia, files);

        ocorrenciaRepository.save(ocorrencia);
    }
}
