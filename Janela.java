import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Interface gráfica da rede social (Swing, sem bibliotecas externas).
 * Desenha o grafo e deixa explorá-lo com o mouse; toda a lógica de grafos continua em {@link RedeSocial}.
 *
 * Duas abas: "Explorar" (clique em pessoas para ver amigos, sugestões e caminhos) e
 * "Algoritmos" (BFS, DFS e pontos de articulação animados passo a passo).
 */
public class Janela extends JFrame {

    // ---------- tema ----------
    private static final Color FUNDO = new Color(0x0F1117);
    private static final Color CARTAO = new Color(0x1A1E2B);
    private static final Color BORDA = new Color(0x2B3145);
    private static final Color TEXTO = new Color(0xE8EAF2);
    private static final Color TEXTO_FRACO = new Color(0x8D93A8);
    private static final Color DESTAQUE = new Color(0xFFC857);
    private static final Color ALERTA = new Color(0xFF4D6D);
    private static final Color AZUL = new Color(0x4F6BFF);
    private static final Color VERMELHO = new Color(0xC2465E);
    private static final Color NEUTRO = new Color(0x2C3248);

    /** Uma cor por grupo isolado. */
    private static final Color[] PALETA = {
            new Color(0x6C8CFF), new Color(0x3DDBA5), new Color(0xFF7A90), new Color(0xB48CFF),
            new Color(0xFFB04C), new Color(0x4CD4F0), new Color(0xE5D86B), new Color(0xFF8CD0)};

    private static final String FONTE = "Segoe UI";
    private static final int LARGURA_TEXTO = 270; // largura dos textos dentro dos cartões laterais

    // ---------- algoritmos que a aba "Algoritmos" sabe animar ----------

    /** Texto de cada algoritmo. As linhas do pseudocódigo seguem a numeração usada em RedeSocial.passos*(). */
    private enum Algoritmo {
        BFS("BFS", "BFS · busca em largura",
                new String[]{"Fila · FIFO", "O(V + E)", "Menor caminho"},
                "Visita a rede em camadas: primeiro os amigos, depois os amigos dos amigos. A fila atende quem "
                        + "chegou primeiro, então o número dentro de cada bolinha é o menor número de saltos "
                        + "até ela. É o que usamos nos graus de separação.",
                new String[]{
                        "BFS(início):",
                        "  fila = [início]; marca início",
                        "  enquanto fila não vazia:",
                        "    u = tira o primeiro da fila",
                        "    para cada amigo v de u:",
                        "      se v ainda não foi visto:",
                        "        marca v; v vai pro fim"}),
        DFS("DFS", "DFS · busca em profundidade",
                new String[]{"Pilha · LIFO", "O(V + E)", "Componentes"},
                "Segue um caminho até o fim e só volta quando não há mais amigos novos (backtracking). A pilha "
                        + "guarda o caminho atual, e o número dentro de cada bolinha é a ordem de visita. "
                        + "É o que usamos para achar os grupos isolados.",
                new String[]{
                        "DFS(início):",
                        "  pilha = [início]; marca início",
                        "  enquanto pilha não vazia:",
                        "    u = topo da pilha",
                        "    se u tem amigo v não visto:",
                        "      marca v; empilha v (desce)",
                        "    senão: desempilha u (volta)"}),
        ARTICULACAO("Articulação", "Pontos de articulação",
                new String[]{"Testa um a um", "O(V·(V+E))", "Tarjan: O(V+E)"},
                "Uma pessoa é ponto de articulação se, ao sair, a rede se parte em mais grupos. Aqui testamos "
                        + "uma por uma: tiramos a pessoa, recontamos os grupos e comparamos. O algoritmo de "
                        + "Tarjan faz isso numa única DFS, em O(V + E).",
                new String[]{
                        "articulação():",
                        "  para cada pessoa p:",
                        "    tira p da rede",
                        "    conta os grupos que sobram",
                        "    se aumentou: p é articulação"});

        final String curto, nome, resumo;
        final String[] chips, codigo;

        Algoritmo(String curto, String nome, String[] chips, String resumo, String[] codigo) {
            this.curto = curto;
            this.nome = nome;
            this.chips = chips;
            this.resumo = resumo;
            this.codigo = codigo;
        }
    }

    /** O que a cor das bolinhas representa na aba "Explorar". */
    private enum ModoCor {
        GRUPOS("Grupos", "Cada cor é um grupo isolado (componente conexo): quem tem a mesma cor se alcança por amizades."),
        GRAU("Grau", "Grau = número de amigos. Quem tem mais é mais popular, mas não necessariamente mais importante "
                + "para manter a rede unida."),
        PROXIMIDADE("Proximidade", "Proximidade: quão perto, em saltos, a pessoa está de todas as que consegue alcançar. "
                + "Cor quente = chega em todo mundo rápido (o centro do grupo). Uma BFS por pessoa: O(V·(V+E))."),
        INTERMEDIACAO("Intermediação", "Intermediação: em quantos menores caminhos entre OUTRAS pessoas ela aparece. "
                + "Cor quente = ponte da rede: sem ela os caminhos se alongam ou somem. Costuma apontar os pontos de "
                + "articulação. Algoritmo de Brandes, O(V·(V+E)).");

        final String nome, explicacao;

        ModoCor(String nome, String explicacao) {
            this.nome = nome;
            this.explicacao = explicacao;
        }
    }

    /** Da cor fria (menor valor) à quente (maior valor). */
    private static final Color[] ESCALA = {new Color(0x4A55C9), new Color(0x2EC4B6), new Color(0xFFD166)};

