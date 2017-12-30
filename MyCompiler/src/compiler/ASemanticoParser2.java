/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import semantico.Classe;
import semantico.ClasseFilha;
import semantico.Global;
import semantico.Metodo;
import semantico.Variavel;

/**
 * Esta classe modela a 2ª análise semântica para a Linguagem Jussara.
 *
 * @author Emerson e Vinicius
 */
public class ASemanticoParser2 {
    
    private Token tokenAtual, tokenAnterior;
    private ArrayList<Token> tokens;
    private boolean umaClasse = false;
    private int umaMain = 0;
    private BufferedWriter saidaSematico;
    private int errosSemanticos = 0;
    private int posicao = -1;
    private Classe classeAtual = null;
    private Metodo metodoAtual = null;
    private Variavel variavelAtual = null;
    private Global global = Global.getInstance();
    private int escopoVariavel = 0; //-1 atual, 0 - global, 1 - classe 
    private int nivel = 0; //variavel que indica em qual nivel a analise está: nivel 0 - corpo principal, nivel 1 - classe, nivel 2 - metodo
    private String nomeVariavelAtribuicao;
    private String objetoChamadaMetodo;
    private Metodo metodoChamado;
    private String tipoOperacao;
    private boolean passagemParametro;
    
    private boolean proximoToken() {
        if (posicao + 1 < tokens.size()) {
            posicao++;
            tokenAnterior = tokenAtual;
            tokenAtual = tokens.get(posicao);
            aceitarToken("Comentário de Linha"); // Pulando comentarios
            aceitarToken("Comentário de Bloco");
            return true;
        }
        return false;
    }

    private boolean aceitarToken(String tipo) {
        if (tokenAtual.getTipo().equals(tipo) || tokenAtual.getNome().equals(tipo)) {
            System.out.println(tokenAtual);
            proximoToken();
            return true;
        }
        return false;
    }

    /**
     * Método que inicia a 2ª análise semântica.
     *
     * @param tokens lista de tokens extraídos da análise léxica
     * @param file diretório para armazenar os resultados
     */
    
