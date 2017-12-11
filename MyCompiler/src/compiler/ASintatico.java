package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emerson e Vinicius
 */
public class ASintatico {

    private Token tokenAtual, tokenAnterior;
    private ArrayList<Token> tokens;
    private int errosSintaticos = 0;
    private int posicao = -1;
    private BufferedWriter saidaSintatico;

    public void iniciar(ArrayList tokens, File file) {

        FileWriter output;
        try {
            output = new FileWriter(new File(file.getParent(), "output_sin_" + file.getName()));
            saidaSintatico = new BufferedWriter(output);
            saidaSintatico.write("Análise Sintática iniciada para o arquivo " + file.getName());
            saidaSintatico.newLine();
            System.out.println("Análise Sintática iniciada para o arquivo " + file.getName());
            this.tokens = tokens;
            inicio();
            if (errosSintaticos == 0) {
                saidaSintatico.write("Análise Sintática finalizada com sucesso para o arquivo " + file.getName());
            }
            saidaSintatico.close();

        } catch (IOException ex) {
            Logger.getLogger(ASintatico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void erroSintatico(String expected[]) {
        errosSintaticos++;
        String expectedTokenNames = "";

        for (int i = 0; i < expected.length; i++) {
            expectedTokenNames += expected[i].toString();
            if (i < expected.length - 1) {
                expectedTokenNames += ", ";
            }
        }
        String errorMsg = String.format("Erro na linha %d. Esperava: %s. Obteve: %s.",
                tokenAtual.getLinha(), expectedTokenNames, tokenAtual.getNome()
                + " " + tokenAtual.getTipo());
        System.out.println(errorMsg);
        try {
            saidaSintatico.write(errorMsg);
            saidaSintatico.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

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
//            try {
//                saidaSintatico.write("Token Atual: " + tokenAtual.toString());
//                saidaSintatico.newLine();
//            } catch (IOException ex) {
//                Logger.getLogger(ASintatico.class.getName()).log(Level.SEVERE, null, ex);
//            }
            System.out.println("Token Atual: " + tokenAtual.toString());
            proximoToken();
            return true;
        }
        return false;
    }

    private void modoPanico(String sync[]) {
        ArrayList<String> cSync = new ArrayList();
        for (String string : sync) {
            if (!cSync.contains(string)) {
                cSync.add(string);
            }
        }

        while (!cSync.contains(tokenAtual.getNome()) || cSync.contains(tokenAtual.getTipo())) {
//            try {
//                saidaSintatico.write("\tToken Consumido: " + tokenAtual.getNome());
//                saidaSintatico.newLine();
//            } catch (IOException ex) {
//                Logger.getLogger(ASintatico.class.getName()).log(Level.SEVERE, null, ex);
//            }
            System.out.println("\tToken Consumido: " + tokenAtual.getNome());
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
        String sync[] = new String[1];
        sync[0] = ";";
        if (aceitarToken("final")) {
            tipo();
            tratamentoConstante();
            if (!aceitarToken(";")) {
                modoPanico(sync);
                aceitarToken(";");
                variavelConstanteObjeto();
            }
            variavelConstanteObjeto();
        } else if (aceitarToken("Identificador")) {
            if (aceitarToken("Identificador")) {
                criarObjetos();
            } else if (aceitarToken("=")) {
                instancia();
            } else {
                chamadaMetodo();
            }
            variavelConstanteObjeto();
        } else if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool")) {
            tipoVazio();
            tratamentoVariavel();
        } else {

        }
    }

    private void classe() {
        if (aceitarToken("class")) {
            if (aceitarToken("Identificador")) {
                herancaNao();
                if (aceitarToken("{")) {
                    variavelConstanteObjeto();
                    metodo();
                    if (aceitarToken("}")) {
                        classe();
                    }
                }
            }
        }

    }

    private boolean tipo() {
        if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool")) {
            return true;
        }
        return false;
    }

    private void tipoVazio() {

    }

    private void tratamentoConstante() {
        String sync[] = new String[10];
        sync[0] = "{";
        sync[1] = "}";

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
        String sync[] = new String[10];
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
        if (aceitarToken("<")) {
            if (aceitarToken("-")) {
                if (aceitarToken(">")) {
                    if (aceitarToken("Identificador")) {

                    }
                }
            }
        }
    }

    /*private void criarObjetosLinhaConsumido() {
        String sync[] = new String[2];

        if (aceitarToken("Identificador")) {
            variosObjetos();
            if (!aceitarToken(";")) {
                sync[0] = ";";
                modoPanico(sync);
                aceitarToken(";");
            }

        } else {
            sync[0] = "Identificador";
            modoPanico(sync);
            aceitarToken("Identificador");
            variosObjetos();
            if (!aceitarToken(";")) {
                sync[0] = ";";
                modoPanico(sync);
                aceitarToken(";");
            }
        }
    }

    private void variosObjetos() {
        String sync[] = new String[10];
        sync[0] = ",";
        if (aceitarToken(",")) {
            if (aceitarToken("Identificador")) {
                variosObjetos();
            } else {
                sync[0] = "Identificador";
                modoPanico(sync);
                aceitarToken("Identificador");
            }
        }
    }*/
    private void variaveis() {
        String sync[] = new String[10];
        if (aceitarToken("Identificador")) {
            fatoracaoVariaveis();
        } else {
            modoPanico(sync);
        }

    }

    private void fatoracaoVariaveis() {
        String sync[] = new String[10];
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                if (aceitarToken("]")) {
                    fatoracaoFatoracaoVariaveis();
                } else {
                    modoPanico(sync);
                }
            } else {
                modoPanico(sync);
            }
        }
        acrescentar();
    }

