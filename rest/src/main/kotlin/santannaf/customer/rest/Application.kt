package santannaf.customer.rest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks

/*
O atributo `scanBasePackages` na anotação `@SpringBootApplication` está sendo usado para especificar explicitamente
os pacotes que o Spring deve escanear em busca de componentes, serviços, repositórios, etc., durante a inicialização da aplicação.
No seu caso, o valor `["santannaf"]` indica que o Spring deve escanear todos os subpacotes dentro de `santannaf`. Isso é útil quando os componentes da aplicação estão organizados em
pacotes fora do pacote padrão onde a classe principal (`Application`) está localizada. Sem isso, o Spring não encontraria automaticamente os componentes definidos em outros pacotes.
 */
@SpringBootApplication(scanBasePackages = ["santannaf"])
class Application

fun main(args: Array<String>) {
    /*
    A linha `Hooks.enableAutomaticContextPropagation();` ativa a propagação automática de contexto no Reactor,
    que é uma biblioteca para programação reativa. Essa funcionalidade permite que o contexto (como informações
    de rastreamento ou segurança) seja automaticamente propagado entre os fluxos reativos, sem a necessidade
    de gerenciá-lo manualmente. Isso é útil em aplicações Spring Boot que utilizam programação reativa, garantindo
     que informações contextuais sejam mantidas ao longo do processamento assíncrono.

     */
    Hooks.enableAutomaticContextPropagation();
    runApplication<Application>(args = args)
}
