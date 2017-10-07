package compiler;

/**
 * Essa classe encapsula as informações dos lexemas identificados, as suas respectivas classes
 * e a linha que se encontram.
 * @author Emerson e Santana
 */

public class Lexema {
    private String nome;
    private String tipo;
    private int linha;
    
    /**
     * Método construtor.
     * @param nome - Identificação do Lexema
     * @param tipo - Token ao qual pertence
     * @param linha - Linha que está
     */

    public Lexema(String nome, String tipo, int linha) {
        this.nome = nome;
        this.tipo = tipo;
        this.linha = linha;
    }

    /**
     * Método para obter o nome.
     * @return nome
     */
    
    public String getNome() {
        return nome;
    }
    
    /**
     * Método para obter o tipo.
     * @return tipo
     */

    public String getTipo() {
        return tipo;
    }

    /**
     * Método para obter a linha.
     * @return linha
     */
    
    public int getLinha() {
        return linha;
    }
}
