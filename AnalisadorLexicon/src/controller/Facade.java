package controller;

import java.io.File;

/**
 *
 * 
 */
public class Facade {
    private static Facade INSTANCE = null;
    
    private ControllerLexico controllerLexico;
    
    public Facade() {
        this.controllerLexico = new ControllerLexico();
    }
    
     public static Facade getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Facade();
        }
        return INSTANCE;
    }
     
     public void analisadorLexico(File arquivo){
         this.controllerLexico.iniciarLexico(arquivo);
     }
}
