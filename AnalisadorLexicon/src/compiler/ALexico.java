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
            bf = new BufferedReader(new FileReader(arquivo));
            String linha = bf.readLine();
            linha = (String) linha.subSequence(0, linha.indexOf("//")); //removendo comentario de linha
            
            Pattern pat = Pattern.compile(linha);
//            Pattern p = Pattern.compile("a*b");
//            Matcher m = p.matcher("aaaaab");
//            boolean b = m.matches();
            
            while (linha != null) {
                String[] dividida = linha.split("");

                for (String string : dividida) {
                    if (!string.matches("")) {
                        System.out.println(string);
                    }
                }
                linha = bf.readLine();
            }
        } catch (Exception ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Remove todos os comentarios lidos do arquivo
     * @param teste
     * @return 
     */
    private String removerComentarios(String teste){
        while(teste.contains("/*")){
            int primeiraOcorrencia = teste.indexOf("/*");
            String temp = (String) teste.subSequence(0, primeiraOcorrencia); //extrai a string antes do delimitador de comentario
            if(teste.contains("*/")) //verifica se existe o fim do comentario
                temp+=teste.subSequence(teste.indexOf("*/", primeiraOcorrencia)+2,teste.length()); //extrai a string depois do delimitador de comentario
            teste = temp;
        }
        return teste;
    }
}
