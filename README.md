# Rede Social de Amizades

Programa em Java que modela uma rede de amizades como um **grafo**. Dá para cadastrar pessoas e amizades e fazer consultas sobre a rede: amigos em comum, sugestões, graus de separação, círculos sociais, grupos isolados, pessoas mais centrais e pontos de articulação.

Há duas formas de usar, que compartilham a mesma lógica de grafos (`RedeSocial`):

- **Interface gráfica** (`Janela`): o grafo desenhado na tela, com consultas por clique e os algoritmos animados passo a passo.
- **Versão de terminal** (`Main`): um menu numerado com as mesmas operações básicas.

Projeto da disciplina de Estrutura de Dados.

## Como executar

Requer Java 17 ou superior.

```bash
javac -encoding UTF-8 *.java
java Janela    # interface gráfica
java Main      # versão de terminal (menu)
```

> No terminal do Windows, se os acentos aparecerem quebrados, rode `chcp 65001` antes de executar.

O `Main` também aceita a entrada redirecionada de um arquivo, o que é útil para testar uma sequência de comandos (o fim do arquivo encerra o programa):

```bash
java Main < entrada.txt
```

**Os dados não são salvos.** A rede fica só na memória: toda execução começa com a rede de exemplo (12 pessoas em 2 grupos), e o que for adicionado ou removido se perde ao fechar o programa.

## Interface gráfica (`Janela`)

Feita com Swing, que já vem no JDK (nenhuma biblioteca externa). O grafo é desenhado na tela com layout automático: as pessoas se repelem e os amigos se atraem. A janela tem duas abas: **Explorar** e **Algoritmos**.

### Aba "Explorar"

**Seleção com o mouse**

- **Clique numa pessoa:** destaca os amigos e mostra as sugestões de amizade (linhas tracejadas), além da proximidade e da intermediação dela.
- **Clique em uma segunda pessoa:** destaca o menor caminho (BFS) em dourado e mostra os graus de separação e os amigos em comum.
- **Clique de novo na mesma pessoa** desmarca; **clique no espaço vazio** limpa a seleção; com duas pessoas já selecionadas, **um novo clique** começa uma seleção nova.
- **Arraste** as bolinhas para reorganizar. O tamanho de cada uma é o grau (número de amigos), que também aparece dentro dela.
- **Cores:** por padrão, cada grupo isolado (componente conexo) tem uma cor.

**Painel lateral**

| Cartão | Botão | O que precisa estar selecionado |
|---|---|---|
| Pessoas | **Adicionar** (ou Enter no campo de nome). A pessoa nova já fica selecionada, pronta para ser conectada. | Nada |
| Pessoas | **Remover pessoa selecionada** | Uma pessoa |
| Amizades | **Conectar** / **Desconectar** | Duas pessoas |
| Consultas | **Mais popular** (marca com um halo dourado quem tem maior grau) | Nada |
| Consultas | **Círculos sociais** | Uma pessoa |
| Consultas | **Grupos isolados** | Nada |
| Consultas | **Lista e matriz** (abre uma janela com as duas representações) | Nada |
| Colorir por | Grupos, Grau, Proximidade, Intermediação (ver abaixo) | Nada |
| (rodapé) | **Reiniciar com dados de exemplo**: descarta as mudanças e volta à rede inicial | Nada |

Quando remover uma pessoa, conectar ou desconectar muda o número de grupos isolados, o painel de resultado avisa.

#### Colorir por centralidade

O cartão **Colorir por** muda o que a cor das bolinhas representa. Cor fria é o menor valor e cor quente é o maior, com uma barra de legenda embaixo e o ranking no painel de resultado:

| Modo | O que mede | Como é calculado |
|---|---|---|
| **Grupos** | Quem se alcança por amizades | DFS (componentes conexos) |
| **Grau** | Número de amigos | Tamanho do conjunto de adjacência |
| **Proximidade** | Quão perto a pessoa está de todas as que alcança | Uma BFS por pessoa, O(V·(V+E)) |
| **Intermediação** | Em quantos menores caminhos entre outras pessoas ela aparece (ponte da rede) | Algoritmo de Brandes, uma BFS por pessoa, O(V·(V+E)) |

- **Proximidade** vai de 0 a 1 (perto de 1 = chega em todo mundo em poucos saltos). É calculada como `(alcançadas / soma das distâncias) × (alcançadas / (V − 1))`: o segundo fator é a correção de Wasserman e Faust, que desconta quem só alcança parte da rede (aqui a rede tem dois grupos, então ninguém alcança todos).
- **Intermediação** é o valor bruto, sem normalização: a soma, para cada par de outras pessoas, da fração dos menores caminhos entre elas que passa pela pessoa. Como o grafo não é direcionado, cada par é contado uma vez só.

No exemplo, o Silas lidera a intermediação (9,5), seguido do Ismael (8,0), que tem só 2 amigos: o que importa é ligar partes da rede, não ter muitos amigos. Na proximidade, a ordem é Silas (0,36), Ismael (0,33) e Gabriel Siqueira (0,29).

