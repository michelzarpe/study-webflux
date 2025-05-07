package santannaf.core.provider

import java.util.UUID
import reactor.core.publisher.Mono

interface FetchCachePeopleProvider {
    fun fetchCacheByKey(key: String): Mono<UUID>
}
