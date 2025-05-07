package santannaf.core.dataprovider.repository.postgres

import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import java.util.UUID
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import santannaf.core.entity.People
import santannaf.core.provider.FetchCachePeopleProvider
import santannaf.core.provider.FetchPeopleProvider
import santannaf.core.provider.SavePeopleProvider

@Repository
class PeoplePostgresProvider(
    private val client: DatabaseClient
) : FetchPeopleProvider, FetchCachePeopleProvider, SavePeopleProvider {
    override fun save(people: People): Mono<People> {
        return client
            .sql("insert into pessoas (id,apelido,nome,nascimento,stack,busca) values (:id,:nickname,:name,:birthday,:stack,:search)")
            .bindValues(
                mapOf(
                    "id" to people.id,
                    "nickname" to people.nickname,
                    "name" to people.name,
                    "birthday" to people.birthday,
                    "stack" to people.stack,
                    "search" to people.search
                )
            )
            .fetch()
            .rowsUpdated()
            .thenReturn(people)
    }

    override fun saveInCache(pair: Triple<UUID, String, UUID>): Mono<UUID> {
        return client.sql("insert into cache (key,data) values (:key,:data) on conflict (key) do update set data = :data")
            .bind("key", pair.second)
            .bind("data", pair.third)
            .fetch()
            .rowsUpdated()
            .thenReturn(pair.first)
    }

    override fun fetchById(id: UUID): Mono<UUID> {
        return client.sql("select id from pessoas where id = :id")
            .bind("id", id)
            .map { row: Row, _: RowMetadata -> row["id"] as UUID }
            .one()
    }

    override fun fetchByTerm(q: String): Mono<UUID> {
        return client.sql("select id from pessoas where busca ilike :q limit 1")
            .bind("q", q)
            .map { row: Row, _: RowMetadata ->  row["id"] as UUID  }
            .one()
    }

    override fun fetchCacheByKey(key: String): Mono<UUID> {
        return client.sql("select data from cache where key = :key")
            .bind("key", key)
            .map { row: Row, _: RowMetadata -> row["data"] as UUID }
            .one()
    }
}
