import com.homework.Board;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BoardTest {
    private Board b;

    @Before
    public void setup() {
        b = new Board(3);
    }

    @Test
    public void testSimpleMoves() {
        /**
         * Set up board as
         * X 0 X
         * O X O
         * X - -
         */
        for (int i = 1; i <= 6; i++)
        {
            assertTrue(b.validMovesRemain());
            // Check that the move is first valid, then stops being valid. Check that the right ID is assigned.
            assertTrue(b.isValidMove(i));
            Board.PlayerId playerId = i % 2 == 1 ? Board.PlayerId.PLAYER_ONE : Board.PlayerId.PLAYER_TWO;
            assertTrue(b.move(i, playerId));
            assertEquals(playerId, b.getCell(i).getPlayerId());
            assertFalse(b.isValidMove(i));
        }

        assertTrue(b.validMovesRemain());
        assertFalse(b.isBoardWon());

        // Player one can win on the next move
        assertTrue(b.isValidMove(7));
        assertTrue(b.move(7, Board.PlayerId.PLAYER_ONE));
        assertFalse(b.isValidMove(7));

        // Game is won and valid moves still remain
        assertTrue(b.isBoardWon());
        assertTrue(b.validMovesRemain());

        // Try to make another move to confirm that nothing changes
        assertFalse(b.move(8, Board.PlayerId.PLAYER_TWO));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveWithNull() {
        assertTrue(b.isValidMove(7));
        assertTrue(b.move(7, null));
    }

    @Test(expected = IllegalStateException.class)
    public void testMoveWithSamePlayer() {
        assertTrue(b.isValidMove(7));
        assertTrue(b.move(7, Board.PlayerId.PLAYER_ONE));

        assertTrue(b.isValidMove(8));
        assertTrue(b.move(8, Board.PlayerId.PLAYER_ONE));
    }

    @Test
    public void testInvalidMove() {
        // Check repeat move
        assertTrue(b.isValidMove(7));
        assertTrue(b.move(7, Board.PlayerId.PLAYER_ONE));

        assertFalse(b.isValidMove(7));
        assertFalse(b.move(7, Board.PlayerId.PLAYER_TWO));

        // Check out of bounds
        for (int i = -100; i < 100; i++) {
            Board b = new Board(3);
            if (i < 1 || i > (b.getBoardSize() * b.getBoardSize())) assertFalse(b.isValidMove(i));
            else assertTrue(b.isValidMove(i));
        }
    }

    @Test
    public void testScoreUpdateOnMove_SingleCell() {
        /**
         * Check that updates to any single cell are reflected in the right score trackers. Check summation later
         */
        int boardSize = 3;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Board b = new Board(boardSize);
                assertEquals(0, b.getRowTotals()[0]);
                assertEquals(0, b.getColTotals()[0]);
                assertEquals(0, b.getBottomToTopDiagTotal());
                assertEquals(0, b.getTopToBottomDiagTotal());
                b.move(b.getMovePosition(row, col), Board.PlayerId.PLAYER_ONE);
                assertEquals(1, b.getRowTotals()[row]);
                assertEquals(1, b.getColTotals()[col]);

                if (b.onBottomToTopDiag(row, col)) assertEquals(1, b.getBottomToTopDiagTotal());
                if (b.onTopToBottomDiag(row, col)) assertEquals(1, b.getTopToBottomDiagTotal());
            }
        }
    }

    @Test
    public void testScoreUpdateOnMove_RowSum() {
        int boardSize = 3;
        int row = 1;
        int row2 = 2;
        Board b = new Board(boardSize);
        assertEquals(0, b.getRowTotals()[0]);
        assertEquals(0, b.getColTotals()[0]);

        for (int col = 0; col < boardSize; col++) {
            b.move(b.getMovePosition(row, col), Board.PlayerId.PLAYER_ONE);
            b.move(b.getMovePosition(row2, col), Board.PlayerId.PLAYER_TWO);
            assertEquals(col+1, b.getRowTotals()[row]);
        }
    }

    @Test
    public void testScoreUpdateOnMove_ColSum() {
        int boardSize = 3;
        int col = 1;
        int col2 = 2;
        Board b = new Board(boardSize);
        assertEquals(0, b.getRowTotals()[0]);
        assertEquals(0, b.getColTotals()[0]);

        for (int row = 0; row < boardSize; row++) {
            b.move(b.getMovePosition(row, col), Board.PlayerId.PLAYER_ONE);
            b.move(b.getMovePosition(row, col2), Board.PlayerId.PLAYER_TWO);
            assertEquals(row+1, b.getColTotals()[col]);
        }
    }

    @Test
    public void testScoreUpdateOnMove_BottomToTopDiagSum() {
        int boardSize = 3;
        Board b = new Board(boardSize);
        assertEquals(0, b.getBottomToTopDiagTotal());

        b.move(b.getMovePosition(2, 0), Board.PlayerId.PLAYER_ONE);
        b.move(b.getMovePosition(2, 1), Board.PlayerId.PLAYER_TWO);
        assertEquals(1, b. getBottomToTopDiagTotal());
        b.move(b.getMovePosition(1, 1), Board.PlayerId.PLAYER_ONE);
        b.move(b.getMovePosition(2, 2), Board.PlayerId.PLAYER_TWO);
        assertEquals(2, b. getBottomToTopDiagTotal());
        b.move(b.getMovePosition(0, 2), Board.PlayerId.PLAYER_ONE);
        b.move(b.getMovePosition(1, 2), Board.PlayerId.PLAYER_TWO);
        assertEquals(3, b. getBottomToTopDiagTotal());
    }

    @Test
    public void testScoreUpdateOnMove_TopToBottomDiagSum() {
        int boardSize = 3;
        Board b = new Board(boardSize);
        assertEquals(0, b.getTopToBottomDiagTotal());

        b.move(b.getMovePosition(0, 0), Board.PlayerId.PLAYER_ONE);
        b.move(b.getMovePosition(2, 1), Board.PlayerId.PLAYER_TWO);
        assertEquals(1, b. getTopToBottomDiagTotal());
        b.move(b.getMovePosition(1, 1), Board.PlayerId.PLAYER_ONE);
        b.move(b.getMovePosition(1, 2), Board.PlayerId.PLAYER_TWO);
        assertEquals(2, b. getTopToBottomDiagTotal());
        b.move(b.getMovePosition(2, 2), Board.PlayerId.PLAYER_ONE);
        b.move(b.getMovePosition(0, 1), Board.PlayerId.PLAYER_TWO);
        assertEquals(3, b. getTopToBottomDiagTotal());
    }

    @Test
    public void testValidMovesRemainAndDraw() {
        /**
         * Set up board as
         * X X O
         * O O X
         * X X O
         * To check for valid remaining moves (only looks for an open square)
         * TODO Loop this
         */
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
        assertFalse(b.isBoardWon());
    }
}