### Aba "Algoritmos": o algoritmo passo a passo

Mostra **como** cada algoritmo pensa, não só o resultado. Escolha o algoritmo, clique numa pessoa para ser o início (BFS e DFS) e use os controles:

- **Voltar ao começo**, **voltar um passo**, **Play/Pausa** e **próximo passo**. Dar Play depois do último passo recomeça do início.
- **Velocidade:** controle deslizante com 5 níveis, de 1,8 s a 0,22 s por passo (o padrão é 0,8 s).

| Algoritmo | Estrutura | O que se vê |
|---|---|---|
| **BFS** | Fila (FIFO) | O grafo se pinta em camadas. A fila aparece desenhada embaixo, e o número dentro de cada bolinha é a distância até o início. |
| **DFS** | Pilha (LIFO) | O algoritmo mergulha por um caminho e volta (backtracking). A pilha mostra o caminho atual, e o número é a ordem de visita. |
| **Articulação** | DFS a cada teste | A cada pessoa "tirada" da rede, os grupos que sobram ficam coloridos. Quem faz o número de grupos aumentar ganha um anel vermelho. |

Em todos há um cartão com a estrutura usada e a complexidade, o **pseudocódigo com a linha em execução destacada** e uma frase explicando cada passo. As cores e anéis têm legenda embaixo do grafo.

Quem gera os passos é a `RedeSocial` (`passosBfs`, `passosDfs`, `passosArticulacao`), que devolve uma lista de "fotografias" do algoritmo (o record `Passo`). A janela só reproduz essa lista, então a lógica dos algoritmos continua toda em um lugar.

**Pontos de articulação:** são as pessoas que, se saírem, deixam a rede com mais grupos. Na rede de exemplo são **Benks, Gabriel Siqueira, Ismael, Silas e Wilson**. O programa testa uma pessoa por vez (tira, recalcula os grupos e compara), o que custa O(V·(V+E)). O algoritmo de Tarjan faz o mesmo numa única DFS, em O(V+E). Fica como ideia de melhoria.

## Modelagem

| Conceito da rede | Conceito de grafo |
|---|---|
| Pessoa | Vértice |
| Amizade | Aresta |
| Número de amigos | Grau do vértice |
| Grupo sem ligação com os outros | Componente conexo |

- **Não direcionado:** se A é amigo de B, B é amigo de A.
- **Não ponderado:** toda amizade tem o mesmo peso.
- **Sem laços:** ninguém é amigo de si mesmo.

### Grafo inicial (12 pessoas em 2 grupos)

```
Grupo A
      Gabriel Xavier
        /        \
  Felipe -------- Silas ---- Ismael ---- Benks ---- Joao Paulo
        \        /
         Helder

Grupo B
  Leonam -------- Fabricio
        \        /
     Gabriel Siqueira ---- Wilson ---- Wallace
```

Lista de ligações:

- **Grupo A:** Felipe–Gabriel Xavier, Felipe–Silas, Felipe–Helder, Gabriel Xavier–Silas, Silas–Helder, Silas–Ismael, Ismael–Benks, Benks–Joao Paulo.
- **Grupo B:** Leonam–Fabricio, Leonam–Gabriel Siqueira, Fabricio–Gabriel Siqueira, Gabriel Siqueira–Wilson, Wilson–Wallace.

## Estrutura de dados

O grafo é guardado como **lista de adjacência**: `Map<String, Set<String>>`, em que cada pessoa aponta para o conjunto dos seus amigos (`TreeMap` e `TreeSet`, então tudo sai em ordem alfabética e sem amizade duplicada).

A opção 12 do menu imprime o mesmo grafo também como **matriz de adjacência** (0 e 1), para comparar as duas representações:

| | Lista de adjacência | Matriz de adjacência |
|---|---|---|
| Memória | O(V + E) | O(V²) |
| Listar amigos de alguém | O(grau) | O(V) |
| Verificar se há amizade | O(log grau) | O(1) |
| Adicionar pessoa | O(log V) | O(V²) (redimensionar) |
| Melhor para | Redes grandes e esparsas, como redes sociais | Grafos pequenos e densos |

Numa rede social cada pessoa conhece só uma pequena parte do total, então a lista é a escolha natural.

## Funcionalidades

