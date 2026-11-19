# Rede Social de Amizades

Programa de terminal em Java que modela uma rede de amizades como um **grafo**. Dá para cadastrar pessoas e amizades e fazer consultas sobre a rede: amigos em comum, sugestões, graus de separação, círculos sociais e grupos isolados.

Projeto da disciplina de Estrutura de Dados.

## Como executar

Requer Java 17 ou superior.

```bash
javac -encoding UTF-8 *.java
java Janela    # interface gráfica
java Main      # versão de terminal (menu)
```

> No terminal do Windows, se os acentos aparecerem quebrados, rode `chcp 65001` antes de executar.

### Interface gráfica (`Janela`)

Feita com Swing, que já vem no JDK (nenhuma biblioteca externa). O grafo é desenhado na tela com layout automático: as pessoas se repelem e os amigos se atraem.

- **Clique numa pessoa:** destaca os amigos e mostra as sugestões de amizade (linhas tracejadas).
- **Clique em uma segunda pessoa:** destaca o menor caminho (BFS) em dourado e mostra os graus de separação.
- **Arraste** as bolinhas para reorganizar. O tamanho de cada uma é o grau (número de amigos), que também aparece dentro dela.
- **Cores:** cada grupo isolado (componente conexo) tem uma cor. Remover o Silas faz o grupo A se dividir em dois, e dá para ver a mudança na hora.
- O painel lateral tem adicionar e remover pessoas, conectar e desconectar amigos, e as consultas (mais popular, círculos sociais, grupos isolados, lista e matriz).

#### Colorir por centralidade

Na aba Explorar, o cartão **Colorir por** muda o que a cor das bolinhas representa. Cor fria é o menor valor e cor quente é o maior, com uma barra de legenda embaixo e o ranking no painel de resultado:

| Modo | O que mede | Como é calculado |
|---|---|---|
| **Grupos** | Quem se alcança por amizades | DFS (componentes conexos) |
| **Grau** | Número de amigos | Tamanho do conjunto de adjacência |
| **Proximidade** | Quão perto a pessoa está de todas as que alcança | Uma BFS por pessoa, O(V·(V+E)) |
| **Intermediação** | Em quantos menores caminhos entre outras pessoas ela aparece (ponte da rede) | Algoritmo de Brandes, uma BFS por pessoa, O(V·(V+E)) |

No exemplo, o Silas lidera a intermediação (9,5), seguido do Ismael (8,0), que tem só 2 amigos: o que importa é ligar partes da rede, não ter muitos amigos. Ao clicar numa pessoa, a proximidade e a intermediação dela também aparecem no painel.

#### Aba "Algoritmos": o algoritmo passo a passo

Mostra **como** cada algoritmo pensa, não só o resultado. Escolha o algoritmo, clique numa pessoa para ser o início (BFS e DFS) e use Play, voltar ou próximo passo:

| Algoritmo | Estrutura | O que se vê |
|---|---|---|
| **BFS** | Fila (FIFO) | O grafo se pinta em camadas. A fila aparece desenhada embaixo, e o número dentro de cada bolinha é a distância até o início. |
| **DFS** | Pilha (LIFO) | O algoritmo mergulha por um caminho e volta (backtracking). A pilha mostra o caminho atual, e o número é a ordem de visita. |
| **Articulação** | DFS a cada teste | A cada pessoa "tirada" da rede, os grupos que sobram ficam coloridos. Quem faz o número de grupos aumentar ganha um anel vermelho. |

Em todos há um cartão com a estrutura usada e a complexidade, o **pseudocódigo com a linha em execução destacada** e uma frase explicando cada passo. As cores e anéis têm legenda embaixo do grafo.

Quem gera os passos é a `RedeSocial` (`passosBfs`, `passosDfs`, `passosArticulacao`), que devolve uma lista de "fotografias" do algoritmo. A janela só reproduz essa lista, então a lógica dos algoritmos continua toda em um lugar.

**Pontos de articulação:** o programa testa uma pessoa por vez (tira, recalcula os grupos e compara), o que custa O(V·(V+E)). O algoritmo de Tarjan faz o mesmo numa única DFS, em O(V+E). Fica como ideia de melhoria.

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

Lista de ligações do grupo A: Felipe–Gabriel Xavier, Felipe–Silas, Felipe–Helder, Gabriel Xavier–Silas, Silas–Helder, Silas–Ismael, Ismael–Benks, Benks–Joao Paulo.

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

| Opção | Função | Técnica | Complexidade |
|---|---|---|---|
| 1 a 4 | Adicionar e remover pessoas e amizades | Operações em `Map` e `Set` | O(log V) (remover pessoa: O(grau)) |
| 5 | Listar amigos e grau | Tamanho do conjunto de adjacência | O(1) |
| 6 | Amigos em comum | Interseção de dois conjuntos | O(grau) |
| 7 | Sugestão de amigos | Amigos de amigos, contados e ordenados por amigos em comum | O(Σ graus) |
| 8 | Graus de separação | **BFS** com mapa de pais | O(V + E) |
| 9 | Círculos sociais | **BFS por níveis** | O(V + E) |
| 10 | Grupos isolados | **DFS** (componentes conexos) | O(V + E) |
| 11 | Mais popular | Maior grau (lista todos em caso de empate) | O(V) |
| 12 | Lista e matriz de adjacência | Percorre o grafo | O(V²) |
| (janela) | Pontos de articulação | Tira cada pessoa e recontagem de grupos | O(V·(V+E)) |
| (janela) | Proximidade e intermediação | Uma BFS por pessoa (Brandes para a intermediação) | O(V·(V+E)) |

Os nomes são normalizados na entrada: `joao  paulo` e `Joao Paulo` são a mesma pessoa.

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

**Dica para a apresentação:** remover o Silas (opção 2) e rodar a opção 10 de novo mostra o grupo A se dividindo em dois, porque ele era o ponto de ligação entre eles.

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
