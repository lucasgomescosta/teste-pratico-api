package br.com.teste_pratico_api.service;

import br.com.teste_pratico_api.domain.dto.request.EnderecoRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.EnderecoResponseDTO;
import br.com.teste_pratico_api.domain.entity.Endereco;
import br.com.teste_pratico_api.exception.EnderecoNotFound;
import br.com.teste_pratico_api.repository.EnderecoRepository;
import br.com.teste_pratico_api.repository.filter.EnderecoFilter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ModelMapper modelMapper;

    public EnderecoResponseDTO criar(EnderecoRequestDTO request) {
        Endereco endereco = modelMapper.map(request, Endereco.class);
        endereco = enderecoRepository.save(endereco);

        return modelMapper.map(endereco, EnderecoResponseDTO.class);
    }

    public EnderecoResponseDTO buscarPorId(Long id) {
        Endereco endereco = obterEnderecoPorId(id);
        return modelMapper.map(endereco, EnderecoResponseDTO.class);
    }

    public Page<EnderecoResponseDTO> listar(EnderecoFilter filter, Pageable pageable) {
        return enderecoRepository.pesquisar(filter, pageable)
                .map(endereco -> modelMapper.map(endereco, EnderecoResponseDTO.class));
    }

    public EnderecoResponseDTO atualizar(Long id, EnderecoRequestDTO request) {
        Endereco endereco = obterEnderecoPorId(id);

        endereco.setNmeLogradouro(request.getNmeLogradouro());
        endereco.setNmeBairro(request.getNmeBairro());
        endereco.setNroCep(request.getNroCep());
        endereco.setNmeCidade(request.getNmeCidade());
        endereco.setNmeEstado(request.getNmeEstado());

        endereco = enderecoRepository.save(endereco);
        return modelMapper.map(endereco, EnderecoResponseDTO.class);
    }

    public void excluir(Long id) {
        Endereco endereco = obterEnderecoPorId(id);
        enderecoRepository.delete(endereco);
    }

    private Endereco obterEnderecoPorId(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new EnderecoNotFound("Endereço não encontrado."));
    }
}
