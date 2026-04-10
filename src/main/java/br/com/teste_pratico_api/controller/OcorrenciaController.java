package br.com.teste_pratico_api.controller;

import br.com.teste_pratico_api.api.ApiPaths;
import br.com.teste_pratico_api.domain.dto.request.OcorrenciaRequestDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaListResponseDTO;
import br.com.teste_pratico_api.domain.dto.response.OcorrenciaResponseDTO;
import br.com.teste_pratico_api.repository.filter.OcorrenciaFilter;
import br.com.teste_pratico_api.service.OcorrenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PostMapping(value = "/cadastro-completo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OcorrenciaResponseDTO> criarCadastroCompleto(
            @RequestPart("request") OcorrenciaRequestDTO request,
            @RequestPart("files") List<MultipartFile> files
    ) {
        OcorrenciaResponseDTO response = ocorrenciaService.cadastroCompleto(request, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<OcorrenciaListResponseDTO>> listarOcorrencias(
            OcorrenciaFilter filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ocorrenciaService.listarOcorrencias(filter, pageable));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<OcorrenciaResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(ocorrenciaService.finalizar(id));
    }

    @PostMapping(value = "/{id}/evidencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadEvidencias(
            @PathVariable Long id,
            @RequestPart("files") List<MultipartFile> files
    ) {
        ocorrenciaService.adicionarEvidencias(id, files);
        return ResponseEntity.noContent().build();
    }
}
