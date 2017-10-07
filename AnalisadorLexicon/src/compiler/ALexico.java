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
 * Essa classe implementa as responsablidades do Analisador Léxico. 
 * São identificados os lexemas e separados por classes (tokens). 
 * A saída contém os lexema e os erros léxicos.
 * 
 * @author Emerson e Santana
 */
public class ALexico {
    
    /**
     * Método que inicializa a análise léxica. 
     * @param arquivo - código-fonte
     */
    
    public void iniciar(File arquivo) {
        BufferedReader bf;
        try {            
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
                    
                    /*separado operadores aritmetricos*/
                    linha = linha.replace("+", " + ").replace("-", " - ").replace("/", " / ").replace("*", " * ");
                    
                    /*Removendo espaços em branco do numero negativo*/
                    
                    Matcher m = Pattern.compile("(-)(\\x09|\\x0A|\\x0B|\\x20)*+(\\d)+(\\.\\d+)?").matcher(linha);
                    
                    while(m.find()){ 
                        String s = (m.group()).substring(1); //desconsidera o sinal - 
                        linha = linha.replace(s, s.trim()); //remove os espaços
                    }
                    
                    /*Separando operadores relacionais*/
                    linha = linha.replace(",", " , ").replace(";", " ; ").replace("!=", " != ").replace("<=", " <= ").replace(">=", " >= ").replace("||", " || ")
                            .replace("&&", " && ");

                    String[] dividida = linha.split("(\\x09|\\x0A|\\x0B|\\x20|\\h|\\s|\\v)+");

                    /*Classificando os lexemas*/
                    for (String lexema : dividida) {
                        if (!lexema.matches("")) {
                            System.out.println(lexema);
                            if (lexema.matches("class|final|if|else|for|scan|print|int|float|bool|true|false|string")) { //palavra reservada
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Palavra Reservada >, ", contadorLinha));
                            } else if (lexema.matches("^[a-z|A-Z](\\w)*")) { //cadeia de caracteres
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Identificador >, ", contadorLinha));
                            } else if (lexema.matches("[\\-]?(\\d)+(\\.\\d+)?")) { //numero
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Número >, ", contadorLinha));
                            } else if (lexema.matches("\\+|\\-|\\*|/|%")) { //operador aritmético
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Operador Aritmético >, ", contadorLinha));
                            } else if (lexema.matches("\\!\\=|\\=|\\<|\\<\\=|\\>|\\>\\=")) { //operador relacional
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Operador Relacional >, ", contadorLinha));
                            } else if (lexema.matches("\\!|\\&\\&|\\|\\|")){ //operador logico
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Operador Lógico >, ", contadorLinha));
                            } else if (lexema.matches(";|,|\\(|\\)|\\[|\\]|\\{|\\}")) { //delimitador
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Delimitador >, ", contadorLinha));
                            } else if (lexema.matches("\"[\\x20-\\x21\\x23-\\x7E]*\"")) { //cadeia de caracteres
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Cadeia de Caracteres >, ", contadorLinha));
                            } else {
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Símbolo ou Expressão Mal Formada >, ", contadorLinha));
                            }
                        }
                    }
                    linha = bf.readLine();
                    contadorLinha++;
                } else {
                    //comentario mal formado
                }
            }

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

}
