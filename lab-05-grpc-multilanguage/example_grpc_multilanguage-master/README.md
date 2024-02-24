# gRPC example

This is a gRPC example application, composed of three modules:
- [contract](contract/) - protocol buffers definition
- [server](server/) - implementation of service
- [client](client/) - invocation of service
- [python_client](python_client/) - invocation of service

## Instructions
Create a [python virtual environment](https://docs.python.org/3/library/venv.html) and install de grpc packages
```
 # virtualenv is only used in Python2, pyvenv is deprecated
 # you may need to replace python with python3 on your machine

 # create a virtual environment
 # windows
 > python -m venv .venv
 > .venv\Scripts\activate

 # linux
 $ python -m venv .venv
 $ source .venv/bin/activate

 # install the packages
 python -m pip install grpcio
 python -m pip install grpcio-tools

```

See the README for each module.  
Start at contract, then go to server, and finally go to the python_client.

----

[SD Faculty](mailto:leic-sod@disciplinas.tecnico.ulisboa.pt)
