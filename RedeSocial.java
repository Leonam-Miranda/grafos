import java.util.*;

/** Grafo não direcionado e não ponderado: pessoas são vértices, amizades são arestas. */
public class RedeSocial {

    // lista de adjacência: pessoa -> conjunto de amigos (ordenado por nome)
    private final Map<String, Set<String>> adj = new TreeMap<>();

    /** Tira espaços extras e põe cada palavra com inicial maiúscula ("joao  paulo" -> "Joao Paulo"). */
    public static String normaliza(String nome) {
        StringBuilder sb = new StringBuilder();
        for (String w : nome.trim().split("\\s+")) {
            if (w.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    // ---------- operações básicas ----------

    public boolean existe(String p) {
        return adj.containsKey(p);
    }

    public boolean addPessoa(String p) {
        if (p.isEmpty() || existe(p)) return false;
        adj.put(p, new TreeSet<>());
        return true;
    }

    public boolean removePessoa(String p) {
        Set<String> amigos = adj.remove(p);
        if (amigos == null) return false;
        for (String a : amigos) adj.get(a).remove(p); // some da lista de cada amigo
        return true;
    }

    public boolean addAmizade(String a, String b) {
        if (a.equals(b) || !existe(a) || !existe(b)) return false;
        boolean nova = adj.get(a).add(b);
        adj.get(b).add(a); // vale nos dois sentidos
        return nova;
    }

    public boolean removeAmizade(String a, String b) {
        if (!existe(a) || !existe(b)) return false;
        boolean existia = adj.get(a).remove(b);
        adj.get(b).remove(a);
        return existia;
    }

    public Set<String> amigos(String p) {
        return Collections.unmodifiableSet(vizinhos(p));
    }

    /** Grau do vértice = número de amigos. */
    public int grau(String p) {
        return vizinhos(p).size();
    }

    /** Conjunto de amigos de p; lança exceção se a pessoa não existir. */
    private Set<String> vizinhos(String p) {
        Set<String> v = adj.get(p);
        if (v == null) throw new IllegalArgumentException("Pessoa não encontrada: " + p);
        return v;
    }

    // ---------- consultas ----------

    public Set<String> amigosEmComum(String a, String b) {
        Set<String> comuns = new TreeSet<>(vizinhos(a));
        comuns.retainAll(vizinhos(b));
        return comuns;
    }

    /** Amigos de amigos que p ainda não conhece, do mais ao menos amigos em comum. */
    public List<Map.Entry<String, Integer>> sugestoes(String p) {
        Set<String> meusAmigos = vizinhos(p);
        Map<String, Integer> contagem = new HashMap<>();
        for (String amigo : meusAmigos)
            for (String candidato : adj.get(amigo))
                if (!candidato.equals(p) && !meusAmigos.contains(candidato))
                    contagem.merge(candidato, 1, Integer::sum);

        List<Map.Entry<String, Integer>> lista = new ArrayList<>(contagem.entrySet());
        lista.sort(Map.Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()));
        return lista;
    }

    public List<String> maisPopulares() {
        int max = adj.values().stream().mapToInt(Set::size).max().orElse(0);
        List<String> res = new ArrayList<>();
        for (var e : adj.entrySet())
            if (e.getValue().size() == max) res.add(e.getKey());
        return res;
    }

    // ---------- algoritmos de grafos ----------

    /** BFS: menor caminho de a até b (lista vazia se não houver ligação). */
    public List<String> caminho(String a, String b) {
        vizinhos(a); // valida: lança exceção se alguma das pessoas não existir
        vizinhos(b);
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
    }

    /** BFS por níveis: o índice i da lista guarda quem está a (i + 1) graus de p. */
    public List<List<String>> circulos(String p) {
        vizinhos(p); // valida: lança exceção se a pessoa não existir
        Set<String> visitados = new HashSet<>(Set.of(p));
        List<String> atual = List.of(p);
        List<List<String>> niveis = new ArrayList<>();
        while (true) {
            List<String> proximo = new ArrayList<>();
            for (String u : atual)
                for (String v : adj.get(u))
                    if (visitados.add(v)) proximo.add(v);
            if (proximo.isEmpty()) return niveis;
            Collections.sort(proximo);
            niveis.add(proximo);
            atual = proximo;
        }
    }

    /** DFS: componentes conexos, ou seja, grupos sem nenhuma ligação entre si. */
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

    /** DFS iterativa (pilha explícita), então não estoura a pilha em redes grandes. */
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

    // ---------- algoritmos passo a passo (para a interface animar) ----------

    /**
     * Uma "fotografia" do algoritmo num instante. A interface só reproduz a lista de passos,
     * então a lógica dos algoritmos continua toda aqui.
     *
     * @param linha      linha do pseudocódigo em execução (índice, ou -1 se nenhuma); a interface mostra o mesmo texto
     * @param texto      explicação do que acabou de acontecer
     * @param atual      pessoa sendo processada (ou null)
     * @param alvo       amigo que está sendo examinado/descoberto a partir de {@code atual} (ou null)
     * @param estrutura  conteúdo da fila (frente primeiro) ou da pilha (base primeiro)
     * @param concluidos pessoas já totalmente processadas
     * @param pai        árvore do percurso: quem descobriu cada pessoa
     * @param rotulo     BFS: distância até o início; DFS: ordem de visita
     * @param destaques  articulação: pontos de articulação encontrados até aqui
     * @param grupos     articulação: grupo de cada pessoa na rede sem {@code atual} (null nos outros algoritmos)
     */
    public record Passo(int linha, String texto, String atual, String alvo,
                        List<String> estrutura, Set<String> concluidos, Map<String, String> pai,
                        Map<String, Integer> rotulo, Set<String> destaques, Map<String, Integer> grupos) {
    }

    private static Passo foto(int linha, String texto, String atual, String alvo, Collection<String> estrutura,
                              Set<String> concluidos, Map<String, String> pai, Map<String, Integer> rotulo,
                              Set<String> destaques, Map<String, Integer> grupos) {
        return new Passo(linha, texto, atual, alvo, List.copyOf(estrutura), new TreeSet<>(concluidos),
                new HashMap<>(pai), new HashMap<>(rotulo), new TreeSet<>(destaques),
                grupos == null ? null : new HashMap<>(grupos));
    }

    /** BFS com fila. Linhas do pseudocódigo: 1 inicia, 2 laço, 3 tira da fila, 5 já visto, 6 novo. */
    public List<Passo> passosBfs(String inicio) {
        vizinhos(inicio);
        List<Passo> passos = new ArrayList<>();
        Deque<String> fila = new ArrayDeque<>(List.of(inicio));
        Set<String> vistos = new HashSet<>(Set.of(inicio));
        Set<String> concluidos = new TreeSet<>();
        Map<String, String> pai = new HashMap<>();
        Map<String, Integer> dist = new HashMap<>(Map.of(inicio, 0));

        passos.add(foto(1, inicio + " é o ponto de partida: entra na fila, a 0 saltos dele mesmo.",
                null, null, fila, concluidos, pai, dist, Set.of(), null));
        while (!fila.isEmpty()) {
            String u = fila.poll();
            passos.add(foto(3, "Tira " + u + " da frente da fila e olha os amigos dele.",
                    u, null, fila, concluidos, pai, dist, Set.of(), null));
            for (String v : adj.get(u)) {
                if (vistos.add(v)) {
                    fila.add(v);
                    pai.put(v, u);
                    dist.put(v, dist.get(u) + 1);
                    passos.add(foto(6, v + " ainda não foi visto: é marcado e vai para o fim da fila (a "
                            + dist.get(v) + (dist.get(v) == 1 ? " salto" : " saltos") + ").",
                            u, v, fila, concluidos, pai, dist, Set.of(), null));
                } else {
                    passos.add(foto(5, v + " já foi visto: ignora.",
                            u, v, fila, concluidos, pai, dist, Set.of(), null));
                }
            }
            concluidos.add(u);
        }
        passos.add(foto(2, "A fila esvaziou: " + vistos.size() + " pessoa(s) alcançada(s). "
                + "Quem não foi alcançado está em outro grupo.", null, null, fila, concluidos, pai, dist, Set.of(), null));
        return passos;
    }

    /** DFS com pilha. Linhas do pseudocódigo: 1 inicia, 2 laço, 5 desce, 6 volta. */
    public List<Passo> passosDfs(String inicio) {
        vizinhos(inicio);
        List<Passo> passos = new ArrayList<>();
        List<String> pilha = new ArrayList<>(List.of(inicio)); // o topo é o último
        Set<String> vistos = new HashSet<>(Set.of(inicio));
        Set<String> concluidos = new TreeSet<>();
        Map<String, String> pai = new HashMap<>();
        Map<String, Integer> ordem = new HashMap<>(Map.of(inicio, 1));

        passos.add(foto(1, inicio + " é o ponto de partida: entra na pilha (visita nº 1).",
                null, null, pilha, concluidos, pai, ordem, Set.of(), null));
        while (!pilha.isEmpty()) {
            String u = pilha.get(pilha.size() - 1);
            String novo = null;
            for (String v : adj.get(u))
                if (!vistos.contains(v)) {
                    novo = v;
                    break;
                }
            if (novo != null) {
                vistos.add(novo);
                pilha.add(novo);
                pai.put(novo, u);
                ordem.put(novo, ordem.size() + 1);
                passos.add(foto(5, "No topo está " + u + ", que tem um amigo novo: " + novo + ". Desce para "
                        + novo + " e o empilha (visita nº " + ordem.get(novo) + ").",
                        u, novo, pilha, concluidos, pai, ordem, Set.of(), null));
            } else {
                pilha.remove(pilha.size() - 1);
                concluidos.add(u);
                passos.add(foto(6, u + " não tem mais amigos novos: sai da pilha e o algoritmo volta"
                        + (pilha.isEmpty() ? "." : " para " + pilha.get(pilha.size() - 1) + "."),
                        u, null, pilha, concluidos, pai, ordem, Set.of(), null));
            }
        }
        passos.add(foto(2, "A pilha esvaziou: " + vistos.size() + " pessoa(s) visitada(s). "
                + "Quem não foi visitado está em outro grupo.", null, null, pilha, concluidos, pai, ordem, Set.of(), null));
        return passos;
    }

    /**
     * Pontos de articulação: pessoas que, ao sair, deixam a rede com mais grupos.
     * Testa uma por uma, O(V·(V+E)); o algoritmo de Tarjan acha todas numa só DFS, em O(V+E).
     * Linhas do pseudocódigo: 2 tira a pessoa, 4 compara e decide.
     */
    public List<Passo> passosArticulacao() {
        List<Passo> passos = new ArrayList<>();
        int antes = grupos().size();
        Set<String> achados = new TreeSet<>();
        for (String p : adj.keySet()) {
            Map<String, Integer> semP = componentesSem(p);
            int depois = new HashSet<>(semP.values()).size();
            passos.add(foto(2, "E se " + p + " saísse da rede? Os grupos que sobram ficam coloridos.",
                    p, null, List.of(), Set.of(), Map.of(), Map.of(), achados, semP));
            boolean ponto = depois > antes;
            if (ponto) achados.add(p);
            passos.add(foto(4, "Sem " + p + " a rede teria " + depois + " grupo(s); hoje tem " + antes + ". "
                    + (ponto ? "Aumentou! " + p + " é ponto de articulação: sem essa pessoa a rede se parte."
                    : "Não aumentou: " + p + " não é ponto de articulação."),
                    p, null, List.of(), Set.of(), Map.of(), Map.of(), achados, semP));
        }
        passos.add(foto(-1, achados.isEmpty() ? "Nenhum ponto de articulação: nenhuma pessoa sozinha parte a rede."
                : "Pontos de articulação: " + String.join(", ", achados) + ". São as pessoas mais críticas da rede.",
                null, null, List.of(), Set.of(), Map.of(), Map.of(), achados, null));
        return passos;
    }

    /** Grupo (componente conexo) de cada pessoa, imaginando a rede sem {@code removida}. */
    private Map<String, Integer> componentesSem(String removida) {
        Map<String, Integer> grupo = new HashMap<>();
        int id = 0;
        for (String inicio : adj.keySet()) {
            if (inicio.equals(removida) || grupo.containsKey(inicio)) continue;
            Deque<String> pilha = new ArrayDeque<>();
            pilha.push(inicio);
            grupo.put(inicio, id);
            while (!pilha.isEmpty()) {
                String u = pilha.pop();
                for (String v : adj.get(u))
                    if (!v.equals(removida) && !grupo.containsKey(v)) {
                        grupo.put(v, id);
                        pilha.push(v);
                    }
            }
            id++;
        }
        return grupo;
    }

    // ---------- centralidade: quem é mais "importante" na rede ----------

    /** BFS a partir de {@code origem}: distância em saltos até cada pessoa alcançável. */
    private Map<String, Integer> distancias(String origem) {
        Map<String, Integer> dist = new HashMap<>(Map.of(origem, 0));
        Deque<String> fila = new ArrayDeque<>(List.of(origem));
        while (!fila.isEmpty()) {
            String u = fila.poll();
            for (String v : adj.get(u))
                if (!dist.containsKey(v)) {
                    dist.put(v, dist.get(u) + 1);
                    fila.add(v);
                }
        }
        return dist;
    }

    /**
     * Proximidade: quão perto, em média, a pessoa está de quem ela consegue alcançar (uma BFS por pessoa,
     * O(V·(V+E))). Vale de 0 a 1; perto de 1 significa chegar em todo mundo em poucos saltos.
     * Quem alcança só parte da rede é descontado na mesma proporção (fórmula de Wasserman e Faust).
     */
    public Map<String, Double> proximidade() {
        int n = adj.size();
        Map<String, Double> resultado = new TreeMap<>();
        for (String p : adj.keySet()) {
            Map<String, Integer> dist = distancias(p);
            int alcancadas = dist.size() - 1;
            long soma = 0;
            for (int d : dist.values()) soma += d;
            resultado.put(p, alcancadas == 0 ? 0.0 : (double) alcancadas / soma * alcancadas / (n - 1));
        }
        return resultado;
    }

    /**
     * Intermediação: em quantos menores caminhos entre OUTRAS duas pessoas a pessoa aparece
     * (algoritmo de Brandes: uma BFS por pessoa, O(V·(V+E))). Se houver vários menores caminhos,
     * cada um conta só uma fração. Alta = ponte da rede.
     */
    public Map<String, Double> intermediacao() {
        Map<String, Double> cb = new TreeMap<>();
        for (String p : adj.keySet()) cb.put(p, 0.0);

        for (String s : adj.keySet()) {
            Deque<String> ordem = new ArrayDeque<>();            // pessoas na ordem em que a BFS as achou
            Map<String, List<String>> pais = new HashMap<>();    // quem vem imediatamente antes num menor caminho
            Map<String, Integer> caminhos = new HashMap<>();     // quantos menores caminhos de s até cada pessoa
            Map<String, Integer> dist = new HashMap<>();
            for (String v : adj.keySet()) {
                pais.put(v, new ArrayList<>());
                caminhos.put(v, 0);
                dist.put(v, -1);
            }
            caminhos.put(s, 1);
            dist.put(s, 0);
            Deque<String> fila = new ArrayDeque<>(List.of(s));
            while (!fila.isEmpty()) {
                String v = fila.poll();
                ordem.push(v);
                for (String w : adj.get(v)) {
                    if (dist.get(w) < 0) {
                        dist.put(w, dist.get(v) + 1);
                        fila.add(w);
                    }
                    if (dist.get(w) == dist.get(v) + 1) {
                        caminhos.merge(w, caminhos.get(v), Integer::sum);
                        pais.get(w).add(v);
                    }
                }
            }
            // volta do mais distante para o mais perto, repassando "crédito" para quem está no meio do caminho
            Map<String, Double> credito = new HashMap<>();
            for (String v : adj.keySet()) credito.put(v, 0.0);
            while (!ordem.isEmpty()) {
                String w = ordem.pop();
                for (String v : pais.get(w))
                    credito.merge(v, (double) caminhos.get(v) / caminhos.get(w) * (1 + credito.get(w)), Double::sum);
                if (!w.equals(s)) cb.merge(w, credito.get(w), Double::sum);
            }
        }
        cb.replaceAll((p, valor) -> valor / 2); // grafo não direcionado: cada par foi contado duas vezes
        return cb;
    }

    // ---------- dados de exemplo ----------

    /** Rede de demonstração: dois grupos sem ligação entre si (usada pelo menu e pela janela). */
    public static RedeSocial exemplo() {
        RedeSocial r = new RedeSocial();
        String[][] amizades = {
                // grupo A
                {"Felipe", "Gabriel Xavier"}, {"Felipe", "Silas"}, {"Felipe", "Helder"},
                {"Gabriel Xavier", "Silas"}, {"Silas", "Helder"}, {"Silas", "Ismael"},
                {"Ismael", "Benks"}, {"Benks", "Joao Paulo"},
                // grupo B
                {"Leonam", "Fabricio"}, {"Leonam", "Gabriel Siqueira"}, {"Fabricio", "Gabriel Siqueira"},
                {"Gabriel Siqueira", "Wilson"}, {"Wilson", "Wallace"}
        };
        for (String[] a : amizades) {
            r.addPessoa(a[0]);
            r.addPessoa(a[1]);
            r.addAmizade(a[0], a[1]);
        }
        return r;
    }

    // ---------- representações ----------

    /** Largura da coluna de nomes: o maior nome cadastrado, para tudo ficar alinhado. */
    private int larguraNomes() {
        return Math.max(1, adj.keySet().stream().mapToInt(String::length).max().orElse(1));
    }

    public String listaAdjacencia() {
        String formato = "%-" + larguraNomes() + "s -> %s%n";
        StringBuilder sb = new StringBuilder();
        adj.forEach((p, amigos) -> sb.append(String.format(formato, p, amigos)));
        return sb.toString();
    }

    public String matrizAdjacencia() {
        int w = larguraNomes();
        List<String> nomes = new ArrayList<>(adj.keySet());
        StringBuilder sb = new StringBuilder(" ".repeat(3 + w));
        for (int i = 0; i < nomes.size(); i++) sb.append(String.format("%3d", i));
        sb.append('\n');
        for (int i = 0; i < nomes.size(); i++) {
            sb.append(String.format("%2d %-" + w + "s", i, nomes.get(i)));
            for (String outro : nomes)
                sb.append(String.format("%3d", adj.get(nomes.get(i)).contains(outro) ? 1 : 0));
            sb.append('\n');
        }
        return sb.toString();
    }
}
