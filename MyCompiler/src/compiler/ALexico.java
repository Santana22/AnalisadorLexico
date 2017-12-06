package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

/**
 * Essa classe implementa as responsablidades do Analisador Léxico. São
 * identificados os lexemas e separados por classes (tokens). A saída contém os
 * lexema e os erros léxicos.
 *
 * @author Emerson e Santana
 */
public class ALexico {
    
    public static boolean continuar = true;

    /**
     * Método que inicializa a análise léxica. Analisa caractere a caractere
     * armazenando em um buffer e divide o texto quando encontrados
     * delimitadores
     *
     * @param arquivo - código-fonte
     */
    public ArrayList<Tokens> iniciar(File arquivo) {
        System.out.println("Iniciando Análise Léxica...");
        ArrayList<Tokens> tokens = new ArrayList();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(arquivo));
            String linha = bf.readLine();
            int contadorLinha = 1;
            char v[];
            while (linha != null) {
                v = linha.toCharArray();
                StringBuilder buffer = new StringBuilder();

                for (int i = 0; i < v.length; i++) { //percorre toda a linha
                    if (v[i] == '\"') { //verifica se é uma cadeia de caracteres
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder().append("\"");

                        for (int k = i + 1; k < v.length; k++) {
                            buffer.append(v[k]);
                            if (v[k - 1] != '\\' && v[k] == '\"') { //verifica se é o fim da cadeia de caracteres
                                i = k;
                                break;
                            }
                            i = k;
                        }
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder();
                    } else if (v[i] == '/') {
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder().append("/");
                        int cL = contadorLinha;
                        if (i + 1 < v.length && v[i + 1] == '*') { //caso seja comentario de bloco
                            boolean encontrou;
                            do {
                                encontrou = false;
                                for (int k = i + 1; k < v.length; k++) {
                                    buffer.append(v[k]);
                                    if (v[k] == '*' && k + 1 < v.length && v[k + 1] == '/') {
                                        buffer.append('/');
                                        encontrou = true;
                                        i = k + 1;
                                        break;
                                    }
                                }
                                if (!encontrou) {
                                    linha = bf.readLine();
                                    if (linha != null) {
                                        i = -1;
                                        v = linha.toCharArray();
                                        contadorLinha++;
                                    } else {
                                        encontrou = true;
                                    }
                                }
                            } while (!encontrou);
                        } else if (i + 1 < v.length && v[i + 1] == '/') { //caso seja comentario de linha
                            for (int k = i + 1; k < v.length; k++) { //termina a linha
                                buffer.append(v[k]);
                            }
                            i = v.length;
                        }
                        analise(buffer, cL, tokens);
                        buffer = new StringBuilder();
                    } else if (v[i] == '-') {
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder().append('-');

                        for (int k = i + 1; k < v.length; k++) { //ignorando espaços
                            if (v[k] != 9 && v[k] != 10 && v[k] != 13 && v[k] != 32) {
                                i = k;
                                break;
                            }
                        }
                        while (i + 1 == v.length) { //caso o sinal seja no fim da linha
                            linha = bf.readLine();
                            if (linha != null) {
                                i = -1;
                                v = linha.toCharArray();
                                contadorLinha++;
                            } else {
                                break;
                            }
                            for (int k = i + 1; k < v.length; k++) { //ignorando espaços
                                if (v[k] != 9 && v[k] != 10 && v[k] != 13 && v[k] != 32) {
                                    i = k;
                                    break;
                                }
                            }
                        }

                        StringBuilder bufferTemp = new StringBuilder();
                        for (int k = i; k < v.length; k++) { //verificando se é um número
                            if (v[k] > 47 && v[k] < 58 || v[k] == '.') {
                                bufferTemp.append(v[k]);
                            } else if (isDelimitador(v[k])) {
                                break;
                            } else { //caso não seja um numero
                                bufferTemp = new StringBuilder();
                                break;
                            }
                        }
                        if (i != 0) {
                            i += bufferTemp.length() - 1;
                        }
                        buffer.append(bufferTemp);
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder();
                    } else if (v[i] == 9 || v[i] == 10 || v[i] == 13 || v[i] == 32 || v[i] == '+' || v[i] == '%' || v[i] == '*' || v[i] == ';' || v[i] == ',' || v[i] == '(' || v[i] == ')' || v[i] == '[' || v[i] == ']' || v[i] == '{' || v[i] == '}' || v[i] == ':' || v[i] == '=' || v[i] == '!') {
                        analise(buffer, contadorLinha, tokens);
                        analise(new StringBuilder().append(v[i]), contadorLinha, tokens);
                        buffer = new StringBuilder();
                    } else if (v[i] == '<' || v[i] == '>' || v[i] == '!') {
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder().append(v[i]);
                        if (i + 1 < v.length && v[i + 1] == '=') {
                            buffer.append(v[++i]);
                        }
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder();
                    } else if (v[i] == '&') {
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder().append(v[i]);
                        if (i + 1 < v.length && v[i + 1] == '&') {
                            buffer.append(v[i + 1]);
                            i++;
                        }
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder();
                    } else if (v[i] == '|') {
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder().append(v[i]);
                        if (i + 1 < v.length && v[i + 1] == '|') {
                            buffer.append(v[i + 1]);
                            i++;
                        }
                        analise(buffer, contadorLinha, tokens);
                        buffer = new StringBuilder();
                    } else {
                        buffer.append(v[i]);
                    }
                }
                analise(buffer, contadorLinha, tokens);
                linha = bf.readLine();
                contadorLinha++;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            FileWriter output = new FileWriter(new File(arquivo.getParent(), "output_lex_" + arquivo.getName()));
            BufferedWriter bw = new BufferedWriter(output);

            bw.write("\nIniciando Análise Léxica...");

            for (Tokens lex : tokens) {
                String tipo = lex.getTipo();
                if (tipo.equals("Cadeia de Caracteres Mal Formada") || tipo.equals("Número Mal Formado") || tipo.equals("Símbolo ou Expressão Mal Formada") || tipo.equals("Comentário de Bloco Mal Formado")) {
                    continuar = false;
                }
                bw.write(lex.toString());
                bw.newLine();
            }
            if (continuar) {
                bw.write("\nAnalise Léxica concluida com sucesso!");
            } else {
                bw.write("\nAnalise Léxica concluida com erro!");
            }

            bw.close();
        } catch (Exception ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tokens;

    }

