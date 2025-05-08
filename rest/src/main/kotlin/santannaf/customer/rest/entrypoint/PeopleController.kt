package santannaf.customer.rest.entrypoint

import java.time.Duration
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import santannaf.core.usecase.FetchPeopleUseCase
import santannaf.core.usecase.SavePeopleUseCase
import santannaf.customer.rest.entrypoint.data.request.PeopleRequest

@RestController
class PeopleController(
    private val fetchUseCase: FetchPeopleUseCase,
    private val saveUseCase: SavePeopleUseCase
) {
    companion object {
        const val TIMEOUT = 800L
    }

    @PostMapping(path = ["/pessoas"])
    @ResponseStatus(HttpStatus.CREATED)
    fun createPeople(
        @RequestBody request: PeopleRequest
    ): Mono<ResponseEntity<*>> { // Define o retorno do método como um `Mono` que encapsula um `ResponseEntity` genérico.
        /*
        A linha selecionada cria um `Mono` a partir de uma chamada que encapsula o objeto `request`.
        O metodo `Mono.fromCallable` é usado para executar uma operação de forma assíncrona e não-bloqueante. Nesse caso, ele encapsula o objeto `request` dentro de um fluxo reativo (`Mono`),
         permitindo que ele seja processado posteriormente no pipeline reativo.
        */
        return Mono.fromCallable { request } // Cria um `Mono` a partir de uma chamada que encapsula o objeto `request`.
            .subscribeOn(Schedulers.boundedElastic()) // Define que a execução será feita em uma thread do pool `boundedElastic`.
            .flatMap(saveUseCase::savePeople) // Encadeia a chamada ao método `savePeople` do `SavePeopleUseCase`, que retorna um `Mono<UUID>`.
            .timeout(Duration.ofMillis(TIMEOUT)) // Define um tempo limite para a execução do fluxo reativo.
            .map { // Transforma o resultado (UUID) em uma resposta HTTP.
                ResponseEntity.created( // Cria uma resposta HTTP com status `201 Created`.
                    UriComponentsBuilder // Constrói a URI do recurso criado.
                        .fromPath("/pessoas/{id}") // Define o caminho base da URI com um placeholder para o ID.
                        .buildAndExpand(it) // Substitui o placeholder `{id}` pelo valor do UUID retornado.
                        .toUri() // Converte a URI construída para o formato `java.net.URI`.
                ).build<Void>() // Finaliza a construção do `ResponseEntity` sem corpo.
            }
    }


    @GetMapping(path = ["/pessoas/{id}"])
    fun fetchPeopleById(@PathVariable id: UUID): Mono<ResponseEntity<*>> {
        return Mono.fromCallable { id }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(fetchUseCase::fetchPeopleById)
            .timeout(Duration.ofMillis(TIMEOUT))
            .map { ResponseEntity.ok().body(it) }
    }

    @GetMapping(path = ["/pessoas"])
    fun fetchPeopleByTerm(@RequestParam t: String): Mono<ResponseEntity<*>> {
        return Mono.fromCallable { t }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(fetchUseCase::fetchPeopleByTerm)
            .timeout(Duration.ofMillis(TIMEOUT))
            .map { ResponseEntity.ok().body(it) }
    }
}
