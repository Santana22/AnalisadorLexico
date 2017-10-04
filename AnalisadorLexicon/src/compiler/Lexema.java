package compiler;

/**
 *
 * @author Santana
 */

public class Lexema {

    private String nome;
    private String tipo;
    private int linha;

    public Lexema(String nome, String tipo, int linha) {
        this.nome = nome;
        this.tipo = tipo;
        this.linha = linha;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public int getLinha() {
        return linha;
    }
}
