package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Essa classe implementa as responsablidades do Analisador Léxico. São
 * identificados os lexemas e separados por classes (tokens). A saída contém os
 * lexema e os erros léxicos.
 *
 * @author Emerson e Santana
 */
public class ALexico {

    /**
     * Método que inicializa a análise léxica.
     *
     * @param arquivo - código-fonte
     */
    public void iniciar(File arquivo) {
        ArrayList<Lexema> lexemas = new ArrayList();
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
                        analise(buffer, contadorLinha, lexemas);
                        boolean encontrou = false;
                        buffer = new StringBuilder().append("\"");
                        for (int k = i + 1; k < v.length; k++) {
                            buffer.append(v[k]);
                            if (v[k-1]!='\\' && v[k] == '\"') { //verifica se é o fim da cadeia de caracteres
                                i = k;
                                encontrou = true;
                                break;
                            }
                        }
                        if (encontrou) { //verifica se o fim da cadeia foi encontrado
                            analise(buffer, contadorLinha, lexemas);
                            buffer = new StringBuilder();
                        } else {
                            System.out.println("cadeia de caracteres mal formada");
                        }
                    } else if (v[i] == '/') {
                        analise(buffer, contadorLinha, lexemas);
                        buffer = new StringBuilder().append("/");
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
                                        System.out.println("comentario mal formado");
                                        encontrou = true;
                                    }
                                }
                            } while (!encontrou);
                        } else if (i + 1 < v.length && v[i + 1] == '/') { //caso seja comentario de linha
                            for (int k = i; k < v.length; k++) { //termina a linha
                                buffer.append(v[k]);
                            }
                        }
                        analise(buffer, contadorLinha, lexemas);
                        buffer = new StringBuilder();
                    } else if (v[i] == '-') {
                        analise(buffer, contadorLinha, lexemas);
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
                        analise(buffer, contadorLinha, lexemas);
                        buffer = new StringBuilder();
                    } else if (v[i] == 9 || v[i] == 10 || v[i] == 13 || v[i] == 32 || v[i] == '+' || v[i] == '%' || v[i] == '*' || v[i] == ';' || v[i] == ',' || v[i] == '(' || v[i] == ')' || v[i] == '[' || v[i] == ']' || v[i] == '{' || v[i] == '}' || v[i] == ':' || v[i] == '=' || v[i] == '!') {
                        analise(buffer, contadorLinha, lexemas);
                        analise(new StringBuilder().append(v[i]), contadorLinha, lexemas);
                        buffer = new StringBuilder();
                    } else if (v[i] == '<' || v[i] == '>' || v[i] == '!') {
                        analise(buffer, contadorLinha, lexemas);
                        buffer = new StringBuilder().append(v[i]);
                        if (i + 1 < v.length && v[i + 1] == '=') {
                            buffer.append(v[i + 1]);
                        }
                        analise(buffer, contadorLinha, lexemas);
                        buffer = new StringBuilder();
                    } else if (v[i] == '&') {
                        analise(buffer, contadorLinha, lexemas);
                        buffer = new StringBuilder().append(v[i]);
                        if (i + 1 < v.length && v[i + 1] == '&') {
                            buffer.append(v[i + 1]);
                        }
                        analise(buffer, contadorLinha, lexemas);
                        buffer = new StringBuilder();
                    } else if (v[i] == '|') {
                        analise(buffer, contadorLinha, lexemas);
                        buffer = new StringBuilder().append(v[i]);
                        if (i + 1 < v.length && v[i + 1] == '|') {
                            buffer.append(v[i + 1]);
                        }
                        analise(buffer, contadorLinha, lexemas);
                        buffer = new StringBuilder();
                    } else {
                        buffer.append(v[i]);
                    }
                }
                linha = bf.readLine();
                contadorLinha++;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            FileWriter output = new FileWriter(new File(arquivo.getParent(), "output_" + arquivo.getName()));
            BufferedWriter bw = new BufferedWriter(output);

            for (Lexema lex : lexemas) {

                bw.write(lex.getNome() + lex.getTipo() + "Linha: " + lex.getLinha());
                bw.newLine();
                System.out.println(lex.getNome() + lex.getTipo() + "Linha: " + lex.getLinha());
            }
            bw.close();

        } catch (Exception ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void analise(StringBuilder buffer, int contadorLinha, ArrayList<Lexema> lexemas) {
        String lexema = buffer.toString();
        if (!lexema.matches("") && !lexema.matches("\\x09|\\x0A|\\x0B|\\x20")) {
            System.out.println(lexema);
            if (lexema.matches("class|final|if|else|for|scan|print|int|float|bool|true|false|string")) { //palavra reservada
                lexemas.add(new Lexema("< " + lexema + " >", " , < Palavra Reservada >, ", contadorLinha));
            } else if (lexema.matches("^[a-z|A-Z](\\w)*")) { //identificador
                lexemas.add(new Lexema("< " + lexema + " >", " , < Identificador >, ", contadorLinha));
            } else if (lexema.matches("[\\-]?(\\d)+(\\.\\d+)?")) { //numero
                lexemas.add(new Lexema("< " + lexema + " >", " , < Número >, ", contadorLinha));
            } else if (lexema.matches("^//.*")) { //comentário de linha
                lexemas.add(new Lexema("< " + lexema + " >", " , < Comentário de Linha >, ", contadorLinha));
            } else if (lexema.matches("^(/\\*).*(\\*/)$")) { //comentário de bloco
                lexemas.add(new Lexema("< " + lexema + " >", " , < Comentário de Bloco >, ", contadorLinha));
            } else if (lexema.matches("\\+|\\-|\\*|/|%")) { //operador aritmético
                lexemas.add(new Lexema("< " + lexema + " >", " , < Operador Aritmético >, ", contadorLinha));
            } else if (lexema.matches("\\!\\=|\\=|\\<|\\<\\=|\\>|\\>\\=")) { //operador relacional
                lexemas.add(new Lexema("< " + lexema + " >", " , < Operador Relacional >, ", contadorLinha));
            } else if (lexema.matches("\\!|\\&\\&|\\|\\|")) { //operador logico
                lexemas.add(new Lexema("< " + lexema + " >", " , < Operador Lógico >, ", contadorLinha));
            } else if (lexema.matches(";|,|\\(|\\)|\\[|\\]|\\{|\\}")) { //delimitador
                lexemas.add(new Lexema("< " + lexema + " >", " , < Delimitador >, ", contadorLinha));
            } else if (lexema.matches("\"[\\x20-\\x21\\x23-\\x7E|\\x5C\\x22]*\"")) { //cadeia de caracteres
                lexemas.add(new Lexema("< " + lexema + " >", " , < Cadeia de Caracteres >, ", contadorLinha));
            } else {
                lexemas.add(new Lexema("< " + lexema + " >", " , < Símbolo ou Expressão Mal Formada >, ", contadorLinha));
            }
        }

    }

    private static boolean isDelimitador(char a) {
        return a == 9 || a == 10 || a == 13 || a == 32 || a == '\"' || a == '+' || a == '-' || a == '/' || a == '*' || a == '&' || a == '<' || a == '>' || a == '=' || a == '!' || a == '%' || a == '|' || a == ';' || a == ',' || a == '(' || a == ')' || a == '[' || a == ']' || a == '{' || a == '}' || a == ':';
    }

}
