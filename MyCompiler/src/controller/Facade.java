package controller;

import java.io.File;

/**
 * Classe responsávl por gerenciar os controllers disponíveis no projeto.
 * @author Emerson e Santana
 */
public class Facade {
    private static Facade INSTANCE = null;
    
    private ControllerLexico controllerLexico;
    
    /**
     * Construtor da Facade.
     */
    
    private Facade() {
        this.controllerLexico = new ControllerLexico();
    }
    
    /**
     * Retorna a uúnica instância do projeto
     * @return 
     */
    
     public static Facade getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Facade();
        }
        return INSTANCE;
    }
     
     /**
      * Metodo ara inicializar a análise léxica.
      * @param arquivo - código a ser analisado
      */
     
     public void analisadorLexico(File arquivo){
         this.controllerLexico.iniciarLexico(arquivo);
     }
}
