package santannaf.core.provider

import java.util.UUID
import reactor.core.publisher.Mono

interface FetchPeopleProvider {
    fun fetchById(id: UUID): Mono<UUID>
    fun fetchByTerm(q: String): Mono<UUID>
}
