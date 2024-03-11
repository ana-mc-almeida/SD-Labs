package pt.tecnico.grpc.client;

import io.grpc.stub.StreamObserver;
import pt.tecnico.grpc.HelloWorld.HelloResponse;

public class HelloObserver implements StreamObserver<HelloResponse> {

    ResponseCollector collector;

    public HelloObserver (ResponseCollector c) {
        collector = c;
    }

    @Override
    public void onNext(HelloResponse r) {
        collector.addString(r.getGreeting());
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
