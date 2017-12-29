package controller;

import java.io.File;
import java.util.ArrayList;

/**
 * Classe responsávl por gerenciar os controllers disponíveis no projeto.
 *
 * @author Emerson e Santana
 */
public class Facade {

    private static Facade INSTANCE = null;

    private ControllerLexico controllerLexico;
    private ControllerSintatico controllerSintatico;
    private ControllerSemantico controllerSemantico;
    
    /**
     * Construtor da Facade.
     */
    private Facade() {
        this.controllerLexico = new ControllerLexico();
        this.controllerSintatico = new ControllerSintatico();
        this.controllerSemantico = new ControllerSemantico();
    }

    /**
     * Retorna a uúnica instância do projeto
     *
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
     *
     * @param arquivo - código a ser analisado
     */
    public ArrayList analisadorLexico(File arquivo) {
        return this.controllerLexico.iniciarLexico(arquivo);
    }

    public void analisadorSintatico(ArrayList tokens, File file) {
        this.controllerSintatico.iniciarSintatico(tokens, file);
    }

    public void analisadorSematico(ArrayList tokens, File file) {
        this.controllerSemantico.iniciar(tokens, file);
    }
}
