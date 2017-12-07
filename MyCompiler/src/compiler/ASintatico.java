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
            System.out.println("Token Atual: " + tokenAtual.toString());
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
        for (String string : sync) {
            while (!tokenAtual.getTipo().contains(string) && !tokenAtual.getNome().contains(string)) {
                //System.out.println("\tPulou Token: " + tokenAtual.toString());
                if (!proximoToken()) {
                    return;
                }
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
                    variasClasses();
                }
            }
        }

    }

    private void tipo() {
        if (aceitarToken("float") || aceitarToken("int") || aceitarToken("string") || aceitarToken("bool")) {
        }

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

    private void criarObjetosLinha() {

    }

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
        }
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
        } else if (tokenAtual.equals("}") && posicao == tokens.size() - 1) {

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
                expressionLogicalRelacional();
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

    }

    private void scanConsumido() {

    }

    private void printConsumido() {

    }

    private void operationFor() {
        if (aceitarToken("Identificador")) {
            if (aceitarToken("=")) {
                operation();
            }
        }
        acessoVetorMatriz();
        if(aceitarToken("=")) {
            operation();
        }
    }

    private void expressionLogicalRelacional() {

    }

    private void operation() {
    }

    private void acessoVetorMatriz() {

    }
}
