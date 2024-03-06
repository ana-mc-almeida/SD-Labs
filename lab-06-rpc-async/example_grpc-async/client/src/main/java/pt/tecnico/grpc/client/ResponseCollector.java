package pt.tecnico.grpc.client;

import java.util.ArrayList;
import java.util.List;

public class ResponseCollector {
    private List<String> responses = new ArrayList<String>();

    public synchronized void addResponse(String response){
        responses.add(response);
        notifyAll();
    }

    public synchronized List<String> getResponses(){
        return responses;
    }

    public synchronized void waitUntilAllReceived(int n) throws InterruptedException{
        while (responses.size()<n){
            wait();
        }
    }
}
