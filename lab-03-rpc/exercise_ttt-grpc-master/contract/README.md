# gRPC TTT - contract

This small library contains the protocol buffers (`protobuf`) of the Tic Tac Toe gRPC application.

The interface is shared by the server, that implements it, and by the client, that needs it to make remote calls.


## Instructions for using Maven

To compile and install:

```
mvn install
```


## To configure the Maven project in Eclipse

'File', 'Import...', 'Maven'-'Existing Maven Projects'

'Select root directory' and 'Browse' to the project base folder.

Check that the desired POM is selected and 'Finish'.


### Note: issue with Apple M1 processors

gRPC has an [open issue with these processors](https://github.com/grpc/grpc-java/issues/7690). 
To work around the issue, you should find the two <protocArtifact> tags in your POM and replace these tags:
  
    <protocArtifact>com.google.protobuf:protoc:${version.protoc}:exe:${os.detected.classifier}</protocArtifact>
    <pluginId>grpc-java</pluginId>
    <pluginArtifact>io.grpc:protoc-gen-grpc-java:${version.grpc}:exe:${os.detected.classifier}</pluginArtifact>

With these:
  
    <protocArtifact>com.google.protobuf:protoc:${version.protoc}:exe:osx-x86_64</protocArtifact>
    <pluginId>grpc-java</pluginId>
    <pluginArtifact>io.grpc:protoc-gen-grpc-java:${version.grpc}:exe:osx-x86_64</pluginArtifact>
  
----

[SD Faculty](mailto:leic-sod@disciplinas.tecnico.ulisboa.pt)
