import java.util.*;

public class Main {

    private static final Scanner in = new Scanner(System.in);
    private static final RedeSocial rede = RedeSocial.exemplo();

    public static void main(String[] args) {
        int op;
        do {
            menu();
            op = lerOpcao();
            System.out.println();
            executar(op);
        } while (op != 0);
    }

    private static void menu() {
        System.out.println("""

                ===== REDE SOCIAL DE AMIZADES =====
                 1 - Adicionar pessoa           7 - Sugestão de amigos
                 2 - Remover pessoa             8 - Graus de separação
                 3 - Adicionar amizade          9 - Círculos sociais
                 4 - Remover amizade           10 - Grupos isolados
                 5 - Amigos e grau de alguém   11 - Mais popular
                 6 - Amigos em comum           12 - Lista e matriz de adjacência
                 0 - Sair""");
        System.out.print("Opção: ");
    }

    private static void executar(int op) {
        switch (op) {
            case 0 -> System.out.println("Até mais!");
            case 1 -> System.out.println(rede.addPessoa(ler("Nome: ")) ? "Pessoa cadastrada." : "Nome vazio ou já cadastrado.");
            case 2 -> {
                String p = pessoa("Pessoa: ");
                if (p != null && rede.removePessoa(p)) System.out.println(p + " foi removida.");
            }
            case 3 -> {
                String[] d = dupla();
                if (d != null)
                    System.out.println(rede.addAmizade(d[0], d[1]) ? "Amizade criada." : "Não foi possível (já são amigos ou é a mesma pessoa).");
            }
            case 4 -> {
                String[] d = dupla();
                if (d != null)
                    System.out.println(rede.removeAmizade(d[0], d[1]) ? "Amizade removida." : "Eles não são amigos.");
            }
            case 5 -> {
                String p = pessoa("Pessoa: ");
                if (p != null) System.out.println(p + " tem " + rede.grau(p) + " amigo(s): " + rede.amigos(p));
            }
            case 6 -> {
                String[] d = dupla();
                if (d != null) {
                    Set<String> comuns = rede.amigosEmComum(d[0], d[1]);
                    System.out.println(comuns.isEmpty() ? "Nenhum amigo em comum." : comuns.size() + " em comum: " + comuns);
                }
            }
            case 7 -> {
                String p = pessoa("Pessoa: ");
                if (p == null) return;
                var sugestoes = rede.sugestoes(p);
                if (sugestoes.isEmpty()) System.out.println("Sem sugestões para " + p + ".");
                for (var s : sugestoes) System.out.println(s.getKey() + " (" + s.getValue() + " amigo(s) em comum)");
            }
            case 8 -> {
                String[] d = dupla();
                if (d == null) return;
                List<String> c = rede.caminho(d[0], d[1]);
                if (c.isEmpty()) System.out.println("Não há ligação entre " + d[0] + " e " + d[1] + ".");
                else System.out.println(String.join(" -> ", c) + " (" + grau(c.size() - 1) + ")");
            }
            case 9 -> {
                String p = pessoa("Pessoa: ");
                if (p == null) return;
                var niveis = rede.circulos(p);
                if (niveis.isEmpty()) System.out.println(p + " não tem amigos.");
                for (int i = 0; i < niveis.size(); i++) System.out.println(grau(i + 1) + ": " + niveis.get(i));
            }
            case 10 -> {
                var grupos = rede.grupos();
                System.out.println(grupos.size() + " grupo(s) isolado(s):");
                for (int i = 0; i < grupos.size(); i++)
                    System.out.println("Grupo " + (i + 1) + " (" + grupos.get(i).size() + "): " + grupos.get(i));
            }
            case 11 -> {
                List<String> top = rede.maisPopulares();
                if (!top.isEmpty())
                    System.out.println("Mais popular: " + String.join(", ", top) + " com " + rede.grau(top.get(0)) + " amigo(s).");
            }
            case 12 -> System.out.println("LISTA DE ADJACÊNCIA\n" + rede.listaAdjacencia()
                    + "\nMATRIZ DE ADJACÊNCIA\n" + rede.matrizAdjacencia());
            default -> System.out.println("Opção inválida.");
        }
    }

    // ---------- entrada e saída ----------

    private static int lerOpcao() {
        if (!in.hasNextLine()) return 0; // fim da entrada = sair
        try {
            return Integer.parseInt(in.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String ler(String msg) {
        System.out.print(msg);
        return in.hasNextLine() ? RedeSocial.normaliza(in.nextLine()) : "";
    }

    /** Lê um nome e devolve null (com aviso) se a pessoa não existir. */
    private static String pessoa(String msg) {
        String p = ler(msg);
        if (rede.existe(p)) return p;
        System.out.println("Pessoa não encontrada.");
        return null;
    }

    private static String[] dupla() {
        String a = pessoa("Primeira pessoa: ");
        String b = a == null ? null : pessoa("Segunda pessoa: ");
        return b == null ? null : new String[]{a, b};
    }

    private static String grau(int n) {
        return n + (n == 1 ? " grau" : " graus");
    }
}
