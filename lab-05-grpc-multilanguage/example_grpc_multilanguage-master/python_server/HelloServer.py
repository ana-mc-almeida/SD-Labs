import sys
sys.path.insert(1, '../contract/target/generated-sources/protobuf/python')
import grpc
import HelloWorld_pb2 as pb2
import HelloWorld_pb2_grpc as pb2_grpc
from HelloWorldServiceImpl import HelloWorldServiceImpl
from concurrent import futures

if __name__ == '__main__':
    try:
        print("HelloServer started")

        # print received arguments
        print("Received arguments:")
        for i in range(1, len(sys.argv)):
            print("  " + sys.argv[i])

        # check number of arguments
        if len(sys.argv) < 2:
            print("Arguments missing!")
            print("Usage: python server.py <port>")
            exit(1)

        # get port
        port = int(sys.argv[1])

        # create server
        server = grpc.server(futures.ThreadPoolExecutor(max_workers=1))
        # add service
        pb2_grpc.add_HelloWorldServiceServicer_to_server(HelloWorldServiceImpl(), server)
        # listen on port
        server.add_insecure_port('[::]:'+str(port))
        # start server
        server.start()
        # print message
        print("Server listening on port " + str(port))
        # print termination message
        print("Press CTRL+C to terminate")
        # wait for server to finish
        server.wait_for_termination()
    except KeyboardInterrupt:
        print("HelloServer stopped")
        exit(0)
