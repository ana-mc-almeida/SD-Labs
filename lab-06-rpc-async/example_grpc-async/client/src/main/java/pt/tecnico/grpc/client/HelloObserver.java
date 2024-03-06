package pt.tecnico.grpc.client;

import io.grpc.stub.StreamObserver;

public class HelloObserver<R> implements StreamObserver<R> {
    @Override
    public void onNext(R r) {
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
