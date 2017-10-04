package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Santana
 */
public class ALexico {

    public void iniciar(File arquivo) {
        BufferedReader bf;
        try {
            String arquivotemp = null;
            String espacos = "(\\x09|\\x0A|\\x0B|\\x20)*";
            ArrayList<Lexema> lexemas = new ArrayList();
            bf = new BufferedReader(new FileReader(arquivo));
            String linha = bf.readLine();
            int contadorLinha = 1;

            while (linha != null) {
                /*remove comentários de bolco*/
                while (linha.contains("/*")) {
                    int primeiraOcorrencia = linha.indexOf("/*");
                    String temp = linha.substring(0, primeiraOcorrencia);
                    if (linha.contains("*/")) { //verifica se a mesma linha contem o fim do comentario
                        temp += linha.substring(linha.indexOf("*/", primeiraOcorrencia) + 2, linha.length());
                    } else {
                        while (linha != null && !linha.contains("*/")) { //enquanto não for encontrada a linha com o fim do comentário
                            linha = bf.readLine();
                            contadorLinha++;
                        }
                        if (linha != null) { //se a linha for encontrada
                            temp += linha.substring(linha.indexOf("*/") + 2);
                        }
                    }
                    linha = temp;
                    //System.out.println(linha);
                }
                if (linha != null) { //verifica se o fim do comentário de bloco foi encontrado
                    if (linha.contains("//")) {
                        linha = (String) linha.subSequence(0, linha.indexOf("//")); //removendo comentario de linha
                    }
                    String[] dividida = linha.split("[ \\t\\n\\x0B\\f\\r]");

                    for (String string : dividida) {
                        if (!string.matches("[ \\t\\n\\x0B\\f\\r]")) {
                            System.out.println(string);
                            if (string.matches("class|final|if|else|for|scan|print|int|float|bool|true|false|string")) {
                                lexemas.add(new Lexema(string, "Palavra Reservada", contadorLinha));
                            } else if (string.matches("^[a-z|A-Z]\\w*")) {
                                lexemas.add(new Lexema(string, "Identificador", contadorLinha));
                            } else if (string.matches("^[-*(\\x09|\\x0A|\\x0B|\\x20)?(\\d)*(\\.\\d+)?]")) {
                                lexemas.add(new Lexema(string, "Número", contadorLinha));
                            } else if (string.matches("\\+|\\-|\\*|/|%")) {
                                lexemas.add(new Lexema(string, "Operador Aritmético", contadorLinha));
                            } else if (string.matches("!=|=|<|<=|>|>=")) {
                                lexemas.add(new Lexema("< " + string + " >", "Operador Relacional", contadorLinha));
                            } else if (string.matches(";|,|\\(|\\)|\\[|\\]|\\{|\\}")) {
                                lexemas.add(new Lexema("< " + string + " >", "Delimitador", contadorLinha));
                            } else if (string.matches("\"[\\x20-\\x21\\x23-\\x7E]*\"")) {
                                lexemas.add(new Lexema("< " + string + " >", "Cadeia de Caracteres", contadorLinha));
                            } else {
                                lexemas.add(new Lexema("< " + string + " >", "Símbolo ou Exepressão Errada", contadorLinha));
                            }
                        }
                    }
                    linha = bf.readLine();
                    contadorLinha++;
                } else {
                    //comentario mal formado
                }
            }
          
            for (Lexema lex : lexemas) {
                System.out.println(lex.getNome() + " " + lex.getTipo() + " " + lex.getLinha());
            }

        } catch (Exception ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