| Opção | Função | Método em `RedeSocial` | Técnica | Complexidade |
|---|---|---|---|---|
| 1 a 4 | Adicionar e remover pessoas e amizades | `addPessoa`, `removePessoa`, `addAmizade`, `removeAmizade` | Operações em `Map` e `Set` | Adicionar pessoa: O(log V). Amizade: O(log grau). Remover pessoa: O(log V + grau · log V) |
| 5 | Listar amigos e grau | `amigos`, `grau` | Tamanho do conjunto de adjacência | Grau: O(1). Listar os amigos: O(grau) |
| 6 | Amigos em comum | `amigosEmComum` | Interseção de dois conjuntos | O(grau · log grau) |
| 7 | Sugestão de amigos | `sugestoes` | Amigos de amigos, contados e ordenados por amigos em comum (empate: ordem alfabética) | O(S · log grau + k · log k), com S = soma dos graus dos amigos e k = número de sugestões |
| 8 | Graus de separação | `caminho` | **BFS** com mapa de pais | O(V + E) |
| 9 | Círculos sociais | `circulos` | **BFS por níveis** | O(V log V + E): o V log V vem de ordenar cada nível |
| 10 | Grupos isolados | `grupos` | **DFS** (componentes conexos) | O(V log V + E): cada grupo é um `TreeSet` ordenado |
| 11 | Mais popular | `maisPopulares` | Maior grau (lista todos em caso de empate) | O(V) |
| 12 | Lista e matriz de adjacência | `listaAdjacencia`, `matrizAdjacencia` | Percorre o grafo | O(V² · log V): a matriz consulta o `TreeSet` em cada uma das V² células |
| 0 | Sair | — | — | — |
| (janela) | Pontos de articulação | `passosArticulacao` | Tira cada pessoa e reconta os grupos | O(V·(V+E)) |
| (janela) | Proximidade e intermediação | `proximidade`, `intermediacao` | Uma BFS por pessoa (Brandes para a intermediação) | O(V·(V+E)) |

**Sobre as complexidades:** seguindo a convenção dos livros, as contas acima tratam *achar uma pessoa* no `TreeMap` (`adj.get(p)`) como O(1). Na verdade essa busca custa O(log V), então o custo real ganha um fator log V a cada acesso. A BFS do `caminho`, por exemplo, fica O(V log V + E). Com `HashMap` e `HashSet` o acesso seria O(1) em média e os valores bateriam com os dos livros, mas a ordem alfabética das listas, dos rankings e da animação se perderia. Com 12 pessoas a diferença não aparece na prática.

A DFS de `grupos()` é **iterativa**, com uma pilha explícita (`ArrayDeque`) em vez de recursão, então não estoura a pilha de chamadas do Java mesmo em redes grandes.

### Comportamento da `RedeSocial`

- **Operações de cadastro** (`addPessoa`, `removePessoa`, `addAmizade`, `removeAmizade`) devolvem `boolean`: `false` quando não há o que fazer (nome vazio ou repetido, pessoa inexistente, amizade já existente ou com a própria pessoa).
- **Consultas** (`amigos`, `grau`, `amigosEmComum`, `sugestoes`, `caminho`, `circulos`, `passosBfs`, `passosDfs`) lançam `IllegalArgumentException` se a pessoa não existir. O `Main` e a `Janela` verificam com `existe` antes de chamar.
- **`caminho`** devolve uma lista vazia quando não há ligação entre as duas pessoas.

### Normalização de nomes

Os nomes são normalizados na entrada (`RedeSocial.normaliza`): tira espaços extras e deixa cada palavra com a inicial maiúscula e o resto minúsculo. Assim, `joao  paulo`, `JOAO PAULO` e `Joao Paulo` são a mesma pessoa.

Limites: a normalização não mexe em acentos, então `João` e `Joao` são pessoas **diferentes**; e nomes com maiúscula no meio perdem o formato (`McDonald` vira `Mcdonald`).

## Exemplo de execução

Com os dados iniciais:

```
Opção: 8
Primeira pessoa: Silas
Segunda pessoa: Joao Paulo
Silas -> Ismael -> Benks -> Joao Paulo (3 graus)

Opção: 9
Pessoa: Felipe
1 grau: [Gabriel Xavier, Helder, Silas]
2 graus: [Ismael]
3 graus: [Benks]
4 graus: [Joao Paulo]

Opção: 7
Pessoa: Gabriel Xavier
Helder (2 amigo(s) em comum)
Ismael (1 amigo(s) em comum)

Opção: 10
2 grupo(s) isolado(s):
Grupo 1 (7): [Benks, Felipe, Gabriel Xavier, Helder, Ismael, Joao Paulo, Silas]
Grupo 2 (5): [Fabricio, Gabriel Siqueira, Leonam, Wallace, Wilson]

Opção: 11
Mais popular: Silas com 4 amigo(s).
```

**Dica para a apresentação:** remover o Silas (opção 2 no terminal, ou "Remover pessoa selecionada" na janela) e consultar os grupos isolados de novo mostra o grupo A se dividindo em dois, porque ele era o ponto de ligação entre eles. Na janela, as cores mudam na hora.

## Estrutura do projeto

```
.
├── Main.java          # versão de terminal (menu)
├── Janela.java        # versão gráfica (Swing)
├── RedeSocial.java    # grafo (lista de adjacência), algoritmos e dados de exemplo
├── APRESENTACAO.md    # slides para exibir durante a apresentação (código explicado)
├── ROTEIRO.md         # roteiro de estudo: o que clicar, o que falar e perguntas prováveis
└── README.md
```

`RedeSocial` não imprime nada: só guarda o grafo e devolve resultados. Toda a interação com o usuário fica em `Main` e `Janela`, que usam exatamente a mesma classe.

## Autores

- Leonam Miranda de Oliveira
- Gabriel Figueira Siqueira
