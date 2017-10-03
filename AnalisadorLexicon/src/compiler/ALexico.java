package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
            bf = new BufferedReader(new FileReader(arquivo));
            String linha = bf.readLine();

//            Pattern pat = Pattern.compile(linha);
//            Pattern p = Pattern.compile("a*b");
//            Matcher m = p.matcher("aaaaab");
//            boolean b = m.matches();
            int contadorLinha = 0;
            
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
                    String[] dividida = linha.split("[\\t\\n\\x0B\\f\\r[ ]]");
                    for (String string : dividida) {
                        arquivotemp = arquivotemp + string;
       
                    }                    
                    linha = bf.readLine();
                    contadorLinha++;
                } else {
                    //comentario mal formado
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
