# Rede Social de Amizades com Grafos

### Estrutura de Dados · Java, sem bibliotecas de grafos

**Leonam Miranda de Oliveira** · **Gabriel Figueira Siqueira**

---

## 1. O que construímos

*Leonam*

Uma rede social simples, modelada como um **grafo**:

| Rede social | Grafo |
|---|---|
| Pessoa | **Vértice** |
| Amizade | **Aresta** |
| Número de amigos | **Grau** |
| Grupo sem ligação com os outros | **Componente conexo** |

- **Não direcionado:** se A é amigo de B, B é amigo de A
- **Não ponderado:** toda amizade vale o mesmo
- **Sem bibliotecas de grafos:** só as coleções do Java e o Swing
- Rede de exemplo: **12 pessoas, 13 amizades, 2 grupos**

---

## 2. Como o código está organizado

*Leonam*

```
 Main.java   (terminal) ──┐
                          ├──▶  RedeSocial.java   (grafo + algoritmos)
 Janela.java (janela)   ──┘
```

| Arquivo | Linhas | Papel |
|---|---|---|
| `RedeSocial.java` | 459 | O grafo e **todos os algoritmos** |
| `Main.java` | 132 | Menu de terminal |
| `Janela.java` | 1771 | Interface gráfica (Swing) |

**Regra de ouro:** a `RedeSocial` **não imprime nem desenha nada**. Só guarda o grafo e devolve resultados. Por isso o terminal e a janela usam o mesmo código.

---

## 3. Como o grafo é guardado

*Leonam* · `RedeSocial.java`, linha 7

```java
private final Map<String, Set<String>> adj = new TreeMap<>();
```

**Lista de adjacência:** cada pessoa aponta para o **conjunto** dos seus amigos.

- `Map`: chave = nome, valor = conjunto de amigos
- `TreeMap` / `TreeSet`: **ordem alfabética**
- `Set`: **não aceita repetido**, então uma amizade nunca duplica
- `private`: ninguém de fora mexe direto no grafo

---

## 4. Lista ou matriz?

*Leonam*

| | Lista de adjacência | Matriz de adjacência |
|---|---|---|
| Memória | **O(V + E)** | O(V²) |
| Listar os amigos de alguém | O(grau) | O(V) |
| Verificar se há amizade | O(log grau) | **O(1)** |

No nosso exemplo, a matriz tem **144 casas** e só **26 são 1**.

Rede social é **esparsa**: cada pessoa conhece poucas entre muitas. Por isso: **lista**.

---

## 5. Adicionar e remover

*Leonam* · `RedeSocial.java`, linhas 32 a 44

```java
public boolean addAmizade(String a, String b) {
    if (a.equals(b) || !existe(a) || !existe(b)) return false;
    boolean nova = adj.get(a).add(b);
    adj.get(b).add(a); // vale nos dois sentidos
    return nova;
}

public boolean removePessoa(String p) {
    Set<String> amigos = adj.remove(p);
    if (amigos == null) return false;
    for (String a : amigos) adj.get(a).remove(p); // some da lista de cada amigo
    return true;
}
```

- `addAmizade` grava **nos dois sentidos**: é isso que faz o grafo ser **não direcionado**
- `removePessoa` tira a pessoa da lista de **cada amigo**: senão sobram "amigos fantasmas"

---

## 6. Proteção do grafo

*Leonam* · `RedeSocial.java`, linhas 53 a 67

```java
public Set<String> amigos(String p) {
    return Collections.unmodifiableSet(vizinhos(p));
}

private Set<String> vizinhos(String p) {
    Set<String> v = adj.get(p);
    if (v == null) throw new IllegalArgumentException("Pessoa não encontrada: " + p);
    return v;
}
```

- `unmodifiableSet`: quem está de fora só **lê**. O grafo muda apenas pelos métodos da classe
- `vizinhos`: **valida** a pessoa e lança uma exceção com mensagem clara

---

## 7. Amigos em comum e sugestões

*Leonam* · `RedeSocial.java`, linhas 71 a 90

```java
public Set<String> amigosEmComum(String a, String b) {
    Set<String> comuns = new TreeSet<>(vizinhos(a));   // cópia
    comuns.retainAll(vizinhos(b));                      // interseção
    return comuns;
}
```

```java
for (String amigo : meusAmigos)                       // para cada amigo meu...
    for (String candidato : adj.get(amigo))           // ...os amigos dele
        if (!candidato.equals(p) && !meusAmigos.contains(candidato))
            contagem.merge(candidato, 1, Integer::sum);   // conta +1
```

- `retainAll` faz a **interseção**; a **cópia** evita apagar amigos de verdade
- `merge(..., 1, Integer::sum)` é o jeito de **contar** com um `Map`
- Depois é só **ordenar** por mais amigos em comum

---

## 8. Menor caminho: a BFS