    public void iniciar(ArrayList <Token> tokens, File file){
        FileWriter output;
        try {
            output = new FileWriter(new File(file.getParent(), "output_sen_" + file.getName()), true);
            saidaSematico = new BufferedWriter(output);
            saidaSematico.newLine();
            System.out.println("2ª Análise Semântica iniciada para o arquivo " + file.getName());
            saidaSematico.write("2ª Análise Semântica iniciada para o arquivo " + file.getName());
            saidaSematico.newLine();
            saidaSematico.close(); //Comente essa linha quando parar de dar erro!
            this.tokens = tokens;
            inicio();
            if (errosSemanticos == 0 && umaClasse && umaMain == 1) {
                System.out.println("2ª Análise Semântica finalizada com sucesso para o arquivo " + file.getName());
                saidaSematico.write("2ª Análise Semântica finalizada com sucesso para o arquivo " + file.getName());
            } else{
                if (!umaClasse) {
                    saidaSematico.write("Erro Grave: Deve existir, pelo menos, uma classe.");
                    saidaSematico.newLine();
                }
                if(umaMain != 1){
                    saidaSematico.write("Erro Grave: Deve existir somente um método main no arquivo.");
                    saidaSematico.newLine();
                }
                System.out.println("2ª Análise Semântica finalizada com erro para o arquivo " + file.getName());
                saidaSematico.write("2ª Análise Semântica finalizada com erro para o arquivo " + file.getName());
            }
            saidaSematico.close();

        } catch (IOException ex) {
            Logger.getLogger(ASintatico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void inicio() {
        proximoToken();
        variavelConstanteObjeto();
        classe();
    }

    private void variavelConstanteObjeto() {
        variavelAtual = new Variavel();
        if (aceitarToken("final")) {
            variavelAtual.setConstante(true);
            tipo();
            tratamentoConstante();
            if (aceitarToken(";")) {
                variavelConstanteObjeto();
            }
            variavelConstanteObjeto();
        } else if (aceitarToken("Identificador")) {
            variavelAtual.setTipo(tokenAnterior.getNome());
            if (aceitarToken("Identificador")) {
                variavelAtual.setNome(tokenAnterior.getNome());
                criarObjetos();
            } else if (aceitarToken("=")) {
                instancia();
            } else {
                chamadaMetodo();
            }
            variavelConstanteObjeto();
        } else if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool")) {
            variavelAtual.setTipo(tokenAnterior.getNome());
            tratamentoVariavel();
        } else {
        }
        variavelAtual = new Variavel();
    }

    private void classe() {
        if (aceitarToken("class")) {
            if(!umaClasse){
                umaClasse = true;
            }
            if (aceitarToken("Identificador")) {
                classeAtual = global.getClasse(tokenAnterior.getNome());
                herancaNao();
                if (aceitarToken("{")) {
                    nivel = 1;
                    variavelConstanteObjeto();
                    metodo();
                    if (aceitarToken("}")) {
                        nivel = 0;
                        classe();
                    }
                } 
            }
        }
    }

    private boolean tipo() {
        if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool")) {
            variavelAtual.setTipo(tokenAnterior.getNome());
            return true;
        }
        return false;
    }
    
    private void tratamentoConstante() {
        if (aceitarToken("Identificador")) {
            variavelAtual.setNome(tokenAnterior.getNome());
            if (aceitarToken("=")) {
                if (aceitarToken("Número")) {
                    verificarTipoConstante();
                    geradorConstante();
                } 
            } 
        } 
    }

    private void tratamentoVariavel() {
        variaveis();
        if (aceitarToken(";")) {
            variavelConstanteObjeto();
        }
    }

    private void geradorConstante() {
        if (aceitarToken(",")) {
            if(!global.addVariavel(variavelAtual)){
                //constante já existente exception
            }
            tratamentoConstante();
        }
    }

    private void herancaNao() {
        if (aceitarToken("<")) {
            if (aceitarToken("-")) {
                if (aceitarToken(">")) {
                    if (aceitarToken("Identificador")) {
                    } 
                } 
            }
        }
    }

    private void variaveis() {
        if (aceitarToken("Identificador")) {
            variavelAtual.setNome(tokenAnterior.getNome());
            fatoracaoVariaveis();
        }
    }

    private void fatoracaoVariaveis() {
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                if (aceitarToken("]")) {
                    fatoracaoFatoracaoVariaveis();
                }
            } 
        }
        acrescentar();
    }

    private void acrescentar() {
        if (aceitarToken(",")) {
            variaveis();
        }
    }

    private void fatoracaoFatoracaoVariaveis() {
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                if (aceitarToken("]")) {
                    fatoracaoFatoracaoVariaveis();
                }
            }
        }
        acrescentar();
    }

    private void program() {
        if (aceitarToken("for")) {
            forConsumido();
        } else if (aceitarToken("if")) {
            ifConsumido();
        } else if (aceitarToken("scan")) {
            scanConsumido();

        } else if (aceitarToken("print")) {
            printConsumido();
        } else if (aceitarToken("<")) {
            if (aceitarToken("<")) {
                returnConsumido();
            }
        } else if (tipo()) {
            variavelAtual=new Variavel();
            variavelAtual.setNome(tokenAnterior.getNome());
            criarVariavel();
        } else if (aceitarToken("-")) {
            classificarVariavel();
        } else if (aceitarToken("Identificador")) {
            nomeVariavelAtribuicao = tokenAnterior.getNome();
            verificarDeclaracaoVariavel();
            if (aceitarToken("=")) {
                escopoVariavel = -1;
                if (tokenAtual.getNome().equals(">")) {
                    instancia();
                } else {
                    operation();
                    if(variavelAtual!=null){
                        if(variavelAtual.isConstante()){
                            //erro não é permitido alterar valor de constante
                        }else if(!variavelAtual.getTipo().equals(tipoOperacao)){
                            //erro no tipo da atribuição
                        }
                    }
                    if (aceitarToken(";")) {

                    }
                }
            } else if (aceitarToken("Identificador")) {
                variavelAtual.setNome(tokenAnterior.getNome());
                criarObjetos();
            } else if (tokenAtual.getNome().equals("[")) {
                fatoracaoAcessoVetorMatriz();
                if (aceitarToken("=")) {
                    operation();
                    if(variavelAtual!=null){
                        if(variavelAtual.isConstante()){
                            //erro não é permitido alterar valor de constante
                        }else if(!variavelAtual.getTipo().equals(tipoOperacao)){
                            //erro no tipo da atribuição
                        }
                    }
                    if (aceitarToken(";")) {

                    }
                }
            } else {
                chamadaMetodo();
            }
            program();
        }
    }

    private void returnConsumido() {
        tiposReturn();
        if (aceitarToken(";")) {
            if(metodoAtual!=null){
                if(metodoAtual.getTipo()!=null){
                    if(!metodoAtual.getTipo().equals(tipoOperacao)){
                        //erro tipo de return incompativel
                    }
                }
            }else if(!(metodoAtual==null&&tipoOperacao==null)){
                //erro tipo de return incompativel
            }
            
            if(!tokenAtual.getNome().equals("}")){
                //codigo abaixo do return não sera executado
            }
            program();
        } 
    }

    private void tiposReturn() {
        operation();
    }

    private void metodo() { 
        if (aceitarToken(":")) {
            if (aceitarToken(":")) {
                comSemRetorno();
            }
        }
    }

    private void comSemRetorno() {
        if (aceitarToken("Identificador")) {
            if(classeAtual!=null){
                metodoAtual = classeAtual.getMetodo(tokenAnterior.getNome());
            }else{
                //metodo fora de classe
            }
            if (aceitarToken("(")) {
                parametros();
                if (aceitarToken(")")) {
                    if (aceitarToken("{")) {
                        nivel = 2;
                        program();
                        if (aceitarToken("}")) {
                            nivel = 1;
                            variavelConstanteObjeto();
                            metodo();
                        }
                    }
                }
            } 
        } else if (aceitarToken("bool")) {
            if (aceitarToken("main")) {
                if(classeAtual!=null){
                    metodoAtual = classeAtual.getMetodo(tokenAnterior.getNome());
                }else{
                    //metodo fora de classe
                }
                umaMain++;
                main();
            } else if (aceitarToken("Identificador")) {
                if(classeAtual!=null){
                    metodoAtual = classeAtual.getMetodo(tokenAnterior.getNome());
                }else{
                    //metodo fora de classe
                }
                if (aceitarToken("(")) {
                    parametros();
                    if (aceitarToken(")")) {
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                metodo();
                            }
                        }
                    }
                }
            } 
        } else if (tipo()) {
            if (aceitarToken("Identificador")) {
                if(classeAtual!=null){
                    metodoAtual = classeAtual.getMetodo(tokenAnterior.getNome());
                }else{
                    //metodo fora de classe
                }
                if (aceitarToken("(")) {
                    parametros();
                    if (aceitarToken(")")) {
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                metodo();
                            }
                        } 
                    } 
                } 
            } 
        }
    }

    private void main() {
        if (aceitarToken("(")) {
            if (aceitarToken(")")) {
                if (aceitarToken("{")) {
                    program();
                    if (aceitarToken("}")) {
                        metodo();
                    } 
                } 
            } 
        } 
    }

    private void parametros() {
        if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool") || aceitarToken("Identificador")) {
            if (aceitarToken("Identificador")) {
                acrescentarParametros();
            } 
        }
    }

    private void acrescentarParametros() {
        if (aceitarToken(",")) {
            parametros();
        }
    }

    private void forConsumido() {
          if (aceitarToken("(")) {
            operationFor();
            if (aceitarToken(";")) {
                exLogicRelational(0);
                if (aceitarToken(";")) {
                    operationFor();
                    if (aceitarToken(")")) {
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                program();
                            }
                        }
                    }
                }
            } 
        }
    }

    private void ifConsumido() {
        if (aceitarToken("(")) {
            exLogicRelational(0);
            if (aceitarToken(")")) {
                if (aceitarToken("{")) {
                    program();
                    if (aceitarToken("}")) {
                        if (aceitarToken("else")) {
                            elseConsumido();
                        }
                        program();
                    }
                }
            } 
        }

    }

    private void elseConsumido() {
        if (aceitarToken("{")) {
            program();
            if (aceitarToken("}")) {
            }
        }
    }

    private void scanConsumido() {
        if (aceitarToken("(")) {
            if (aceitarToken("Identificador")) {
                multiplasLeituras();
                if (aceitarToken(")")) {
                    if (aceitarToken(";")) {
                        program();
                    }
                } 
            }
        } 
    }

    private void multiplasLeituras() {
        if (aceitarToken(",")) {
            if (aceitarToken("Identificador")) {
                multiplasLeituras();
            } 
        }
    }

    private void printConsumido() {
        if (aceitarToken("(")) {
            impressao();
            multiplasImpressoes();
            if (aceitarToken(")")) {
                if (aceitarToken(";")) {
                    program();
                } 
            } 
        } 
    }

    private void impressao() {
        operation();
    }

    private void multiplasImpressoes() {
        if (aceitarToken(",")) {
            impressao();
            multiplasImpressoes();
        }
    }

    private void classificarVariavel() {
        if (aceitarToken("-")) {
            if (aceitarToken(">")) {
                escopoVariavel = 0;
                operationLine();
            }
        } else if (aceitarToken(">")) {
            escopoVariavel = 1;
            operationLine();
        }
    }

    private void operationFor() {
        if (aceitarToken("Identificador")) {
            nomeVariavelAtribuicao = tokenAnterior.getNome();
            verificarDeclaracaoVariavel();
            if (aceitarToken("=")) {
                operation();
            } 
        } else {
            acessoVetorMatriz();
            if (aceitarToken("=")) {
                operation();
            }
        }
    }

    private void operation() {
        tipoOperacao = null;
        selectOperation(0);
    }

    private void selectOperation(int parenteses) {
        if (aceitarToken("(")) {
            selectOperation(++parenteses);
        } else {
            value();
            if (aceitarToken("Operador Aritmético")) {
                exAritmeticas(parenteses);
            } else if ((aceitarToken("=") && aceitarToken("=")) || aceitarToken("Operador Relacional") || aceitarToken("Operador Lógico")) {
                exLogicRelational(parenteses);
            } else if (parenteses > 0 && aceitarToken(")")) {

            }
        }
    }

    private int exAritmeticas(int parenteses) {
        if (aceitarToken("(")) {
            parenteses = exAritmeticas(++parenteses);
        } else {
            value();
            if (aceitarToken("Operador Aritmético")) {
                parenteses = exAritmeticas(parenteses);
            } else if (parenteses > 0 && aceitarToken(")")) {
                do {
                    parenteses--;
                } while (parenteses > 0 && aceitarToken(")"));
                if (aceitarToken("Operador Aritmético")) {
                    parenteses = exAritmeticas(parenteses);
                } else {
                    return parenteses;
                }
            } else {
                return parenteses;
            }
        }
        return parenteses;
    }

    private void exLogicRelational(int parenteses) {
        if (aceitarToken("(")) {
            exLogicRelational(++parenteses);
        } else {
            value();
            if ((aceitarToken("=") && aceitarToken("=")) || aceitarToken("Operador Relacional") || aceitarToken("Operador Lógico")) {
                exLogicRelational(parenteses);
            } else if (parenteses > 0 && aceitarToken(")")) {
                do {
                    parenteses--;
                } while (parenteses > 0 && aceitarToken(")"));
                if ((aceitarToken("=") && aceitarToken("=")) || aceitarToken("Operador Relacional") || aceitarToken("Operador Lógico")) {
                    exLogicRelational(parenteses);
                }
            }
        }
    }

    
    private void operationLine() {
        operationFor();
        if (aceitarToken(";")) {
            program();
        } 
    }

    private void acessoVetorMatriz() {
        if (aceitarToken("Identificador")) {
            nomeVariavelAtribuicao = tokenAnterior.getNome();
            verificarDeclaracaoVariavel();
            if (aceitarToken("[")) {
                if (aceitarToken("Número")) {
                    verificarAcessoVetor();
                    if (aceitarToken("]")) {
                        fatoracaoAcessoVetorMatriz();
                    }
                } 
            }
        }
    }

    private void value() {
        String tipo = null;
        if (aceitarToken("Identificador")) {
            if (tokenAtual.getNome().equals("(") || tokenAtual.getNome().equals(":")) {
                if(!passagemParametro){
                    chamadaMetodo();
                    if(metodoChamado!=null){
                        if(metodoChamado.getTipo()!=null){
                           tipo = metodoChamado.getTipo();
                        }else{
                            //tipo incompativel na chamada de metodo
                        }
                }
                }else{
                    //erro não permitida chamada de metodo na passagem de parametros
                }
            } else if (tokenAtual.getNome().equals("[")) {
                tipo = getTipoTokenAtual();
                fatoracaoAcessoVetorMatriz();
            }
        } else if (aceitarToken("Número") || aceitarToken("Cadeia de Caracteres") || aceitarToken("true") || aceitarToken("false")) {
            tipo = getTipoTokenAtual();
        }
        verificarTipoOperacao(tipo);
    }

    private void fatoracaoAcessoVetorMatriz() {
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                if (aceitarToken("]")) {
                    fatoracaoAcessoVetorMatriz();
                } 
            }
        }
    }

    private void criarVariavel() {
        variaveis();
        if (aceitarToken(";")) {
            program();
        } 
    }

    private void instancia() {
        if (aceitarToken(">")) {
            if (aceitarToken("Identificador")) {
                if (aceitarToken("(")) {
                    if (aceitarToken(")")) {
                        if (aceitarToken(";")) {
                        }
                    } else {
                        passagemParametros();
                        if (aceitarToken(")") && aceitarToken(";")) {
                            passagemParametro = false;
                        } 
                    }
                }
            }
        }
    }

    private void passagemParametros() {
        passagemParametro = true;
        operation();
        if (aceitarToken(",")) {
            passagemParametros();
        }
    }

    private void criarObjetos() {
        if (aceitarToken(",")) {
            if (aceitarToken("Identificador")) {
                criarObjetos();
            }
        } else if (aceitarToken(";")) {
        }
    }

    private void chamadaMetodo() {
        objetoChamadaMetodo = tokenAnterior.getNome();
        if (aceitarToken("Identificador")) {
            Variavel obj = buscarObjeto();
            objetoChamadaMetodo = tokenAnterior.getNome();
            if (aceitarToken(":")) {
                if (aceitarToken(":")) {
                    if (aceitarToken("Identificador")) {
                        if(obj!=null){
                            Variavel obj2 = buscarObjeto(obj);
                            if(obj2!=null&&tokenAtual.getNome().equals("(")){
                                objetoChamadaMetodo = tokenAnterior.getNome();
                                buscarChamadaMetodo(obj2);
                            }else if(obj2!=null&&tokenAtual.getNome().equals(";")){
                                buscarObjeto(obj2);
                            }
                        }
                        if (aceitarToken("(")) {
                            fatoracaoChamadaMetodo();
                        } else if (aceitarToken(";")) {
                        } 
                    }
                }
            }
        } else if (aceitarToken(":")) {
            if (aceitarToken(":")) {
                if (aceitarToken("Identificador")) {
                    Variavel obj = buscarObjeto();
                    objetoChamadaMetodo = tokenAnterior.getNome();
                    if(obj!=null&&tokenAtual.getNome().equals("(")){
                        buscarChamadaMetodo(obj);
                    }else if(obj!=null&&tokenAtual.getNome().equals(";")){
                        buscarObjeto(obj);
                    }
                    if (aceitarToken("(")) {
                        fatoracaoChamadaMetodo();
                    } else if (aceitarToken(";")) {
                    }
                }
            }
        } else if (aceitarToken("(")) {
            if(classeAtual!=null){
                metodoChamado = classeAtual.getMetodo(objetoChamadaMetodo);
                if(metodoChamado==null){
                    //metodo não declarado nesse escopo
                }
            }else{
                //chamada de metodo fora de classe
            }
            fatoracaoChamadaMetodo();
        }
    }

    private void fatoracaoChamadaMetodo() {
        if (aceitarToken(")")) {
            passagemParametro = false;
            if (aceitarToken(";")) {
            }
        } else {
            passagemParametros();
            if (aceitarToken(")")) {
                passagemParametro = false;
                if (aceitarToken(";")) {
                }
            }
        }
    }
    
    private boolean isFloat(){
        return tokenAnterior.getNome().contains(".");
    }
    
    private void verificarTipoConstante(){
        if(variavelAtual.getTipo().equals("int")&&isFloat()){
            System.out.println("erro tipo incompativel atribuido a constante");
        }else if(variavelAtual.getTipo().equals("float")&&!isFloat()){
            System.out.println("erro tipo incompativel atribuido a constante");
        }
    }

    private void verificarAcessoVetor() {
        if(isFloat()||Integer.parseInt(tokenAtual.getNome())<0){
            System.out.println("erro indice invalido");
        }
    }

    private void verificarDeclaracaoVariavel() {
        if(escopoVariavel==0){ //global
            variavelAtual = global.getVariavel(nomeVariavelAtribuicao);
            if(variavelAtual==null){
                System.out.println("variavel não declada nesse escopo");
            }
        }else if(escopoVariavel==1){ //classe
            if(classeAtual==null){
                System.out.println("variavel não declada nesse escopo");
            }else{
                variavelAtual = classeAtual.getVariavel(nomeVariavelAtribuicao);
                if(variavelAtual==null){
                    System.out.println("variavel não declada nesse escopo");
                }
            }
        }else if(escopoVariavel==-1){
            if(nivel==0){
                variavelAtual = global.getVariavel(nomeVariavelAtribuicao);
            }else if(nivel == 1){
                variavelAtual = classeAtual.getVariavel(nomeVariavelAtribuicao);
            }else if (nivel == 2){
                variavelAtual = metodoAtual.getVariavel(nomeVariavelAtribuicao);
            }
        }
    }
    
    private Variavel buscarObjeto(){
        Variavel objetoAtual = metodoAtual.getVariavel(objetoChamadaMetodo);
        if(objetoAtual == null){
            objetoAtual = classeAtual.getVariavel(objetoChamadaMetodo);
        }
        if(objetoAtual == null){
            objetoAtual = global.getVariavel(objetoChamadaMetodo);
        }
        if(objetoAtual == null){
            //erro objeto não encontrado
        }
        return objetoAtual;
    }
    
    private void buscarChamadaMetodo(Variavel objetoAtual){
        String tipo = objetoAtual.getTipo();
        if(tipo ==null || tipo.equals("float") || tipo.equals("int") || tipo.equals("string") || tipo.equals("bool")){
            //erro tipo do objeto incompativel
        }else{
            Classe c = global.getClasse(tipo);
            if(c==null){
                //tipo do objeto não existe
            }else{
                metodoChamado = c.getMetodo(tokenAnterior.getNome());
                if(metodoChamado==null){
                    //metodo não declarado nesse escopo
                }   
            }
        }
    }
    
    private Variavel buscarObjeto(Variavel obj) {
        String tipo = obj.getTipo();
        if(tipo ==null || tipo.equals("float") || tipo.equals("int") || tipo.equals("string") || tipo.equals("bool")){
            //erro tipo do objeto incompativel
        }else{
            Classe c = global.getClasse(tipo);
            if(c==null){
                //tipo do objeto não existe
            }else{
                return c.getVariavel(objetoChamadaMetodo);
            }
        }
        return null;
    }

    private Variavel buscarVariavel() {
        Variavel v;
        if(metodoAtual!=null){
            v = metodoAtual.getVariavel(tokenAnterior.getNome());
        }else if(classeAtual!=null){
            v = classeAtual.getVariavel(tokenAnterior.getNome());
        }else{
            v = global.getVariavel(tokenAnterior.getNome());
        }
        return v;
    }

    private String getTipoTokenAtual(){
        String tipo = tokenAnterior.getTipo();
        if(tipo.equals("identificador")){
            Variavel v = buscarVariavel();
            if(v!=null){
                return v.getTipo();
            }else{
                //erro variavel não declarada
            }
        }else if(tipo.equals("true")||tipo.equals("false")){
            return "bool";
        }else if(tipo.equals("Cadeia de Caracteres")){
            return "string";
        }else if(tipo.equals("Número")&&isFloat()){
            return "float";
        }else if(tipo.equals("Número")&&!isFloat()){
            return "int";
        }
        return "";
    }
    
    private void verificarTipoOperacao(String tipo){
        if(tipo==null){
            //erro tipo desconhecido
            return;
        }
        if(tipoOperacao==null){
            tipoOperacao = tipo;
        }else if(!tipoOperacao.equals(tipo)){
            //erro tipo incompativel
        }
        
    }
}
