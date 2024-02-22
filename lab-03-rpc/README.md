Invocação de procedimentos remotos com gRPC
===========================================

Objectivos da aula
------------------

*   Distribuir uma aplicação originalmente centralizada usando o gRPC
*   Descrever, em detalhe, os componentes do sistema gRPC

Materiais de apoio à aula
-------------------------

*   [Introdução ao gRPC (slides preparados pelo corpo docente de SD))](./introducao-ao-gRPC.pdf)
*   [Tutorial de gRPC para Java](https://grpc.io/docs/tutorials/basic/java.html)
*   [Documentação de _Protocol Buffers_](https://developers.google.com/protocol-buffers/docs/overview)
*   [API de gRPC para Java](https://grpc.io/grpc-java/javadoc/index.html)

Antes de começar a experimentar programar com gRPC...
-----------------------------------------------------

Comece por folhear os [slides de introdução ao gRPC](./introducao-ao-gRPC.pdf) que fornecemos acima.  
Como são sucintos, é natural que suscitem algumas dúvidas.  
O exercício seguinte ajudará a esclarecê-las, assim como os materiais de apoio listados acima.  
E, claro, pode sempre esclarecer qualquer questão contactando os docentes (em aula, horário de dúvidas, ou moodle).

Exercício a resolver até ao fim da aula
---------------------------------------

Neste exercício iremos transformar uma implementação do Jogo do Galo (_Tic Tac Toe_) numa aplicação distribuída utilizando o gRPC.

![Tic Tac Toe](./ttt.png)  

1.  Estude uma implementação local do Jogo do Galo/_Tic Tac Toe_.
    1.  Faça **_Clone_** ou **_Download_** do código fonte do [jogo ![GitHub](../_img/github.png)](https://github.com/tecnico-distsys/example_ttt) 
    2.  Analise o código do jogo de forma a compreender a implementação.
    3.  Compile e execute o código com o comando:  
        mvn compile exec:java  
          
        
2.  Pretende-se que a nova versão da aplicação seja dividida em dois processos: servidor e cliente, através do gRPC.  
    Vamos começar por estudar a tecnologia gRPC.
    1.  Faça **_Clone_** ou **_Download_** do código fonte do [exemplo gRPC ![GitHub](../_img/github.png)](https://github.com/tecnico-distsys/example_grpc)   
        
    2.  Veja como a aplicação está estruturada em três módulos: _contract_, _server_ e _client_.  
        Cada módulo tem um POM próprio.
    3.  Nos passos seguintes, vamos compilar e executar o exemplo seguindo as instruções README.md de cada módulo.  
        
    4.  Comece pelo módulo contract, executando o comando: mvn install  
        Este comando vai passar pela etapa _generate-sources_, que vai invocar o _protoc_, o compilador de _protocol buffers_ que vai gerar código Java para lidar com os tipos de dados descritos no ficheiro .proto.  
        Familiarize-se com o código e responda às seguintes questões:
        1.  Onde estão definidas as mensagens trocadas entre o cliente e o servidor?
        2.  Onde estão definidos os procedimentos remotos no servidor?
        3.  Onde estão os ficheiros gerados pelo compilador de _Protocol Buffers_?
        4.  Onde são feitas as invocações remotas no cliente?
        5.  As invocações remotas são síncronas (bloqueantes) ou assíncronas?
    5.  Abra uma consola, entre na diretoria do módulo server e corra o servidor:  
        mvn compile exec:java
    6.  Abra uma outra consola, entre na diretoria do módulo client e execute o cliente:  
        mvn compile exec:java  
        Depois de ver o _Hello World_ a funcionar corretamente no seu computador, avance para o passo seguinte.  
          
        
3.  Vamos agora transformar o Jogo do Galo numa aplicação cliente-servidor com gRPC organizada em três módulos.  
    À semelhança do exemplo, o contrato irá definir a interface remota, com detalhes sobre as mensagens a trocar.  
    O servidor irá manter o estado do jogo (tabuleiro).  
    O cliente irá ter a interface utilizador na consola.  
      
    1.  Faça **_Clone_** ou **_Download_** do [código inicial do exercício](https://github.com/tecnico-distsys/exercise_ttt-grpc) ![GitHub](../_img/github.png)  
          
        
    2.  Baseando-se no módulo contract da aplicação de exemplo, modifique o ficheiro .proto com as definições necessárias para as chamadas remotas de procedimentos currentBoard, play e checkWinner.  
        Sugestão: consulte a [documentação dos _Protocol Buffers_](https://developers.google.com/protocol-buffers/docs/overview).
        1.  Declare todas as mensagens de pedido e resposta para cada procedimento do jogo.  
            Note que algumas mensagens podem ser vazias, mas devem ser declaradas na mesma.
        2.  Cada campo deve ter uma etiqueta numérica única.
        3.  Complete o serviço TTT com as definições dos procedimentos que definiu (assim como as mensagens que definiu).
        4.  Instale o módulo com o comando mvn install.  
            Analise o código Java gerado na pasta target/generated-sources/.
            1.  Onde estão definidas as mensagens?
            2.  E os procedimentos?  
                  
                
    3.  Baseando-se no módulo server da aplicação de exemplo, modifique o código inicial do módulo server.
        1.  Confirme que o módulo contract é uma dependência do projeto.
        2.  Modifique a classe TTTServiceImpl de forma a implementar os procedimentos remotos declarados no contrato, utilizando a classe TTTGame (que implementa a lógica do jogo) definida no código base. A classe de implementação do serviço estende a classe do serviço definido no contrato e faz _override_ dos procedimentos declarados no contrato.  
              
            Exemplo de um método:
            
            public class TTTServiceImpl extends TTTGrpc.TTTImplBase {
            	private TTTGame ttt = new TTTGame();
            
            	@Override
            	public void currentBoard(CurrentBoardRequest request, StreamObserver<CurrentBoardResponse> responseObserver) {
            		CurrentBoardResponse response = CurrentBoardResponse.newBuilder().setBoard(ttt.toString()).build();
            		responseObserver.onNext(response);
            		responseObserver.onCompleted();
            	}
            								
            
              
            Relembre a mensagem definida no contrato:
            
            message CurrentBoardRequest {
             	// No arguments for this request.
            }
            
            message CurrentBoardResponse {
            	string board = 1;
            }							
            
        3.  Confirme que a classe TTTServer inicia um servidor numa porta que recebe como argumento, instanciando a classe de implementação do serviço.
        4.  Tenha em conta que o acesso a variáveis partilhadas tem de ser [sincronizado](../02-tools-sockets/java-synch/index.html).
            1.  Porque é que esta sincronização é necessária?
            2.  Onde é que há possibilidade de concorrência?
        5.  Lance o servidor:  
            mvn compile exec:java  
              
            
    4.  Por fim, complete o código do módulo client.
        1.  Confirme que o módulo contract é uma dependência do projeto.
        2.  Confirme que a classe TTTClient instancia um _stub_ do serviço TTT (através de um endereço e porta recebidos como argumentos).
        3.  Adicione as chamadas remotas aos procedimentos play e checkWinner que estão em falta.  
              
            Exemplo de chamada local:
            
            winner = ttt.checkWinner();
            								
            
            Exemplo de chamada remota correspondente:
            
            winner = stub.checkWinner(CheckWinnerRequest.getDefaultInstance()).getResult();
            								
            
    5.  Experimente jogar remotamente através do cliente construído:  
        mvn compile exec:java  
        

* * *

© Docentes de Sistemas Distribuídos, [Dep. Eng. Informática](http://www.dei.tecnico.ulisboa.pt/), [Técnico Lisboa](http://www.ist.eu)