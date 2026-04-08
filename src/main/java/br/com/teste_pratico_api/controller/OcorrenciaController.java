package br.com.teste_pratico_api.controller;

import br.com.teste_pratico_api.api.ApiPaths;
import br.com.teste_pratico_api.domain.dto.request.OcorrenciaRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaResponseDTO;
import br.com.teste_pratico_api.repository.filter.OcorrenciaFilter;
import br.com.teste_pratico_api.service.OcorrenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.OCORRENCIAS)
@RequiredArgsConstructor
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    @PostMapping
    public ResponseEntity<OcorrenciaResponseDTO> criar(@RequestBody OcorrenciaRequestDTO request) {
        OcorrenciaResponseDTO response = ocorrenciaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OcorrenciaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ocorrenciaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<OcorrenciaResponseDTO>> listar(OcorrenciaFilter filter, Pageable pageable) {
        return ResponseEntity.ok(ocorrenciaService.listar(filter, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OcorrenciaResponseDTO> atualizar(@PathVariable Long id,
                                                           @RequestBody OcorrenciaRequestDTO request) {
        return ResponseEntity.ok(ocorrenciaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        ocorrenciaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
