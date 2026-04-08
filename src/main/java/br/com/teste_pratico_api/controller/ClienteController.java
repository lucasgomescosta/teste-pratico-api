package br.com.teste_pratico_api.controller;

import br.com.teste_pratico_api.api.ApiPaths;
import br.com.teste_pratico_api.domain.dto.request.ClienteRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.ClienteResponseDTO;
import br.com.teste_pratico_api.repository.filter.ClienteFilter;
import br.com.teste_pratico_api.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.CLIENTES)
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(@RequestBody ClienteRequestDTO request) {
        ClienteResponseDTO response = clienteService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listar(ClienteFilter filter, Pageable pageable) {
        return ResponseEntity.ok(clienteService.listar(filter, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody ClienteRequestDTO request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
