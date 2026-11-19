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
        return Collections.unmodifiableSet(adj.get(p));
    }

    /** Grau do vértice = número de amigos. */
    public int grau(String p) {
        return adj.get(p).size();
    }

    // ---------- consultas ----------

    public Set<String> amigosEmComum(String a, String b) {
        Set<String> comuns = new TreeSet<>(adj.get(a));
        comuns.retainAll(adj.get(b));
        return comuns;
    }

    /** Amigos de amigos que p ainda não conhece, do mais ao menos amigos em comum. */
    public List<Map.Entry<String, Integer>> sugestoes(String p) {
        Map<String, Integer> contagem = new HashMap<>();
        for (String amigo : adj.get(p))
            for (String candidato : adj.get(amigo))
                if (!candidato.equals(p) && !adj.get(p).contains(candidato))
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

    private void dfs(String u, Set<String> visitados, Set<String> grupo) {
        visitados.add(u);
        grupo.add(u);
        for (String v : adj.get(u))
            if (!visitados.contains(v)) dfs(v, visitados, grupo);
    }

    // ---------- representações ----------

    public String listaAdjacencia() {
        StringBuilder sb = new StringBuilder();
        adj.forEach((p, amigos) -> sb.append(String.format("%-16s -> %s%n", p, amigos)));
        return sb.toString();
    }

    public String matrizAdjacencia() {
        List<String> nomes = new ArrayList<>(adj.keySet());
        StringBuilder sb = new StringBuilder(" ".repeat(19));
        for (int i = 0; i < nomes.size(); i++) sb.append(String.format("%3d", i));
        sb.append('\n');
        for (int i = 0; i < nomes.size(); i++) {
            sb.append(String.format("%2d %-16s", i, nomes.get(i)));
            for (String outro : nomes)
                sb.append(String.format("%3d", adj.get(nomes.get(i)).contains(outro) ? 1 : 0));
            sb.append('\n');
        }
        return sb.toString();
    }
}
