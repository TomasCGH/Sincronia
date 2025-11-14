package co.edu.uco.backendvictus.application.usecase;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface UseCase<I, O> {

    Mono<O> execute(I input);
}
