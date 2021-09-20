package br.unisul.aula.projturma.service;

import br.unisul.aula.projturma.dto.DadosProfessorDTO;
import br.unisul.aula.projturma.dto.TurmaDTO;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TurmaService {

    public void registrarTurma(TurmaDTO dto) {
        DadosProfessorDTO professorDTO = new DadosProfessorDTO();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DadosProfessorDTO> exchange =
                restTemplate.exchange("http://localhost:9091/professor/nome/" + dto.getNomeProfessor(),
                HttpMethod.GET, null, DadosProfessorDTO.class);
        professorDTO = exchange.getBody();

        System.out.println("Retorno Prof DTO: "+professorDTO);
    }
}
