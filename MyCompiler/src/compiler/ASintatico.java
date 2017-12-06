package compiler;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Emerson e Vinicius
 */
public class ASintatico {
    
    private static String sync[];
    
    private Token tokenAtual, tokenAnterior;
    private ArrayList<Token> tokens;
    private int posicao = -1;
    private BufferedWriter saidaSintatico;
    
    public void iniciar(ArrayList tokens) {
        
        this.tokens = tokens;
        inicio();
        
    }
    
    private boolean proximoToken() {
        if (posicao + 1 < tokens.size()) {
            posicao++;
            tokenAnterior = tokenAtual;
            tokenAtual = tokens.get(posicao);
            aceitarToken("Comentário"); // Pulando comentarios
            //System.out.println("Token Atual: " + currentToken.toString());
            return true;
        }
        return false;
    }
    
    private boolean aceitarToken(String tipo) {
        if (tokenAtual.getTipo().contains(tipo) || tokenAtual.getNome().contains(tipo)) {
            proximoToken();
            return true;
        }
        return false;
    }
    
    private void modoPanico(String sync[]) {
        // ArrayList<Token> sync = Arrays.asList(sync);
        while (!sync.contains(tokenAtual.getNome())) {
            System.out.println("\tPulou Token: " + tokenAtual.toString());
            if (!proximoToken()) {
                return;
            }
        }
        
    }
    
    private boolean expect(String tipo) {
        if (aceitarToken(tipo)) {
            return true;
        }
        return false;
    }

    /**
     * *********************
     **** Não-Terminais **** ********************
     */
    // <inicio> ::= <variavel ou constante ou Objeto> <classe>
    private void inicio() {
        proximoToken();
        variavelConstanteObjeto();
        classe();
    }
    
    private void variavelConstanteObjeto() {
        if (aceitarToken("final")) {
            tipo();
            tratamentoConstante();
            aceitarToken(";");
        } else if (aceitarToken("Identificador")) {
            criarObjetosLinha();
            variavelConstanteObjeto();
        } else {
            tipo();
            tratamentoVariavel();
        }
    }
    
    private void classe() {
        
    }
    
    private void tipo() {
        if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool")) {
        }
        
    }
    
    private void tratamentoConstante() {
        if (aceitarToken("Identificador")) {
            if (aceitarToken("=")) {
                if (aceitarToken("Número")) {
                    geradorConstante();
                } else {
                    modoPanico(sync);
                }
            } else {
                modoPanico(sync);
            }
        } else {
            modoPanico(sync);
        }
    }
    
    private void tratamentoVariavel() {
        variaveis();
        if (aceitarToken(";")) {
            variavelConstanteObjeto();
        } else {
            modoPanico(sync);
        }
    }
    
    private void geradorConstante() {
        if (aceitarToken(",")) {
            tratamentoConstante();
        }
    }
    
    private void herancaNao() {
        
    }
    
    private void criarObjetosLinha() {
        
    }
    
    private void variaveis() {
        if(aceitarToken("Identificador")){
            fatoracaoVariaveis();
        } else{
            modoPanico(sync);
        }
        
    }
    
    private void fatoracaoVariaveis(){
        
    }
    
}
