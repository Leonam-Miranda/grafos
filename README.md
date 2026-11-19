# Rede Social de Amizades

Programa de terminal em Java que modela uma rede de amizades como um **grafo**. Dá para cadastrar pessoas e amizades e fazer consultas sobre a rede: amigos em comum, sugestões, graus de separação, círculos sociais e grupos isolados.

Projeto da disciplina de Estrutura de Dados.

## Como executar

Requer Java 17 ou superior.

```bash
javac -encoding UTF-8 *.java
java Main
```

> No terminal do Windows, se os acentos aparecerem quebrados, rode `chcp 65001` antes de executar.

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
├── Main.java          # menu, leitura de dados e dados iniciais
├── RedeSocial.java    # grafo (lista de adjacência) e algoritmos
└── README.md
```

`RedeSocial` não imprime nada: só guarda o grafo e devolve resultados. Toda a interação com o usuário fica em `Main`.

## Autores

- [Nome 1]
- [Nome 2]
