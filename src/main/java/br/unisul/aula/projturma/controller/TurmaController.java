package br.unisul.aula.projturma.controller;

import br.unisul.aula.projturma.dto.TurmaDTO;
import br.unisul.aula.projturma.service.TurmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TurmaController {

    @Autowired
    private TurmaService turmaService;

    @PostMapping
    public void incluirTurma(@RequestBody TurmaDTO dto){
        turmaService.registrarTurma(dto);
    }
}
