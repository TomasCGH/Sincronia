package co.edu.uco.backendvictus.application.service;

import org.springframework.stereotype.Service;

import co.edu.uco.backendvictus.application.dto.departamento.DepartamentoResponse;
import co.edu.uco.backendvictus.application.usecase.departamento.ListDepartamentoUseCase;
import reactor.core.publisher.Flux;

@Service
public class DepartamentoService {

    private final ListDepartamentoUseCase listDepartamentoUseCase;

    public DepartamentoService(final ListDepartamentoUseCase listDepartamentoUseCase) {
        this.listDepartamentoUseCase = listDepartamentoUseCase;
    }

    public Flux<DepartamentoResponse> listarDepartamentos() {
        return listDepartamentoUseCase.execute();
    }

    public Flux<DepartamentoResponse> streamDepartamentos() {
        return listarDepartamentos();
    }
}
