# Roteiro de apresentação (cerca de 10 minutos)

Todos os números abaixo vêm da rede de exemplo (12 pessoas, 13 amizades, 2 grupos) e foram conferidos rodando o programa.

## Divisão da fala

| Parte | Quem fala | Tempo |
|---|---|---|
| 1. Abertura e modelagem | Leonam | 1 min |
| 2. A estrutura de dados | Leonam | 1,5 min |
| 3. Explorar a rede | Leonam | 2 min |
| 4. Colorir por centralidade | Gabriel | 1,5 min |
| 5. Algoritmos por dentro | Gabriel | 3 min |
| 6. A demonstração final | Leonam | 1 min |

**Leonam** fala cerca de 5,5 min e **Gabriel** cerca de 4,5 min, com a parte 6 de volta ao Leonam para fechar. O corte segue o conteúdo: o Leonam cuida do **modelo e da estrutura** (o que é o grafo e como está guardado), e o Gabriel cuida dos **algoritmos** (o que se calcula em cima dele).

**Mouse:** quem está falando controla a tela. A passagem de uma parte para a outra está marcada no texto (**Passagem**), e vale combinar antes quem se levanta ou troca de lugar.

**Perguntas:** cada pergunta provável, no fim, indica quem responde, mas qualquer um pode complementar.

## Antes de começar

- Compile e abra: `javac -encoding UTF-8 *.java` e depois `java Janela`.
- **Maximize a janela.** A aba "Algoritmos" precisa de uns 880 px de altura para mostrar tudo sem rolar.
- Clique em **Reiniciar com dados de exemplo** se a rede já foi mexida.
- Plano B se a janela não abrir: `java Main` abre o menu de terminal com as mesmas funções (a opção 12 mostra a lista e a matriz).
- Ensaiem a **passagem** entre as partes pelo menos uma vez: é onde mais se perde tempo.

## 1. Abertura e modelagem (1 min) — Leonam — sem clicar

**Fale:**
- "Modelamos uma rede de amizades como um **grafo**: pessoa é vértice, amizade é aresta."
- "É **não direcionado** (se A é amigo de B, B é amigo de A) e **não ponderado** (toda amizade vale o mesmo)."
- "Nada de biblioteca de grafos: a estrutura e os algoritmos são nossos. Só usamos as coleções do Java (`TreeMap`, `HashMap`, `ArrayDeque`) e o Swing para a janela."

## 2. A estrutura de dados (1,5 min) — Leonam

**Clique:** `Lista e matriz` (aba Explorar).

**Fale:**
- "Guardamos o grafo como **lista de adjacência**: cada pessoa aponta para o conjunto dos seus amigos."
- "Aqui está a mesma rede como **matriz**. Ela gasta O(V²) de memória, e quase tudo é zero."
- "Numa rede social cada pessoa conhece uma fração pequena do total, então a lista (O(V + E)) é a escolha natural. A matriz só ganha para perguntar 'A e B são amigos?' em O(1), e a lista faz isso em O(log grau)."

Feche a janelinha.

## 3. Explorar a rede (2 min) — Leonam

**Clique:** no **Silas**.

**Fale:**
- "Ele tem **4 amigos**. O tamanho da bolinha e o número dentro dela são o **grau**."
- "A linha tracejada é uma **sugestão de amizade**: o Benks é amigo de um amigo dele. Contamos amigos em comum, que é só olhar os amigos dos amigos."

**Clique:** no **Joao Paulo**.

**Fale:**
- "O caminho dourado é o **menor caminho** entre os dois: Silas, Ismael, Benks, Joao Paulo. São **3 graus de separação**. Isso é uma BFS, e eu mostro por dentro daqui a pouco."

**Clique:** no vazio, depois em **Mais popular**.

**Fale:** "O halo dourado marca o Silas, com 4 amigos."

**Passagem (Leonam):** "Até aqui vimos o resultado. Agora o Gabriel mostra quem é mais importante na rede e como o programa chega a esses resultados."

## 4. Colorir por centralidade (1,5 min) — Gabriel

**Clique:** `Intermediação` (cartão "Colorir por").

**Fale:**
- "Agora a cor mostra a **intermediação**: em quantos menores caminhos entre outras pessoas aparece cada uma."
- "O **Silas é o mais amarelo** (9,5) e o **Ismael vem logo depois (8,0)**, e ele só tem 2 amigos. O que importa não é ter muitos amigos, é **ligar partes da rede**."

**Clique:** `Grau`, depois `Intermediação` de novo.

