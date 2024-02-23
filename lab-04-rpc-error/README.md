   Tratamento de erros com gRPC e métodos remotos bloqueantes body { padding-left: 1em; padding-right: 1em; } .underlined { text-decoration: underline !important; }

[Labs SD](../index.html) >

Tratamento de erros com gRPC e métodos remotos bloqueantes
==========================================================

Objetivos da semana
-------------------

*   Aprender a enviar e receber erros com gRPC
*   Aprender a implementar métodos remotos bloqueantes

Comece por ler os materiais de apoio à aula
-------------------------------------------

*   [Tratamento de erros com gRPC](https://tecnico-distsys.github.io/04-rpc-error/grpc-error/index.html) [Concorrência e Sincronização em Java](https://tecnico-distsys.github.io/02-tools-sockets/java-synch/index.html)

* * *

Exercício
---------

O ponto de partida será [a solução que se espera que o grupo tenha construído na aula anterior para o Jogo do Galo em gRPC](https://tecnico-distsys.github.io/03-rpc/index.html).

> A minha solução está [aqui](../lab-03-rpc/exercise_ttt-grpc-master/)

O objetivo deste novo exercício é estender essa solução de modo a ser devolvido um erro caso um pedido de jogada leve argumentos inválidos, assim como adicionar-lhe alguns testes unitários.

Vamos então começar!

### Enviar informação de erro do servidor para o cliente

Vamos agora adicionar um retorno de erro ao servidor caso a mensagem do pedido seja com uma jogada fora do tabuleiro. Relembramos que a operação play recebe o nome do jogador, e a coluna e a linha em que o mesmo pretende fazer umas jogada.

1.  Comece por aprender sobre ler os materiais sobre [o tratamento de erros com gRPC](grpc-error/index.html).
2.  Vamos agora estender a sua solução. No servidor, comece por importar a definição de um estado de erro para argumentos inválidos:
    
    import static io.grpc.Status.INVALID\_ARGUMENT;
    ...
                    
    
3.  Verifique se a jogada está fora do tabuleiro e, em caso afirmativo, devolver o erro.
    
    ...
    PlayResult result = ttt.play(row, column, player);
    
    if (result == PlayResult.OUT\_OF\_BOUNDS){
        responseObserver.onError(INVALID\_ARGUMENT.withDescription("Input has to be a valid position").asRuntimeException());
    }
    else{
        // Send a single response through the stream.
        PlayResponse response = PlayResponse.newBuilder().setPlay(result).build();
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    } 
    ...
                    
    
4.  Do lado do cliente, deve apanhar uma exceção e imprimir a mensagem de erro:
    
    play\_res = null;
    ...
    try{
        play\_res =  stub.play(PlayRequest.newBuilder().setRow(row).setColumn(column).setPlayer(player).build()).getPlay();
        if (play\_res != PlayResult.SUCCESS) {
            displayResult(play\_res);
        }
    }
    catch (StatusRuntimeException e) {
        System.out.println("Caught exception with description: " + 
            e.getStatus().getDescription());
    } 
                    
    
      
    

### Implementar um método bloqueante

Vamos agora adicionar uma variante bloqueante da operação checkWinner.

1.  No ficheiro .proto, acrescente uma nova operação chamada waitForWinner, cujas mensagens de pedido e respostas são idênticas às da operação checkWinner. A grande diferença é que a waitForWinner deve bloquear-se enquanto o jogo não tiver terminado.
2.  Depois de gerar os novos _stubs_, acrescente o novo método à classe do servidor.
3.  [Relembre as primitivas para programação concorrente em Java](../02-tools-sockets/java-synch/index.html)
4.  No novo método, use a primitiva wait() para, enquanto o jogo não tenha ainda terminado, a _thread_ que executa esse método se bloquear. Lembre-se que, para chamar wait(), precisa estar dentro de um método (ou bloco) synchronized.
5.  Precisa também chamar notifyAll() sempre que o estado do jogo muda com uma nova jogada.
6.  Finalmente, estenda o cliente para também invocar esta nova operação.
7.  Experimente! Lance um cliente que fará as jogadas. Em paralelo, lance outro cliente que simplesmente invoca waitForWinner.

Atenção que na próxima semana há mini exercício!
------------------------------------------------

Na próxima aula laboratorial (consultar o calendário das aulas laboratoriais), ser-lhe-á entregue uma alínea adicional que estende a solução construída pelo guião acima (por exemplo, criando novos procedimentos remotos que representem novas operações sobre o jogo e fazer o tratamento de possíveis excepções que ocorram devido ao uso erróneo das mesmas). É, pois, esperado que, nessa aula, cada estudante traga este guião inteiramente resolvido.

* * *

© Docentes de Sistemas Distribuídos, [Dep. Eng. Informática](http://www.dei.tecnico.ulisboa.pt/), [Técnico Lisboa](http://www.ist.eu)