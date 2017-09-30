package compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Santana
 */
public class ALexico {

    public void init(File arquivo) {

        BufferedReader bf;
        try {
            bf = new BufferedReader(new FileReader(arquivo));
            String linha = bf.readLine();

        } catch (Exception ex) {
            Logger.getLogger(ALexico.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
