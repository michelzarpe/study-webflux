package santannaf.core.usecase

import jakarta.inject.Named
import java.util.UUID
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import santannaf.core.exception.PeopleNotFoundException
import santannaf.core.provider.FetchCachePeopleProvider
import santannaf.core.provider.FetchPeopleProvider
import santannaf.core.provider.SavePeopleProvider

@Named
class FetchPeopleUseCase(
    private val fetchProvider: FetchPeopleProvider,
    private val fetchCacheProvider: FetchCachePeopleProvider,
    private val saveProvider: SavePeopleProvider
) {
    fun fetchPeopleById(id: UUID): Mono<UUID> {
        return Mono.fromCallable { id }
            .map { id -> "pessoas:id:$id" }
            .flatMap(fetchCacheProvider::fetchCacheByKey)
            .switchIfEmpty(
                Mono.fromCallable { id }
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(fetchProvider::fetchById)
                    .switchIfEmpty(Mono.error(PeopleNotFoundException()))
            )
            .onErrorResume { Mono.error(it) }
    }

    fun fetchPeopleByTerm(term: String): Mono<UUID?> {
        return Mono.fromCallable { "cache:findByTerm:$term" }
            .flatMap(fetchCacheProvider::fetchCacheByKey)
            .switchIfEmpty(
                Mono.fromCallable { "%$term%" }
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(fetchProvider::fetchByTerm)
                    .switchIfEmpty(Mono.empty())
                    .map {
                        Triple(
                            UUID.randomUUID(),
                            "cache:findByTerm:$term",
                            UUID.randomUUID()
                        )
                    }
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(saveProvider::saveInCache)
                    .map { it }
            )
            .onErrorResume { Mono.error(it) }
    }
}
