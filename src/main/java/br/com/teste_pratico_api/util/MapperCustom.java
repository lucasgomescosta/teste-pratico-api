package br.com.teste_pratico_api.util;

import br.com.teste_pratico_api.domain.dto.response.OcorrenciaListResponseDTO;
import br.com.teste_pratico_api.domain.entity.Ocorrencia;
import br.com.teste_pratico_api.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public final class MapperCustom {

    private final StorageService storageService;

    public OcorrenciaListResponseDTO toListResponseDTO(Ocorrencia ocorrencia) {
        OcorrenciaListResponseDTO dto = new OcorrenciaListResponseDTO();

        dto.setCodOcorrencia(ocorrencia.getCodOcorrencia());
        dto.setDtaOcorrencia(ocorrencia.getDtaOcorrencia());
        dto.setStaOcorrencia(ocorrencia.getStaOcorrencia());

        dto.setCodCliente(ocorrencia.getCliente().getCodCliente());
        dto.setNmeCliente(ocorrencia.getCliente().getNmeCliente());
        dto.setNroCpf(ocorrencia.getCliente().getNroCpf());

        dto.setCodEndereco(ocorrencia.getEndereco().getCodEndereco());
        dto.setNmeLogradouro(ocorrencia.getEndereco().getNmeLogradouro());
        dto.setNmeBairro(ocorrencia.getEndereco().getNmeBairro());
        dto.setNroCep(ocorrencia.getEndereco().getNroCep());
        dto.setNmeCidade(ocorrencia.getEndereco().getNmeCidade());
        dto.setNmeEstado(ocorrencia.getEndereco().getNmeEstado());

        dto.setLinksEvidencias(
                Optional.ofNullable(ocorrencia.getFotos())
                        .orElse(List.of())
                        .stream()
                        .map(foto -> storageService.gerarLinkTemporario(foto.getDscPathBucket()))
                        .toList()
        );

        return dto;
    }
}
