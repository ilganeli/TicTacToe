package com.homework;

import com.homework.impl.AIPlayer;
import com.homework.impl.AIPlayer.Algorithm;
import com.homework.impl.HumanPlayer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int MAX_INVALID_MOVE_COUNT = 3;
    private static final int DEFAULT_BOARD_SIZE = 3;

    public static void main(String args[]) {
        Algorithm algo = Algorithm.RANDOM;
        if (args.length > 0) {
            if (args[0].equals("RANDOM")) {
                algo = Algorithm.RANDOM;
            } else if (args[0].equals("GREEDY")) {
                algo = Algorithm.GREEDY;
            } else if (args[0].equals("EVIL")) {
                algo = Algorithm.EVIL;
            } else {
                System.out.println("If attempting to specify algorithm for AI Player, please input either [RANDOM] " +
                        "[EVIL], or [GREEDY]. Defaulting to RANDOM.");
            }
        }

        // TODO Problem spec defines player 1 as human and player 2 as Computer, we could later accept this as args
        List<Player> players = new ArrayList<Player>(2);
        players.add(new HumanPlayer(Board.PlayerId.PLAYER_ONE, "Human"));
        players.add(new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", algo));

        // TODO We could read in board size as command line argument if we wished. Default is 3x3
        Board b = new Board(DEFAULT_BOARD_SIZE);

        // Just to be safe, check that we have enough players
        if (players.size() < 2) {
            throw new Error("Not enough players. Please ensure at least two players are ready.");
        }

        runGame(players, b);
    }

    /**
     * Given a list of players and a board, run the tic tac toe game.
     * @param players List of players. For now, there must be two players.
     * @param b The tic tac toe board of arbitrary size >= 1x1
     */
    private static void runGame(List<Player> players, Board b) {
        if (players.size() != 2) {
            throw new IllegalArgumentException("The game only supports two players presently. " +
                    "Please provide valid input.");
        }

        // In the hypothetical situation where someone (computer or person) keeps making illegal moves, terminate the
        // match and concede victory to the other player.
        int invalidMoveCounter = 0;
        int currentPlayerIdx = 0;

        // Show current board state
        b.showBoard();

        // Safe to check for valid moves remain because TicTacToe is guaranteed to terminate as long as players
        // make valid moves and we include an internal check to ensure that players eventually make a valid move.
        while (b.validMovesRemain()) {
            Player currentPlayer = players.get(currentPlayerIdx);
            int move = getNextValidMove(b, currentPlayer, invalidMoveCounter);

            invalidMoveCounter = 0;

            // Once we reach here, the game is guaranteed not to be won, valid moves remain, and the proposed move
            // is valid. In theory, updated should always be true. Check it to be safe and throw an error if false.
            boolean updated = b.move(move, currentPlayer.getPlayerId());
            if (!updated) throw new Error("Updated should be true since we've checked that moves are available and " +
                    "the proposed move is known to be valid. This would only fail if the state update failed or if " +
                    "one of the above checks is flawed.");

            b.showBoard();

            // Check for victory or draw, otherwise switch player
            // TODO This can be readily updated to be run via callback from Board to avoid checking the logic manually
            if (b.isBoardWon()) {
                win(currentPlayer);
            } else if (!b.validMovesRemain()) {
                draw();
            } else {
                if (++currentPlayerIdx >= players.size()) currentPlayerIdx = 0; // Get next player
            }
        }

        endGame();
    }

    /**
     * A player may make an illegal move, therefore wrap the move logic in some checks to ensure that the game doesn't
     * run on forever if players keep making consecutive illegal moves.
     * @param b The board state
     * @param currentPlayer Current player
     * @param invalidMoveCounter The number of illegal moves to date
     * @return
     */
    private static int getNextValidMove(Board b, Player currentPlayer, int invalidMoveCounter) {
        int move = currentPlayer.getNextMove(b); // This is not guaranteed to be valid so check for validity.
        while (!b.isValidMove(move)) { // Allow some retries to provide a valid move (e.g. typo or algorithm flaw)
            if (invalidMoveCounter++ > MAX_INVALID_MOVE_COUNT) {
                System.out.println("You've made more than " + MAX_INVALID_MOVE_COUNT + " illegal moves. " +
                        "Ending the game. You should really take this more seriously.");
                endGame();
            }
            move = currentPlayer.getNextMove(b);
        }
        return move;
    }

    private static void endGame() {
        System.exit(0);
    }

    public static void win(Player p) {
        System.out.println(p.getName() + " player wins!");
        endGame();
    }

    public static void draw() {
        System.out.println("Game ends in a draw. Better luck next time!");
        endGame();
    }
}
