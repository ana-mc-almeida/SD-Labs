package pt.tecnico.grpc.client;

import io.grpc.stub.StreamObserver;
import pt.tecnico.grpc.client.ResponseCollector;
import pt.tecnico.grpc.HelloWorld.HelloResponse;

public class HelloObserver<R> implements StreamObserver<HelloResponse> {

    private ResponseCollector collector;

    public HelloObserver(ResponseCollector collector){
        this.collector = collector;
    }

    @Override
    public void onNext(HelloResponse r) {
        collector.addResponse(r.getGreeting());
        System.out.println("Received response: " + r);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Received error: " + throwable);
    }

    @Override
    public void onCompleted() {
        System.out.println("Request completed");
    }
}