    private static Color corEscala(double t) {
        double x = Math.max(0, Math.min(1, t)) * (ESCALA.length - 1);
        int i = Math.min((int) x, ESCALA.length - 2);
        double f = x - i;
        Color a = ESCALA[i], b = ESCALA[i + 1];
        return new Color((int) Math.round(a.getRed() + (b.getRed() - a.getRed()) * f),
                (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * f),
                (int) Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * f));
    }

    // ---------- estado ----------
    private RedeSocial rede = RedeSocial.exemplo();
    private final Painel painel = new Painel();
    private final JTextArea resultado = new JTextArea();
    private final JTextField campoNome = new JTextField();
    private Botao btnRemover, btnConectar, btnDesconectar, btnCirculos;

    private String sel1, sel2;                              // pessoas selecionadas (no máximo duas)
    private List<String> caminho = List.of();               // menor caminho entre sel1 e sel2
    private List<Map.Entry<String, Integer>> sugestoes = List.of();
    private Set<String> foco;                               // quem fica em evidência (o resto esmaece); null = todos
    private Set<String> arestasFoco = Set.of();             // arestas em evidência ("a|b")
    private Set<String> populares = Set.of();               // pessoas com halo dourado
    private ModoCor modoCor = ModoCor.GRUPOS;
    private Botao[] btnCor;

    // aba "Algoritmos"
    private boolean modoAlgoritmo;
    private Algoritmo algoritmo = Algoritmo.BFS;
    private String inicioAlgoritmo;                         // de onde BFS/DFS começam
    private List<RedeSocial.Passo> passos = List.of();
    private int indice = -1;                                // passo em exibição
    private final javax.swing.Timer reprodutor = new javax.swing.Timer(800, e -> avancar());
    private static final int[] ATRASOS = {1800, 1200, 800, 450, 220};

    private final CardLayout cartas = new CardLayout();
    private JPanel areaCartas;
    private Botao abaExplorar, abaAlgoritmos;
    private Botao[] btnAlg;
    private Etiqueta[] chips;
    private JTextArea textoResumo, textoPasso;
    private JLabel rotuloPasso;
    private Codigo codigo;
    private Botao btnInicio, btnAnterior, btnPlay, btnProximo;

    public Janela() {
        super("Rede Social · Grafos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(FUNDO);
        setLayout(new BorderLayout());
        add(painel, BorderLayout.CENTER);
        add(criarLateral(), BorderLayout.EAST);
        setSize(1260, 900);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        painel.sincronizar();
        atualizarSelecao(null);
        escolherAlgoritmo(Algoritmo.BFS, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Janela().setVisible(true));
    }

    // =====================================================================
    //  Ações da aba "Explorar"
    // =====================================================================

    /** Clique numa pessoa (ou no vazio, com null). */
    private void selecionar(String p) {
        if (modoAlgoritmo) { // na aba de algoritmos o clique escolhe onde BFS/DFS começam
            if (p != null && algoritmo != Algoritmo.ARTICULACAO) gerarPassos(p);
            return;
        }
        populares = Set.of();
        if (p == null) {
            sel1 = sel2 = null;
        } else if (sel1 == null || sel2 != null) {
            sel1 = p;
            sel2 = null;
        } else if (p.equals(sel1)) {
            sel1 = null; // clicar de novo na mesma pessoa desmarca
        } else {
            sel2 = p;
        }
        atualizarSelecao(null);
    }

    private void adicionarPessoa() {
        String nome = RedeSocial.normaliza(campoNome.getText());
        if (nome.isEmpty()) {
            mostrar("Digite um nome para cadastrar.");
            return;
        }
        if (!rede.addPessoa(nome)) {
            mostrar(nome + " já está na rede.");
            return;
        }
        campoNome.setText("");
        painel.sincronizar();
        populares = Set.of();
        sel1 = nome;
        sel2 = null;
        atualizarSelecao(nome + " entrou na rede. Clique em outra pessoa e depois em Conectar.");
    }

    private void removerPessoa() {
        if (sel1 == null || sel2 != null) return;
        String p = sel1;
        int antes = rede.grupos().size();
        rede.removePessoa(p);
        sel1 = sel2 = null;
        populares = Set.of();
        painel.sincronizar();
        atualizarSelecao(p + " foi removida." + efeitoGrupos(antes));
    }

    private void conectar() {
        if (sel1 == null || sel2 == null) return;
        int antes = rede.grupos().size();
        rede.addAmizade(sel1, sel2);
        painel.sincronizar();
        atualizarSelecao(sel1 + " e " + sel2 + " agora são amigos." + efeitoGrupos(antes));
    }

    private void desconectar() {
        if (sel1 == null || sel2 == null) return;
        int antes = rede.grupos().size();
        rede.removeAmizade(sel1, sel2);
        painel.sincronizar();
        atualizarSelecao(sel1 + " e " + sel2 + " deixaram de ser amigos." + efeitoGrupos(antes));
    }

    private void reiniciar() {
        rede = RedeSocial.exemplo();
        sel1 = sel2 = null;
        populares = Set.of();
        painel.reiniciar();
        atualizarSelecao("Rede reiniciada com os dados de exemplo.");
    }

    private void maisPopular() {
        List<String> top = rede.maisPopulares();
        if (top.isEmpty()) return;
        sel1 = sel2 = null;
        atualizarSelecao(null);
        populares = new HashSet<>(top);
        mostrar("Mais popular: " + String.join(", ", top) + "\n" + plural(rede.grau(top.get(0)), "amigo", "amigos")
                + ".\n\nO halo dourado marca quem tem mais amigos (maior grau).");
        painel.repaint();
    }

    private void circulos() {
        if (sel1 == null || sel2 != null) return;
        List<List<String>> niveis = rede.circulos(sel1);
        StringBuilder sb = new StringBuilder("Círculos sociais de " + sel1 + "\n");
        if (niveis.isEmpty()) sb.append("\n").append(sel1).append(" ainda não tem amigos.");
        for (int i = 0; i < niveis.size(); i++)
            sb.append('\n').append(grau(i + 1)).append(": ").append(String.join(", ", niveis.get(i)));
        mostrar(sb.toString());
    }

    private void gruposIsolados() {
        List<Set<String>> gs = painel.grupos;
        StringBuilder sb = new StringBuilder(plural(gs.size(), "grupo isolado", "grupos isolados") + ":\n");
        for (int i = 0; i < gs.size(); i++)
            sb.append("\nGrupo ").append(i + 1).append(" (").append(gs.get(i).size()).append("): ")
                    .append(String.join(", ", gs.get(i)));
        mostrar(sb.toString());
    }

    private void mostrarMatriz() {
        JTextArea area = new JTextArea("LISTA DE ADJACÊNCIA\n" + rede.listaAdjacencia()
                + "\nMATRIZ DE ADJACÊNCIA\n" + rede.matrizAdjacencia());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        area.setBackground(CARTAO);
        area.setForeground(TEXTO);
        area.setBorder(new EmptyBorder(16, 18, 16, 18));
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(null);
        JDialog d = new JDialog(this, "Lista e matriz de adjacência", true);
        d.add(sp);
        d.setSize(780, 560);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    /** Recalcula caminho, sugestões, foco e botões a partir da seleção, e atualiza o painel de resultado. */
    private void atualizarSelecao(String aviso) {
        caminho = List.of();
        sugestoes = List.of();
        foco = null;
        arestasFoco = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        if (sel1 == null) {
            sb.append("Clique numa pessoa para ver os amigos e as sugestões.\n\n")
                    .append("Clique em uma segunda pessoa para ver o menor caminho entre as duas.\n\n")
                    .append("Arraste as bolinhas para reorganizar. O tamanho de cada uma mostra quantos amigos tem.");
        } else if (sel2 == null) {
            Set<String> amigos = rede.amigos(sel1);
            sugestoes = rede.sugestoes(sel1);
            sb.append(sel1).append('\n').append(plural(amigos.size(), "amigo", "amigos"))
                    .append(amigos.isEmpty() ? "." : ": " + String.join(", ", amigos)).append("\n\n")
                    .append("Proximidade ").append(numero(rede.proximidade().get(sel1), 2))
                    .append(" · intermediação ").append(numero(rede.intermediacao().get(sel1), 1)).append("\n\n");
            if (sugestoes.isEmpty()) {
                sb.append("Sem sugestões de amizade.");
            } else {
                sb.append("Sugestões (linhas tracejadas):");
                for (var s : sugestoes)
                    sb.append("\n• ").append(s.getKey()).append(" — ")
                            .append(plural(s.getValue(), "amigo em comum", "amigos em comum"));
            }
            foco = new HashSet<>(amigos);
            foco.add(sel1);
            for (var s : sugestoes) foco.add(s.getKey());
            for (String a : amigos) arestasFoco.add(chave(sel1, a));
        } else {
            caminho = rede.caminho(sel1, sel2);
            sb.append(sel1).append(" → ").append(sel2).append("\n\n");
            if (caminho.isEmpty()) {
                sb.append("Não há ligação entre eles: estão em grupos isolados.");
            } else {
                sb.append(String.join(" → ", caminho)).append('\n')
                        .append(grau(caminho.size() - 1)).append(" de separação");
                foco = new HashSet<>(caminho);
                for (int i = 0; i + 1 < caminho.size(); i++) arestasFoco.add(chave(caminho.get(i), caminho.get(i + 1)));
            }
            Set<String> comuns = rede.amigosEmComum(sel1, sel2);
            sb.append("\n\nAmigos em comum: ").append(comuns.isEmpty() ? "nenhum" : String.join(", ", comuns));
        }

        boolean um = sel1 != null && sel2 == null;
        boolean dois = sel1 != null && sel2 != null;
        boolean saoAmigos = dois && rede.amigos(sel1).contains(sel2);
        btnRemover.setEnabled(um);
        btnCirculos.setEnabled(um);
        btnConectar.setEnabled(dois && !saoAmigos);
        btnDesconectar.setEnabled(saoAmigos);

        mostrar(aviso == null ? sb.toString() : aviso + "\n\n" + sb);
        painel.repaint();
    }

    private void mostrar(String texto) {
        resultado.setText(texto);
        resultado.setCaretPosition(0);
    }

    private String efeitoGrupos(int antes) {
        int agora = rede.grupos().size();
        return agora == antes ? "" : "\nA rede agora tem " + plural(agora, "grupo isolado", "grupos isolados") + ".";
    }

    // =====================================================================
    //  Ações da aba "Algoritmos"
    // =====================================================================

    private void mudarModo(boolean algoritmos) {
        if (modoAlgoritmo == algoritmos) return;
        reprodutor.stop();
        String inicio = sel1;
        modoAlgoritmo = algoritmos;
        abaExplorar.setAtivo(!algoritmos);
        abaAlgoritmos.setAtivo(algoritmos);
        cartas.show(areaCartas, algoritmos ? "algoritmo" : "explorar");
        sel1 = sel2 = null;
        populares = Set.of();
        atualizarSelecao(null);
        if (algoritmos) {
            escolherAlgoritmo(algoritmo, inicio);
        } else {
            passos = List.of();
            indice = -1;
        }
        painel.esquentar(30); // a faixa da fila/pilha ocupa a parte de baixo: os nós se reacomodam
        painel.repaint();
    }

    private void escolherAlgoritmo(Algoritmo a, String inicio) {
        algoritmo = a;
        for (Algoritmo x : Algoritmo.values()) btnAlg[x.ordinal()].setAtivo(x == a);
        for (int i = 0; i < chips.length; i++) chips[i].setText(a.chips[i]);
        textoResumo.setText(a.resumo);
        codigo.definir(a.codigo);
        gerarPassos(inicio);
    }

    /** Pede à RedeSocial a lista de passos do algoritmo escolhido e volta ao primeiro. */
    private void gerarPassos(String inicio) {
        reprodutor.stop();
        if (inicio != null) inicioAlgoritmo = inicio;
        List<RedeSocial.Passo> novos = List.of();
        if (algoritmo == Algoritmo.ARTICULACAO) {
            novos = rede.passosArticulacao();
        } else if (inicioAlgoritmo != null && rede.existe(inicioAlgoritmo)) {
            novos = algoritmo == Algoritmo.BFS ? rede.passosBfs(inicioAlgoritmo) : rede.passosDfs(inicioAlgoritmo);
        }
        passos = novos;
        indice = passos.isEmpty() ? -1 : 0;
        atualizarPasso();
    }

    private void avancar() {
        if (indice < passos.size() - 1) indice++;
        if (indice >= passos.size() - 1) reprodutor.stop();
        atualizarPasso();
    }

    private void voltar() {
        reprodutor.stop();
        if (indice > 0) indice--;
        atualizarPasso();
    }

    private void irParaInicio() {
        reprodutor.stop();
        if (!passos.isEmpty()) indice = 0;
        atualizarPasso();
    }

    private void alternarReproducao() {
        if (passos.isEmpty()) return;
        if (reprodutor.isRunning()) {
            reprodutor.stop();
        } else {
            if (indice >= passos.size() - 1) indice = 0; // terminou: recomeça
            reprodutor.setInitialDelay(reprodutor.getDelay()); // senão o 1º passo ignora a velocidade escolhida
            reprodutor.start();
        }
        atualizarPasso();
    }

    private RedeSocial.Passo passoAtual() {
        return modoAlgoritmo && indice >= 0 && indice < passos.size() ? passos.get(indice) : null;
    }

    private void atualizarPasso() {
        boolean tem = indice >= 0 && indice < passos.size();
        RedeSocial.Passo p = tem ? passos.get(indice) : null;
        String dica = algoritmo == Algoritmo.ARTICULACAO ? ""
                : "Clique em uma pessoa no grafo para escolher onde o algoritmo começa.";
        textoPasso.setText(p != null ? p.texto() : dica);
        rotuloPasso.setText(p != null ? "Passo " + (indice + 1) + " de " + passos.size() : "Aguardando o início");
        codigo.marcar(p == null ? -1 : p.linha());
        btnInicio.setEnabled(tem && indice > 0);
        btnAnterior.setEnabled(tem && indice > 0);
        btnProximo.setEnabled(tem && indice < passos.size() - 1);
        btnPlay.setEnabled(tem);
        btnPlay.setIcone(reprodutor.isRunning() ? Icone.PAUSE : Icone.PLAY);
        painel.repaint();
    }

    /** Valor bruto da métrica de cada pessoa (para colorir e para o ranking). */
    private Map<String, Double> valoresDe(ModoCor modo) {
        switch (modo) {
            case PROXIMIDADE:
                return rede.proximidade();
            case INTERMEDIACAO:
                return rede.intermediacao();
            default:
                Map<String, Double> graus = new TreeMap<>();
                for (Set<String> g : rede.grupos())
                    for (String p : g) graus.put(p, (double) rede.grau(p));
                return graus;
        }
    }

    private static String numero(double v, int casas) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%." + casas + "f", v);
    }

    private void colorirPor(ModoCor modo) {
        modoCor = modo;
        for (ModoCor m : ModoCor.values()) btnCor[m.ordinal()].setAtivo(m == modo);
        painel.recalcularEscala();

        StringBuilder sb = new StringBuilder(modo.explicacao);
        if (modo != ModoCor.GRUPOS) {
            List<Map.Entry<String, Double>> ranking = new ArrayList<>(valoresDe(modo).entrySet());
            ranking.sort(Map.Entry.<String, Double>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()));
            sb.append("\n\nMais altos:");
            for (int i = 0; i < Math.min(5, ranking.size()); i++) {
                var e = ranking.get(i);
                sb.append('\n').append(i + 1).append(". ").append(e.getKey()).append(" — ")
                        .append(modo == ModoCor.GRAU ? plural((int) Math.round(e.getValue()), "amigo", "amigos")
                                : numero(e.getValue(), modo == ModoCor.PROXIMIDADE ? 2 : 1));
            }
        }
        mostrar(sb.toString());
        painel.repaint();
    }

    // =====================================================================
    //  Utilidades
    // =====================================================================

    private static String plural(int n, String singular, String plural) {
        return n + " " + (n == 1 ? singular : plural);
    }

    private static String grau(int n) {
        return plural(n, "grau", "graus");
    }

    private static String chave(String a, String b) {
        return a.compareTo(b) < 0 ? a + "|" + b : b + "|" + a;
    }

    private static Color comAlfa(Color c, int alfa) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alfa);
    }

    /** Texto somente leitura, com quebra de linha automática, para os cartões laterais. */
    private static JTextArea criarTexto(int altura) {
        JTextArea t = new JTextArea();
        t.setEditable(false);
        t.setFocusable(false);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setOpaque(false);
        t.setBorder(null);
        t.setFont(new Font(FONTE, Font.PLAIN, 13));
        t.setForeground(TEXTO);
        t.setPreferredSize(new Dimension(LARGURA_TEXTO, altura));
        return t;
    }

    // =====================================================================
    //  Painel lateral
    // =====================================================================

    private JPanel criarLateral() {
        JPanel lateral = new JPanel(new BorderLayout(0, 12));
        lateral.setBackground(FUNDO);
        lateral.setBorder(new EmptyBorder(18, 6, 18, 18));
        lateral.setPreferredSize(new Dimension(350, 0));

        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.add(alinhado(rotulo("Rede Social", 24, TEXTO, true)));
        cabecalho.add(alinhado(rotulo("Grafo de amizades · Estrutura de Dados", 12, TEXTO_FRACO, false)));
        cabecalho.add(Box.createVerticalStrut(12));

        abaExplorar = new Botao("Explorar", NEUTRO);
        abaExplorar.setAtivo(true);
        abaExplorar.addActionListener(e -> mudarModo(false));
        abaAlgoritmos = new Botao("Algoritmos", NEUTRO);
        abaAlgoritmos.addActionListener(e -> mudarModo(true));
        JPanel abas = new JPanel(new GridLayout(1, 2, 8, 0));
        abas.setOpaque(false);
        abas.add(abaExplorar);
        abas.add(abaAlgoritmos);
        abas.setAlignmentX(Component.LEFT_ALIGNMENT);
        abas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cabecalho.add(abas);

        areaCartas = new JPanel(cartas);
        areaCartas.setOpaque(false);
        areaCartas.add(criarPainelExplorar(), "explorar");
        areaCartas.add(criarPainelAlgoritmo(), "algoritmo");

        lateral.add(cabecalho, BorderLayout.NORTH);
        lateral.add(areaCartas, BorderLayout.CENTER);
        return lateral;
    }

    private JPanel criarPainelExplorar() {
        JPanel corpo = new JPanel(new BorderLayout(0, 12));
        corpo.setOpaque(false);

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));

        // pessoas
        Cartao pessoas = new Cartao("Pessoas");
        estilizarCampo(campoNome);
        campoNome.addActionListener(e -> adicionarPessoa());
        Botao btnAdd = new Botao("Adicionar", AZUL);
        btnAdd.addActionListener(e -> adicionarPessoa());
        JPanel linhaAdd = new JPanel(new BorderLayout(8, 0));
        linhaAdd.setOpaque(false);
        linhaAdd.add(campoNome, BorderLayout.CENTER);
        linhaAdd.add(btnAdd, BorderLayout.EAST);
        pessoas.adicionar(linhaAdd);
        pessoas.adicionar(Box.createVerticalStrut(8));
        btnRemover = new Botao("Remover pessoa selecionada", VERMELHO);
        btnRemover.addActionListener(e -> removerPessoa());
        pessoas.adicionar(btnRemover);
        topo.add(pessoas);
        topo.add(Box.createVerticalStrut(10));

        // amizades
        Cartao amizades = new Cartao("Amizades (selecione duas pessoas)");
        btnConectar = new Botao("Conectar", AZUL);
        btnConectar.addActionListener(e -> conectar());
        btnDesconectar = new Botao("Desconectar", VERMELHO);
        btnDesconectar.addActionListener(e -> desconectar());
        JPanel linhaAmizade = new JPanel(new GridLayout(1, 2, 8, 0));
        linhaAmizade.setOpaque(false);
        linhaAmizade.add(btnConectar);
        linhaAmizade.add(btnDesconectar);
        amizades.adicionar(linhaAmizade);
        topo.add(amizades);
        topo.add(Box.createVerticalStrut(10));

        // consultas
        Cartao consultas = new Cartao("Consultas");
        Botao btnPopular = new Botao("Mais popular", NEUTRO);
        btnPopular.addActionListener(e -> maisPopular());
        btnCirculos = new Botao("Círculos sociais", NEUTRO);
        btnCirculos.addActionListener(e -> circulos());
        Botao btnGrupos = new Botao("Grupos isolados", NEUTRO);
        btnGrupos.addActionListener(e -> gruposIsolados());
        Botao btnMatriz = new Botao("Lista e matriz", NEUTRO);
        btnMatriz.addActionListener(e -> mostrarMatriz());
        JPanel grade = new JPanel(new GridLayout(2, 2, 8, 8));
        grade.setOpaque(false);
        grade.add(btnPopular);
        grade.add(btnCirculos);
        grade.add(btnGrupos);
        grade.add(btnMatriz);
        consultas.adicionar(grade);
        topo.add(consultas);
        topo.add(Box.createVerticalStrut(10));

        // o que a cor significa
        Cartao cores = new Cartao("Colorir por");
        btnCor = new Botao[ModoCor.values().length];
        JPanel gradeCores = new JPanel(new GridLayout(2, 2, 8, 8));
        gradeCores.setOpaque(false);
        for (ModoCor m : ModoCor.values()) {
            Botao b = new Botao(m.nome, NEUTRO);
            b.addActionListener(e -> colorirPor(m));
            btnCor[m.ordinal()] = b;
            gradeCores.add(b);
        }
        btnCor[ModoCor.GRUPOS.ordinal()].setAtivo(true);
        cores.adicionar(gradeCores);
        topo.add(cores);

        // resultado
        Cartao cartaoResultado = new Cartao("Resultado");
        resultado.setEditable(false);
        resultado.setLineWrap(true);
        resultado.setWrapStyleWord(true);
        resultado.setOpaque(false);
        resultado.setForeground(TEXTO);
        resultado.setFont(new Font(FONTE, Font.PLAIN, 13));
        resultado.setBorder(new EmptyBorder(0, 0, 0, 6));
        JScrollPane rolagem = new JScrollPane(resultado);
        rolagem.setBorder(null);
        rolagem.setOpaque(false);
        rolagem.getViewport().setOpaque(false);
        estilizarRolagem(rolagem.getVerticalScrollBar());
        cartaoResultado.setLayout(new BorderLayout(0, 8));
        cartaoResultado.removeAll();
        cartaoResultado.add(cartaoResultado.titulo(), BorderLayout.NORTH);
        cartaoResultado.add(rolagem, BorderLayout.CENTER);

        Botao btnReiniciar = new Botao("Reiniciar com dados de exemplo", NEUTRO);
        btnReiniciar.addActionListener(e -> reiniciar());

        corpo.add(topo, BorderLayout.NORTH);
        corpo.add(cartaoResultado, BorderLayout.CENTER);
        corpo.add(btnReiniciar, BorderLayout.SOUTH);
        return corpo;
    }

    /** Aba "Algoritmos": escolha do algoritmo, explicação, pseudocódigo com a linha atual e controles do passo a passo. */
    private JComponent criarPainelAlgoritmo() {
        Coluna coluna = new Coluna();

        // escolha do algoritmo
        Cartao cEscolha = new Cartao("Algoritmo");
        btnAlg = new Botao[Algoritmo.values().length];
        JPanel linhaAlg = new JPanel(new GridLayout(1, btnAlg.length, 6, 0));
        linhaAlg.setOpaque(false);
        for (Algoritmo a : Algoritmo.values()) {
            Botao b = new Botao(a.curto, NEUTRO);
            b.addActionListener(e -> escolherAlgoritmo(a, inicioAlgoritmo));
            btnAlg[a.ordinal()] = b;
            linhaAlg.add(b);
        }
        cEscolha.adicionar(linhaAlg);
        coluna.add(cEscolha);
        coluna.add(Box.createVerticalStrut(10));

        // como funciona
        Cartao cInfo = new Cartao("Como funciona");
        chips = new Etiqueta[3];
        JPanel linhaChips = new JPanel(new GridLayout(1, 3, 6, 0));
        linhaChips.setOpaque(false);
        for (int i = 0; i < chips.length; i++) {
            chips[i] = new Etiqueta();
            linhaChips.add(chips[i]);
        }
        cInfo.adicionar(linhaChips);
        cInfo.adicionar(Box.createVerticalStrut(8));
        textoResumo = criarTexto(104);
        cInfo.adicionar(textoResumo);
        coluna.add(cInfo);
        coluna.add(Box.createVerticalStrut(10));

        // pseudocódigo
        Cartao cCodigo = new Cartao("Pseudocódigo");
        codigo = new Codigo();
        cCodigo.adicionar(codigo);
        coluna.add(cCodigo);
        coluna.add(Box.createVerticalStrut(10));

        // passo a passo
        Cartao cPasso = new Cartao("Passo a passo");
        rotuloPasso = rotulo("", 12, DESTAQUE, true);
        cPasso.adicionar(rotuloPasso);
        cPasso.adicionar(Box.createVerticalStrut(4));
        textoPasso = criarTexto(76);
        cPasso.adicionar(textoPasso);
        cPasso.adicionar(Box.createVerticalStrut(8));

        btnInicio = new Botao(Icone.INICIO, "Voltar ao começo", NEUTRO);
        btnInicio.addActionListener(e -> irParaInicio());
        btnAnterior = new Botao(Icone.ANTERIOR, "Voltar um passo", NEUTRO);
        btnAnterior.addActionListener(e -> voltar());
        btnPlay = new Botao(Icone.PLAY, "Reproduzir / pausar", AZUL);
        btnPlay.addActionListener(e -> alternarReproducao());
        btnProximo = new Botao(Icone.PROXIMO, "Próximo passo", NEUTRO);
        btnProximo.addActionListener(e -> {
            reprodutor.stop();
            avancar();
        });
        JPanel controles = new JPanel(new GridLayout(1, 4, 6, 0));
        controles.setOpaque(false);
        controles.add(btnInicio);
        controles.add(btnAnterior);
        controles.add(btnPlay);
        controles.add(btnProximo);
        cPasso.adicionar(controles);
        cPasso.adicionar(Box.createVerticalStrut(8));

        JSlider velocidade = new JSlider(0, ATRASOS.length - 1, 2);
        velocidade.setOpaque(false);
        velocidade.setFocusable(false);
        velocidade.setUI(new Controle(velocidade));
        velocidade.addChangeListener(e -> reprodutor.setDelay(ATRASOS[velocidade.getValue()]));
        reprodutor.setDelay(ATRASOS[velocidade.getValue()]);
        JPanel linhaVel = new JPanel(new BorderLayout(8, 0));
        linhaVel.setOpaque(false);
        linhaVel.add(rotulo("Velocidade", 12, TEXTO_FRACO, false), BorderLayout.WEST);
        linhaVel.add(velocidade, BorderLayout.CENTER);
        cPasso.adicionar(linhaVel);
        coluna.add(cPasso);

        JScrollPane rolagem = new JScrollPane(coluna, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.setBorder(null);
        rolagem.setOpaque(false);
        rolagem.getViewport().setOpaque(false);
        estilizarRolagem(rolagem.getVerticalScrollBar());
        return rolagem;
    }

    private static JLabel rotulo(String texto, int tamanho, Color cor, boolean negrito) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font(FONTE, negrito ? Font.BOLD : Font.PLAIN, tamanho));
        l.setForeground(cor);
        return l;
    }

    private static <T extends JComponent> T alinhado(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private static void estilizarCampo(JTextField campo) {
        campo.setFont(new Font(FONTE, Font.PLAIN, 14));
        campo.setBackground(FUNDO);
        campo.setForeground(TEXTO);
        campo.setCaretColor(TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA, 1), new EmptyBorder(6, 10, 6, 10)));
    }

    private static void estilizarRolagem(JScrollBar barra) {
        barra.setPreferredSize(new Dimension(8, 0));
        barra.setOpaque(false);
        barra.setUnitIncrement(18);
        barra.setUI(new BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return botaoVazio();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return botaoVazio();
            }

            private JButton botaoVazio() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                // sem trilho: só a barrinha
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x444C68));
                g2.fillRoundRect(r.x + 1, r.y, r.width - 2, r.height, 6, 6);
                g2.dispose();
            }
        });
    }

    /** Controle deslizante no tema escuro (o visual padrão do Swing é claro). */
    private static class Controle extends javax.swing.plaf.basic.BasicSliderUI {
        Controle(JSlider s) {
            super(s);
        }

        @Override
        protected Dimension getThumbSize() {
            return new Dimension(16, 16);
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cy = trackRect.y + trackRect.height / 2;
            g2.setColor(new Color(0x353C55));
            g2.fillRoundRect(trackRect.x, cy - 2, trackRect.width, 4, 4, 4);
            g2.setColor(AZUL);
            g2.fillRoundRect(trackRect.x, cy - 2, thumbRect.x + thumbRect.width / 2 - trackRect.x, 4, 4, 4);
            g2.dispose();
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width - 1, thumbRect.height - 1);
            g2.dispose();
        }

        @Override
        public void paintFocus(Graphics g) {
            // sem contorno de foco
        }
    }

    /** Coluna rolável que acompanha a largura da janela lateral. */
    private static class Coluna extends JPanel implements Scrollable {
        Coluna() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) {
            return 120;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /** Cartão de cantos arredondados com título em caixa alta. */
    private static class Cartao extends JPanel {
        private final JLabel titulo;

        Cartao(String texto) {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(12, 14, 14, 14));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            titulo = rotulo(texto.toUpperCase(), 11, TEXTO_FRACO, true);
            titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(titulo);
            add(Box.createVerticalStrut(8));
        }

        JLabel titulo() {
            return titulo;
        }

        /** Adiciona um componente que ocupa toda a largura e só a altura de que precisa. */
        void adicionar(Component c) {
            if (c instanceof JComponent jc) {
                jc.setAlignmentX(Component.LEFT_ALIGNMENT);
                jc.setMaximumSize(new Dimension(Integer.MAX_VALUE, jc.getPreferredSize().height));
            }
            add(c);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARTAO);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(BORDA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Pastilha com um dado curto do algoritmo (estrutura usada, complexidade...). */
    private static class Etiqueta extends JLabel {
        Etiqueta() {
            super("", SwingConstants.CENTER);
            setOpaque(false);
            setFont(new Font(FONTE, Font.BOLD, 11));
            setForeground(TEXTO);
            setBorder(new EmptyBorder(7, 4, 7, 4));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x2A3046));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Pseudocódigo com a linha em execução destacada. */
    private static class Codigo extends JPanel {
        private static final int ALTURA_LINHA = 20;
        private static final int LINHAS_MAX = 7;
        private String[] linhas = {};
        private int atual = -1;

        Codigo() {
            setOpaque(false);
            setPreferredSize(new Dimension(LARGURA_TEXTO, ALTURA_LINHA * LINHAS_MAX + 6));
        }

        void definir(String[] novas) {
            linhas = novas;
            atual = -1;
            repaint();
        }

        void marcar(int linha) {
            atual = linha;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            FontMetrics fm = g2.getFontMetrics();
            for (int i = 0; i < linhas.length; i++) {
                int y = 3 + i * ALTURA_LINHA;
                boolean ativa = i == atual;
                if (ativa) {
                    g2.setColor(comAlfa(DESTAQUE, 38));
                    g2.fillRoundRect(0, y, getWidth(), ALTURA_LINHA - 2, 8, 8);
                    g2.setColor(DESTAQUE);
                    g2.fillRoundRect(0, y + 2, 3, ALTURA_LINHA - 6, 3, 3);
                }
                int base = y + (ALTURA_LINHA - 2 - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(ativa ? DESTAQUE : new Color(0x59607A));
                g2.drawString(String.valueOf(i), 9, base);
                g2.setColor(ativa ? Color.WHITE : new Color(0xB4BAD0));
                g2.drawString(linhas[i], 26, base);
            }
            g2.dispose();
        }
    }

    private enum Icone {INICIO, ANTERIOR, PLAY, PAUSE, PROXIMO}

    /** Botão arredondado que se desenha sozinho (o visual padrão do Swing ignora cores no Windows). */
    private static class Botao extends JButton {
        private final Color base;
        private boolean ativo;
        private Icone icone;

        Botao(String texto, Color base) {
            super(texto);
            this.base = base;
            iniciar();
        }

        Botao(Icone icone, String dica, Color base) {
            super();
            this.base = base;
            this.icone = icone;
            setToolTipText(dica);
            iniciar();
        }

        private void iniciar() {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setOpaque(false);
            setFont(new Font(FONTE, Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 33));
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        void setAtivo(boolean ativo) {
            this.ativo = ativo;
            repaint();
        }

        void setIcone(Icone icone) {
            this.icone = icone;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Color cor = ativo ? AZUL : base;
            Color fundo;
            if (!isEnabled()) fundo = new Color(0x222739);
            else if (getModel().isPressed()) fundo = cor.darker();
            else if (getModel().isRollover()) fundo = cor.brighter();
            else fundo = cor;
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(isEnabled() ? Color.WHITE : new Color(0x5C637A));
            if (icone != null) {
                desenharIcone(g2);
            } else {
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
            }
            g2.dispose();
        }

        private void desenharIcone(Graphics2D g2) {
            double cx = getWidth() / 2.0, cy = getHeight() / 2.0, s = 7;
            switch (icone) {
                case PLAY -> g2.fill(seta(cx - s * 0.7, cy, s, true));
                case PAUSE -> {
                    g2.fill(new RoundRectangle2D.Double(cx - s * 0.95, cy - s, s * 0.7, s * 2, 2, 2));
                    g2.fill(new RoundRectangle2D.Double(cx + s * 0.25, cy - s, s * 0.7, s * 2, 2, 2));
                }
                case PROXIMO -> {
                    g2.fill(seta(cx - s * 0.9, cy, s * 0.9, true));
                    g2.fill(new RoundRectangle2D.Double(cx + s * 0.75, cy - s * 0.9, s * 0.4, s * 1.8, 2, 2));
                }
                case ANTERIOR -> g2.fill(seta(cx + s * 0.7, cy, s, false));
                case INICIO -> {
                    g2.fill(seta(cx + s * 0.9, cy, s * 0.9, false));
                    g2.fill(new RoundRectangle2D.Double(cx - s * 1.15, cy - s * 0.9, s * 0.4, s * 1.8, 2, 2));
                }
            }
        }

        private static Path2D seta(double x, double cy, double s, boolean direita) {
            double ponta = direita ? x + s * 1.6 : x - s * 1.6;
            Path2D p = new Path2D.Double();
            p.moveTo(x, cy - s);
            p.lineTo(ponta, cy);
            p.lineTo(x, cy + s);
            p.closePath();
            return p;
        }
    }

    // =====================================================================
    //  Painel do grafo: desenho, layout automático e mouse
    // =====================================================================

    private class Painel extends JPanel {

        private static final double MARGEM = 46;
        private static final double MARGEM_TOPO = 66; // deixa espaço para a linha de contagem no canto
        private static final double TEMP_MIN = 0.6;

        class No {
            String nome;
            double x, y, dx, dy;
            double rigidez; // quão rápido a força muda quando o nó se move (limita o tamanho do passo)
            boolean fixo; // true enquanto o usuário arrasta
        }

        final Map<String, No> nos = new TreeMap<>();
        Map<String, Integer> corNo = new HashMap<>();
        List<Set<String>> grupos = List.of();
        private Map<String, Double> escala = new HashMap<>(); // métrica normalizada de cada pessoa (modo de cor ≠ grupos)

        private final Random sorteio = new Random(7);
        private double temp = 0; // "temperatura" do layout: quanto os nós ainda podem se mexer
        private No arrastando;
        private Point inicio;
        private boolean moveu;
        private RedeSocial.Passo cachePasso;          // cores dos grupos do passo de articulação (calculadas uma vez)
        private Map<String, Integer> cacheCores = Map.of();

        Painel() {
            setBackground(FUNDO);
            setPreferredSize(new Dimension(880, 700));
            new javax.swing.Timer(16, e -> {
                if (temp > TEMP_MIN) {
                    passo();
                    repaint();
                }
            }).start();

            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    arrastando = noEm(e.getX(), e.getY());
                    inicio = e.getPoint();
                    moveu = false;
                    if (arrastando != null) arrastando.fixo = true;
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (arrastando == null) return;
                    if (!moveu && inicio.distance(e.getPoint()) < 4) return;
                    moveu = true;
                    arrastando.x = limitar(e.getX(), MARGEM, getWidth() - MARGEM);
                    arrastando.y = limitar(e.getY(), MARGEM_TOPO, getHeight() - margemBase());
                    esquentar(25);
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (arrastando != null) arrastando.fixo = false;
                    if (!moveu) selecionar(arrastando == null ? null : arrastando.nome);
                    arrastando = null;
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(
                            noEm(e.getX(), e.getY()) != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        // ---------- sincronização com o grafo ----------

        void reiniciar() {
            nos.clear();
            corNo.clear();
            sincronizar();
        }

        /** Faz os nós refletirem as pessoas que existem na rede e recalcula as cores dos grupos. */
        void sincronizar() {
            List<Set<String>> gs = rede.grupos();
            Set<String> todos = new TreeSet<>();
            for (Set<String> g : gs) todos.addAll(g);
            nos.keySet().removeIf(n -> !todos.contains(n));

            double w = largura(), h = altura();
            for (String p : todos)
                if (!nos.containsKey(p)) {
                    No n = new No();
                    n.nome = p;
                    double ang = sorteio.nextDouble() * 2 * Math.PI;
                    double r = Math.min(w, h) * (0.15 + 0.2 * sorteio.nextDouble());
                    n.x = w / 2 + Math.cos(ang) * r;
                    n.y = h / 2 + Math.sin(ang) * r;
                    nos.put(p, n);
                }
            List<Set<String>> ordenados = new ArrayList<>(gs);
            ordenados.sort((a, b) -> b.size() - a.size());
            corNo = escolherCores(ordenados, corNo);
            grupos = ordenados;
            recalcularEscala();
            esquentar(40);
            repaint();
        }

        /**
         * Cada grupo ganha uma cor. Quem já tinha cor tenta manter a mesma, para as cores não
         * "piscarem" quando a rede muda (e para um grupo que se parte manter a cor original numa das metades).
         */
        private Map<String, Integer> escolherCores(List<Set<String>> ordenados, Map<String, Integer> anterior) {
            Map<String, Integer> novo = new HashMap<>();
            Set<Integer> usadas = new HashSet<>();
            for (Set<String> g : ordenados) {
                Map<Integer, Integer> votos = new HashMap<>();
                for (String p : g) {
                    Integer c = anterior.get(p);
                    if (c != null) votos.merge(c, 1, Integer::sum);
                }
                int escolhida = -1, melhor = 0;
                for (var v : votos.entrySet())
                    if (!usadas.contains(v.getKey()) && v.getValue() > melhor) {
                        escolhida = v.getKey();
                        melhor = v.getValue();
                    }
                if (escolhida < 0) {
                    escolhida = 0;
                    while (usadas.contains(escolhida)) escolhida++;
                }
                usadas.add(escolhida);
                for (String p : g) novo.put(p, escolhida);
            }
            return novo;
        }

        /** Cores dos grupos que sobram quando o passo de articulação "tira" uma pessoa. */
        private Map<String, Integer> coresDoPasso(RedeSocial.Passo p) {
            if (p != cachePasso) {
                Map<Integer, Set<String>> porGrupo = new TreeMap<>();
                for (var e : p.grupos().entrySet())
                    porGrupo.computeIfAbsent(e.getValue(), k -> new TreeSet<>()).add(e.getKey());
                List<Set<String>> ordenados = new ArrayList<>(porGrupo.values());
                ordenados.sort((a, b) -> b.size() - a.size());
                cacheCores = escolherCores(ordenados, corNo);
                cachePasso = p;
            }
            return cacheCores;
        }

        // ---------- layout automático (força: nós se repelem, amigos se atraem) ----------

        void esquentar(double t) {
            temp = Math.max(temp, t);
        }

        private double largura() {
            return getWidth() > 0 ? getWidth() : 880;
        }

        private double altura() {
            return getHeight() > 0 ? getHeight() : 700;
        }

        /** Espaço reservado embaixo: na aba de algoritmos cabe a faixa da fila/pilha e a legenda. */
        private double margemBase() {
            return modoAlgoritmo ? 175 : 60;
        }

        private void passo() {
            No[] a = nos.values().toArray(new No[0]);
            int n = a.length;
            if (n == 0) return;
            double w = largura(), h = altura();
            double k = Math.min(115, 0.5 * Math.sqrt(w * h / n)); // distância ideal entre amigos
            double corte = 3 * k;                                  // além disso, ninguém se repele

            for (No u : a) {
                u.dx = u.dy = 0;
                u.rigidez = 0.2; // a gravidade já conta
            }

            for (int i = 0; i < n; i++)
                for (int j = i + 1; j < n; j++) {
                    double ddx = a[i].x - a[j].x, ddy = a[i].y - a[j].y;
                    double d = Math.max(Math.hypot(ddx, ddy), 1);
                    if (d > corte) continue;
                    double suave = 1 - (d / corte) * (d / corte); // força vai a zero aos poucos, sem "degrau"
                    double f = k * k / d * suave * suave;
                    a[i].dx += ddx / d * f;
                    a[i].dy += ddy / d * f;
                    a[j].dx -= ddx / d * f;
                    a[j].dy -= ddy / d * f;
                    double r = k * k / (Math.max(d, 10) * Math.max(d, 10)); // derivada da repulsão
                    a[i].rigidez += r;
                    a[j].rigidez += r;
                }

            for (No u : a)
                for (String v : rede.amigos(u.nome)) {
                    No o = nos.get(v);
                    if (o == null || u.nome.compareTo(v) >= 0) continue;
                    double ddx = u.x - o.x, ddy = u.y - o.y;
                    double d = Math.max(Math.hypot(ddx, ddy), 1);
                    double f = d * d / k;
                    u.dx -= ddx / d * f;
                    u.dy -= ddy / d * f;
                    o.dx += ddx / d * f;
                    o.dy += ddy / d * f;
                    double r = 2 * d / k; // derivada da atração
                    u.rigidez += r;
                    o.rigidez += r;
                }

            double maiorPasso = 0;
            double centroY = (MARGEM_TOPO + h - margemBase()) / 2;
            for (No u : a) {
                u.dx += (w / 2 - u.x) * 0.2; // gravidade leve para o centro da área livre
                u.dy += (centroY - u.y) * 0.2;
                if (u.fixo) continue;
                double len = Math.hypot(u.dx, u.dy);
                if (len < 1e-9) continue;
                // O passo é proporcional a 1/rigidez: um nó muito "preso" (muitos amigos, ou muito perto de
                // outros) anda pouco por quadro. Com passo fixo ele passaria do equilíbrio e ficaria tremendo.
                double deslocamento = Math.min(len * 0.6 / u.rigidez, temp);
                maiorPasso = Math.max(maiorPasso, deslocamento);
                u.x = limitar(u.x + u.dx / len * deslocamento, MARGEM, w - MARGEM);
                u.y = limitar(u.y + u.dy / len * deslocamento, MARGEM_TOPO, h - margemBase());
            }
            temp *= 0.99;
            if (maiorPasso < 0.05) temp = 0; // ninguém se move mais de forma perceptível: o layout assentou
        }

        private double limitar(double v, double min, double max) {
            return Math.max(min, Math.min(max, v));
        }

        // ---------- geometria ----------

        private double raio(String p) {
            return Math.min(34, 15 + 3.5 * rede.grau(p));
        }

        private No noEm(int x, int y) {
            No achado = null;
            for (No u : nos.values())
                if (Math.hypot(u.x - x, u.y - y) <= raio(u.nome) + 4) achado = u;
            return achado;
        }

        /** Cor do grupo isolado da pessoa. */
        private Color corGrupo(String p) {
            return PALETA[corNo.getOrDefault(p, 0) % PALETA.length];
        }

        /** Cor da bolinha: pela métrica escolhida em "Explorar"; nos algoritmos, sempre pelo grupo. */
        private Color cor(String p) {
            if (!modoAlgoritmo && modoCor != ModoCor.GRUPOS) return corEscala(escala.getOrDefault(p, 0.0));
            return corGrupo(p);
        }

        /** Valor da métrica de cada pessoa, esticado para 0..1 (mínimo = 0, máximo = 1). */
        void recalcularEscala() {
            escala = new HashMap<>();
            if (modoCor == ModoCor.GRUPOS || nos.isEmpty()) return;
            Map<String, Double> v = valoresDe(modoCor);
            double min = Collections.min(v.values()), max = Collections.max(v.values());
            for (var e : v.entrySet())
                escala.put(e.getKey(), max - min < 1e-12 ? 0.5 : (e.getValue() - min) / (max - min));
        }

        // ---------- desenho ----------

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            int w = getWidth(), h = getHeight();

            g.setPaint(new GradientPaint(0, 0, new Color(0x151A2B), 0, h, FUNDO));
            g.fillRect(0, 0, w, h);
            g.setColor(new Color(255, 255, 255, 14));
            for (int x = 20; x < w; x += 32)
                for (int y = 20; y < h; y += 32) g.fillOval(x - 1, y - 1, 2, 2);

            RedeSocial.Passo p = passoAtual();
            if (p != null) {
                desenharArestasAlg(g, p);
                desenharNosAlg(g, p);
                desenharCabecalhoAlg(g);
                desenharEstrutura(g, w, h, p);
                desenharLegendaAlg(g, h);
            } else {
                desenharArestas(g);
                desenharSugestoes(g);
                desenharNos(g);
                desenharLegenda(g, h);
                if (modoAlgoritmo) {
                    g.setFont(new Font(FONTE, Font.BOLD, 14));
                    g.setColor(DESTAQUE);
                    g.drawString("Clique em uma pessoa para escolher onde o " + algoritmo.curto + " começa.", 22, h - 150);
                }
            }
            g.dispose();
        }

        // --- modo "Explorar" ---

        private void desenharArestas(Graphics2D g) {
            for (No u : nos.values())
                for (String v : rede.amigos(u.nome)) {
                    No o = nos.get(v);
                    if (o == null || u.nome.compareTo(v) >= 0) continue;
                    boolean realce = arestasFoco.contains(chave(u.nome, v));
                    boolean esmaece = foco != null && !realce;
                    Line2D linha = new Line2D.Double(u.x, u.y, o.x, o.y);
                    if (realce && !caminho.isEmpty()) {
                        g.setColor(comAlfa(DESTAQUE, 55));
                        g.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g.draw(linha);
                        g.setColor(DESTAQUE);
                        g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    } else {
                        g.setColor(comAlfa(cor(u.nome), esmaece ? 40 : realce ? 230 : 125));
                        g.setStroke(new BasicStroke(realce ? 3f : 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    }
                    g.draw(linha);
                }
        }

        private void desenharSugestoes(Graphics2D g) {
            if (sel1 == null || sel2 != null || !nos.containsKey(sel1)) return;
            No a = nos.get(sel1);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{7f, 7f}, 0f));
            for (var s : sugestoes) {
                No b = nos.get(s.getKey());
                if (b == null) continue;
                g.setColor(comAlfa(DESTAQUE, 170));
                g.draw(new Line2D.Double(a.x, a.y, b.x, b.y));
            }
        }

        private void desenharNos(Graphics2D g) {
            for (No u : nos.values()) {
                double r = raio(u.nome);
                Color c = cor(u.nome);
                boolean esmaece = foco != null && !foco.contains(u.nome);
                Composite original = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, esmaece ? 0.3f : 1f));

                corpo(g, u, r, c);
                if (populares.contains(u.nome)) {
                    g.setColor(DESTAQUE);
                    g.setStroke(new BasicStroke(3f));
                    g.draw(new Ellipse2D.Double(u.x - r - 6, u.y - r - 6, 2 * (r + 6), 2 * (r + 6)));
                }
                boolean selecionado = u.nome.equals(sel1) || u.nome.equals(sel2);
                if (selecionado || caminho.contains(u.nome)) {
                    g.setColor(selecionado ? Color.WHITE : DESTAQUE);
                    g.setStroke(new BasicStroke(3f));
                    g.draw(new Ellipse2D.Double(u.x - r - 3, u.y - r - 3, 2 * (r + 3), 2 * (r + 3)));
                }
                textoCentro(g, u, String.valueOf(rede.grau(u.nome)));
                g.setComposite(original);
                nome(g, u, r, esmaece);
            }
        }

        private void desenharLegenda(Graphics2D g, int h) {
            int amizades = 0;
            for (No u : nos.values()) amizades += rede.grau(u.nome);
            g.setFont(new Font(FONTE, Font.PLAIN, 12));
            g.setColor(TEXTO_FRACO);
            g.drawString(plural(nos.size(), "pessoa", "pessoas") + " · " + plural(amizades / 2, "amizade", "amizades")
                    + " · " + plural(grupos.size(), "grupo", "grupos"), 22, 28);

            if (modoCor != ModoCor.GRUPOS) {
                desenharLegendaEscala(g, h);
                return;
            }
            int linhas = Math.min(grupos.size(), 8);
            int y = h - 20 - (linhas - 1) * 22;
            for (int i = 0; i < linhas; i++, y += 22) {
                Set<String> grupo = grupos.get(i);
                g.setColor(cor(grupo.iterator().next()));
                g.fill(new Ellipse2D.Double(22, y - 10, 12, 12));
                g.setColor(TEXTO_FRACO);
                g.drawString("Grupo " + (i + 1) + " · " + plural(grupo.size(), "pessoa", "pessoas"), 42, y);
            }
        }

        /** Barra de cores da métrica: do menor ao maior valor. */
        private void desenharLegendaEscala(Graphics2D g, int h) {
            int x = 22, y = h - 44, largura = 180, altura = 10;
            g.setFont(new Font(FONTE, Font.BOLD, 11));
            g.setColor(TEXTO_FRACO);
            g.drawString("COR = " + modoCor.nome.toUpperCase(), x, y - 10);
            for (int i = 0; i < largura; i++) {
                g.setColor(corEscala(i / (double) (largura - 1)));
                g.fillRect(x + i, y, 1, altura);
            }
            g.setFont(new Font(FONTE, Font.PLAIN, 12));
            g.setColor(TEXTO_FRACO);
            g.drawString("menor", x, y + altura + 16);
            String maior = "maior";
            g.drawString(maior, x + largura - g.getFontMetrics().stringWidth(maior), y + altura + 16);
        }

        // --- modo "Algoritmos" ---

        private Set<String> vistosDe(RedeSocial.Passo p) {
            Set<String> vistos = new HashSet<>(p.estrutura());
            vistos.addAll(p.concluidos());
            if (p.atual() != null) vistos.add(p.atual());
            return vistos;
        }

        private Color corAlg(String nome, Map<String, Integer> coresAp) {
            if (coresAp != null && coresAp.containsKey(nome)) return PALETA[coresAp.get(nome) % PALETA.length];
            return cor(nome);
        }

        private void desenharArestasAlg(Graphics2D g, RedeSocial.Passo p) {
            boolean ap = algoritmo == Algoritmo.ARTICULACAO;
            Map<String, Integer> coresAp = ap && p.grupos() != null ? coresDoPasso(p) : null;
            Set<String> vistos = vistosDe(p);
            for (No u : nos.values())
                for (String v : rede.amigos(u.nome)) {
                    No o = nos.get(v);
                    if (o == null || u.nome.compareTo(v) >= 0) continue;
                    Line2D linha = new Line2D.Double(u.x, u.y, o.x, o.y);
                    if (ap) {
                        boolean tocaAtual = u.nome.equals(p.atual()) || v.equals(p.atual());
                        g.setColor(comAlfa(corAlg(u.nome, coresAp), tocaAtual ? 60 : 160));
                        g.setStroke(tocaAtual
                                ? new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{4f, 6f}, 0f)
                                : new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g.draw(linha);
                        continue;
                    }
                    boolean arvore = v.equals(p.pai().get(u.nome)) || u.nome.equals(p.pai().get(v));
                    boolean exame = p.atual() != null && p.alvo() != null
                            && ((u.nome.equals(p.atual()) && v.equals(p.alvo()))
                            || (v.equals(p.atual()) && u.nome.equals(p.alvo())));
                    if (exame) {
                        g.setColor(comAlfa(DESTAQUE, 60));
                        g.setStroke(new BasicStroke(11f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g.draw(linha);
                        g.setColor(DESTAQUE);
                        g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    } else if (arvore) {
                        g.setColor(comAlfa(TEXTO, 225));
                        g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    } else {
                        boolean ativa = vistos.contains(u.nome) && vistos.contains(v);
                        g.setColor(comAlfa(cor(u.nome), ativa ? 90 : 28));
                        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    }
                    g.draw(linha);
                }
        }

        private void desenharNosAlg(Graphics2D g, RedeSocial.Passo p) {
            boolean ap = algoritmo == Algoritmo.ARTICULACAO;
            Map<String, Integer> coresAp = ap && p.grupos() != null ? coresDoPasso(p) : null;
            Set<String> vistos = vistosDe(p);
            for (No u : nos.values()) {
                double r = raio(u.nome);
                boolean atual = u.nome.equals(p.atual());
                boolean espera = p.estrutura().contains(u.nome) && !atual;
                boolean fantasma = ap && atual;                // pessoa "tirada" no teste de articulação
                boolean achado = p.destaques().contains(u.nome);
                boolean alcancado = ap || vistos.contains(u.nome);
                Color c = corAlg(u.nome, coresAp);

                Composite original = g.getComposite();
                float alfa = fantasma ? 0.3f : alcancado ? 1f : 0.25f;
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
                corpo(g, u, r, c);
                if (fantasma) {
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{5f, 5f}, 0f));
                    g.draw(new Ellipse2D.Double(u.x - r - 3, u.y - r - 3, 2 * (r + 3), 2 * (r + 3)));
                }
                if (achado) {
                    g.setColor(comAlfa(ALERTA, 70));
                    g.setStroke(new BasicStroke(9f));
                    g.draw(new Ellipse2D.Double(u.x - r - 5, u.y - r - 5, 2 * (r + 5), 2 * (r + 5)));
                    g.setColor(ALERTA);
                    g.setStroke(new BasicStroke(3.5f));
                    g.draw(new Ellipse2D.Double(u.x - r - 5, u.y - r - 5, 2 * (r + 5), 2 * (r + 5)));
                }
                if (!ap && atual) {
                    g.setColor(comAlfa(DESTAQUE, 70));
                    g.setStroke(new BasicStroke(10f));
                    g.draw(new Ellipse2D.Double(u.x - r - 4, u.y - r - 4, 2 * (r + 4), 2 * (r + 4)));
                    g.setColor(DESTAQUE);
                    g.setStroke(new BasicStroke(4f));
                    g.draw(new Ellipse2D.Double(u.x - r - 4, u.y - r - 4, 2 * (r + 4), 2 * (r + 4)));
                } else if (espera) {
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(2.5f));
                    g.draw(new Ellipse2D.Double(u.x - r - 3, u.y - r - 3, 2 * (r + 3), 2 * (r + 3)));
                }
                String texto = ap ? String.valueOf(rede.grau(u.nome))
                        : p.rotulo().containsKey(u.nome) ? String.valueOf(p.rotulo().get(u.nome)) : "";
                textoCentro(g, u, texto);
                g.setComposite(original);
                nome(g, u, r, !alcancado);
            }
        }

        private void desenharCabecalhoAlg(Graphics2D g) {
            g.setFont(new Font(FONTE, Font.BOLD, 14));
            g.setColor(TEXTO);
            String titulo = algoritmo.nome;
            if (algoritmo != Algoritmo.ARTICULACAO && inicioAlgoritmo != null) titulo += " · início: " + inicioAlgoritmo;
            g.drawString(titulo, 22, 28);
            g.setFont(new Font(FONTE, Font.PLAIN, 12));
            g.setColor(TEXTO_FRACO);
            g.drawString(algoritmo == Algoritmo.ARTICULACAO
                    ? "A cada teste, os grupos que sobram sem a pessoa tirada aparecem coloridos."
                    : algoritmo == Algoritmo.BFS ? "O número dentro de cada bolinha é a distância até o início."
                    : "O número dentro de cada bolinha é a ordem de visita.", 22, 46);
        }

        /** Faixa de baixo: a fila (BFS), a pilha (DFS) ou os pontos de articulação já encontrados. */
        private void desenharEstrutura(Graphics2D g, int w, int h, RedeSocial.Passo p) {
            boolean ap = algoritmo == Algoritmo.ARTICULACAO;
            boolean fila = algoritmo == Algoritmo.BFS;
            int y = h - 150;
            g.setFont(new Font(FONTE, Font.BOLD, 11));
            g.setColor(TEXTO_FRACO);
            g.drawString(ap ? "PONTOS DE ARTICULAÇÃO ENCONTRADOS"
                    : fila ? "FILA  ·  sai quem chegou primeiro" : "PILHA  ·  sai quem entrou por último", 22, y);

            List<String> itens = ap ? new ArrayList<>(p.destaques()) : p.estrutura();
            int x = 22, topo = y + 12, altura = 34;
            if (itens.isEmpty()) {
                g.setFont(new Font(FONTE, Font.ITALIC, 13));
                g.setColor(TEXTO_FRACO);
                g.drawString(ap ? "nenhum ainda" : "vazia", x, topo + 22);
                return;
            }
            g.setFont(new Font(FONTE, Font.BOLD, 13));
            FontMetrics fm = g.getFontMetrics();
            for (int i = 0; i < itens.size(); i++) {
                String s = itens.get(i);
                int largura = fm.stringWidth(s) + 26;
                if (x + largura > w - 30) {
                    g.setColor(TEXTO_FRACO);
                    g.drawString("…", x, topo + 22);
                    break;
                }
                RoundRectangle2D bloco = new RoundRectangle2D.Double(x, topo, largura, altura, 12, 12);
                g.setColor(ap ? ALERTA : cor(s));
                g.fill(bloco);
                boolean proximo = !ap && (fila ? i == 0 : i == itens.size() - 1);
                if (proximo) {
                    g.setColor(DESTAQUE);
                    g.setStroke(new BasicStroke(3f));
                    g.draw(bloco);
                }
                g.setColor(new Color(0x0F1117));
                g.drawString(s, x + 13, topo + 22);
                if (!ap) {
                    String legenda = null;
                    if (fila) legenda = i == 0 ? "frente" : i == itens.size() - 1 ? "fim" : null;
                    else legenda = i == itens.size() - 1 ? "topo" : i == 0 ? "base" : null;
                    if (legenda != null) {
                        g.setFont(new Font(FONTE, Font.PLAIN, 11));
                        g.setColor(proximo ? DESTAQUE : TEXTO_FRACO);
                        g.drawString(legenda, x + (largura - g.getFontMetrics().stringWidth(legenda)) / 2, topo + altura + 15);
                        g.setFont(new Font(FONTE, Font.BOLD, 13));
                    }
                }
                x += largura + 8;
            }
        }

        private void desenharLegendaAlg(Graphics2D g, int h) {
            boolean ap = algoritmo == Algoritmo.ARTICULACAO;
            g.setFont(new Font(FONTE, Font.PLAIN, 12));
            int y1 = h - 52, y2 = h - 30;
            if (ap) {
                marcador(g, 22, y1, ALERTA, true, "anel vermelho: ponto de articulação");
                marcador(g, 300, y1, Color.WHITE, false, "pontilhado: pessoa tirada no teste");
                g.setColor(TEXTO_FRACO);
                g.drawString("cores: os grupos que sobram sem ela", 22, y2 + 4);
            } else {
                marcador(g, 22, y1, DESTAQUE, true, "sendo processada agora");
                marcador(g, 230, y1, Color.WHITE, true, "esperando na " + (algoritmo == Algoritmo.BFS ? "fila" : "pilha"));
                marcador(g, 22, y2, TEXTO, false, "linha clara: caminho já percorrido");
                g.setColor(TEXTO_FRACO);
                g.drawString("apagada: ainda não alcançada", 230, y2 + 4);
            }
        }

        private void marcador(Graphics2D g, int x, int y, Color cor, boolean anel, String texto) {
            g.setColor(cor);
            if (anel) {
                g.setStroke(new BasicStroke(2.5f));
                g.draw(new Ellipse2D.Double(x, y - 10, 12, 12));
            } else {
                g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{3f, 4f}, 0f));
                g.draw(new Line2D.Double(x, y - 4, x + 14, y - 4));
            }
            g.setColor(TEXTO_FRACO);
            g.drawString(texto, x + 22, y);
        }

        // --- partes comuns dos dois modos ---

        /** Brilho e corpo (esfera com gradiente) da bolinha. */
        private void corpo(Graphics2D g, No u, double r, Color c) {
            g.setColor(comAlfa(c, 45));
            g.fill(new Ellipse2D.Double(u.x - r - 7, u.y - r - 7, 2 * (r + 7), 2 * (r + 7)));
            g.setPaint(new RadialGradientPaint(new Point2D.Double(u.x - r * 0.3, u.y - r * 0.35), (float) (r * 1.3),
                    new float[]{0f, 1f}, new Color[]{c.brighter(), c.darker()}));
            g.fill(new Ellipse2D.Double(u.x - r, u.y - r, 2 * r, 2 * r));
        }

        private void textoCentro(Graphics2D g, No u, String texto) {
            if (texto.isEmpty()) return;
            g.setFont(new Font(FONTE, Font.BOLD, 13));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(new Color(0x0F1117));
            g.drawString(texto, (float) (u.x - fm.stringWidth(texto) / 2.0), (float) (u.y + fm.getAscent() / 2.0 - 1));
        }

        private void nome(Graphics2D g, No u, double r, boolean esmaece) {
            g.setFont(new Font(FONTE, Font.BOLD, 12));
            FontMetrics fm = g.getFontMetrics();
            float tx = (float) (u.x - fm.stringWidth(u.nome) / 2.0);
            float ty = (float) (u.y + r + 17);
            g.setColor(new Color(0, 0, 0, esmaece ? 60 : 160));
            g.drawString(u.nome, tx + 1, ty + 1);
            g.setColor(esmaece ? comAlfa(TEXTO, 110) : TEXTO);
            g.drawString(u.nome, tx, ty);
        }
    }
}