*Leonam* · `RedeSocial.java`, linhas 106 a 121

```java
Map<String, String> pai = new HashMap<>(); // quem descobriu cada pessoa
Deque<String> fila = new ArrayDeque<>();
pai.put(a, null);
fila.add(a);
while (!fila.isEmpty() && !pai.containsKey(b)) {
    String u = fila.poll();
    for (String v : adj.get(u))
        if (!pai.containsKey(v)) {
            pai.put(v, u);
            fila.add(v);
        }
}
LinkedList<String> caminho = new LinkedList<>();
if (!pai.containsKey(b)) return caminho;
for (String x = b; x != null; x = pai.get(x)) caminho.addFirst(x); // reconstrói de trás pra frente
return caminho;
```

- **Fila:** `add` no fim, `poll` do início. Sai **quem entrou primeiro**, então o primeiro caminho achado é o **menor**
- **`pai`:** serve de "já visitei" **e** guarda quem descobriu cada pessoa, para montar o caminho no fim

---

## 9. Ao vivo: explorar a rede

*Leonam*

- Clicar no **Silas**: amigos, grau e **sugestão** (Benks)
- Clicar no **Joao Paulo**: menor caminho, **3 graus de separação**

```
Silas → Ismael → Benks → Joao Paulo
```

---

## 10. Quem é o mais importante? Centralidade

*Gabriel*

| Métrica | Pergunta | Maiores valores |
|---|---|---|
| **Grau** | Quem tem mais amigos? | Silas (4) |
| **Proximidade** | Quem chega em todo mundo mais rápido? | Silas (0,36), Ismael (0,33) |
| **Intermediação** | Quem é **ponte** da rede? | Silas (9,5), Ismael (8,0) |

`RedeSocial.java`, linhas 350 a 354 (proximidade)

```java
Map<String, Integer> dist = distancias(p);
int alcancadas = dist.size() - 1;
long soma = 0;
for (int d : dist.values()) soma += d;
resultado.put(p, alcancadas == 0 ? 0.0 : (double) alcancadas / soma * alcancadas / (n - 1));
```

Silas: alcança 6 pessoas, soma das distâncias = 9, logo 6/9 × 6/11 ≈ **0,36**

---

## 11. Intermediação: o Ismael é uma ponte

*Gabriel* · `RedeSocial.java`, linhas 364 a 407 (algoritmo de Brandes)

```
 Benks, Joao Paulo   ←──  Ismael  ──→   Silas, Felipe, Gabriel Xavier, Helder
     2 pessoas                                       4 pessoas
```

O Ismael tem **só 2 amigos**, mas todo caminho entre esses dois lados passa por ele: 2 × 4 = **8 pares**.

**Em três passos:**
1. Uma **BFS por pessoa**, contando quantos menores caminhos chegam em cada um
2. **Volta** do mais distante ao mais perto, repassando "crédito" a quem está no meio
3. **Divide por 2** (o grafo é não direcionado: cada par foi contado duas vezes)

```java
credito.merge(v, (double) caminhos.get(v) / caminhos.get(w) * (1 + credito.get(w)), Double::sum);
```

**Ranking:** Silas 9,5 · Ismael 8,0 · Benks 5,0 · Gabriel Siqueira 4,0 · Wilson 3,0

---

## 12. BFS ou DFS: fila ou pilha

*Gabriel*

|  | **BFS** | **DFS** |
|---|---|---|
| Estrutura | **Fila** (FIFO) | **Pilha** (LIFO) |
| Colocar | `fila.add(v)`: no **fim** | `pilha.push(v)`: no **início** |
| Tirar | `fila.poll()`: do **início** | `pilha.pop()`: do **início** |
| Anda | em **camadas** | **mergulha** e volta (backtracking) |
| Serve para | **menor caminho** | **grupos isolados** |

Nos dois, o mesmo `ArrayDeque`. **A diferença no código é uma linha.**

---

## 13. Grupos isolados com DFS

*Gabriel* · `RedeSocial.java`, linhas 143 a 166

```java
public List<Set<String>> grupos() {
    Set<String> visitados = new HashSet<>();
    List<Set<String>> grupos = new ArrayList<>();
    for (String p : adj.keySet())
        if (!visitados.contains(p)) {
            Set<String> grupo = new TreeSet<>();
            dfs(p, visitados, grupo);
            grupos.add(grupo);
        }
    return grupos;
}

private void dfs(String inicio, Set<String> visitados, Set<String> grupo) {
    Deque<String> pilha = new ArrayDeque<>();
    pilha.push(inicio);
    visitados.add(inicio);
    while (!pilha.isEmpty()) {
        String u = pilha.pop();
        grupo.add(u);
        for (String v : adj.get(u))
            if (visitados.add(v)) pilha.push(v);
    }
}
```