    /**
     * Classifica os lexemas de acordo com a sintaxe
     *
     * @param buffer
     * @param contadorLinha
     * @param lexemas
     * @return
     */
    private void analise(StringBuilder buffer, int contadorLinha, ArrayList<Tokens> lexemas) {
        String lexema = buffer.toString();
        if (!lexema.matches("") && !lexema.matches("\\x09|\\x0A|\\x0B|\\x20")) {
            if (lexema.matches("class|final|if|else|for|scan|print|int|float|bool|true|false|string")) { //palavra reservada
                lexemas.add(new Tokens(lexema, "Palavra Reservada", contadorLinha));
            } else if (lexema.matches("^[a-zA-Z](\\w)*")) { //identificador
                lexemas.add(new Tokens(lexema, "Identificador", contadorLinha));
            } else if (lexema.matches("[\\-]?(\\d)+(\\.\\d+)?")) { //numero
                lexemas.add(new Tokens(lexema, "Número", contadorLinha));
            } else if (lexema.matches("^//.*")) { //comentário de linha
                lexemas.add(new Tokens(lexema, "Comentário de Linha", contadorLinha));
            } else if (lexema.matches("^(/\\*).*(\\*/)$")) { //comentário de bloco
                lexemas.add(new Tokens(lexema, "Comentário de Bloco", contadorLinha));
            } else if (lexema.matches("\\+|\\-|\\*|/|%")) { //operador aritmético
                lexemas.add(new Tokens(lexema, "Operador Aritmético", contadorLinha));
            } else if (lexema.matches("\\!\\=|\\=|\\<|\\<\\=|\\>|\\>\\=")) { //operador relacional
                lexemas.add(new Tokens(lexema, "Operador Relacional", contadorLinha));
            } else if (lexema.matches("\\!|\\&\\&|\\|\\|")) { //operador logico
                lexemas.add(new Tokens(lexema, "Operador Lógico", contadorLinha));
            } else if (lexema.matches(";|,|\\(|\\)|\\[|\\]|\\{|\\}")) { //delimitador
                lexemas.add(new Tokens(lexema, "Delimitador", contadorLinha));
            } else if (lexema.matches("\"[\\x20-\\x21\\x23-\\x7E\\x5C\\x22]*\"")) { //cadeia de caracteres
                lexemas.add(new Tokens(lexema, "Cadeia de Caracteres", contadorLinha));
            } else if (lexema.matches("^\".*")) { //cadeia de caracteres mal formada
                lexemas.add(new Tokens(lexema, "Cadeia de Caracteres Mal Formada", contadorLinha));
            } else if (lexema.matches("[\\-]?(\\d)+(\\..*)?") || lexema.matches("[\\-]?(\\.\\d+)?")) { //número mal formado
                lexemas.add(new Tokens(lexema, "Número Mal Formado>, ", contadorLinha));
            } else if (lexema.matches("^(/\\*).*")) { //comentário de bloco
                lexemas.add(new Tokens(lexema, "Comentário de Bloco Mal Formado", contadorLinha));
            } else {
                lexemas.add(new Tokens(lexema, "Símbolo ou Expressão Mal Formada", contadorLinha));
            }
        }

    }

    /**
     * Retorna verdadeiro caso o caractere seja um delimitador
     *
     * @param a
     * @return
     */
    private static boolean isDelimitador(char a) {
        return a == 9 || a == 10 || a == 13 || a == 32 || a == '\"' || a == '+' || a == '-' || a == '/' || a == '*' || a == '&' || a == '<' || a == '>' || a == '=' || a == '!' || a == '%' || a == '|' || a == ';' || a == ',' || a == '(' || a == ')' || a == '[' || a == ']' || a == '{' || a == '}' || a == ':';
    }

}
