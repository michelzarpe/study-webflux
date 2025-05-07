package santannaf.core.usecase

import jakarta.inject.Named
import java.util.UUID
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import santannaf.core.entity.PeopleAbstractRequest
import santannaf.core.provider.SavePeopleProvider

@Named
class SavePeopleUseCase(
    private val saveProvider: SavePeopleProvider
) {
    fun savePeople(request: PeopleAbstractRequest): Mono<UUID> {
        val search = request.buildSearch()
        val people = request.buildPeople(search)
        val id = people.id
        val triple = Triple(id, "pessoas:id:$id", id)

        val toCache = saveProvider.saveInCache(triple).subscribeOn(Schedulers.boundedElastic())
        val toDatabase = saveProvider.save(people).subscribeOn(Schedulers.boundedElastic())

        return Mono.zip(toCache, toDatabase)
            .flatMap { Mono.just(id) }
            .onErrorResume { Mono.error(it) }
    }
}
