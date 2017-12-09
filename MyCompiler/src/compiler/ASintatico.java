package compiler;

import java.io.BufferedWriter;
import java.io.File;
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

    public void iniciar(ArrayList tokens, File file) {

        System.out.println("Análise Sintática iniciada para o arquivo " + file.getName());

        this.tokens = tokens;
        inicio();

    }

    private boolean proximoToken() {
        if (posicao + 1 < tokens.size()) {
            posicao++;
            tokenAnterior = tokenAtual;
            tokenAtual = tokens.get(posicao);
            aceitarToken("Comentário"); // Pulando comentarios
            return true;
        }
        return false;
    }

    private boolean aceitarToken(String tipo) {
        if (tokenAtual.getTipo().equals(tipo) || tokenAtual.getNome().equals(tipo)) {
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
            System.out.println("\tToken consumido: " + tokenAtual.getNome());
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
        } else if (aceitarToken("Identificador")) {
            if(aceitarToken("Identificador")){
                criarObjetos();
            }else if(aceitarToken("=")){
                instancia();
            }else{
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
                        variasClasses();
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

    private void variasClasses() {
        if (aceitarToken("class")) {
            classe();
        } else if (tokenAtual.equals("}") && proximoToken()) {

        }
    }

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
        if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool")) {
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
                expressionLogicaRelacional();
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
            expressionLogicaRelacional();
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
        if (aceitarToken("-")) {
            expressionAritmeticasConsumida();
        } else if (aceitarToken("Número") || aceitarToken("Identificador") || aceitarToken("Cadeia") || aceitarToken("true") || aceitarToken("false")) {
            valueVazio();
        } else {

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
            if (aceitarToken("[")) {
                if (aceitarToken("Número")) {
                    if (aceitarToken("]")) {
                        fatoracaoAcessoVetorMatriz();
                    }
                }
            }
        }
    }

    private void expressionAritmeticasConsumida() {
        relacionalAritmetica();
    }

    private void relacionalAritmetica() {
        addValor();
    }

    private void addValor() {
        value();
    }

    private void value() {
        if (aceitarToken("Número") || aceitarToken("Identificador") || aceitarToken("Cadeia") || aceitarToken("true") || aceitarToken("false")) {

        }
    }

    private void valueVazio() {

    }

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
                    passagemParametros();
                    if (aceitarToken(")")) {
                        if (aceitarToken(";")) {
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
        passagemParametros();
        if (aceitarToken(")")) {
            if (aceitarToken(";")) {
            }
        }
    }

}
