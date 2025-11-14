package co.edu.uco.backendvictus.application.service;

import org.springframework.stereotype.Service;

import co.edu.uco.backendvictus.application.dto.ciudad.CiudadResponse;
import co.edu.uco.backendvictus.application.usecase.ciudad.ListCiudadUseCase;
import reactor.core.publisher.Flux;

@Service
public class CiudadService {

    private final ListCiudadUseCase listCiudadUseCase;

    public CiudadService(final ListCiudadUseCase listCiudadUseCase) {
        this.listCiudadUseCase = listCiudadUseCase;
    }

    public Flux<CiudadResponse> listarCiudades() {
        return listCiudadUseCase.execute();
    }

    public Flux<CiudadResponse> streamCiudades() {
        return listarCiudades();
    }
}
