# Roteiro de apresentação (cerca de 17 minutos)

Este roteiro diz **o que clicar**, **o que vai aparecer na tela**, **o que aquilo significa**, **o que falar** e, principalmente, **qual trecho do código Java mostrar e como explicá-lo**. Em cada parte, primeiro a tela mostra o resultado, e depois o bloco **"No código"** mostra *como o Java faz aquilo*. Esse bloco é o centro da apresentação. Todos os números vêm da rede de exemplo (12 pessoas, 13 amizades, 2 grupos) e foram conferidos rodando o programa.

> **Como usar as falas:** elas estão escritas como se fossem ditas em voz alta, mas **não é para decorar**. Leiam, entendam a ideia e falem com as próprias palavras. O bloco "O que significa" é o que vocês precisam *saber* para responder qualquer pergunta sobre aquela tela.

## Índice

1. [Divisão da fala](#divisão-da-fala)
2. [Antes de começar](#antes-de-começar)
3. [Glossário: as palavras que vão aparecer](#glossário-as-palavras-que-vão-aparecer)
4. [Como ler a tela](#como-ler-a-tela)
5. [As partes da apresentação, cada uma com o seu código](#1-abertura-modelagem-e-mapa-do-código-2-min--leonam)
6. [Bônus: a interface por dentro](#bônus-a-interface-por-dentro-código-do-swing)
7. [Perguntas que podem fazer](#perguntas-que-podem-fazer)

## Divisão da fala

| Parte | Quem fala | Tempo | Código que se mostra |
|---|---|---|---|
| 1. Abertura, modelagem e mapa do código | Leonam | 2 min | os 3 arquivos |
| 2. A estrutura de dados | Leonam | 3 min | `adj`, `addPessoa`, `addAmizade`, `removePessoa`, `amigos` |
| 3. Explorar a rede | Leonam | 3 min | `amigosEmComum`, `sugestoes`, `caminho` |
| 4. Colorir por centralidade | Gabriel | 2 min | `distancias`, `proximidade`, `intermediacao` |
| 5. Algoritmos por dentro | Gabriel | 5 min | `passosBfs`, `Passo`, `grupos` e `dfs`, `componentesSem` |
| 6. A demonstração final | Gabriel | 1,5 min | `removerPessoa` (na janela) |
| Encerramento | Leonam | 0,5 min | — |

Total: cerca de **17 minutos**, com o Leonam falando uns 8,5 min e o Gabriel uns 8,5 min. O corte segue o conteúdo: o **Leonam cuida do modelo e da estrutura** (o que é o grafo e como está guardado no código), e o **Gabriel cuida dos algoritmos** (o que se calcula em cima dele).

**Se o tempo for menor (versão de uns 11 minutos):** cortem tudo que está marcado como *(opcional)* e mantenham o coração do código, que é: a linha 7 (`adj`), `addAmizade`, `caminho` (BFS), `dfs` (a comparação fila contra pilha) e `Passo`/`foto`.

- **Mouse:** quem está falando controla a tela. A passagem de uma parte para a outra está marcada no texto (**Passagem**).
- **Perguntas:** cada pergunta provável, no fim, indica quem responde, mas qualquer um pode complementar.

## Antes de começar

- Compile e abra: `javac -encoding UTF-8 *.java` e depois `java Janela`.
- **Maximize a janela.** A aba "Algoritmos" precisa de uns 900 px de altura para mostrar tudo sem rolar.
- Clique em **Reiniciar com dados de exemplo** se a rede já foi mexida.
- **Deixem o código aberto** num editor (VS Code ou IntelliJ) com a **fonte grande** (Ctrl + roda do mouse), para a plateia ler de longe. Para saltar a uma linha, usem **Ctrl+G** e digitem o número.
- **As linhas citadas neste roteiro são as da versão atual dos arquivos.** Se alguém editar o código, os números mudam: nesse caso procurem pelo **nome do método** com **Ctrl+F**.
- Alternem entre a janela e o editor com **Alt+Tab**, e combinem antes quem faz isso em cada parte.
- **A posição das bolinhas pode mudar** de uma abertura para outra (o desenho se arruma sozinho). Os números e os nomes não mudam. Por isso as falas dizem "o Silas", e não "a bolinha da esquerda".
- Ensaiem a **passagem** entre as partes pelo menos uma vez: é onde mais se perde tempo.
- Plano B se a janela não abrir: `java Main` abre o menu de terminal com as mesmas funções (a opção 12 mostra a lista e a matriz).

## Glossário: as palavras que vão aparecer

Estas são as definições para vocês dominarem. Quando usarem um termo pela primeira vez, expliquem em uma frase.

| Termo | O que quer dizer, em português simples |
|---|---|
| **Grafo** | Um conjunto de **pontos** ligados por **linhas**. Serve para representar qualquer coisa que tenha "coisas" e "relações" entre elas. |
| **Vértice** (ou nó) | Cada ponto do grafo. Aqui, uma **pessoa**. |
| **Aresta** | Cada linha que liga dois vértices. Aqui, uma **amizade**. |
| **Não direcionado** | A ligação vale nos dois sentidos: se A é amigo de B, B é amigo de A. |
| **Não ponderado** | Todas as arestas "valem o mesmo". Aqui, toda amizade conta igual (não existe amizade mais forte). |
| **Grau** | O número de arestas de um vértice. Aqui, **quantos amigos** a pessoa tem. |
| **Caminho** | Uma sequência de amizades que leva de uma pessoa a outra (A é amigo de B, que é amigo de C...). |
| **Menor caminho** | O caminho com **menos amizades** entre duas pessoas. |
| **Graus de separação** | O número de amizades do menor caminho. "3 graus" = 3 amizades no meio do caminho. |
| **Componente conexo** (aqui, "grupo isolado") | Um pedaço do grafo onde todo mundo consegue chegar em todo mundo. Pessoas de grupos diferentes **não** têm caminho entre si. |
| **BFS** (busca em largura) | Algoritmo que percorre o grafo **em camadas**: primeiro quem está a 1 amizade, depois a 2, depois a 3... Usa uma **fila**. |
| **DFS** (busca em profundidade) | Algoritmo que segue **um caminho até o fim** e só então volta para tentar outro. Usa uma **pilha**. |
| **Fila (FIFO)** | Estrutura em que **sai primeiro quem entrou primeiro**, como uma fila de banco. |
| **Pilha (LIFO)** | Estrutura em que **sai primeiro quem entrou por último**, como uma pilha de pratos. |
| **Ponto de articulação** | Uma pessoa que, **se sair da rede, parte um grupo em pedaços**. É um ponto frágil: a "ponte" que mantém o grupo unido. |
| **Centralidade** | Um número que mede **quão importante** uma pessoa é na rede. Existem vários jeitos de medir (grau, proximidade, intermediação). |
| **Proximidade** | Mede **quão perto**, em média, a pessoa está de todas as que ela consegue alcançar. Alta = chega em todo mundo com poucas amizades. |
| **Intermediação** | Mede **em quantos menores caminhos entre outras pessoas** ela aparece. Alta = é uma **ponte**: muita gente precisa passar por ela. |
| **Lista de adjacência** | Jeito de guardar o grafo: cada pessoa tem uma lista dos seus amigos. |
| **Matriz de adjacência** | Outro jeito: uma tabela pessoa × pessoa com 1 (são amigos) ou 0 (não são). |
| **Grafo esparso** | Grafo com **poucas arestas** perto do máximo possível. Rede social é esparsa: cada pessoa conhece poucas entre muitas. |
| **O(V + E)** | "Proporcional ao número de vértices **mais** arestas". É o custo de uma BFS ou DFS: cada pessoa e cada amizade são vistas uma vez. |

## Como ler a tela

### Na aba "Explorar"

| O que você vê | O que significa |
|---|---|
| **Uma bolinha** | Uma pessoa (vértice). |
| **Uma linha entre duas bolinhas** | Uma amizade (aresta). |
| **Tamanho da bolinha** | Quanto maior, mais amigos (maior grau). |
| **Número dentro da bolinha** | O grau exato: quantos amigos a pessoa tem. |
| **Cor da bolinha** (modo "Grupos") | Cada cor é um **grupo isolado**. Mesma cor = se alcançam por amizades. |
| **Legenda embaixo à esquerda** | Quantas pessoas tem cada grupo. |
| **Texto no canto de cima** | "12 pessoas · 13 amizades · 2 grupos": o tamanho da rede. |
| **Anel branco** | A pessoa em que você clicou. |
| **Bolinhas apagadas** | Pessoas que não têm a ver com o que você consultou (para focar a atenção). |
| **Linha tracejada dourada** | Uma **sugestão de amizade**: alguém que a pessoa selecionada ainda não conhece, mas que é amigo de um amigo. |
| **Linha dourada grossa** | O **menor caminho** entre duas pessoas selecionadas. |
| **Halo dourado** | A pessoa **mais popular** (com mais amigos). Aparece com o botão "Mais popular". |

### Na aba "Algoritmos"

| O que você vê | O que significa |
|---|---|
| **Bolinha apagada** | Pessoa que o algoritmo **ainda não alcançou**. |
| **Anel branco** | Pessoa **esperando na fila (BFS) ou na pilha (DFS)**: já foi descoberta, mas ainda não foi processada. |
| **Anel dourado grosso** | A pessoa **sendo processada agora**. |
| **Número dentro da bolinha** | No **BFS**: a distância (quantas amizades) até o início. No **DFS**: a ordem em que a pessoa foi visitada. |
| **Linha clara e grossa** | O caminho **já percorrido**: a amizade pela qual cada pessoa foi descoberta. |
| **Linha dourada grossa** | A amizade que o algoritmo está **examinando neste instante**. |
| **Faixa "FILA" ou "PILHA" embaixo** | O conteúdo da fila/pilha **agora**. Na fila, "frente" é quem sai primeiro. Na pilha, "topo" é quem sai primeiro. |
| **Cartão "Como funciona"** (à direita) | Três etiquetas (estrutura usada, complexidade, para que serve) e um resumo. |
| **Cartão "Pseudocódigo"** | O algoritmo escrito em passos. **A linha amarela é a que está rodando agora.** |
| **Cartão "Passo a passo"** | Uma frase explicando o que acabou de acontecer, e os botões de controle. |
| **Botões ⏮ ◀ ▶ ▶⏭** | Voltar ao começo, voltar um passo, reproduzir/pausar, ir ao próximo passo. |

> **Dica de ritmo:** nos primeiros 5 ou 6 passos de cada algoritmo, usem o botão de **próximo passo** (para explicar cada um com calma). Depois apertem **Play** e deixem terminar sozinho.

---

## 1. Abertura, modelagem e mapa do código (2 min) — Leonam

**Na tela:** a janela recém-aberta, na aba "Explorar", com a rede de exemplo: dois grupos separados (um azul com 7 pessoas e um verde com 5).

**O que significa:** a rede está **modelada como um grafo**:

| Na rede social | No grafo |
|---|---|
| Pessoa | Vértice |
| Amizade | Aresta |
| Número de amigos | Grau |
| Grupo sem ligação com os outros | Componente conexo |

O grafo é **não direcionado** (amizade vale nos dois sentidos), **não ponderado** (toda amizade vale o mesmo) e **sem laços** (ninguém é amigo de si mesmo). O exemplo tem **dois grupos que não se conhecem**, de propósito, para mostrar mais para frente o que acontece quando a rede se parte.

**Fale (Leonam):**

> "Olá, nós somos o Leonam e o Gabriel. Nosso trabalho é uma **rede social de amizades**, feita em Java, para mostrar como os **grafos** funcionam na prática.
>
> A ideia de um grafo é simples: são **pontos ligados por linhas**. Aqui, cada **pessoa é um ponto**, que chamamos de **vértice**, e cada **amizade é uma linha**, que chamamos de **aresta**.
>
> Nosso grafo é **não direcionado**: se o Silas é amigo do Felipe, o Felipe também é amigo do Silas. E é **não ponderado**: toda amizade vale o mesmo.
>
> Um detalhe importante: **não usamos nenhuma biblioteca de grafos**. A estrutura e os algoritmos são nossos. Só usamos as coleções que já vêm no Java, e o Swing para desenhar a janela.
>
> Esta rede de exemplo tem 12 pessoas, 13 amizades, e dois grupos que não se conhecem, em azul e em verde. Antes de ver como funciona, vou mostrar como o código está organizado."

### No código: o mapa dos arquivos (cerca de 40 s)

**Abrir:** o editor, mostrando os três arquivos do projeto.

| Arquivo | Linhas | O que tem |
|---|---|---|
| `RedeSocial.java` | 459 | **O grafo e todos os algoritmos.** Não imprime nada, não desenha nada, não lê nada do usuário. |
| `Main.java` | 132 | O **menu de terminal**: lê o que a pessoa digita e chama a `RedeSocial`. |
| `Janela.java` | 1771 | A **interface gráfica** (Swing): desenha o grafo, trata o mouse e chama a `RedeSocial`. |

```
 Main.java   (terminal) ──┐
                          ├──▶  RedeSocial.java   (grafo + algoritmos)
 Janela.java (janela)   ──┘
```

**O que significa:** a regra de ouro do projeto é que **a `RedeSocial` não sabe que existe tela**. Ela só guarda o grafo e *devolve resultados* (listas, conjuntos, números). Isso traz duas vantagens:
1. O **mesmo código** serve para o menu de terminal e para a janela. Dá para ver: `Main.java` (linha 6) e a janela começam a rede com a mesma chamada, `RedeSocial.exemplo()`.
2. Dá para trocar a interface sem mexer em nenhum algoritmo.

**Fale (Leonam):**

> "O programa tem três arquivos. O principal é o `RedeSocial.java`: é lá que mora o **grafo** e **todos os algoritmos**. Ele segue uma regra: **não imprime nada e não desenha nada**, só guarda o grafo e devolve resultados.
>
> Os outros dois são só a **casca**. O `Main` é um menu de terminal, e o `Janela` é a interface gráfica que vocês estão vendo. Os dois usam exatamente a mesma `RedeSocial`. Assim, se a gente quiser trocar a tela, os algoritmos não mudam.
>
> Então tudo o que o Gabriel e eu vamos explicar de código está nesse arquivo de 459 linhas."

---

## 2. A estrutura de dados (3 min) — Leonam

**Clique:** `Lista e matriz` (cartão "Consultas", aba Explorar). Feche a janelinha no fim.

**Na tela:** uma janela escura com duas partes.
1. **Lista de adjacência**: uma linha por pessoa, no formato `Benks -> [Ismael, Joao Paulo]`.
2. **Matriz de adjacência**: uma tabela 12 × 12 de zeros e uns, com os nomes à esquerda e os números das colunas em cima.

**O que significa:**
- Na **lista**, cada pessoa aponta para a lista dos seus amigos. Só guarda o que **existe**.
- Na **matriz**, cada linha é uma pessoa e cada coluna também. O número **1** quer dizer "são amigos". É **simétrica** (se A–B é 1, B–A também é 1), porque o grafo é não direcionado.
- A matriz tem 12 × 12 = **144 casas**, mas só **26 são 1** (13 amizades contadas nos dois sentidos). O resto é zero: **espaço desperdiçado**.
- **Custo de memória:** lista = O(V + E), matriz = O(V²). Para uma rede com um milhão de pessoas, a matriz teria um trilhão de casas.

| | Lista de adjacência | Matriz de adjacência |
|---|---|---|
| Memória | O(V + E) | O(V²) |
| Listar os amigos de alguém | O(grau) | O(V) |
| Verificar se há amizade | O(log grau) | O(1) |

**Fale (Leonam):**

> "Guardamos o grafo como **lista de adjacência**: cada pessoa aponta para a lista dos seus amigos. Por exemplo, o Benks aponta para o Ismael e para o Joao Paulo.
>
> A outra forma de guardar seria a **matriz**, que é essa tabela. Cada linha e cada coluna é uma pessoa, e o 1 quer dizer que são amigos. Dá para ver que quase tudo é zero: das **144 casas, só 26 são 1**.
>
> Numa rede social, isso é o normal: **cada pessoa conhece só uma pequena parte do total**. Por isso a lista é a escolha natural: ela gasta memória proporcional ao número de pessoas mais o de amizades. A matriz gasta o **quadrado** do número de pessoas, e quase tudo seria zero.
>
> A matriz só ganha em uma coisa: perguntar se duas pessoas são amigas é instantâneo. Mas isso a gente quase não precisa."

### No código: como o grafo é guardado (cerca de 1 min 30 s)

**Abrir:** `RedeSocial.java`, linha 7 e depois as linhas 26 a 67 (**Ctrl+G** e o número da linha).

#### O grafo inteiro cabe em uma linha (linha 7)

```java
private final Map<String, Set<String>> adj = new TreeMap<>();
```

- `Map<String, Set<String>>` é um **dicionário**: a **chave** é o nome da pessoa e o **valor** é o **conjunto** dos amigos dela. Isso é a **lista de adjacência**.
- `TreeMap` e `TreeSet` guardam tudo **em ordem alfabética**. E o `Set` **não aceita elemento repetido**, então uma amizade nunca fica duplicada.
- `private`: ninguém de fora da classe mexe direto no grafo. `final`: a variável sempre aponta para o mesmo mapa.
- A variável é declarada com os tipos `Map` e `Set`, e só o `new` escolhe `TreeMap`. Dá para trocar por `HashMap` mudando só essa linha.

#### Adicionar e remover (linhas 26 a 51)

```java
public boolean addPessoa(String p) {                            // linha 26
    if (p.isEmpty() || existe(p)) return false;                 // nome vazio ou repetido: recusa
    adj.put(p, new TreeSet<>());                                // pessoa nova, ainda sem amigos
    return true;
}

public boolean addAmizade(String a, String b) {                 // linha 39
    if (a.equals(b) || !existe(a) || !existe(b)) return false;  // ninguém é amigo de si; as duas precisam existir
    boolean nova = adj.get(a).add(b);                           // põe B na lista de A
    adj.get(b).add(a);                                          // vale nos dois sentidos: põe A na lista de B
    return nova;
}

public boolean removePessoa(String p) {                         // linha 32
    Set<String> amigos = adj.remove(p);                         // tira a pessoa do mapa e guarda a lista dela
    if (amigos == null) return false;                           // não existia
    for (String a : amigos) adj.get(a).remove(p);               // some da lista de cada amigo
    return true;
}
```

- **`addAmizade` grava nos dois sentidos.** É isso que faz o grafo ser **não direcionado**. Se gravasse só um lado, seria direcionado. É também por isso que a matriz é simétrica.
- **`Set.add` devolve `true` se o elemento era novo.** A tela aproveita isso para dizer "Amizade criada" ou "Já são amigos".
- **`removePessoa` precisa tirar a pessoa da lista de cada amigo.** Se esquecesse, sobrariam "amigos fantasmas" apontando para alguém que não existe mais.
- Os métodos devolvem `boolean` (deu certo ou não) e **não imprimem nada**: quem decide o que mostrar é a interface.

#### Proteção do grafo (linhas 53 a 67)

```java
public Set<String> amigos(String p) {                           // linha 53
    return Collections.unmodifiableSet(vizinhos(p));            // devolve uma visão SÓ DE LEITURA
}

private Set<String> vizinhos(String p) {                        // linha 63
    Set<String> v = adj.get(p);
    if (v == null) throw new IllegalArgumentException("Pessoa não encontrada: " + p);
    return v;
}
```

- `unmodifiableSet` entrega a lista de amigos **só para leitura**. Quem tentar `add` de fora leva um erro. Assim o grafo **só muda pelos métodos da classe** (`addAmizade`, `removeAmizade`...), que mantêm a simetria.
- `vizinhos` é o ponto único que **valida** a pessoa: se não existe, lança uma exceção com mensagem clara, em vez de um `NullPointerException` difícil de entender. Quase todos os métodos passam por ele.

**Fale (Leonam):**

> "Agora o código. O grafo inteiro está **nesta linha**: um `Map` de `String` para `Set` de `String`. A chave é o nome da pessoa, e o valor é o **conjunto dos amigos** dela. Isso é a lista de adjacência. Usei `TreeMap` e `TreeSet` para ficar em ordem alfabética, e porque o `Set` já **não deixa repetir**: uma amizade nunca fica duplicada.
>
> Olhem o `addAmizade`. Ele coloca o B na lista do A **e** o A na lista do B. Essas duas linhas são o que faz o grafo ser **não direcionado**. Se tivesse só uma, a amizade seria de mão única.
>
> O `removePessoa` tem uma pegadinha: além de tirar a pessoa do mapa, ele precisa tirá-la da lista de **cada amigo**. Se não fizesse isso, ficariam amigos fantasmas.
>
> E uma decisão de projeto: o método `amigos` devolve a lista **só para leitura**. Quem está de fora não consegue estragar o grafo. Ele só muda pelos métodos da própria classe."

---

## 3. Explorar a rede (3 min) — Leonam

**Clique:** no **Silas** → no **Joao Paulo** → no vazio → `Mais popular`.

### 3a. Clicando no Silas

**Na tela:** o Silas fica com um **anel branco**. Os quatro amigos dele (Felipe, Gabriel Xavier, Helder e Ismael) ficam em destaque, e o resto escurece. Aparece uma **linha tracejada dourada** até o **Benks**. No painel "Resultado":

```
Silas
4 amigos: Felipe, Gabriel Xavier, Helder, Ismael

Proximidade 0,36 · intermediação 9,5

Sugestões (linhas tracejadas):
• Benks — 1 amigo em comum
```

**O que significa:**
- O Silas é a maior bolinha porque tem **4 amigos** (o maior grau da rede).
- A **sugestão** é o que redes sociais chamam de "pessoas que você talvez conheça". Para cada amigo do Silas, olhamos os amigos *dele* que o Silas ainda não conhece, e contamos quantas vezes cada candidato aparece. O Benks aparece uma vez (via Ismael), então tem **1 amigo em comum**.
- Proximidade e intermediação aparecem aí também (a parte 4 explica).

### 3b. Clicando no Joao Paulo (segunda pessoa)

**Na tela:** o caminho entre os dois fica em **dourado grosso**, e o resto escurece. No painel:

```
Silas → Joao Paulo

Silas → Ismael → Benks → Joao Paulo
3 graus de separação

Amigos em comum: nenhum
```

**O que significa:** o **menor caminho** tem 3 amizades: Silas é amigo do Ismael, que é amigo do Benks, que é amigo do Joao Paulo. Esse número é o **grau de separação**. Por trás, o programa roda uma **BFS** (que a parte 5 mostra por dentro).

### 3c. "Mais popular"

**Na tela:** um **halo dourado** em volta do Silas, e o texto "Mais popular: Silas, 4 amigos".

**O que significa:** a pessoa com o maior **grau** (mais amizades). Se houvesse empate, apareceriam todos os empatados.

**Fale (Leonam):**

> "Agora a gente pode explorar a rede com o mouse. Vou clicar no **Silas**. O tamanho de cada bolinha, e o número dentro dela, é o **grau**: quantos amigos a pessoa tem. O Silas tem 4, o maior da rede.
>
> Os amigos dele ficam em destaque. E essa **linha tracejada** é uma **sugestão de amizade**: o Benks ainda não é amigo do Silas, mas é amigo de um amigo dele, o Ismael. É a mesma ideia do 'pessoas que você talvez conheça' das redes sociais: olhamos os **amigos dos amigos** e contamos quantos amigos em comum cada um tem.
>
> Se eu clicar numa **segunda pessoa**, o Joao Paulo, aparece o **menor caminho** entre os dois: Silas, Ismael, Benks, Joao Paulo. São **3 graus de separação**, ou seja, 3 amizades no meio do caminho. Por baixo disso tem um algoritmo chamado **BFS**, que o Gabriel vai mostrar por dentro.
>
> E aqui, **Mais popular** marca com um halo dourado quem tem mais amigos: o Silas, com 4.
>
> Por trás de tudo isso tem código Java, e é isso que eu mostro agora."

### No código: amigos em comum, sugestões e menor caminho (cerca de 1 min 45 s)

**Abrir:** `RedeSocial.java`, linhas 71 a 122.

#### Amigos em comum (linhas 71 a 75)

```java
public Set<String> amigosEmComum(String a, String b) {
    Set<String> comuns = new TreeSet<>(vizinhos(a));   // COPIA os amigos de A
    comuns.retainAll(vizinhos(b));                      // fica só com quem também é amigo de B
    return comuns;
}
```

- `retainAll` faz a **interseção** de dois conjuntos: "mantenha só o que também está no outro".
- Reparem na **cópia** (`new TreeSet<>(...)`) antes do `retainAll`. Sem ela, o `retainAll` **apagaria amigos do A de verdade** dentro do grafo, porque alteraria o conjunto original. Copiar protege o grafo.

#### Sugestões de amizade (linhas 78 a 90)

```java
Set<String> meusAmigos = vizinhos(p);
Map<String, Integer> contagem = new HashMap<>();
for (String amigo : meusAmigos)                                     // para cada amigo meu...
    for (String candidato : adj.get(amigo))                         // ...olho os amigos DELE
        if (!candidato.equals(p) && !meusAmigos.contains(candidato))  // que não sou eu e que ainda não é meu amigo
            contagem.merge(candidato, 1, Integer::sum);             // conta +1 para esse candidato
...
lista.sort(Map.Entry.<String, Integer>comparingByValue().reversed() // mais amigos em comum primeiro
        .thenComparing(Map.Entry.comparingByKey()));                // empate: ordem alfabética
```

- São **dois laços encaixados**: os amigos dos meus amigos.
- `contagem.merge(candidato, 1, Integer::sum)` é o jeito de **contar** com um `Map`: se o candidato ainda não está no mapa, põe 1; se já está, soma 1. No fim, o número é quantos amigos em comum.
- Depois só **ordena**: do maior para o menor número de amigos em comum e, em caso de empate, por nome.
- **Custo:** O(S · log grau + k · log k), em que S é a soma dos graus dos amigos e k é o número de sugestões. O log grau vem do `contains` no `TreeSet` dos meus amigos, e o k · log k vem da ordenação no fim. Não precisa olhar a rede inteira.

#### Menor caminho: a BFS (linhas 103 a 122)

```java
Map<String, String> pai = new HashMap<>();        // quem DESCOBRIU cada pessoa
Deque<String> fila = new ArrayDeque<>();
pai.put(a, null);                                 // o início não foi descoberto por ninguém
fila.add(a);                                      // entra no FIM da fila
while (!fila.isEmpty() && !pai.containsKey(b)) {  // para quando a fila acaba OU quando acha o destino
    String u = fila.poll();                       // tira do INÍCIO da fila
    for (String v : adj.get(u))
        if (!pai.containsKey(v)) {                // ainda não visto?
            pai.put(v, u);                        // v foi descoberto por u
            fila.add(v);                          // v entra no FIM da fila
        }
}
LinkedList<String> caminho = new LinkedList<>();
if (!pai.containsKey(b)) return caminho;          // não chegou em b: não há ligação (lista vazia)
for (String x = b; x != null; x = pai.get(x)) caminho.addFirst(x);   // reconstrói de trás pra frente
return caminho;
```

- **O mapa `pai` faz dois trabalhos:** serve de "já visitei?" (`containsKey`) e guarda **quem descobriu cada pessoa**. Com ele dá para reconstruir o caminho no fim.
- **A fila é o que garante o menor caminho.** `add` põe no **fim** e `poll` tira do **início**: sai quem entrou primeiro (FIFO). Então todo mundo a 1 amizade é processado antes de qualquer um a 2.
- A condição `!pai.containsKey(b)` faz o laço **parar assim que acha o destino**, sem explorar o resto.
- **Reconstrução:** a partir do destino `b`, anda para trás seguindo `pai` até chegar no início (cujo pai é `null`), e usa `addFirst` para inverter a ordem. Para Silas → Joao Paulo: Joao Paulo ← Benks ← Ismael ← Silas.
- **Custo O(V + E):** cada pessoa entra na fila uma vez e cada amizade é olhada no máximo duas vezes.

**Fale (Leonam):**

> "Vou mostrar três métodos. O primeiro, **amigos em comum**: copio os amigos do A e chamo `retainAll` com os amigos do B, que mantém só quem está nos dois, a **interseção**. A cópia é importante: sem ela, o `retainAll` apagaria amigos do A direto no grafo.
>
> O segundo, a **sugestão**. São dois laços: para cada amigo meu, olho os amigos *dele*. Se não sou eu e ainda não é meu amigo, **conto mais um** para esse candidato com o `merge`. No fim ordeno por quem tem mais amigos em comum.
>
> O terceiro é o **menor caminho**, que é a **BFS**. Duas ideias. A primeira é a **fila**: `add` coloca no fim, `poll` tira do início, então sai quem entrou primeiro, e todo mundo a uma amizade é visto antes de quem está a duas. Por isso o primeiro caminho que a gente encontra é o menor. A segunda é esse mapa `pai`, que guarda **quem descobriu cada pessoa**. Quando chega no destino, é só andar para trás pelos pais e montar o caminho."

**Passagem (Leonam → Gabriel):**

> "Até aqui vimos o resultado e o código que guarda a rede e faz as buscas básicas. Agora o Gabriel mostra quem é mais importante na rede, e os algoritmos mais elaborados."

> **Se sobrar tempo:** clique no **Gabriel Xavier**. As sugestões aparecem **ordenadas por amigos em comum**: Helder (2 em comum) vem antes do Ismael (1 em comum). Isso mostra que a sugestão não é aleatória: quem tem mais amigos em comum aparece primeiro.

---

## 4. Colorir por centralidade (2 min) — Gabriel

**Clique:** `Intermediação` (cartão "Colorir por"). Depois `Grau`, depois `Intermediação` de novo. Se sobrar tempo, `Proximidade`.

**Na tela:** as bolinhas mudam de cor. **Azul/roxo = menor valor**, **verde-água = médio**, **amarelo = maior valor**. Embaixo à esquerda aparece uma **barra de cores** com "menor" e "maior". O painel "Resultado" explica a métrica e mostra os 5 maiores valores.

**O que significa:** "importante" pode querer dizer coisas diferentes. Cada modo responde a uma pergunta:

| Modo | A pergunta que responde | Maiores valores no exemplo |
|---|---|---|
| **Grau** | Quem tem **mais amigos**? | Silas (4), depois Felipe e Gabriel Siqueira (3) |
| **Proximidade** | Quem chega em **todo mundo mais rápido**? | Silas (0,36), Ismael (0,33), Gabriel Siqueira (0,29) |
| **Intermediação** | Quem é **ponte**, quem fica no meio do caminho dos outros? | Silas (9,5), Ismael (8,0), Benks (5,0), Gabriel Siqueira (4,0), Wilson (3,0) |

**Como entender a intermediação (o ponto mais interessante):**

O **Ismael tem só 2 amigos** (Silas e Benks), mas a intermediação dele é **8,0**, a segunda maior. Por quê? Porque ele é a **única ligação** entre dois lados:
- de um lado, **Benks e Joao Paulo** (2 pessoas);
- do outro, **Silas, Felipe, Gabriel Xavier e Helder** (4 pessoas).

Cada uma das 2 pessoas de um lado precisa passar pelo Ismael para chegar em cada uma das 4 do outro: 2 × 4 = **8 pares**, e para todos o caminho passa por ele. Por isso é **8,0**. Se o Ismael sair, os dois lados se separam.

O mesmo raciocínio vale para o Silas (9,5) e o Benks (5,0). O **Felipe (0,5)** tem pouquinho: ele só aparece em *metade* dos caminhos entre Gabriel Xavier e Helder, porque ali existem **dois caminhos de mesmo tamanho** (um por ele, outro pelo Silas).

**Como entender a proximidade:** é o contrário de "distância média". O Silas chega nas outras 6 pessoas do grupo dele com um total de 9 saltos (4 pessoas a 1, o Benks a 2 e o Joao Paulo a 3). Quanto **menor** a soma, **maior** a proximidade. Ele é o centro do grupo dele.

**Fale (Gabriel):**

> "Agora uma pergunta: **quem é o mais importante da rede?** A resposta depende do que a gente chama de importante, e aqui dá para ver três jeitos de medir. A **cor** da bolinha mostra o valor: azul é o menor, amarelo é o maior.
>
> Vou começar pela **intermediação**. Ela mede **em quantos menores caminhos entre outras duas pessoas a pessoa aparece**. Ou seja, quem é uma **ponte**. O Silas é o mais amarelo, e o **Ismael vem logo depois**.
>
> Repara que o Ismael tem **só 2 amigos**. Mas ele é a **única ligação** entre o Benks e o Joao Paulo, de um lado, e o resto do grupo, do outro. Todo caminho entre esses dois lados passa por ele. Por isso a intermediação dele é alta.
>
> Se eu mudo para **grau**, quem tem mais amigos é o Silas, e o Ismael parece uma pessoa comum. Então não adianta olhar só quantos amigos a pessoa tem: **o que importa é ligar partes da rede**.
>
> E tem também a **proximidade**, que mostra quem chega em todo mundo mais rápido, o centro do grupo. Cada métrica responde uma pergunta diferente."

### No código: proximidade e intermediação (cerca de 1 min)

**Abrir:** `RedeSocial.java`, linhas 327 a 357 (proximidade) e 364 a 407 (intermediação).

#### Proximidade (linhas 327 a 357)

```java
private Map<String, Integer> distancias(String origem) {      // linha 327: uma BFS
    Map<String, Integer> dist = new HashMap<>(Map.of(origem, 0));
    Deque<String> fila = new ArrayDeque<>(List.of(origem));
    while (!fila.isEmpty()) {
        String u = fila.poll();
        for (String v : adj.get(u))
            if (!dist.containsKey(v)) {
                dist.put(v, dist.get(u) + 1);                  // distância = a de quem descobriu + 1
                fila.add(v);
            }
    }
    return dist;
}

// dentro de proximidade(), para cada pessoa p (resumido):
Map<String, Integer> dist = distancias(p);
int alcancadas = dist.size() - 1;                              // quantas pessoas p alcança (sem contar ela)
long soma = 0;
for (int d : dist.values()) soma += d;                         // soma das distâncias
resultado.put(p, alcancadas == 0 ? 0.0 : (double) alcancadas / soma * alcancadas / (n - 1));
```

- `distancias` é **a mesma BFS** do `caminho`, mas em vez de guardar o pai, guarda a **distância** (a de quem descobriu, mais 1).
- A fórmula tem **dois fatores**: `alcancadas / soma` é o **inverso da distância média** (quanto mais perto, maior), e `alcancadas / (n - 1)` **desconta** quem só alcança uma parte da rede.
- **Conta do Silas:** alcança 6 pessoas, e a soma das distâncias é 9 (quatro a 1, uma a 2 e uma a 3). Logo 6/9 × 6/11 ≈ **0,36**.
- **Custo:** uma BFS por pessoa, **O(V·(V + E))**.

#### Intermediação (linhas 364 a 407) *(opcional: se sobrar tempo ou se perguntarem)*

É uma adaptação do **algoritmo de Brandes**, que calcula a intermediação de todo mundo sem testar todos os pares de pessoas. Dá para explicar em três ideias, sem ler linha por linha:

1. **Para cada pessoa `s`, roda uma BFS** (linhas 380 a 394), anotando duas coisas: **quantos menores caminhos** chegam em cada pessoa (`caminhos`) e **quem vem imediatamente antes** dela num menor caminho (`pais`).
2. **Volta do mais distante para o mais perto** (linhas 398 a 403), repassando "**crédito**": cada pessoa passa para quem veio antes uma fração do crédito, proporcional à parte dos menores caminhos que passa por ali:
   ```java
   credito.merge(v, (double) caminhos.get(v) / caminhos.get(w) * (1 + credito.get(w)), Double::sum);
   ```
3. **Soma** esse crédito no total de cada pessoa e, no fim, **divide por 2** (linha 405), porque o grafo não é direcionado e cada par foi contado duas vezes.

O ganho: em vez de listar todos os caminhos entre todos os pares, faz **uma BFS por pessoa**, **O(V·(V + E))**.

**Fale (Gabriel):**

> "No código, a **proximidade** usa a mesma BFS que o Leonam mostrou, mas em vez de guardar quem descobriu cada pessoa, guarda a **distância**. Somo as distâncias até todo mundo que a pessoa alcança e uso o inverso: quanto mais perto, maior o valor. No caso do Silas são 6 pessoas e a soma é 9, o que dá 0,36.
>
> A **intermediação** usa o algoritmo de **Brandes**. A ideia, em resumo: para cada pessoa, roda uma BFS contando quantos menores caminhos chegam em cada um. Depois volta do mais distante para o mais perto, distribuindo 'crédito' para quem está no meio do caminho. Quem acumula mais crédito é a **ponte** da rede. O custo é uma BFS por pessoa."

---

## 5. Algoritmos por dentro (5 min) — Gabriel

**Clique:** na aba `Algoritmos` → `BFS` → no **Silas** → próximo passo algumas vezes → `Play`.

### 5a. BFS (busca em largura), cerca de 2 min (1 min na tela e 1 min no código)

**Na tela:** todas as bolinhas ficam **apagadas**, menos o Silas (início). Embaixo aparece a faixa **FILA** com o Silas. À direita: o resumo, o **pseudocódigo**, e "Passo 1 de 25".

**O que significa, passo a passo** (o que o algoritmo faz com a rede de exemplo):

| Passo | O que acontece | Linha do pseudocódigo |
|---|---|---|
| 1 | Silas é o ponto de partida: **entra na fila**, com distância 0. | `fila = [início]; marca início` |
| 2 | **Tira o Silas da frente** da fila e olha os amigos dele. A faixa FILA fica **vazia por um instante**: não é erro, o Silas acabou de sair e os amigos dele ainda não entraram. | `u = tira o primeiro da fila` |
| 3 a 6 | Cada amigo (Felipe, Gabriel Xavier, Helder, Ismael) **ainda não foi visto**: é marcado e vai para o **fim da fila**, com distância 1. | `marca v; v vai pro fim` |
| depois | Tira o Felipe da frente: os amigos dele **já foram vistos**, então são **ignorados**. | `se v ainda não foi visto` |
| ... | Chega o Ismael, que descobre o **Benks** (distância 2). Depois o Benks descobre o **Joao Paulo** (distância 3). | |
| 25 (último) | A fila **esvaziou**: 7 pessoas alcançadas. As 5 do grupo verde **continuam apagadas**. | `enquanto fila não vazia` |

- **Por que a fila dá o menor caminho?** Porque quem entra primeiro na fila sai primeiro. Então **todos a 1 amizade são processados antes de qualquer um a 2**, e esses antes de qualquer um a 3. A primeira vez que alguém é alcançado já é pelo **menor** caminho. O número dentro de cada bolinha é essa distância.
- **Complexidade O(V + E):** cada pessoa entra e sai da fila **uma vez**, e cada amizade é olhada **no máximo duas vezes**.
- **Quem ficou apagado?** O grupo verde. A BFS só alcança quem tem **caminho** até o início. É isso que revela os grupos isolados.

**Fale (Gabriel):**

> "Agora vou mostrar **por dentro** como o programa descobre os caminhos. Esse é o algoritmo **BFS**, busca em largura. Vou escolher o Silas como ponto de partida.
>
> Ele usa uma **fila**, como uma fila de banco: **sai quem entrou primeiro**. Embaixo está a fila, hoje só com o Silas. À direita, o pseudocódigo, e a linha amarela é a que está rodando agora.
>
> Primeiro, o algoritmo **tira o Silas da fila e olha os amigos dele**. Cada amigo que ainda não foi visto é marcado e vai para o **fim da fila**. O número dentro da bolinha é a **distância** até o Silas: os amigos dele ficam com 1.
>
> Aí ele tira o próximo da fila, o Felipe, e olha os amigos dele. Mas todos já foram vistos, então ele **ignora**. Só quando chegar no Ismael é que aparece alguém novo: o Benks, com distância 2. E depois o Joao Paulo, com 3.
>
> A ideia é: como a fila atende **quem chegou primeiro**, todo mundo a 1 amizade é processado antes de qualquer um a 2. **Por isso a BFS dá o menor caminho.** E o custo é **proporcional ao número de pessoas mais amizades**, porque cada um é visto uma vez.
>
> Repara que o **grupo verde continua apagado**: a BFS só alcança quem tem caminho até o início."

#### No código: a mesma BFS, com "fotografias" (cerca de 1 min)

**Abrir:** `RedeSocial.java`, linhas 185 a 232.

O `passosBfs` é **o mesmo laço do `caminho`** que o Leonam mostrou, com **uma coisa a mais**: depois de cada decisão, **tira uma fotografia** do estado do algoritmo.

```java
while (!fila.isEmpty()) {
    String u = fila.poll();
    passos.add(foto(3, "Tira " + u + " da frente da fila e olha os amigos dele.", u, null, fila, ...));
    for (String v : adj.get(u)) {
        if (vistos.add(v)) {                      // true se v era NOVO (já marca como visto)
            fila.add(v);
            pai.put(v, u);
            dist.put(v, dist.get(u) + 1);
            passos.add(foto(6, v + " ainda não foi visto: ...", u, v, fila, ...));
        } else {
            passos.add(foto(5, v + " já foi visto: ignora.", u, v, fila, ...));
        }
    }
    concluidos.add(u);
}
```

- **Cada `foto(...)` vira um passo da animação.** O primeiro número (3, 6 ou 5) é a **linha do pseudocódigo** que acende na janela.
- `vistos.add(v)` devolve `true` se o elemento era novo: **verifica e marca numa chamada só**.

**O tipo `Passo` e o método `foto` (linhas 185 a 196):**

```java
public record Passo(int linha, String texto, String atual, String alvo,
                    List<String> estrutura, Set<String> concluidos, Map<String, String> pai,
                    Map<String, Integer> rotulo, Set<String> destaques, Map<String, Integer> grupos) { }

private static Passo foto(int linha, String texto, ..., Collection<String> estrutura, ...) {
    return new Passo(linha, texto, atual, alvo, List.copyOf(estrutura), new TreeSet<>(concluidos),
            new HashMap<>(pai), new HashMap<>(rotulo), ...);
}
```

- `record` (Java 16+) é uma classe **só de dados e imutável**: o Java cria sozinho o construtor e os métodos de leitura (`p.atual()`, `p.texto()`).
- **Por que o `foto` faz cópias** (`List.copyOf`, `new TreeSet<>(...)`, `new HashMap<>(...)`)? Porque o algoritmo continua **alterando** a fila, o mapa `pai` etc. enquanto roda. Sem copiar, todos os passos apontariam para as mesmas coleções e **mostrariam o estado final**. A cópia congela cada instante.
- **Como a janela usa isso** (`Janela.java`, `gerarPassos`, linha 406): chama `rede.passosBfs(inicio)`, guarda a lista e só **mostra o passo número `indice`**. O botão de próximo passo é um `indice++` (`avancar`, linha 421). **A janela não implementa nenhum algoritmo**: reproduz o que a `RedeSocial` calculou.

**Fale (Gabriel):**

> "Para a animação eu não reescrevi o algoritmo: o `passosBfs` é **o mesmo laço** do menor caminho, com uma coisa a mais. Depois de cada decisão, ele **tira uma fotografia** do estado, com o método `foto`. Cada foto é um passo da animação, e o número que vai junto é a linha do pseudocódigo que acende na tela.
>
> Cada foto é um `record`, que é uma classe só de dados. E o `foto` **faz cópias** da fila e dos mapas, porque o algoritmo continua mexendo neles: sem copiar, todas as fotos mostrariam o estado final.
>
> A janela, então, **não sabe nada de BFS**. Ela só guarda essa lista de fotos e mostra a de número `indice`. O botão de próximo passo é um `indice++`."

### 5b. DFS (busca em profundidade), cerca de 1 min 30 s

**Clique:** `DFS` (o início continua sendo o Silas) → `Play`.

**Na tela:** agora é a faixa **PILHA**, com "base" e "topo". O número dentro da bolinha é a **ordem de visita**.

**O que significa:**
- A DFS **mergulha**: vai do Silas para o Felipe, do Felipe para o Gabriel Xavier... segue um caminho até **não ter mais amigo novo**.
- Quando chega num beco sem saída (por exemplo, o Helder, que só tem amigos já visitados), ela **volta** (backtracking), tirando a pessoa da **pilha**, até achar alguém com amigo novo.
- A **pilha** guarda o **caminho atual**. A ordem de visita no exemplo é: Silas 1, Felipe 2, Gabriel Xavier 3, Helder 4, Ismael 5, Benks 6, Joao Paulo 7.
- Também custa **O(V + E)**. A diferença para a BFS é só a **ordem**, que vem da estrutura usada (**pilha** em vez de fila).
- **Para que serve:** a DFS é a base para achar os **grupos isolados**. Roda a DFS e vê quem foi alcançado.

**Fale (Gabriel):**

> "Agora o **DFS**, busca em profundidade. Muda uma coisa só: em vez de fila, usa uma **pilha**, como uma pilha de pratos, em que **sai o último que entrou**.
>
> O efeito é completamente diferente. A BFS ia em camadas. O DFS **mergulha**: vai do Silas para o Felipe, do Felipe para o Gabriel Xavier, e segue por um caminho até não ter mais ninguém novo. Aí ele **volta** um passo e tenta outro caminho. Isso se chama **backtracking**.
>
> O número agora é a **ordem de visita**. A pilha embaixo mostra o **caminho atual**, da base até o topo.
>
> O custo é o mesmo, proporcional a pessoas mais amizades. A diferença é a **ordem**. E a DFS é a base para achar os **grupos isolados**: roda a partir de uma pessoa e vê quem ela alcança."

#### No código: o que muda de BFS para DFS (cerca de 45 s)

**Abrir:** `RedeSocial.java`, linhas 143 a 166 (`grupos` e `dfs`). Deixem à mão as linhas 111 e 115 (`caminho`) para comparar.

```java
// DFS que acha os grupos isolados (linhas 143 a 166)
public List<Set<String>> grupos() {
    Set<String> visitados = new HashSet<>();
    List<Set<String>> grupos = new ArrayList<>();
    for (String p : adj.keySet())
        if (!visitados.contains(p)) {                // pessoa ainda não visitada = começo de um grupo novo
            Set<String> grupo = new TreeSet<>();
            dfs(p, visitados, grupo);                // descobre o grupo inteiro dela
            grupos.add(grupo);
        }
    return grupos;
}

private void dfs(String inicio, Set<String> visitados, Set<String> grupo) {
    Deque<String> pilha = new ArrayDeque<>();
    pilha.push(inicio);
    visitados.add(inicio);
    while (!pilha.isEmpty()) {
        String u = pilha.pop();                      // tira do INÍCIO
        grupo.add(u);
        for (String v : adj.get(u))
            if (visitados.add(v)) pilha.push(v);     // põe no INÍCIO
    }
}
```

**A diferença entre BFS e DFS, no código, é uma linha:**

| | BFS (`caminho`, linhas 111 e 115) | DFS (`dfs`, linhas 161 e 164) |
|---|---|---|
| Colocar | `fila.add(v)` → no **fim** | `pilha.push(v)` → no **início** |
| Tirar | `fila.poll()` → do **início** | `pilha.pop()` → do **início** |
| Resultado | entra no fim e sai do início: **FIFO** (fila) | entra no início e sai do início: **LIFO** (pilha) |

As duas usam o **mesmo `ArrayDeque`**. Só mudam os métodos. Com a **fila** o algoritmo anda em camadas, e com a **pilha** ele mergulha.

- **`grupos()` acha os componentes conexos:** para cada pessoa ainda não visitada, roda **uma DFS**, que descobre o grupo inteiro dela. O número de vezes que uma DFS é iniciada é o **número de grupos** (2 no exemplo).
- **A DFS é iterativa** (pilha explícita), não recursiva. Numa cadeia muito longa, a recursão estouraria a pilha de chamadas do Java.
- **Atenção:** a DFS da **animação** (`passosDfs`, linha 235) é escrita de outro jeito: olha o topo da pilha e, se não há amigo novo, *desempilha* (a "volta"). Ela faz isso para a animação mostrar o **backtracking**. A de `grupos()` é a versão compacta.

**Fale (Gabriel):**

> "No código, a diferença entre BFS e DFS é **uma linha**. Nos dois uso o mesmo `ArrayDeque`. Na BFS eu **coloco no fim** com `add` e **tiro do início** com `poll`: é uma **fila**. Na DFS eu **coloco no início** com `push` e **tiro do início** com `pop`: é uma **pilha**. Só isso já muda o comportamento: de andar em camadas para mergulhar.
>
> E é a DFS que acha os **grupos isolados**: para cada pessoa que ainda não foi visitada, rodo uma DFS, que descobre o grupo inteiro dela. Quantas vezes eu preciso começar uma DFS é o número de grupos."

### 5c. Pontos de articulação, cerca de 1 min 30 s

**Clique:** `Articulação` → `Play`.

**Na tela:** diferente dos outros, **todas as bolinhas ficam coloridas**. A cada passo, uma pessoa fica **fantasma** (com a borda tracejada) e as outras são recoloridas conforme os **grupos que sobram sem ela**. Embaixo, a faixa **"PONTOS DE ARTICULAÇÃO ENCONTRADOS"** vai acumulando nomes em vermelho. No painel "Passo a passo", frases do tipo:

> *Sem Silas a rede teria 3 grupo(s); hoje tem 2. Aumentou! Silas é ponto de articulação: sem essa pessoa a rede se parte.*

> *Sem Felipe a rede teria 2 grupo(s); hoje tem 2. Não aumentou: Felipe não é ponto de articulação.*

**O que significa:**
- O algoritmo **tira uma pessoa de cada vez** e **conta os grupos que sobram**. Se o número **aumenta**, a pessoa estava mantendo o grupo unido: é um **ponto de articulação**.
- **Silas:** tirando ele, o grupo azul se parte em dois (Felipe, Gabriel Xavier e Helder de um lado; Ismael, Benks e Joao Paulo do outro). Então 2 grupos viram 3.
- **Felipe:** tirando ele, os outros continuam ligados pelo Silas. O número **não muda**.
- No final são **cinco**: **Benks, Gabriel Siqueira, Ismael, Silas e Wilson**.
- Eles são exatamente as cinco pessoas de **maior intermediação**. É a mesma ideia por dois caminhos: quem é ponte.
- **Custo:** do jeito que fizemos (tirar cada pessoa e recontar), é **O(V·(V+E))**. O algoritmo de **Tarjan** faz isso numa única DFS, em **O(V+E)**, mas é bem mais difícil de visualizar.

**Fale (Gabriel):**

> "O último algoritmo responde uma pergunta: **quais pessoas, se saírem, quebram a rede?** Elas se chamam **pontos de articulação**.
>
> A gente testa uma por uma: **tira a pessoa** e **conta quantos grupos sobram**. A pessoa que está sendo testada fica tracejada, e os grupos que sobram ficam coloridos.
>
> Quando eu tiro o **Silas**, o grupo azul **se parte em dois**, e a rede passa de 2 para 3 grupos. Então ele é um ponto de articulação. Já o **Felipe**: se ele sair, os outros continuam ligados pelo Silas, e o número de grupos **não muda**.
>
> No final, são **cinco pessoas**: Benks, Gabriel Siqueira, Ismael, Silas e Wilson. E são exatamente as mesmas de maior intermediação: **a mesma ideia, vista por dois caminhos.**
>
> Existe um algoritmo mais esperto, o de **Tarjan**, que acha todas em uma só passada. A gente fez do jeito mais simples de visualizar.
>
> O Silas é um desses pontos. Vou mostrar o código desse teste e depois confirmar ao vivo."

#### No código: tirar uma pessoa e recontar os grupos (cerca de 1 min)

**Abrir:** `RedeSocial.java`, linhas 280 a 322.

```java
// passosArticulacao() (linha 280), resumido
int antes = grupos().size();                             // quantos grupos a rede tem hoje
for (String p : adj.keySet()) {                          // para cada pessoa p...
    Map<String, Integer> semP = componentesSem(p);       // ...os grupos da rede SEM p
    int depois = new HashSet<>(semP.values()).size();    // quantos grupos sobraram
    boolean ponto = depois > antes;                      // aumentou? então p é ponto de articulação
    if (ponto) achados.add(p);
    ...
}

// componentesSem(removida) (linha 303): uma DFS que finge que `removida` não existe, resumido
for (String inicio : adj.keySet()) {
    if (inicio.equals(removida) || grupo.containsKey(inicio)) continue;   // pula quem foi "tirada" e quem já tem grupo
    ...                                                                   // DFS com pilha, como a de grupos()
        if (!v.equals(removida) && !grupo.containsKey(v)) { grupo.put(v, id); pilha.push(v); }
    id++;                                                                 // terminou um grupo: o próximo ganha outro número
}
```

- **A ideia é direta:** para cada pessoa, calcula os grupos **como se ela não existisse** e compara com o número de grupos de hoje. Se aumentou, ela era um ponto de articulação.
- `componentesSem` é uma **DFS** igual à de `grupos()`, com uma diferença: **ignora a pessoa removida** (`!v.equals(removida)`). Devolve um mapa `pessoa → número do grupo`.
- `new HashSet<>(semP.values()).size()` **conta os números de grupo diferentes**: o `Set` descarta os repetidos, então o tamanho é o número de grupos.
- **A janela usa esse mapa** para recolorir os grupos que sobram a cada passo da animação.
- **Custo:** uma DFS por pessoa, **O(V·(V + E))**. O algoritmo de **Tarjan** faz tudo numa DFS só, em O(V + E), mas é bem mais difícil de explicar e de animar. *(É a melhoria natural do trabalho.)*

**Fale (Gabriel):**

> "O código do teste é direto. Para cada pessoa, chamo o `componentesSem`, que calcula os grupos da rede **fingindo que essa pessoa não existe**. É uma DFS igual à dos grupos, só que ignora a pessoa removida. Depois conto quantos grupos diferentes sobraram e comparo com quantos tinha: se **aumentou**, a pessoa era um ponto de articulação.
>
> Esse jeito faz uma DFS por pessoa, então custa mais do que o de Tarjan, que faz tudo numa passada só. A gente preferiu esse porque dá para **explicar e animar**."

---

## 6. A demonstração final (1,5 min) — Gabriel

**Clique:** aba `Explorar` → no **Silas** → `Remover pessoa selecionada`. Depois `Reiniciar com dados de exemplo`. Depois clique no **Joao Paulo**, no **Wallace** e em `Conectar`.

### 6a. Tirando o Silas

**Na tela:** o Silas some. O grupo azul **se parte em dois** e ganha **duas cores diferentes** (uma das metades mantém o azul, a outra ganha uma cor nova). Na legenda aparecem **3 grupos**. O painel mostra:

```
Silas foi removida.
A rede agora tem 3 grupos isolados.
```

**O que significa:** é a confirmação **ao vivo** do que o algoritmo de articulação acabou de dizer. Ao tirar um ponto de articulação, o número de grupos aumenta. O Silas era a pessoa que mantinha o grupo unido.

### 6b. Conectando os dois grupos

**Na tela:** depois do `Reiniciar`, os dois grupos voltam. Ao selecionar o **Joao Paulo** (do grupo azul) e o **Wallace** (do verde) e clicar em `Conectar`, os dois grupos **viram um só** e ficam com a mesma cor. O painel mostra: "A rede agora tem 1 grupo isolado".

**O que significa:** uma **única** amizade entre os dois grupos transforma a rede em um grupo só. O contrário do que aconteceu com o Silas: aqui o número de grupos **diminui**.

**Fale (Gabriel):**

> "Vamos confirmar ao vivo. A gente acabou de ver que o **Silas é um ponto de articulação**. Vou **remover o Silas**.
>
> Olha o que aconteceu: o grupo azul **se partiu em dois**, e a legenda agora mostra **3 grupos**. Era exatamente o que o algoritmo previu.
>
> Agora o contrário. Vou **reiniciar** a rede e criar **uma única amizade**, entre o Joao Paulo, do grupo azul, e o Wallace, do grupo verde. Com só uma amizade, os dois grupos **viram um só**.
>
> E o código disso é curtinho: o botão chama o `removerPessoa` da janela, que usa o **mesmo `rede.removePessoa`** que o Leonam mostrou, e depois recalcula os grupos com `rede.grupos()`, que é a DFS."

### No código: o que a janela faz ao clicar em "Remover" (cerca de 20 s)

**Abrir:** `Janela.java`, linhas 221 a 230 (`removerPessoa`).

```java
private void removerPessoa() {
    ...
    int antes = rede.grupos().size();            // quantos grupos havia
    rede.removePessoa(p);                        // o MESMO método da RedeSocial (linha 32), mostrado na parte 2
    ...
    painel.sincronizar();                        // redesenha: recalcula os grupos (DFS) e as cores
    atualizarSelecao(p + " foi removida." + efeitoGrupos(antes));   // "A rede agora tem 3 grupos isolados."
}
```

**O que significa:** a janela **não tem lógica de grafo própria**. Ela chama os métodos da `RedeSocial` e mostra o resultado. O número "3 grupos" que aparece vem de `rede.grupos().size()`, a DFS da parte 5.

**Passagem (Gabriel → Leonam):**

> "E para fechar, o Leonam."

---

## Encerramento (0,5 min) — Leonam

**Fale (Leonam):**

> "Para fechar, o que a gente mostrou foi: o grafo é uma forma simples de representar uma rede de pessoas. A **forma de guardar**, a lista de adjacência, importa para a memória. Os **algoritmos de busca**, BFS e DFS, que no código só se diferenciam por usar uma **fila** ou uma **pilha**, respondem perguntas como menor caminho e grupos isolados com custo proporcional ao tamanho da rede. E com algoritmos mais elaborados, a gente descobre quem é **ponte** e quem mantém a rede unida. Obrigado!"

---

## Bônus: a interface por dentro (código do Swing)

*Usem se perguntarem como a janela funciona, ou se sobrar tempo. O foco do trabalho é a `RedeSocial`, mas a janela também é código nosso.*

**A ideia central:** `Janela.java` **não implementa nenhum algoritmo de grafo**. Ela chama `rede.caminho(...)`, `rede.sugestoes(...)`, `rede.passosBfs(...)` e só **desenha o que volta**. Tudo que é Swing e Java2D fica na janela.

| O que a janela faz | Onde está | Como funciona |
|---|---|---|
| **Desenhar o grafo** | `Painel.paintComponent` (linha 1401) | Pinta o fundo, depois as arestas (`Line2D`), as bolinhas (`Ellipse2D` com gradiente radial) e os nomes. Tudo é desenhado do zero com **Java2D**, sem imagem pronta. |
| **Posição das bolinhas** | `Painel.passo` (linha 1296) | **Simulação de forças:** todas as bolinhas se **repelem**, amigos se **atraem** como molas, e uma leve **gravidade** puxa para o centro. A cada quadro cada bolinha anda um pouquinho, e o desenho se arruma sozinho. |
| **Animação** | `new javax.swing.Timer(16, ...)` (linha 1155) | A cada 16 ms (uns 60 quadros por segundo) calcula um passo da simulação e redesenha. Quando as bolinhas param de se mexer, o cálculo para. |
| **Clique e arrastar** | `MouseAdapter` dentro de `Painel()`; `selecionar` (linha 184) | Descobre em qual bolinha o clique caiu: um clique seleciona a pessoa, um arrasto move a bolinha. |
| **Passo a passo** | `gerarPassos` (linha 406), `avancar` (linha 420) | Pede a lista de `Passo` à `RedeSocial` e mostra o passo `indice`. Um `Timer` faz o `indice++` para o botão Play. |
| **Cores por métrica** | `colorirPor` (linha 489) | Pede `rede.proximidade()` ou `rede.intermediacao()`, ajusta os valores para uma escala de 0 a 1 e converte em cor. |
| **Botões e cartões** | `Botao` (linha 1024), `Cartao` | Desenhados à mão, porque o visual padrão do Swing no Windows **ignora as cores** e ficaria claro demais no tema escuro. |

**A simulação de forças, em uma frase:** é como pendurar as bolinhas em molas elásticas (as amizades) e fazer todas se afastarem como ímãs de mesmo polo. O desenho para quando as forças se equilibram.

**Fale (Gabriel ou Leonam), cerca de 40 s:**

> "A janela foi feita só com o **Swing e o Java2D**, que já vêm no Java. Ela **não tem nenhum algoritmo de grafo**: chama a `RedeSocial` e desenha o que volta. As bolinhas se arrumam sozinhas por uma **simulação de forças**: todas se repelem, e amigos se puxam como se fossem molas. A cada 16 milissegundos calculamos um passo e redesenhamos, e quando as forças se equilibram o desenho para."

---

## Perguntas que podem fazer

Quem responde cada uma está entre parênteses. Se a pergunta vier fora da sua área, quem sabe responde e o outro complementa.

**Por que BFS e não DFS para o menor caminho?** *(Gabriel)*
A BFS descobre as pessoas em **ordem de distância**: todas a 1 amizade, depois a 2, depois a 3. Então a primeira vez que alcança alguém já é pelo menor caminho. A DFS acha *um* caminho, mas não necessariamente o menor, porque mergulha por onde vier primeiro.

**E se as amizades tivessem pesos (força da amizade)?** *(Gabriel)*
Aí a BFS deixa de servir, porque ela conta **número de amizades**, não **peso**. Nesse caso se usa o **algoritmo de Dijkstra**, que troca a fila comum por uma **fila de prioridade** (sempre processa quem está mais perto, pelo peso). Com todos os pesos iguais a 1, o Dijkstra dá o mesmo resultado da BFS. Não implementamos porque o nosso grafo é **não ponderado**.

**Qual a complexidade dos algoritmos?** *(Gabriel)*
- **BFS e DFS:** O(V + E), cada pessoa e cada amizade são vistas uma vez. Isso conta como O(1) achar cada pessoa no `TreeMap`, como nos livros. Na verdade cada busca custa O(log V), o que dá O(V log V + E).
- **`grupos()` e `circulos()`:** somam O(V log V) para devolver os resultados em ordem alfabética (cada grupo é um `TreeSet`, e cada nível dos círculos é ordenado).
- **Sugestões:** O(S · log grau + k · log k), em que S é a soma dos graus dos amigos e k é o número de sugestões.
- **Proximidade e intermediação:** O(V·(V + E)), uma BFS por pessoa.
- **Pontos de articulação** do jeito que fizemos (tirar cada pessoa e recontar os grupos): O(V·(V + E)).

**Existe um jeito mais rápido de achar pontos de articulação?** *(Gabriel)*
Sim, o algoritmo de **Tarjan** acha todos numa única DFS, em O(V + E). Escolhemos o teste de "tirar e contar" porque é **mais fácil de visualizar** e de explicar. Ficou como melhoria.

**Qual a diferença entre ponto de articulação e intermediação?** *(Gabriel)*
Ponto de articulação é **sim ou não**: se a pessoa sair, o grupo se parte? Intermediação é um **número**: quantos caminhos dependem da pessoa. No exemplo os cinco pontos de articulação são justamente os cinco de maior intermediação, mas a intermediação ainda distingue quanto cada um pesa (Silas 9,5, Wilson 3,0).

**O que é um grupo isolado, tecnicamente?** *(Gabriel)*
É um **componente conexo**: um conjunto de pessoas em que todas se alcançam por amizades, e que não tem nenhuma ligação com o resto. A DFS (ou a BFS) a partir de uma pessoa percorre exatamente o componente dela.

**Como funciona a sugestão de amigos?** *(Leonam)*
Para cada amigo da pessoa, olhamos os amigos dele que ela ainda **não conhece** e contamos quantas vezes cada candidato aparece. Quem aparece mais tem **mais amigos em comum** e vem primeiro. O custo é proporcional à soma dos graus dos amigos, com um fator log grau por causa do `contains` no `TreeSet`, mais a ordenação das sugestões no fim. Não precisa olhar a rede inteira.

**Por que lista de adjacência e não matriz?** *(Leonam)*
Redes sociais são **esparsas**: cada pessoa conhece poucas entre muitas. A lista gasta O(V + E); a matriz gasta O(V²), quase tudo zero (no exemplo, 26 uns em 144 casas). A matriz só ganha para perguntar "A e B são amigos?" em O(1).

**Por que a matriz é simétrica?** *(Leonam)*
Porque o grafo é **não direcionado**: se A é amigo de B, B é amigo de A. Cada amizade aparece duas vezes na matriz.

**A DFS é recursiva?** *(Leonam)*
Não. Usamos uma **pilha explícita**, para não estourar a pilha de chamadas do Java em redes com cadeias muito longas.

**O programa funciona para outro grafo?** *(Leonam)*
Sim. Dá para adicionar e remover pessoas e amizades na janela, e todos os algoritmos e métricas recalculam. Dois homônimos contam como a mesma pessoa, porque o nome é a chave.

**Vocês usaram alguma biblioteca?** *(Leonam)*
Nenhuma biblioteca de grafos. A estrutura e os algoritmos são nossos. Só usamos as coleções que já vêm no Java (`TreeMap`, `HashMap`, `ArrayDeque`) e o **Swing** para a janela.

### Perguntas sobre o código

**Por que `TreeMap` e `TreeSet` em vez de `HashMap` e `HashSet`?** *(Leonam)*
Pela **ordem alfabética previsível**: as listas, os rankings, a ordem em que a BFS e a DFS visitam os amigos e a animação saem sempre iguais. O preço é pequeno: as operações custam O(log n) em vez de O(1) em média. Como a variável é declarada como `Map` e `Set`, dá para trocar por `HashMap` e `HashSet` mudando só a linha do `new`.

**Por que `amigos()` devolve `Collections.unmodifiableSet`?** *(Leonam)*
Para **proteger o grafo**. Quem chamar `amigos(p)` recebe só uma visão de leitura e não consegue adicionar nem remover. O grafo só muda por `addAmizade`, `removeAmizade` etc., que mantêm a simetria (amizade nos dois sentidos).

**O que acontece se pedirem algo de uma pessoa que não existe?** *(Leonam)*
O método `vizinhos` (linha 63) lança `IllegalArgumentException("Pessoa não encontrada: ...")`, com mensagem clara. O menu e a janela validam antes, então o usuário nunca vê a exceção.

**Qual a diferença entre `add`/`poll` e `push`/`pop` do `ArrayDeque`?** *(Gabriel)*
É o que separa fila de pilha. O `ArrayDeque` é uma lista com duas pontas. `add` põe no **fim** e `poll` tira do **início**: é uma **fila** (FIFO), usada na BFS. `push` põe no **início** e `pop` tira do **início**: é uma **pilha** (LIFO), usada na DFS.

**O que é um `record` e por que `Passo` é um?** *(Gabriel)*
Um `record` (Java 16 ou mais novo) é uma classe **só de dados e imutável**: o Java gera sozinho o construtor, os métodos de leitura (`p.atual()`), `equals` e `toString`. Serve bem para "fotografias" que não devem mudar depois de criadas.

**Por que o `foto()` copia as coleções?** *(Gabriel)*
Porque o algoritmo continua **alterando** a fila, o mapa `pai` etc. enquanto roda. Sem copiar, todos os passos apontariam para as mesmas coleções e mostrariam o **estado final**. A cópia congela cada instante.

**A DFS da animação é a mesma de `grupos()`?** *(Gabriel)*
Não, e é bom saber disso. A de `grupos()` (`dfs`, linha 156) é a **compacta**: marca a pessoa ao empilhar. A da animação (`passosDfs`, linha 235) olha o **topo** da pilha e, se não há amigo novo, **desempilha**: é a forma que deixa o *backtracking* visível. As duas visitam as mesmas pessoas de um grupo, mas a **ordem** pode ser diferente.

**Como a janela desenha e anima o grafo?** *(Gabriel)*
Veja o **Bônus** acima: Java2D em `paintComponent`, uma simulação de forças em `passo` e um `Timer` de 16 ms que repete tudo.

**Vocês testaram o código?** *(os dois)*
Resposta honesta: rodamos a rede de exemplo e conferimos os resultados (menor caminho, grupos, pontos de articulação). **Não há testes automatizados no repositório**; o próximo passo natural seria escrever testes com JUnit. Não digam que há testes automatizados, porque não há.
