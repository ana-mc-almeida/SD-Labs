package pt.tecnico.ttt.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.ttt.*;
import pt.tecnico.ttt.PlayResult;

import java.util.Scanner;

public class TTTClient {

	/** Set flag to true to print debug messages. 
	 * The flag can be set using the -Ddebug command line option. */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

	/** The main method is the starting point for the program. */
	public static void main(String[] args) {
		System.out.println(TTTClient.class.getSimpleName());

		// receive and print arguments
		debug(String.format("Received %d arguments", args.length));
		for (int i = 0; i < args.length; i++) {
			debug(String.format("arg[%d] = %s", i, args[i]));
		}

		// check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", TTTClient.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final String target = host + ":" + port;
		debug("Target: " + target);

		// Channel is the abstraction to connect to a service endpoint.
		// Let us use plaintext communication because we do not have certificates.
		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

		// It is up to the client to determine whether to block the call.
		// Here we create a blocking stub, but an async stub,
		// or an async stub with Future are always possible.
		TTTGrpc.TTTBlockingStub stub = TTTGrpc.newBlockingStub(channel);

		playGame(stub);

		// A Channel should be shutdown before stopping the process.
		channel.shutdownNow();
	}

	private static void playGame(TTTGrpc.TTTBlockingStub stub) {

		int player = 0; /* Player number - 0 or 1 */
		int go = 0; /* Square selection number for turn */
		int row = 0; /* Row index for a square */
		int column = 0; /* Column index for a square */
		int winner = -1; /* The winning player */
		PlayResult play_res;

		/*
		 * Using try with scanner - ensures the resource is closed in the end, even if
		 * there are exceptions.
		 */
		try (Scanner scanner = new Scanner(System.in)) {

			/* The main game loop. The game continues for up to 9 turns, */
			/* as long as there is no winner. */
			do {
				/* Get valid player square selection. */
				do {
					/* Print current board. */
					debug("Call currentBoard");
					System.out.println(stub.currentBoard(CurrentBoardRequest.getDefaultInstance()).getBoard());

					System.out.printf(
							"%nPlayer %d, please enter the number of the square "
									+ "where you want to place your %c (or 0 to refresh the board): ",
							player, (player == 1) ? 'X' : 'O');
					go = scanner.nextInt();
					debug("go = " + go);

					if (go == 0) {
						play_res = PlayResult.UNKNOWN;
						continue;
					}

					/* Get row index of board. */
					row = --go / 3;
					/* Get column index of board. */
					column = go % 3;
					debug("row = " + row + ", column = " + column);

					// TODO call play and set the proper play result
					play_res = PlayResult.UNKNOWN;
					if (play_res != PlayResult.SUCCESS) {
						displayResult(play_res);
					}

				} while (play_res != PlayResult.SUCCESS);

				// TODO call check winner and set the winning player.

				/* Select next player. */
				player = (player + 1) % 2;
				debug("player " + player);

			} while (winner == -1);

			/* Game is over so display the final board. */
			debug("Call currentBoard");
			System.out.println(stub.currentBoard(CurrentBoardRequest.getDefaultInstance()).getBoard());

			/* Display result message. */
			if (winner == 2) {
				System.out.println();
				System.out.println("How boring, it is a draw");
			} else {
				System.out.println();
				System.out.println("Congratulations, player " + winner + ", YOU ARE THE WINNER!");
			}
		}
	}

	/** Helper method to display result as text. */
	private static void displayResult(PlayResult play_res) {
		switch (play_res) {
		case OUT_OF_BOUNDS:
			System.out.print("Position outside board.");
			break;
		case SQUARE_TAKEN:
			System.out.print("Square already taken.");
			break;
		case WRONG_TURN:
			System.out.print("Not your turn.");
			break;
		case GAME_FINISHED:
			System.out.print("Game has finished.");
			break;
		default:
			System.out.println("Unexpected result: " + play_res.toString());
			break;
		}
		System.out.println(" Try again...");
	}

}
