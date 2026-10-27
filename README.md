# Paladar

O Paladar é um aplicativo Android de restaurantes feito para a disciplina de Desenvolvimento Mobile da Universidade Positivo.
No Trabalho 3, os dados deixaram de ficar apenas em memória e passaram a ser salvos no próprio aparelho com **Room (SQLite)**.

## Funcionalidades

- Cadastrar, editar e remover restaurantes
- Cadastrar e remover categorias
- Ver os detalhes de um restaurante (lidos do banco pelo id) e de uma categoria
- Marcar e desmarcar restaurantes como favoritos
- Tela de favoritos
- Todos os dados continuam salvos depois de fechar e abrir o app

## Tecnologias

- Kotlin
- Jetpack Compose e Material 3
- Navigation Compose
- Room 2.8.4 (com KSP 2.3.10)
- Coroutines

## Como executar

1. Abrir a pasta do projeto no Android Studio.
2. Esperar o Gradle terminar o sync.
3. Executar em um emulador ou celular Android.

Na primeira execução o banco é criado já com 3 categorias e 2 restaurantes iniciais.
Para voltar ao estado inicial, desinstale o app do aparelho ou limpe os dados dele.

---

# Documentação do processo

## 1. Como estava o projeto no Trabalho 2 e o que mudou

No Trabalho 2, o Paladar tinha duas listas (`restaurantes` e `categorias`) guardadas em `mutableStateListOf` no arquivo `Data.kt`. Adicionar e remover itens funcionava, mas tudo era perdido quando o app era fechado. Também não existia edição de itens: o único "update" era o botão de favorito.

No Trabalho 3 mudou o seguinte:

- As duas `data class` (`Restaurante` e `Categoria`) viraram `@Entity`.
- Foram criados dois DAOs e a classe `AppDatabase`.
- O `Data.kt` foi apagado: nenhuma tela lê mais lista fixa em memória.
- Cada tela de lista busca os dados do banco ao abrir (`LaunchedEffect` chamando uma função `suspend`).
- Adicionar e remover passaram a chamar `dao.inserir(...)` e `dao.deletar(...)`.
- A edição de restaurante foi criada de verdade, usando `dao.atualizar(...)`.
- A tela de detalhes do restaurante passou a buscar o item no banco pelo id recebido na navegação.
- As telas de favoritos e de detalhes da categoria passaram a usar consultas do banco.
- O banco nasce com os dados iniciais que antes ficavam no `Data.kt`.

## 2. Entity, DAO e Database

**Entities**

- `Restaurante` (tabela `restaurantes`): `id`, `nome`, `nota`, `preco`, `categoriaId`, `descricao`, `favorito`.
- `Categoria` (tabela `categorias`): `id`, `nome`, `descricao`.

Mantive os mesmos campos do Trabalho 2, porque as telas já usavam todos eles. O `id` passou a ser `@PrimaryKey(autoGenerate = true)` com valor padrão `0`, para o Room gerar o número sozinho. O `categoriaId` liga o restaurante a uma categoria (sem chave estrangeira formal, para manter simples). O `favorito` continua sendo uma coluna do restaurante, então favoritar é um `@Update`.

**DAOs**

- `RestauranteDao` e `CategoriaDao`, cada um com os 4 métodos básicos como `suspend fun`: `@Insert`, `@Query` (buscar todos), `@Update` e `@Delete`.

**Database**

- `AppDatabase` (`abstract class : RoomDatabase()`) com as duas entidades, `version = 1` e os dois DAOs expostos.
- Segue o padrão Singleton visto em aula (`companion object` + `@Volatile` + `synchronized`).
- Um `Callback.onCreate` insere as 3 categorias e os 2 restaurantes iniciais. Como roda só quando o banco é criado, o que o usuário apagar não volta.

## 3. Como estruturei as queries dos DAOs

Além dos 4 métodos básicos, criei consultas filtradas onde as telas precisavam:

- `buscarPorId(id)` nos dois DAOs: usada na tela de detalhes do restaurante (seção 3.3 do enunciado), na de detalhes da categoria e para mostrar o nome da categoria dentro do detalhe do restaurante.
- `RestauranteDao.buscarPorCategoria(categoriaId)`: lista os restaurantes na tela de detalhes da categoria.
- `RestauranteDao.buscarFavoritos()`: usada na tela de favoritos (`WHERE favorito = 1`).

Preferi filtrar no SQL em vez de carregar tudo e filtrar no Kotlin, porque assim cada tela já recebe só o que precisa.

Toda chamada ao banco roda dentro de `CoroutineScope(Dispatchers.IO).launch { ... }` (ou `withContext(Dispatchers.IO)` para buscar), com `try/catch` e `Log.e(...)` em caso de erro. Ao inserir, crio o objeto antes do `launch`, porque os campos do formulário são limpos logo em seguida e a coroutine poderia ler campos já vazios.

## 4. Dificuldades na migração de `mutableStateListOf` para Room

A principal mudança foi que as telas deixaram de mexer direto na lista (`restaurantes.add(...)`, `restaurantes.remove(...)`). Agora cada operação chama o DAO em uma coroutine e, depois, busca a lista de novo no banco para a tela refletir a mudança. Cada tela ganhou uma função `carregar()`.

O que quebrou e como resolvi:

- **Gradle:** o plugin do KSP precisou ser adicionado e sincronizado antes das dependências do Room, na ordem que o enunciado pede.
- **Referências ao `Data.kt`:** ao apagar o `Data.kt`, a `TelaCategorias` ainda usava a lista `categorias` e deu 13 erros de "Unresolved reference". Resolvi migrando a `TelaCategorias` para o Room antes de apagar o arquivo.
- **Consulta que não existia:** a tela de detalhes da categoria dava "Unresolved reference 'buscarPorCategoria'" porque o método não estava no `RestauranteDao`. Adicionei a query e o erro sumiu.
- **Código colado no lugar errado:** ao adaptar a `TelaRestaurantes` e o detalhe do restaurante, cheguei a colar um `onClick` dentro de outro, a duplicar o corpo de um botão e a perder o botão de remover. Consertei conferindo a estrutura das chaves de cada função.
- **Banco vazio:** com Room, a lista começou vazia, já que os dados iniciais não vinham mais do `Data.kt`. Resolvi com o `Callback.onCreate`. Para testar o seed é preciso desinstalar o app ou limpar os dados, porque ele só roda quando o banco é criado.

## 5. Teste de persistência

O vídeo abaixo mostra o teste de aceitação: adicionar um item, fechar o app completamente e abrir de novo, com o item continuando lá.

[Vídeo do teste de persistência](https://youtu.be/PaKQLu8-V9I)

## 6. Dificuldades dos integrantes

Fiz este trabalho individualmente, então as dificuldades são as da seção 4. As que mais deram trabalho foram a ordem das mudanças (migrar uma tela de cada vez para o app continuar compilando) e conferir onde cada trecho de código entrava dentro de funções grandes do `Telas.kt`.
## 6. Dificuldades dos integrantes

Fiz este trabalho individualmente, então as dificuldades são as da seção 4. As que mais deram trabalho foram a ordem das mudanças (migrar uma tela de cada vez para o app continuar compilando) e conferir onde cada trecho de código entrava dentro de funções grandes do `Telas.kt`.
