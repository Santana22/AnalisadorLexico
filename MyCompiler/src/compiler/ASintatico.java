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


    private boolean expect(String tipo) {
        if (aceitarToken(tipo)) {
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
        if (aceitarToken("Identificador")) {
            if (aceitarToken("=")) {
                if (aceitarToken("Número")) {
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
        if (aceitarToken("Identificador")) {
            if (aceitarToken(":")) {
                if (aceitarToken(":")) {
                    if (aceitarToken("Identificador")) {
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
        } else {
            passagemParametros();
            if (aceitarToken(")")) {
                if (aceitarToken(";")) {
                }
            }
        }
    }

}
