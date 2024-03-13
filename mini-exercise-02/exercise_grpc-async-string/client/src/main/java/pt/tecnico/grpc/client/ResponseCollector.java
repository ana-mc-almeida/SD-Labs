package pt.tecnico.grpc.client;

import java.util.ArrayList;

public class ResponseCollector {
    ArrayList<String> collectedResponses;

    public ResponseCollector() {
        collectedResponses = new ArrayList<String>();
    }

    synchronized public void addString(String s) {
        collectedResponses.add(s);
        notifyAll();
    }

    synchronized public String getStrings() {
        String res = new String();
        for (String s : collectedResponses) {
            res = res.concat(s);
        }
        return res;
    }

    synchronized public void waitUntilAllReceived(int n) throws InterruptedException {
        while (collectedResponses.size() < n) 
            wait();
    }

    synchronized public void waitUntilStringReceived(String str) throws InterruptedException {
        int size = collectedResponses.size();

        while (size == 0 || !collectedResponses.get(size - 1).equals(str)) {
            wait();
            size = collectedResponses.size();
        }
    }
}
