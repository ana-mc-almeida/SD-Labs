package pt.tecnico.grpc.client;

/* these imported classes are generated by the hello-world-server contract */
import pt.tecnico.grpc.HelloWorld;
import pt.tecnico.grpc.HelloWorldServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HelloClient {

	public static void main(String[] args) throws Exception {
		System.out.println(HelloClient.class.getSimpleName());

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", HelloClient.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);

		final int numServers = 2;

		// Channel is the abstraction to connect to a service endpoint
		// Let us use plaintext communication because we do not have certificates
		ManagedChannel[] channels = new ManagedChannel[numServers];
		HelloWorldServiceGrpc.HelloWorldServiceStub[] stubs = 
			new HelloWorldServiceGrpc.HelloWorldServiceStub[numServers];

		for (int i = 0; i < numServers; i++) {
			String target = host + ":" + (port + i);
			channels[i] = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
			stubs[i] = HelloWorldServiceGrpc.newStub(channels[i]);
		}
		
		ResponseCollector c = new ResponseCollector();
			
		HelloWorld.HelloRequest request = HelloWorld.HelloRequest.newBuilder().setName("Alice").build();
		stubs[0].greeting(request, new HelloObserver(c));

		request = HelloWorld.HelloRequest.newBuilder().setName("Bob").build();
		stubs[1].greeting(request, new HelloObserver(c));

		c.waitUntilStringReceived("Hello Alice");

		System.out.println("Collected strings: " + c.getStrings());

		System.out.println("Shutting down");

		for (ManagedChannel ch : channels)
			ch.shutdown();		
	}

}
