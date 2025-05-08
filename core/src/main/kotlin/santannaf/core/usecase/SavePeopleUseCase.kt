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

/*
`Mono.zip` é uma função da biblioteca Reactor (usada em programação reativa com Kotlin/Java) que
combina múltiplos publishers (`Mono` ou `Flux`) em um único `Mono`. Ele espera que todos os publishers
fornecidos sejam concluídos e, em seguida, emite um único item contendo os resultados combinados.

### Funcionamento:
- **Entrada**: Recebe dois ou mais publishers (`Mono` ou `Flux`).
- **Saída**: Retorna um `Mono` que emite um `Tuple` (ou outro tipo de combinação) contendo os valores emitidos por cada publisher.
- **Execução paralela**: Os publishers são executados em paralelo, e o `Mono.zip` só emite o resultado quando todos os publishers forem concluídos com sucesso.
- **Erro**: Se qualquer um dos publishers falhar, o `Mono.zip` emitirá o erro imediatamente.

### Exemplo:
```kotlin
val mono1 = Mono.just("A")
val mono2 = Mono.just("B")

Mono.zip(mono1, mono2)
    .map { tuple -> "${tuple.t1} e ${tuple.t2}" }
    .subscribe { println(it) } // Saída: "A e B"
```

### No seu código:
```kotlin
return Mono.zip(toCache, toDatabase)
    .flatMap { Mono.just(id) }
    .onErrorResume { Mono.error(it) }
```

1. **`Mono.zip(toCache, toDatabase)`**: Combina os resultados de `toCache` e `toDatabase`. Ambos precisam ser concluídos com sucesso.
2. **`flatMap { Mono.just(id) }`**: Após a conclusão, retorna o `id` como resultado final.
3. **`onErrorResume { Mono.error(it) }`**: Caso qualquer um dos publishers falhe, o erro será propagado.

### Resumo:
`Mono.zip` é útil para combinar múltiplas operações assíncronas que dependem umas das outras ou precisam ser concluídas
juntas antes de prosseguir.
 */
