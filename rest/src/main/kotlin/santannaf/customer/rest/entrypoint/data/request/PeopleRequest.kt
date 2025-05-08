package santannaf.customer.rest.entrypoint.data.request

import java.time.LocalDate
import santannaf.core.entity.PeopleAbstractRequest

data class PeopleRequest(
    val apelido: String,
    val nome: String,
    val nascimento: LocalDate,
    val stack: List<String>? = null
) : PeopleAbstractRequest {

    /*
    O trecho selecionado define um `companion object` em Kotlin, que é um bloco estático associado à classe `PeopleRequest`.
    Ele contém constantes (`const val`) que são valores imutáveis e de uso comum na classe.

    ### O que faz:
    1. **`companion object`**: Permite criar membros estáticos (acessíveis sem instanciar a classe).
    2. **Constantes**:
        - `ZERO`: Representa o valor `0`.
        - `THIRD_TWO`: Representa o valor `32`.
        - `ONE_HUNDRED`: Representa o valor `100`.

    Essas constantes são usadas no bloco `init` para validar os tamanhos de strings (`apelido`, `nome`, `stack`).

    ### Por que:
        - **Reutilização**: Evita repetição de valores fixos no código.
        - **Legibilidade**: Facilita a compreensão do propósito dos valores.
        - **Manutenção**: Centraliza os valores, tornando mais fácil alterá-los no futuro.
     */

    companion object {
        const val ZERO = 0
        const val THIRD_TWO = 32
        const val ONE_HUNDRED = 100
    }

    /*
    O bloco `init` em Kotlin é usado para inicializar a lógica que deve ser executada quando uma instância da classe é criada.
    Ele é útil para:
        1. **Validação de Dados**: Como no seu código, o `init` é usado para validar os valores das propriedades (`apelido`, `nome`, `stack`) logo após a criação do objeto.
        2. **Lógica de Inicialização**: Permite executar código adicional que não pode ser diretamente atribuído às propriedades.
        3. **Evitar Código Repetido**: Centraliza a lógica de inicialização, evitando duplicação em construtores.

        No seu caso, o `init` garante que os valores das propriedades atendam aos requisitos definidos antes que o objeto seja usado.
    */
    init {
        require(apelido.length in ZERO..THIRD_TWO && apelido.toDoubleOrNull() == null) { "Apelido length must be between 0 and 32 or type invalid" }
        require(nome.length in ZERO..ONE_HUNDRED && nome.toDoubleOrNull() == null) { "Nome length must be between 0 and 100 or type invalid" }
        stack?.forEach {
            require(it.length in ZERO..THIRD_TWO && it.toDoubleOrNull() == null) { "Stack length must be between 0 and 32 or type invalid" }
        }
    }

    override fun fetchNickname(): String = apelido
    override fun fetchBirthday(): LocalDate = nascimento
    override fun fetchName(): String = nome
    override fun fetchStack(): Array<String>? = stack?.toTypedArray()
}
