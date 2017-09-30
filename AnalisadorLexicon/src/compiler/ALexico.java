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
}
