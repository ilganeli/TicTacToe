import com.homework.Board;
import com.homework.Player;
import com.homework.impl.AIPlayer;
import com.homework.impl.HumanPlayer;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestHumanPlayer {
    Player p;
    @Before
    public void setup() {
        p = new HumanPlayer(Board.PlayerId.PLAYER_ONE, "HUMAN");
    }

    // fullBoard is used in subsequent tests. Ensure that it works by itself without throwing exceptions
    @Test
    public void checkBoardFull() {
        fullBoard(p);
    }

    // wonBoard is used in subsequent tests. Ensure that it works by itself without throwing exceptions
    @Test
    public void checkBoardWon() {
        wonBoard(p);
    }

    @Test(expected = IllegalStateException.class)
    public void testFullBoard() {
        Board b = fullBoard(p);
        p.getNextMove(b);
    }

    @Test(expected = IllegalStateException.class)
    public void testWonBoard() {
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
