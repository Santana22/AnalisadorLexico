package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

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

                    linha = linha.replace(",", " , ").replace(";", " ; ").replace("!=", " != ").replace("<=", " <= ").replace(">=", " >= ").replace("||", " || ")
                            .replace("&&", " && ");

                    
                    
                    String[] dividida = linha.split("\\x09|\\x0A|\\x0B|\\x20|\\h|\\s|\\v");

                    for (String lexema : dividida) {
                        if (!lexema.matches("\"\\\\x09|\\\\x0A|\\\\x0B|\\\\x20|\\\\h|\\\\s|\\\\v")) {
                            System.out.println(lexema);
                            if (lexema.matches("class|final|if|else|for|scan|print|int|float|bool|true|false|string")) {
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Palavra Reservada >, ", contadorLinha));
                            } else if (lexema.matches("^[a-z|A-Z](\\w)*")) {
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Identificador >, ", contadorLinha));
                            } else if (lexema.matches("[\\-]?(\\x09|\\x0A|\\x0B|\\x20|\\h|\\s|\\v)\\d[\\d]*[\\[.]\\d[\\d]*]?")||lexema.matches("(\\x09|\\x0A|\\x0B|\\x20|\\h|\\s|\\v)\\d\\d*[\\[.]\\d[\\d]*]?")) {
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Número >, ", contadorLinha));
                            } else if (lexema.matches("\\d")){
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Digito >, ", contadorLinha));
                            } else if (lexema.matches("\\+|\\-|\\*|/|%")) {
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Operador Aritmético >, ", contadorLinha));
                            } else if (lexema.matches("\\!\\=|\\=|\\<|\\<\\=|\\>|\\>\\=")) {
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Operador Relacional >, ", contadorLinha));
                            } else if (lexema.matches("\\!|\\&\\&|\\|\\|")){
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Operador Lógico >, ", contadorLinha));
                            } else if (lexema.matches(";|,|\\(|\\)|\\[|\\]|\\{|\\}")) {
                                lexemas.add(new Lexema("< " + lexema + " >", " , < Delimitador >, ", contadorLinha));
                            } else if (lexema.matches("\"[\\x20-\\x21\\x23-\\x7E]*\"")) {
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