**Fale:** "Pelo grau o Ismael parece comum. Pela intermediação ele é o segundo mais crítico. Cada métrica responde uma pergunta diferente. A proximidade (0,36 no Silas) mostra quem chega em todo mundo mais rápido."

## 5. Algoritmos por dentro (3 min) — Gabriel

**Clique:** na aba **Algoritmos**, em **BFS**, e no **Silas**. Depois **Play** (ou o botão de próximo passo para ir devagar).

**Fale (BFS):**
- "A BFS visita a rede em **camadas** e usa uma **fila**: sai quem chegou primeiro. Olhem a fila embaixo."
- "O número dentro da bolinha é a **distância até o Silas**. Por isso a BFS dá o menor caminho em grafo sem peso."
- "À direita está o pseudocódigo, e a linha que está rodando fica destacada. Complexidade **O(V + E)**."

**Clique:** em **DFS**.

**Fale (DFS):**
- "A DFS usa uma **pilha** e **mergulha fundo**: vai por um caminho até não ter amigo novo e só então volta (backtracking). O número agora é a **ordem de visita**."
- "Compare com a BFS: mesma complexidade, ordem totalmente diferente. A DFS é a base dos **grupos isolados**."

**Clique:** em **Articulação** e **Play**.

**Fale (pontos de articulação):**
- "Testamos uma pessoa por vez: **tiramos** ela da rede e **contamos os grupos que sobram**. Os grupos que sobram aparecem coloridos."
- "Quando o número de grupos **aumenta**, a pessoa ganha um anel vermelho. No fim são cinco: **Benks, Gabriel Siqueira, Ismael, Silas e Wilson**."
- "São praticamente as mesmas de maior intermediação."

**Passagem (Gabriel):** "O Silas é um desses pontos. O Leonam mostra o que acontece quando ele sai da rede."

## 6. A demonstração final (1 min) — Leonam

**Clique:** na aba **Explorar**, no **Silas**, e em **Remover pessoa selecionada**.

**Fale:** "Ao tirar o Silas, o grupo azul **se parte em dois**. A legenda passa de 2 para **3 grupos**. Ele era um ponto de articulação."

**Clique:** `Reiniciar com dados de exemplo`. Depois selecione o **Joao Paulo** e o **Wallace** e clique em **Conectar**.

**Fale:** "Uma amizade entre os dois grupos e a rede inteira vira **1 grupo só**."

## Perguntas que podem fazer

Quem responde cada uma está entre parênteses. Se a pergunta vier fora da sua área, quem sabe responde e o outro complementa.

**Por que BFS e não DFS para o menor caminho?** *(Gabriel)*
A BFS descobre as pessoas em ordem de distância, então a primeira vez que alcança alguém já é pelo menor caminho. A DFS acha *um* caminho, não o menor.

**E se as amizades tivessem pesos (força da amizade)?** *(Gabriel)*
Aí a BFS deixa de servir e se usa o **algoritmo de Dijkstra**, que troca a fila comum por uma fila de prioridade. Com todos os pesos iguais a 1, o Dijkstra dá o mesmo resultado da BFS. Não implementamos porque o nosso grafo é não ponderado.

**Qual a complexidade dos algoritmos?** *(Gabriel)*
BFS e DFS: O(V + E). Proximidade e intermediação fazem uma BFS por pessoa: O(V·(V + E)). Os pontos de articulação do jeito que fizemos (tirar cada pessoa e recontar os grupos) também são O(V·(V + E)).

**Existe um jeito mais rápido de achar pontos de articulação?** *(Gabriel)*
Sim, o algoritmo de **Tarjan** acha todos numa única DFS, em O(V + E). Escolhemos o teste de "tirar e contar" porque é mais fácil de visualizar. Ficou como melhoria.

**Como funciona a sugestão de amigos?** *(Leonam)*
Para cada amigo da pessoa, olhamos os amigos dele que ela ainda não conhece e contamos quantas vezes cada candidato aparece. Quem aparece mais tem mais amigos em comum. O custo é a soma dos graus.

**Por que lista de adjacência e não matriz?** *(Leonam)*
Redes sociais são **esparsas**: cada pessoa conhece poucas entre muitas. A lista gasta O(V + E); a matriz gasta O(V²), quase tudo zero.

**O programa funciona para outro grafo?** *(Leonam)*
Sim. Dá para adicionar e remover pessoas e amizades na janela, e todos os algoritmos recalculam. Dois homônimos contam como a mesma pessoa, porque o nome é a chave.
