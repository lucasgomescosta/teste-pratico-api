package br.com.teste_pratico_api.service;

import br.com.teste_pratico_api.domain.dto.request.ClienteRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.ClienteResponseDTO;
import br.com.teste_pratico_api.domain.entity.Cliente;
import br.com.teste_pratico_api.excetion.ClienteNotFound;
import br.com.teste_pratico_api.repository.ClienteRepository;
import br.com.teste_pratico_api.repository.filter.ClienteFilter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;


    public ClienteResponseDTO criar(ClienteRequestDTO request) {
        Cliente cliente = modelMapper.map(request, Cliente.class);
        cliente.setDtaCriacao(LocalDateTime.now());

        cliente = clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = obterClientePorId(id);

        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    public Page<ClienteResponseDTO> listar(ClienteFilter filter, Pageable pageable) {
        return clienteRepository.pesquisar(filter, pageable)
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class));
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO request) {
        Cliente cliente = obterClientePorId(id);

        cliente.setNmeCliente(request.getNmeCliente());
        cliente.setDtaNascimento(request.getDtaNascimento());
        cliente.setNroCpf(request.getNroCpf());

        cliente = clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    public void excluir(Long id) {
        Cliente cliente = obterClientePorId(id);
        clienteRepository.delete(cliente);
    }

    private Cliente obterClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFound("Cliente com id: " + id + " não encontrado"));
    }
}
