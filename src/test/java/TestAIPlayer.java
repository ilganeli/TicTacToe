import com.homework.Board;
import com.homework.Player;
import com.homework.impl.AIPlayer;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestAIPlayer {
    @Test
    public void getNextRandomMove() {
        Player p = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", AIPlayer.Algorithm.RANDOM);
        // Since by default moves are randomly chosen, check that even with a large number
        // of randomly chosen moves, returned moves are valid
        for (int i = 0; i < 100; i++) {
            Board b = new Board(3);
            assertTrue(b.isValidMove(p.getNextMove(b)));
        }
    }

    /**
     * Run a simulation of two AI players playing multiple games. Ensure that the game is either won
     * or draws.
     */
    @Test
    public void verifyAiFillsBoard() {
        Player p = new AIPlayer(Board.PlayerId.PLAYER_ONE, "AI_1", AIPlayer.Algorithm.RANDOM);
        Player p2 = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI_2", AIPlayer.Algorithm.RANDOM);

        // Since by default moves are randomly chosen, check that even with a large number
        // of randomly chosen moves, returned moves are valid
        for (int i = 0; i < 500; i++) {
            Board b = new Board(3);
            int moveCounter = 0;
            Player currentPlayer = p;
            int nextMove = currentPlayer.getNextMove(b);
            assertTrue(b.isValidMove(nextMove));
            while (b.isValidMove(nextMove)) {
                moveCounter++;
                assertTrue(b.move(nextMove, currentPlayer.getPlayerId()));
                currentPlayer = currentPlayer == p? p2 : p;

                if (b.validMovesRemain() && !b.isBoardWon()) {
                    nextMove = currentPlayer.getNextMove(b);
                    assertTrue(b.isValidMove(nextMove));
                }
            }

            assertTrue(moveCounter >= 5); // Need at least 5 moves to win a 3x3 board
            assertTrue(b.isBoardWon() || !b.validMovesRemain());
        }
    }

    // wonBoard is used in subsequent tests. Ensure that it works by itself without throwing exceptions
    @Test
    public void checkBoardWon() {
        Player p = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", AIPlayer.Algorithm.RANDOM);
        Player p2 = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", AIPlayer.Algorithm.GREEDY);
        wonBoard(p);
        wonBoard(p2);
    }

    // fullBoard is used in subsequent tests. Ensure that it works by itself without throwing exceptions
    @Test
    public void checkBoardFull() {
        Player p = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", AIPlayer.Algorithm.RANDOM);
        Player p2 = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", AIPlayer.Algorithm.GREEDY);
        fullBoard(p);
        fullBoard(p2);
    }

    @Test(expected = IllegalStateException.class)
    public void testFullBoard_Random() {
        Player p = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", AIPlayer.Algorithm.RANDOM);
        Board b = fullBoard(p);
        p.getNextMove(b);
    }

    @Test(expected = IllegalStateException.class)
    public void testWonBoard_Random() {
        Player p = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", AIPlayer.Algorithm.RANDOM);
        Board b = wonBoard(p);
        p.getNextMove(b);
    }

    @Test(expected = IllegalStateException.class)
    public void testFullBoard_Greedy() {
        Player p = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", AIPlayer.Algorithm.GREEDY);
        Board b = fullBoard(p);
        p.getNextMove(b);
    }

    @Test(expected = IllegalStateException.class)
    public void testWonBoard_Greedy() {
        Player p = new AIPlayer(Board.PlayerId.PLAYER_TWO, "AI", AIPlayer.Algorithm.GREEDY);
        Board b = wonBoard(p);
        p.getNextMove(b);
    }

    private Board fullBoard(Player p) {
        Board b = new Board(3);
        assertTrue(b.move(1, Board.PlayerId.PLAYER_ONE));
        assertTrue(b.move(3, Board.PlayerId.PLAYER_TWO));
        assertTrue(b.move(2, Board.PlayerId.PLAYER_ONE));
        assertTrue(b.move(4, Board.PlayerId.PLAYER_TWO));
        assertTrue(b.move(6, Board.PlayerId.PLAYER_ONE));
        assertTrue(b.move(5, Board.PlayerId.PLAYER_TWO));
        assertTrue(b.move(7, Board.PlayerId.PLAYER_ONE));
        assertTrue(b.move(9, Board.PlayerId.PLAYER_TWO));
        assertTrue(b.validMovesRemain());
        assertTrue(b.move(8, Board.PlayerId.PLAYER_ONE));
        assertFalse(b.validMovesRemain());
        return b;
    }

    private Board wonBoard(Player p) {
        Board b = new Board(3);
        assertTrue(b.move(1, Board.PlayerId.PLAYER_ONE));
        assertTrue(b.move(4, Board.PlayerId.PLAYER_TWO));
        assertTrue(b.move(2, Board.PlayerId.PLAYER_ONE));
        assertTrue(b.move(5, Board.PlayerId.PLAYER_TWO));
        assertFalse(b.isBoardWon());
        assertTrue(b.validMovesRemain());
        assertTrue(b.move(3, Board.PlayerId.PLAYER_ONE));
        assertTrue(b.isBoardWon());
        assertTrue(b.validMovesRemain());
        return b;
    }
}

