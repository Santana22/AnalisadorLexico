package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta classe modela a análise sintática para a Linguagem Jussara.
 *
 * @author Emerson e Vinicius
 */
public class ASintatico {

    private Token tokenAtual, tokenAnterior;
    private ArrayList<Token> tokens;
    private int errosSintaticos = 0;
    private int nivel = 0; //variavel que indica em qual nivel a analise está: nivel 0 - corpo principal, nivel 1 - classe, nivel 2 - metodo e nivel 3 - codicionais
    private int posicao = -1;
    private BufferedWriter saidaSintatico;

    /**
     * Método que inicia a análise sintática.
     *
     * @param tokens lista de tokens extraídos da análise léxica
     * @param file diretório para armazenar os resultados
     */
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
                System.out.println("Análise Sintática finalizada com sucesso para o arquivo " + file.getName());
                saidaSintatico.write("Análise Sintática finalizada com sucesso para o arquivo " + file.getName());
            } else{
                System.out.println("Análise Sintática finalizada com erro para o arquivo " + file.getName());
                saidaSintatico.write("Análise Sintática finalizada com erro para o arquivo " + file.getName());
            }
            saidaSintatico.close();

        } catch (IOException ex) {
            Logger.getLogger(ASintatico.class.getName()).log(Level.SEVERE, null, ex);
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
            proximoToken();
            return true;
        }
        return false;
    }

    private void inicio() {
        proximoToken();
        variavelConstanteObjeto();
        classe();
    }

    private void variavelConstanteObjeto() {
        if (aceitarToken("final")) {
            tipo();
            tratamentoConstante();
            if (aceitarToken(";")) {
                variavelConstanteObjeto();
            } else {
                //panicMode();
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
            nivel = 1;
            if (aceitarToken("Identificador")) {
                herancaNao();
                if (aceitarToken("{")) {
                    variavelConstanteObjeto();
                    metodo();
                    if (aceitarToken("}")) {
                        nivel = 0;
                        classe();
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
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
        if (aceitarToken("Identificador")) {
            if (aceitarToken("=")) {
                if (aceitarToken("Número")) {
                    geradorConstante();
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else {
            panicMode();
        }
    }

    private void tratamentoVariavel() {
        variaveis();
        if (aceitarToken(";")) {
            variavelConstanteObjeto();
        } else {
            panicMode();
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

                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        }
    }

    private void variaveis() {
        if (aceitarToken("Identificador")) {
            fatoracaoVariaveis();
        } else {
            panicMode();
        }

    }

    private void fatoracaoVariaveis() {
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                if (aceitarToken("]")) {
                    fatoracaoFatoracaoVariaveis();
                } else {
                    panicMode();
                }
            } else {
                panicMode();
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
                } else {
                    panicMode();
                }
            } else {
                panicMode();
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
                if (tokenAtual.getNome().equals(">")) {
                    instancia();
                } else {
                    operation();
                    if (aceitarToken(";")) {

                    }
                }
            } else if (aceitarToken("Identificador")) {
                criarObjetos();
            } else if (tokenAtual.getNome().equals("[")) {
                fatoracaoAcessoVetorMatriz();
                if (aceitarToken("=")) {
                    operation();
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
            program();
        } else {
            panicMode();
        }
    }

    private void tiposReturn() {
        operation();
    }

    private void metodo() {
        if (aceitarToken(":")) {
            if (aceitarToken(":")) {
                nivel = 2;
                comSemRetorno();
            } else {
                panicMode();
            }
        }
    }

    private void comSemRetorno() {
        if (aceitarToken("Identificador")) {
            if (aceitarToken("(")) {
                parametros();
                if (aceitarToken(")")) {
                    if (aceitarToken("{")) {
                        program();
                        if (aceitarToken("}")) {
                            nivel = 1;
                            variavelConstanteObjeto();
                            metodo();
                        } else {
                            panicMode();
                        }
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else if (aceitarToken("bool")) {
            if (aceitarToken("main")) {
                main();
            } else if (aceitarToken("Identificador")) {
                if (aceitarToken("(")) {
                    parametros();
                    if (aceitarToken(")")) {
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                metodo();
                            } else {
                                panicMode();
                            }
                        } else {
                            panicMode();
                        }
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else if (tipo()) {
            if (aceitarToken("Identificador")) {
                if (aceitarToken("(")) {
                    parametros();
                    if (aceitarToken(")")) {
                        if (aceitarToken("{")) {
                            program();
                            if (aceitarToken("}")) {
                                metodo();
                            } else {
                                panicMode();
                            }
                        } else {
                            panicMode();
                        }
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else {
            panicMode();
        }
    }

    private void main() {
        if (aceitarToken("(")) {
            if (aceitarToken(")")) {
                if (aceitarToken("{")) {
                    program();
                    if (aceitarToken("}")) {
                        metodo();
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else {
            panicMode();
        }
    }

    private void parametros() {
        if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool") || aceitarToken("Identificador")) {
            tipoVazio();
            if (aceitarToken("Identificador")) {
                acrescentarParametros();
            } else {
                panicMode();
            }
        }
    }

    private void acrescentarParametros() {
        if (aceitarToken(",")) {
            parametros();
        }
    }

    private void forConsumido() {
        nivel = 3;
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
                                nivel = 2;
                                program();
                            } else {
                                panicMode();
                            }
                        } else {
                            panicMode();
                        }
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else {
            panicMode();
        }
    }

    private void ifConsumido() {
        nivel = 3;
        if (aceitarToken("(")) {
            exLogicRelational(0);
            if (aceitarToken(")")) {
                if (aceitarToken("{")) {
                    program();
                    if (aceitarToken("}")) {
                        nivel = 2;
                        if (aceitarToken("else")) {
                            elseConsumido();
                        }
                        program();
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else {
            panicMode();
        }

    }

    private void elseConsumido() {
        nivel = 3;
        if (aceitarToken("{")) {
            program();
            if (aceitarToken("}")) {
                nivel = 2;
            } else {
                panicMode();
            }
        } else {
            panicMode();
        }
    }

    private void scanConsumido() {
        if (aceitarToken("(")) {
            if (aceitarToken("Identificador")) {
                multiplasLeituras();
                if (aceitarToken(")")) {
                    if (aceitarToken(";")) {
                        program();
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else {
            panicMode();
        }
    }

    private void multiplasLeituras() {
        if (aceitarToken(",")) {
            if (aceitarToken("Identificador")) {
                multiplasLeituras();
            } else {
                panicMode();
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
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else {
            panicMode();
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
            } else {
                panicMode();
            }
        } else {
            acessoVetorMatriz();
            if (aceitarToken("=")) {
                operation();
            } else {
                panicMode();
            }
        }
    }

    private void operation() {
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
            parenteses = exAritmeticas(parenteses);
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
        } else {
            panicMode();
        }
    }

    private void acessoVetorMatriz() {
        if (aceitarToken("Identificador")) {
            if (aceitarToken("[")) {
                if (aceitarToken("Número")) {
                    if (aceitarToken("]")) {
                        fatoracaoAcessoVetorMatriz();
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            }
        }
    }

    private void value() {
        if (aceitarToken("Identificador")) {
            if (tokenAtual.getNome().equals("(") || tokenAtual.getNome().equals(":")) {
                chamadaMetodo();
            } else if (tokenAtual.getNome().equals("[")) {
                fatoracaoAcessoVetorMatriz();
            }
        } else if (aceitarToken("Número") || aceitarToken("Cadeia de Caracteres") || aceitarToken("true") || aceitarToken("false")) {
        }
    }

    private void fatoracaoAcessoVetorMatriz() {
        if (aceitarToken("[")) {
            if (aceitarToken("Número")) {
                if (aceitarToken("]")) {
                    fatoracaoAcessoVetorMatriz();
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        }
    }

    private void criarVariavel() {
        variaveis();
        if (aceitarToken(";")) {
            program();
        } else {
            panicMode();
        }
    }

    private void instancia() {
        if (aceitarToken(">")) {
            if (aceitarToken("Identificador")) {
                if (aceitarToken("(")) {
                    if (aceitarToken(")")) {
                        if (aceitarToken(";")) {
                        } else {
                            panicMode();
                        }
                    } else {
                        passagemParametros();
                        if (aceitarToken(")") && aceitarToken(";")) {
                        } else {
                            panicMode();
                        }
                    }

                } else {
                    panicMode();
                }
            } else {
                panicMode();
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
            } else {
                panicMode();
            }
        } else if (aceitarToken(";")) {
        } else {
            panicMode();
        }
    }

    private void chamadaMetodo() {
        if (aceitarToken("Identificador")) {
            if (aceitarToken(":")) {
                if (aceitarToken(":")) {
                    if (aceitarToken("Identificador")) {
                        if (aceitarToken("(")) {
                            fatoracaoChamadaMetodo();
                        } else if (aceitarToken(";")) {
                        } else {
                            panicMode();
                        }
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            }
        } else if (aceitarToken(":")) {
            if (aceitarToken(":")) {
                if (aceitarToken("Identificador")) {
                    if (aceitarToken("(")) {
                        fatoracaoChamadaMetodo();
                    } else if (aceitarToken(";")) {
                    } else {
                        panicMode();
                    }
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        } else if (aceitarToken("(")) {
            fatoracaoChamadaMetodo();
        }
    }

    private void fatoracaoChamadaMetodo() {
        if (aceitarToken(")")) {
            if (aceitarToken(";")) {
            }
        } else {
            passagemParametros();
            if (aceitarToken(")")) {
                if (aceitarToken(";")) {
                } else {
                    panicMode();
                }
            } else {
                panicMode();
            }
        }
    }

    /*
    nivel - 0: corpo principal do arquivo
    nivel - 1: declaracão de classe
    nivel - 2: declaração de metodo
    nivel - 3: estruturas condicionais
     */
    /**
     * Metodo de recuperação de erro
     */
    private void panicMode() {
        try {
            errosSintaticos++;
            saidaSintatico.write("Erro sintático próximo ao token " + tokenAtual.getNome() + " na linha " + tokenAtual.getLinha());
            System.out.println("Erro sintático próximo ao token " + tokenAtual.getNome() + " na linha " + tokenAtual.getLinha());
            saidaSintatico.newLine();
            searchNextSync();
        if (aceitarToken(";")) {
            if (nivel == 0) {
                inicio();
            } else if (nivel == 1) {
                variavelConstanteObjeto();
                metodo();
                if (aceitarToken("}")) {
                    classe();
                }
            } else if (nivel == 2) {
                program();
                if (aceitarToken("}")) {
                    metodo();
                } else {
                    panicMode();
                }
            } else if (nivel == 3) {
                program();
                if (aceitarToken("}")) {
                    program();
                }
            } else {
                panicMode();
            }
        } else if (aceitarToken("{")) {
            if (nivel == 1) {
                variavelConstanteObjeto();
                metodo();
                if (aceitarToken("}")) {
                    classe();
                }
            } else if (nivel == 2) {
                program();
                if (aceitarToken("}")) {
                    metodo();
                } else {
                    panicMode();
                }
            } else if (nivel == 3) {
                program();
                if (aceitarToken("}")) {
                    program();
                }
            } else {
                panicMode();
            }
        } else if (aceitarToken("}")) {
            if (nivel == 1) {
                nivel = 0;
                classe();
            } else if (nivel == 2) {
                nivel = 1;
                variavelConstanteObjeto();
                metodo();
            } else if (nivel == 3) {
                nivel = 2;
                program();
            }
        } else if (tokenAtual.getNome().equals(":")) {
            if (nivel != 0) {
                metodo();
            } else {
                panicMode();
            }
        } else if (tokenAtual.getNome().equals("class")) {
            classe();
        } 
        } catch (IOException ex) {
            Logger.getLogger(ASintatico.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Procura o proximo token de sincronização
     */
    private void searchNextSync() {
        if (tokenAtual.getNome().equals(";") || tokenAtual.getNome().equals("{") || tokenAtual.getNome().equals("}") || (tokenAtual.getNome().equals(":") && showProx() != null && showProx().getNome().equals(":")) || tokenAtual.getNome().equals("class")) {

        } else {
            if (proximoToken()) {
                searchNextSync();
            }
        }
    }

    /**
     * devolve o proximo token caso exista
     *
     * @return
     */
    private Token showProx() {
        if (posicao + 1 < tokens.size()) {
            return tokens.get(posicao + 1);
        }
        return null;
    }

}