- Cada pessoa ainda não visitada começa **uma DFS**, que descobre o **grupo inteiro** dela
- Quantas DFS são iniciadas = **número de grupos** (2 no exemplo)
- **Iterativa** (pilha explícita): não estoura a pilha de chamadas em redes grandes

---

## 14. Algoritmos animados: o tipo `Passo`

*Gabriel* · `RedeSocial.java`, linhas 185 a 196

```java
public record Passo(int linha, String texto, String atual, String alvo,
                    List<String> estrutura, Set<String> concluidos, Map<String, String> pai,
                    Map<String, Integer> rotulo, Set<String> destaques, Map<String, Integer> grupos) {
}
```

```java
return new Passo(linha, texto, atual, alvo, List.copyOf(estrutura), new TreeSet<>(concluidos),
        new HashMap<>(pai), new HashMap<>(rotulo), new TreeSet<>(destaques),
        grupos == null ? null : new HashMap<>(grupos));
```

- `passosBfs` é **o mesmo laço da BFS**, mas depois de cada decisão **tira uma fotografia** (`foto`)
- `record`: classe **só de dados**, imutável
- `foto` **copia** as coleções: sem copiar, todos os passos mostrariam o **estado final**
- A janela **não tem algoritmo nenhum**: só mostra o passo `indice`

```java
if (indice < passos.size() - 1) indice++;   // Janela.java, linha 421
```

---

## 15. Pontos de articulação

*Gabriel* · `RedeSocial.java`, linhas 282 a 290

```java
int antes = grupos().size();
for (String p : adj.keySet()) {
    Map<String, Integer> semP = componentesSem(p);
    int depois = new HashSet<>(semP.values()).size();
    boolean ponto = depois > antes;
    if (ponto) achados.add(p);
}
```

**Ideia:** tirar cada pessoa e **recontar os grupos**. Se o número **aumenta**, ela é um ponto de articulação.

- `componentesSem(p)`: uma DFS que **ignora** a pessoa removida (`!v.equals(removida)`)
- Resultado no exemplo: **Benks, Gabriel Siqueira, Ismael, Silas, Wilson**
- São as **mesmas cinco** de maior intermediação

---

## 16. Ao vivo: remover o Silas

*Gabriel* · `Janela.java`, linhas 224 a 229

```java
int antes = rede.grupos().size();
rede.removePessoa(p);
painel.sincronizar();
atualizarSelecao(p + " foi removida." + efeitoGrupos(antes));
```

- Clicar no **Silas** e em **Remover pessoa selecionada**
- Resultado: **"A rede agora tem 3 grupos isolados."**
- A janela usa o **mesmo** `removePessoa` e o **mesmo** `grupos()` do código que vimos

---

## 17. Custos

*Gabriel*

| Operação | Custo |
|---|---|
| BFS (`caminho`) | **O(V + E)** \* |
| DFS (`grupos`) | **O(V + E)** \* + O(V log V) para devolver cada grupo em ordem (`TreeSet`) |
| `sugestoes` | O(S · log grau + k · log k): S = soma dos graus dos amigos, k = nº de sugestões |
| Proximidade, intermediação | O(V·(V + E)): uma BFS por pessoa |
| Pontos de articulação (nosso jeito) | O(V·(V + E)): uma DFS por pessoa |
| Pontos de articulação (**Tarjan**) | **O(V + E)**: uma única DFS |

\* Contando como O(1) achar cada pessoa no `TreeMap`, como nos livros. Na verdade cada busca custa O(log V), o que dá O(V log V + E). Com `HashMap` a busca seria O(1) em média.

Escolhemos o teste de "tirar e contar" porque é **mais fácil de explicar e de animar**.

---

## 18. Como a janela funciona *(opcional)*

*Gabriel ou Leonam*

- **Swing e Java2D** apenas, sem biblioteca externa
- **Desenho:** `paintComponent` pinta arestas, bolinhas e nomes (`Janela.java`, linha 1401)
- **Posição das bolinhas:** simulação de **forças**. Todas se **repelem**, amigos se **atraem** como molas, e uma leve **gravidade** puxa para o centro (linha 1296)
- **Animação:** um `Timer` de 16 ms, cerca de 60 quadros por segundo (linha 1155)
- **A janela não tem algoritmo de grafo:** chama a `RedeSocial` e desenha o que volta

---

## 19. Conclusão

*Leonam*

- O **grafo** representa bem uma rede de pessoas
- A **lista de adjacência** guarda só o que existe: **O(V + E)** de memória
- **BFS** e **DFS** diferem por uma linha: **fila** ou **pilha**
- Com **BFS** achamos o menor caminho, com **DFS** os grupos isolados
- Com algoritmos mais elaborados, descobrimos quem é **ponte**

### Obrigado! Perguntas?

Código: `github.com/Leonam-Miranda/grafos`
