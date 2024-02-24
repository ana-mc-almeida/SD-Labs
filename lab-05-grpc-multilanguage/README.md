gRPC Multi-linguagem
====================

Objetivos
---------

*   Desenvolvimento de aplicações distribuídas com gRPC com múltiplas linguagens de programação.
*   Em particular, desenvolvimento de um servidor de nomes que permite publicar e pesquisar serviços gRPC no servidor de nomes

Materiais de apoio à aula
-------------------------

*   [https://grpc.io/docs/languages/python/basics/](https://grpc.io/docs/languages/python/basics/)

Pré-requisitos
--------------

*   Python 3.5+
*   Packages: [grpcio](https://pypi.org/project/grpcio/), [grpcio-tools](https://pypi.org/project/grpcio-tools/) e [venv](https://docs.python.org/3/library/venv.html)

Setup e instalação das packages
-------------------------------

*   Windows:

*   Correr o seguinte comando para criar um ambiente virtual:
    
        python -m venv .venv
    
*   Correr o comando para ativar o ambiente virtual:
    
        .venv\Scripts\activate
    
*   Correr o comando para instalar a package grpcio:
    
        python -m pip install grpcio
    
*   Correr o comando para instalar a package grpcio-tools:
    
        python -m pip install grpcio-tools
    
*   Correr o comando para desativar o ambiente virtual:
    
        deactivate
    

*   Linux:

*   Correr o seguinte comando para criar um ambiente virtual:
    
        python -m venv .venv
    
*   Correr o comando para ativar o ambiente virtual:
    
        source .venv/bin/activate
    
*   Correr o comando para instalar a package grpcio:
    
        python -m pip install grpcio
    
*   Correr o comando para instalar a package grpcio-tools:
    
        python -m pip install grpcio-tools
    
*   Correr o comando para desativar o ambiente virtual:
    
        deactivate
    

Java vs Python gRPC
-------------------

1.  Começe por fazer **Clone** ou **Download** do código fonte do [grpc\_example\_multilanguage](https://github.com/tecnico-distsys/example_grpc_multilanguage).
2.  Crie um ambiente virtual na diretoria base seguindo as instruções dadas na secção "_Setup e instalação das packages_".
3.  Na diretoria **contract**, compile e execute os seguintes comandos:
    *   `mvn install`
    *   `mvn exec:exec`
    *   Assegure-se que, na sua máquina, o interpretador Python é lançado pelo comando que está indicado na tag [executable no pom](https://github.com/tecnico-distsys/example_grpc_multilanguage/blob/master/contract/pom.xml#L169). Se não for, corrija o valor nessa tag e corra o último comando de novo.
4.  Analise a diretoria `generated-sources/protobuf` e o código gerado nas diretorias `java` e `python`.
5.  Teste o servidor, executando na diretoria **server** o comando `mvn compile exec:java`.
6.  Teste o cliente, executando na diretoria **python\_client** o comando `python client.py`.
7.  Analise as diferenças e as semelhanças entre os dois clientes `java` na pasta **client** e `python` na pasta **python\_code**:

*   Criação de stubs:
    
    *   Java:
        
          final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
          HelloWorldServiceGrpc.HelloWorldServiceBlockingStub stub = HelloWorldServiceGrpc.newBlockingStub(channel);
        
    *   Python:
        
          with grpc.insecure\_channel('localhost:8080') as channel:
              stub = pb2\_grpc.HelloWorldServiceStub(channel)
        
*   Chamadas aos procedimentos remotos:
    
    *   Java:
        
          HelloWorld.HelloRequest request = HelloWorld.HelloRequest.newBuilder().setName("friend").build();
          HelloWorld.HelloResponse response = stub.greeting(request);
        
    *   Python:
        
          response = stub.greeting(pb2.HelloRequest(name\='friend'))
        

9.  Termine agora o servidor `java` e teste o servidor `python` na pasta **python\_server** correndo o comando `python HelloServer.py`. Corra ambos os clientes `java` e `python`.
10.  Analise as diferencças e as semelhanças entre os dois servidores `java` na pasta **server** e `python` na pasta **python\_server**:

*   Adição do serviço ao servidor:
    
    *   Java:
        
          Server server = ServerBuilder.forPort(port).addService(impl).build();;
        
    *   Python:
        
          pb2\_grpc.add\_HelloWorldServiceServicer\_to\_server(HelloWorldServiceImpl(), server)
        
*   Acesso aos campos dos pedidos:
    
    *   Java:
        
          List hobbies = request.getHobbiesList();
        
    *   Python:
        
          hobbies = request.hobbies
        

Sobre a compilação do proto para Python
---------------------------------------

*   O comando descrito abaixo gera 2 ficheiros .py na <diretoria-output> indicada: o <nome-do-proto>\_pb2.py e o <nome-do-proto>\_pb2\_grpc.py com classes que representam os tipos de dados das mensagens e com classes de suporte ao servidor e ao cliente do RPC. Nos exemplos deste guião a compilação é automatizada com o Exec Maven Plugin.

python -m grpc\_tools.protoc -I<pasta-para-o-contrato> --python\_out=<diretoria-output> --grpc\_python\_out=<diretoria-output> <protos-para-compilar>

Exercício
---------

Neste exercício iremos implementar um servidor de nomes **em Python** que oferece esse serviço, usando a tecnologia gRPC, a outros processos programados em Java (ou outra linguagem).

O servidor de nomes permitirá que outros processos servidores registem o serviço remoto que oferecem, bem como que os processos cliente descubram quais os endereços (nome DNS, porto) dos servidores que atualmente oferecem um dado serviço.

Pode haver mais que um servidor a oferecer o mesmo serviço remoto (por exemplo, num serviço replicado), sendo que cada servidor pode ser distinguido por um qualificador (por exemplo, "A", "B" e "C", ou "primary" e "backup").

Este exercício permite-lhe responder a um dos objetivos do projeto de SD. Por essa razão, propomos que use como ponto de partida o código do projeto já desenvolvido até ao momento pelo seu grupo.

### Criação do esqueleto do servidor de nomes no seu projeto

1.  No código base do projeto encontra uma diretoria `NameServer`, que já tem um primeiro esqueleto do código desse servidor. Seguindo as instruções que apresentamos mais abaixo, irá preencher esse esqueleto.
2.  Crie um ficheiro `NameServer.proto` na sub-diretoria apropriada dentro de `Contract`, preenchendo já os elementos `syntax` e `package` apropriadamente. Este ficheiro também será completado mais adiante.
3.  Confirme se o `pom.xml` na diretoria `Contract` já está pronto para gerar _stubs_ a partir desse novo `.proto`, tanto em Java como em Python.

### Implementação da operação de registo

1.  Implemente a operação `register`, que permite a um servidor inserir uma entrada no servidor de nomes.  
    *   Comece por definir esta operação no ficheiro `.proto` adequado.
    *   Esta chamada remota recebe como parâmetros:
    
    *   um nome do serviço oferecido pelo servidor (uma string);
    *   um qualificador associado ao servidor a registar (uma string);
    *   o endereço do servidor, na forma `host:port` (uma string);
    
    *   E deve retornar:
    
    *   uma mensagem de resposta vazia quando for possível registar o servidor;
    *   uma exceção com a descrição `Not possible to register the server` quando não for possível registar o serviço;
    
    *   Configure o código base para ter o servidor de nomes a aguardar ligações no porto definido no enunciado do projeto (neste caso, `5001`).
    *   Crie a classe `ServerEntry` que irá conter a informação para cada servidor, nomeadamente, a combinação `host:port` e o qualificador.
    *   Crie a classe `ServiceEntry` que irá guardar o nome de um serviço e um conjunto de `ServerEntry`s.
    *   Crie a classe `NamingServer` que irá guardar toda a informação que o servidor necessita, ou seja, contém um mapa que permite associar um nome de um serviço à `ServiceEntry` correspondente.
    *   Crie o serviço que `NamingServerServiceImpl` que estende a classe gerada pelo protobuf e implemente a operação `register`.

### Implementação da operação de procura de servidores

2.  Implemente a operação `lookup`, que permite a um cliente encontrar servidores, relativos a um serviço.
    *   Comece por definir esta operação no ficheiro `.proto` adequado.
    *   Este procedimento remoto recebe como parâmetros:
        *   o nome do serviço a que o cliente pretende aceder;
        *   um qualificador que se pretende que os servidores retornados tenham associado.
    *   E deve retornar:
        *   uma lista de servidores para o qualificador e serviço pedidos;
        *   uma lista com todos os servidores do serviço caso não seja dado qualquer qualificador;
        *   uma lista vazia caso o qualificador e/ou o serviço não existam.

### Implementação da operação de remoção de servidores

3.  Implemente a operação `delete`, que permite a um servidor remover-se do registo de nomes.
    *   Comece por definir esta operação no ficheiro `.proto` adequado, tal como foi feito anteriormente.
    *   Esta chamada recebe como parâmetros:
        *   o nome do serviço;
        *   o `host:port` do servidor.
    *   E deve retornar:
    
    *   uma mensagem de resposta vazia quando for possível remover o servidor;
    *   uma exceção como a descrição `Not possible to remove the server` quando não for possível remover o servidor.
    

### Utilização do servidor de nomes pelos cliente e servidor do projeto

3.  Finalmente, adapte o código dos programas cliente e servidor do seu projeto para usarem os serviços implementados acima pelo servidor de nomes.

* * *

© Docentes de Sistemas Distribuídos, [Dep. Eng. Informática](http://www.dei.tecnico.ulisboa.pt/), [Técnico Lisboa](http://www.ist.eu)