package br.unisul.aula.projturma.service;

import br.unisul.aula.projturma.dto.DadosAlunoDTO;
import br.unisul.aula.projturma.dto.DadosProfessorDTO;
import br.unisul.aula.projturma.dto.InfoTurmaDTO;
import br.unisul.aula.projturma.dto.TurmaDTO;
import br.unisul.aula.projturma.model.Turma;
import br.unisul.aula.projturma.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private RestTemplate restTemplate;

    public void registrarTurma(TurmaDTO dto) {
        DadosProfessorDTO professorDTO = consumirProfessor(dto);

        List<DadosAlunoDTO> listaAlunosDTO = consumirAlunos(dto);

        Turma turma = new Turma();
        turma.setNome(dto.getNomeTurma());
        turma.setProfessorID(professorDTO.getId());
        Set<Long> matricula = new HashSet<>();
        for (DadosAlunoDTO alunoDTO: listaAlunosDTO){
            matricula.add(alunoDTO.getId());
        }
        turma.setMatriculaAlunos(matricula);
        turmaRepository.save(turma);

    }

    private List<DadosAlunoDTO> consumirAlunos(TurmaDTO dto) {
        List<DadosAlunoDTO> alunoDTOList = new ArrayList<>();
        for (int i = 0; i < dto.getNomeAlunos().size(); i++) {
            DadosAlunoDTO alunoDTO = new DadosAlunoDTO(dto.getNomeAlunos().get(i));
            alunoDTOList.add(alunoDTO);
        }
        return getEndPointAlunosPost(alunoDTOList, "nome");
    }

    private List<DadosAlunoDTO> getEndPointAlunosPost(List<DadosAlunoDTO> alunoDTOList, String destino) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<DadosAlunoDTO>> dadosAlunoDTOHttpEntity =
                new HttpEntity<>(alunoDTOList, httpHeaders);
        ResponseEntity<DadosAlunoDTO[]> exchange =
                restTemplate.exchange("http://aluno/aluno/"+destino,HttpMethod.POST,
                dadosAlunoDTOHttpEntity,DadosAlunoDTO[].class);
        List<DadosAlunoDTO> listaAlunosDTO = new ArrayList<>(Arrays.asList(exchange.getBody()));
        return listaAlunosDTO;
    }

    private DadosProfessorDTO consumirProfessor(TurmaDTO dto) {
        ResponseEntity<DadosProfessorDTO> exchange =
                restTemplate.exchange("http://professor/professor/nome/" + dto.getNomeProfessor(),
                HttpMethod.GET, null, DadosProfessorDTO.class);
        DadosProfessorDTO professorDTO  = exchange.getBody();
        return professorDTO;
    }

    public List<InfoTurmaDTO> listarTodas() {
        List<Turma> turmaList = turmaRepository.findAll();
        List<InfoTurmaDTO> infoTurmaDTOS =new ArrayList<>();
        for (Turma turma: turmaList){
            InfoTurmaDTO dto = new InfoTurmaDTO();
            dto.setId(turma.getId());
            dto.setNomeTurma(turma.getNome());
            dto.setNomeProfessor(buscarNomeProfessor(turma.getProfessorID()));
            List<DadosAlunoDTO> dadosAlunoDTOS = buscarNomesAlunos(turma.getMatriculaAlunos());
            List<String> nomesAlunos = new ArrayList<>();
            for (int i = 0; i < dadosAlunoDTOS.size(); i++) {
                nomesAlunos.add(dadosAlunoDTOS.get(i).getNome());
            }
            dto.setNomesAlunos(nomesAlunos);

            infoTurmaDTOS.add(dto);
            
        }

        return infoTurmaDTOS;
    }

    private List<DadosAlunoDTO> buscarNomesAlunos(Set<Long> matriculaAlunos) {
            List<DadosAlunoDTO> alunoDTOList = new ArrayList<>();
            for (Long matricula: matriculaAlunos) {
                DadosAlunoDTO alunoDTO = new DadosAlunoDTO(matricula);
                alunoDTOList.add(alunoDTO);
            }
        return getEndPointAlunosPost(alunoDTOList, "ids");
    }

    private String buscarNomeProfessor(Long professorID) {
        ResponseEntity<DadosProfessorDTO> exchange =
                restTemplate.exchange("http://professor/professor/" + professorID,
                        HttpMethod.GET, null, DadosProfessorDTO.class);
        DadosProfessorDTO professorDTO  = exchange.getBody();
        return professorDTO.getNome();
    }
}
