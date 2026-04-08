package br.com.teste_pratico_api.service;

import br.com.teste_pratico_api.domain.dto.request.OcorrenciaRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaResponseDTO;
import br.com.teste_pratico_api.domain.entity.Cliente;
import br.com.teste_pratico_api.domain.entity.Endereco;
import br.com.teste_pratico_api.domain.entity.Ocorrencia;
import br.com.teste_pratico_api.excetion.ClienteNotFound;
import br.com.teste_pratico_api.excetion.EnderecoNotFound;
import br.com.teste_pratico_api.excetion.OcorrenciaNotFound;
import br.com.teste_pratico_api.repository.ClienteRepository;
import br.com.teste_pratico_api.repository.EnderecoRepository;
import br.com.teste_pratico_api.repository.OcorrenciaRepository;
import br.com.teste_pratico_api.repository.filter.OcorrenciaFilter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ModelMapper modelMapper;

    public OcorrenciaResponseDTO criar(OcorrenciaRequestDTO request) {
        Cliente cliente = obterClientePorId(request.getCodCliente());
        Endereco endereco = obterEnderecoPorId(request.getCodEndereco());

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setCliente(cliente);
        ocorrencia.setEndereco(endereco);
        ocorrencia.setDtaOcorrencia(request.getDtaOcorrencia());
        ocorrencia.setStaOcorrencia(request.getStaOcorrencia());

        ocorrencia = ocorrenciaRepository.save(ocorrencia);
        return modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class);
    }

    public OcorrenciaResponseDTO buscarPorId(Long id) {
        Ocorrencia ocorrencia = obterOcorrenciaPorId(id);
        return modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class);
    }

    public Page<OcorrenciaResponseDTO> listar(OcorrenciaFilter filter, Pageable pageable) {
        return ocorrenciaRepository.pesquisar(filter, pageable)
                .map(ocorrencia -> modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class));
    }

    public OcorrenciaResponseDTO atualizar(Long id, OcorrenciaRequestDTO request) {
        Ocorrencia ocorrencia = obterOcorrenciaPorId(id);
        Cliente cliente = obterClientePorId(request.getCodCliente());
        Endereco endereco = obterEnderecoPorId(request.getCodEndereco());

        ocorrencia.setCliente(cliente);
        ocorrencia.setEndereco(endereco);
        ocorrencia.setDtaOcorrencia(request.getDtaOcorrencia());
        ocorrencia.setStaOcorrencia(request.getStaOcorrencia());

        ocorrencia = ocorrenciaRepository.save(ocorrencia);
        return modelMapper.map(ocorrencia, OcorrenciaResponseDTO.class);
    }

    public void excluir(Long id) {
        Ocorrencia ocorrencia = obterOcorrenciaPorId(id);
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

}