    private void acrescentar() {
        String sync[] = new String[10];
        if (aceitarToken(",")) {
            variaveis();
        }

    }

    private void fatoracaoFatoracaoVariaveis() {
        String sync[] = new String[10];
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                if (aceitarToken("]")) {
                    fatoracaoFatoracaoVariaveis();
                } else {
                    modoPanico(sync);
                }
            } else {
                modoPanico(sync);
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
            criarVariavel();
        } else if (aceitarToken("-")) {
            classificarVariavel();
        } else if (aceitarToken("Identificador")) {
            if (aceitarToken("=")) {
                instancia();
            } else if (aceitarToken("Identificador")) {
                criarObjetos();
            } else {
                chamadaMetodo();
            }
            program();
        }
    }

    private void returnConsumido() {
        tiposReturn();
        if (aceitarToken(";")) {
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

//    private void variasClasses() {
//        if (aceitarToken("class")) {
//            classe();
//        } else if (tokenAtual.equals("}") && proximoToken()) {
//
//        }
//    }
    private void comSemRetorno() {
        String sync[] = new String[10];
        sync[0] = "{";
        if (aceitarToken("Identificador")) {
            if (aceitarToken("(")) {
                parametros();
                if (aceitarToken(")")) {
                    if (aceitarToken("{")) {
                        program();
                        if (aceitarToken("}")) {
                            variavelConstanteObjeto();
                            metodo();
                        }
                    }
                } else {
                    modoPanico(sync);
                }
            }
        } else if (aceitarToken("bool")) {
            if (aceitarToken("main")) {
                main();
            }

            if (aceitarToken("Identificador")) {
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
        } else {
            tipo();
            if (aceitarToken("Identificador")) {
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
            tipoVazio();
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
        } else {

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
                operationLine();
            }
        } else if (aceitarToken(">")) {
            operationLine();
        }
    }

    private void operationFor() {
        if (aceitarToken("Identificador")) {
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

    private void expressionLogicaRelacional() {
        relacional();
        variasExpression();
    }

    private void operation() {
        selectOperation(0);
    }
    
    private void selectOperation(int parenteses){
        if(aceitarToken("(")){
            selectOperation(++parenteses);
        }else{
            value();
            if(aceitarToken("Operador Aritmético")){
                exAritmeticas(parenteses);
            }else if((aceitarToken("=")&&aceitarToken("="))||aceitarToken("Operador Relacional")||aceitarToken("Operador Lógico")){
                exLogicRelational(parenteses);
            }else if(parenteses>0&&aceitarToken(")")){
                
            }
        }
    }
    
    private void exAritmeticas(int parenteses){
        if(aceitarToken("(")){
            exAritmeticas(++parenteses);
        }else{
            value();
            if(aceitarToken("Operador Aritmético")){
                exAritmeticas(parenteses);
            }else if(parenteses>0&&aceitarToken(")")){
                do{
                    parenteses--;
                }while(parenteses>0&&aceitarToken(")"));
                if(aceitarToken("Operador Aritmético")){
                    exAritmeticas(parenteses);
                }
            }
        }
    }
    
    private void exLogicRelational(int parenteses){
        if(aceitarToken("(")){
            exLogicRelational(++parenteses);
        }else{
            exAritmeticas(parenteses);
            if((aceitarToken("=")&&aceitarToken("="))||aceitarToken("Operador Relacional")||aceitarToken("Operador Lógico")){
                exLogicRelational(parenteses);
            }else if(parenteses>0&&aceitarToken(")")){
                do{
                    parenteses--;
                }while(parenteses>0&&aceitarToken(")"));
                if((aceitarToken("=")&&aceitarToken("="))||aceitarToken("Operador Relacional")||aceitarToken("Operador Lógico")){
                    exLogicRelational(parenteses);
                }
            }
        }
    }

//        if (aceitarToken("-")) {
//            expressionAritmeticasConsumida();
//        } else if (aceitarToken("Número") || aceitarToken("Identificador") || aceitarToken("Cadeia de Caracteres") || aceitarToken("true") || aceitarToken("false")) {
//            valueVazio();
//        }
    private void operationLine() {
        operationFor();
        if (aceitarToken(";")) {
            program();
        }
    }

    private void acessoVetorMatriz() {
        if (aceitarToken("Identificador")) {
            if (aceitarToken("[")) {
                if (aceitarToken("Número")) {
                    if (aceitarToken("]")) {
                        fatoracaoAcessoVetorMatriz();
                    }
                }
            }
        }
    }

    private void expressionAritmeticas() {
        relacionalAritmetica();
    }

    private void relacionalAritmetica() {
        if (aceitarToken("(")) {
            addValor();
            operadorAritmeticos();
            relacionalAritmetica();
            if(aceitarToken(")")){
                continuar();
            }
        } else if (aceitarToken("-")) {
            if (aceitarToken("(")) {
                addValor();
                operadorAritmeticos();
                relacionalAritmetica();
                if(aceitarToken(")")){
                    continuar();
                }
            }
        } else {
            addValor();
            fatoracaoRelacionalAritmetico();
        }
    }

    private void operadorAritmeticos() {
        if (aceitarToken("+") || aceitarToken("-") || aceitarToken("/") || aceitarToken("%") || aceitarToken("*")) {
            relacionalAritmetica();
        } else {
            String sync[] = new String[5];
            sync[0] = "+";
            sync[1] = "*";
            sync[2] = "/";
            sync[3] = "%";
            sync[4] = "-";
            erroSintatico(sync);
            modoPanico(sync);
            operadorAritmeticos();
        }
    }

    private void continuar() {
        if (aceitarToken("+") || aceitarToken("-") || aceitarToken("/") || aceitarToken("%") || aceitarToken("*")) {
            operadorAritmeticosConsumido();
            relacionalAritmetica();
        }

    }

    private void operadorAritmeticosConsumido() {

    }

    private void fatoracaoRelacionalAritmetico() {
        continuar();
    }

    private void addValor() {
        value();
    }

    private void value() {
        if(aceitarToken("Identificador")){
            fatoracaoAcessoVetorMatriz();
        }else if (aceitarToken("Número") || aceitarToken("Cadeia de Caracteres") || aceitarToken("true") || aceitarToken("false")) {
         } /*else{
            String sync[] = new String[5];
            sync[0] = "Número";
            sync[1] = "Identificador";
            sync[2] = "Cadeia de caracteres";
            sync[3] = "true";
            sync[4] = "false";
            erroSintatico(sync);
            modoPanico(sync);
        } */
    }

//    private void valueVazio() {
//
//    }
    private void fatoracaoAcessoVetorMatriz() {
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                if (aceitarToken("]")) {

                }
            }
        }
    }

    private void relacional() {
        if (aceitarToken("(")) {
            expressionLogicaRelacional();
            if (aceitarToken(")")) {

            }
        } else if (aceitarToken("!")) {
            if (aceitarToken("(")) {
                expressionLogicaRelacional();
                if (aceitarToken(")")) {

                }
            }

        } else {
            addValor();
            operadorRelacional();
        }
    }

    private void operadorRelacional() {
        if (aceitarToken("!")) {
            if (aceitarToken("=")) {
                addValor();
            }
        } else if (aceitarToken("=")) {
            if (aceitarToken("=")) {
                addValor();
            }
        } else if (aceitarToken(">")) {
            if (aceitarToken("=")) {
                addValor();
            }
            addValor();
        } else if (aceitarToken("<")) {
            if (aceitarToken("=")) {
                addValor();
            }
            addValor();
        }
    }

    private void criarVariavel() {
        variaveis();
        if (aceitarToken(";")) {
            program();
        }
    }

    private void variasExpression() {
        if (aceitarToken("&")) {
            if (aceitarToken("&")) {
                operadorLogicoVazio();
            }
            variasExp();
        } else if (aceitarToken("|")) {
            if (aceitarToken("|")) {
                operadorLogicoVazio();
                variasExp();
            }
        }
    }

    private void operadorLogicoVazio() {

    }

    private void variasExp() {
        expressionLogicaRelacional();
    }

    private void instancia() {
        if (aceitarToken(">")) {
            if (aceitarToken("Identificador")) {
                if (aceitarToken("(")) {
                    if(aceitarToken(")")){
                        if (aceitarToken(";")) {
                        }
                    }else{
                        passagemParametros();
                        if (aceitarToken(")")&&aceitarToken(";")) {
                        }   
                    }
                    
                }
            }
        }
    }

    private void passagemParametros() {
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
        if (aceitarToken(":")) {
            if (aceitarToken(":")) {
                if (aceitarToken("Identificador")) {
                    if (aceitarToken("(")) {
                        fatoracaoChamadaMetodo();
                    } else if (aceitarToken(";")) {
                    }
                }
            }
        } else if (aceitarToken("(")) {
            fatoracaoChamadaMetodo();
        }
    }

    private void fatoracaoChamadaMetodo() {
        if (aceitarToken(")")) {
            if (aceitarToken(";")) {
            }
        }else{
            passagemParametros();
            if (aceitarToken(")")) {
                if (aceitarToken(";")) {
                }
                }
        }
    }

}
