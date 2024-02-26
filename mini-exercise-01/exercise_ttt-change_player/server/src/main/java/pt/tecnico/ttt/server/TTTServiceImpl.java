package pt.tecnico.ttt.server;

import io.grpc.stub.StreamObserver;
import pt.tecnico.ttt.*;

import static io.grpc.Status.INVALID_ARGUMENT;

public class TTTServiceImpl extends TTTGrpc.TTTImplBase {

	/** Game implementation. */
	private TTTGame ttt = new TTTGame();

	@Override
	public void currentBoard(CurrentBoardRequest request, StreamObserver<CurrentBoardResponse> responseObserver) {
		// StreamObserver is used to represent the gRPC stream between the server and
		// client in order to send the appropriate responses (or errors, if any occur).

		CurrentBoardResponse response = CurrentBoardResponse.newBuilder().setBoard(ttt.toString()).build();

		// Send a single response through the stream.
		responseObserver.onNext(response);
		// Notify the client that the operation has been completed.
		responseObserver.onCompleted();
	}

	@Override
	public void play(PlayRequest request, StreamObserver<PlayResponse> responseObserver){

		PlayResult result = ttt.play(request.getRow(), request.getColumn(), request.getPlayer());

		if (result == PlayResult.OUT_OF_BOUNDS){
			responseObserver.onError(INVALID_ARGUMENT.withDescription("Input has to be a valid position").asRuntimeException());
		}
		else{
			// Send a single response through the stream.
			PlayResponse response = PlayResponse.newBuilder().setPlayResult(result).build();
			responseObserver.onNext(response);
			// Notify the client that the operation has been completed.
			responseObserver.onCompleted();
		}
	}

	@Override
	public void checkWinner(CheckWinnerRequest request, StreamObserver<CheckWinnerResponse> responseObserver){

		CheckWinnerResponse response = CheckWinnerResponse.newBuilder().setResult(ttt.checkWinner()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override 
	public void waitForWinner(WaitForWinnerRequest request, StreamObserver<WaitForWinnerResponse> responseObserver){

		WaitForWinnerResponse response = WaitForWinnerResponse.newBuilder().setResult(ttt.waitForWinner()).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override 
	public void changePlayer(ChangePlayerRequest request, StreamObserver<ChangePlayerResponse> responseObserver){

		ChangePlayerResponse response = ChangePlayerResponse.newBuilder().setPlayResult(ttt.changePlayer(request.getPlayer())).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

}
