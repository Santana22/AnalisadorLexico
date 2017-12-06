package compiler;

import java.io.BufferedWriter;
import java.util.ArrayList;

/**
 *
 * @author Emerson e Vinicius
 */
public class ASintatico {

    private Token tokenAtual, tokenAnterior;
    private ArrayList<Token> tokens;
    private int posicao = -1;
    private BufferedWriter saidaSintatico;

    public void iniciar(ArrayList tokens) {

    }

    private boolean proximoToken(String tipo) {
        if (posicao + 1 < tokens.size()) {
            posicao++;
            tokenAnterior = tokenAtual;
            tokenAtual = tokens.get(posicao);
            ignorarToken(tipo); // Pulando comentarios
            //System.out.println("Token Atual: " + currentToken.toString());
            return true;
        }
        return false;
    }

    private boolean ignorarToken(String tipo) {
        if (tokenAtual.getTipo().contains(tipo)) {
            proximoToken(tipo);
            return true;
        }
        return false;
    }
    
    private void modoPanico(){   
    }
    
    /***********************
     **** Não-Terminais ****
     **********************/

    private void inicio() {
        variavelConstanteObjeto();
        classe();
    }

    private void variavelConstanteObjeto() {

    }

    private void classe() {

    }

    private void tipo() {

    }

    private void tratamentoConstante() {

    }

    private void tratamentoVariavel() {

    }

    private void geradorConstante() {

    }

    private void herancaNao() {

    }

    private void criarObjetosLinha() {

    }

}
