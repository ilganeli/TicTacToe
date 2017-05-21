package com.homework;

import java.util.Collections;

/**
 * Class to track the state of the TicTacToe board.
 * Cells are numbered top to bottom, left to right on a 2-D NxN board:
 * E.g. 1 2 3
 *      4 5 6
 *      7 8 9
 */
public class Board {
    // Assuming standard square TTT board. We can allow a configurable board size since TicTacToe easily generalizes to
    // an N x N board
    private static final int DEFAULT_SIZE = 3;

    private int boardSize = DEFAULT_SIZE;

    // Track the current board state, use a custom Class instead of just storing IDs in case we want to later update
    // cell-specific behaviors, e.g. display properties or metadata.
    private Cell[][] boardState;

    /**
     * We can determine whether a given move is a winning move in constant time by treating each X or O as a +1 or -1
     * and checking whether the total for a row, column, or diagonal is equal to the board size. This approach is
     * useful since it also allows us to efficiently implement strategies for determining the optimal next move.
     * <p>
     * We can store 2*boardSize + 2 sums:
     * 1) Net score per each row
     * 2) Net score per each col
     * 3) Net score for diagonal top left to bottom right
     * 4) Net score for diagonal bottom left to top right
     */
    private int[] rowTotals;
    private int[] colTotals;
    private int topToBottomDiagTotal = 0;
    private int bottomToTopDiagTotal = 0;

    // Track the last move made to ensure that moves trade back and forth
    private PlayerId lastMove = null;

    private boolean boardWon = false;

    // Track the number of moves made to check for a draw condition
    private int moveCounter = 0;

    /**
     * Create a new boardSize x boardSize TicTacToe board
     *
     * @param boardSize The number of rows and columns to have in the board. Must be > 1.
     * @throws IllegalArgumentException if boardSize < 1
     */
    public Board(int boardSize) {
        if (boardSize <= 1) throw new IllegalArgumentException("Board must have at least 1 cell.");
        this.boardSize = boardSize;

        boardState = new Cell[boardSize][boardSize];
        rowTotals = new int[boardSize];
        colTotals = new int[boardSize];

        // Initialize the board
        for (int i = 0; i < boardSize; i++) {
            rowTotals[i] = 0;
            colTotals[i] = 0;
            for (int j = 0; j< boardSize; j++) {
                boardState[i][j] = new Cell();
            }
        }
    }

    public Board() {
        this(DEFAULT_SIZE);
    }

    public int[] getRowTotals() {
        return rowTotals;
    }

    public int[] getColTotals() {
        return colTotals;
    }

    public int getTopToBottomDiagTotal() {
        return topToBottomDiagTotal;
    }

    public int getBottomToTopDiagTotal() {
        return bottomToTopDiagTotal;
    }

    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Specifically check whether it's possible to continue making moves. Using a move counter allows
     * us to efficiently check whether the game is ongoing.
     * @return True if at least one square remains open on the board
     */
    public boolean validMovesRemain() {
        return moveCounter < (boardSize * boardSize);
    }

    /**
     * @return True if the board has been won by some player
     */
    public boolean isBoardWon() {
        return boardWon;
    }

    /**
     * Check whether a given move is valid.
     * A move is valid if the cell is within the confines of the board, e.g. 1-9 on a 3x3 board, and unoccupied.
     *
     * @param movePosition The number of the cell to check
     * @return True if the move is valid
     */
    public boolean isValidMove(int movePosition) {
        return (movePosition >= 1) && (movePosition <= boardSize * boardSize) &&
                (getCell(movePosition).isFree());
    }

    /**
     * Handle the translation of a position input into a row/col representation and return the resolved cell.
     *
     * @param movePosition The number of the cell to claim counting from top to bottom
     * @return
     */
    public Cell getCell(int movePosition) {
        int zeroIdx = movePosition - 1;
        int row = zeroIdx / boardSize;
        int col = zeroIdx % boardSize;
        return boardState[row][col];
    }

    /**
     * Handle the translation of a row/col input into a move position
     *
     * @param row The row of the board
     * @param col The col of the board
     * @return The position from 1 to (boardSize * boardSize) to move
     */
    public int getMovePosition(int row, int col) {
        return row * boardSize + col + 1; // 1 index vs 0 index
    }

    /**
     * Claim a cell on the board for a given player.
     * This method should be called with alternating players and the caller should then check for victory condition.
     *
     * @param movePosition The number of the cell to claim counting from top top bottom, left to right on a 2-D board,
     * @param playerId     The id of the player claiming the cell
     * @return True if the cell was unoccupied, the game is not yet won, this was a valid move,
     *         and the state was successfully updated.
     * @throws IllegalStateException If a player attempts to move twice in a row or playerId is null.
     */
    public boolean move(int movePosition, PlayerId playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player may not be null.");
        }

        if (lastMove == playerId) {
            throw new IllegalStateException(playerId.name() + " just moved. Other player must play next.");
        }

        if (boardWon) return false;

        if (!isValidMove(movePosition)) {
            // We terminate here since we should be protected from this eventuality by outside logic
            return false;
        }

        // Update the board state
        boolean wasUpdated = getCell(movePosition).setPlayerId(playerId);

        // Update score counters
        if (wasUpdated) {
            lastMove = playerId;
            moveCounter++;

            int row = (movePosition-1) / boardSize;
            int col = (movePosition-1) % boardSize;

            updateScore(playerId, row, col);

            // TODO Instead of setting a boolean flag, we could call a callback to have this class notify a listener
            if (moveWinsGame(row, col)) {
                boardWon = true;
            }
        }

        return wasUpdated;
    }

    /**
     * Helper function to update scores for a given move. This function assumes that the move is valid.
     * @param playerId The id of the player making the move
     * @param row The move row
     * @param col The move column
     */
    private void updateScore(PlayerId playerId, int row, int col) {
        if (outOfBounds(row, col)) {
            throw new IllegalArgumentException("Please ensure row/col are within board boundaries. " +
                    "Board size: " + boardSize + "; (Row, Col):(" + row + "," + col + ")");
        }
        if (playerId == null) throw new IllegalArgumentException("PlayerId may not be null.");

        rowTotals[row] += playerId.getNumVal();
        colTotals[col] += playerId.getNumVal();

        // Bottom to top diagonal, e.g. (1,1), (2,2), (3,3)
        if (onTopToBottomDiag(row, col)) {
            topToBottomDiagTotal += playerId.getNumVal();
        }

        // Top to bottom diagonal, e.g. (1,3) (2,2), (3,1)
        if (onBottomToTopDiag(row, col)) {
            bottomToTopDiagTotal += playerId.getNumVal();
        }
    }

    public boolean onTopToBottomDiag(int row, int col) {
        return row == col;
    }

    public boolean onBottomToTopDiag(int row, int col) {
        return row + col == (boardSize - 1);
    }

    /**
     * Helper function to check whether the board has been won for a particular row or column (also checks diagonals).
     * @param row The row to check
     * @param col The column to check
     * @return True if the board is won
     */
    private boolean moveWinsGame(int row, int col) {
        if (outOfBounds(row, col)) {
            throw new IllegalArgumentException("Please ensure row/col are within board boundaries. " +
                    "Board size: " + boardSize + "; (Row, Col):(" + row + "," + col + ")");
        }

        // Check for victory condition. We know if the total is equal to boardSize or -boardSize game is won
        // We only check the absolute value because we know that if the game was won on this turn, the moving
        // player is the victor so we don't need to track the winning player.
        // TODO We could break this out into individual checks if we wanted to know how the game was won
        return (Math.abs(rowTotals[row]) == boardSize) ||
            (Math.abs(colTotals[col]) == boardSize) ||
            (Math.abs(bottomToTopDiagTotal) == boardSize) ||
            (Math.abs(topToBottomDiagTotal) == boardSize);
    }

    private boolean outOfBounds(int row, int col) {
        return (row >= boardSize) || (row < 0) || (col >= boardSize) || (col < 0);
    }

    // Print the board, row by row
    public void showBoard() {
        System.out.println("Game board: ");
        /**
         * Use 2 characters per cell plus one star on each side and an extra space, e.g.
         *      *********
         *      * X O X *
         *      * X O X *
         *      * O O X *
         *      *********
         */
        String border = String.join("", Collections.nCopies(boardSize * 2 + 3, "*"));
        System.out.println(border);
        for (int i = 0; i < boardState.length; i++) {
            System.out.print("*");
            for (int j = 0; j < boardState.length; j++) {
                System.out.print(" " + boardState[i][j].getSymbol());
            }
            System.out.println(" *");
        }
        System.out.println(border);
    }

    public enum PlayerId {
        PLAYER_ONE(1, "X"), PLAYER_TWO(-1,"O");

        private final String symbol;
        private int numVal;

        PlayerId(int numVal, String symbol) {
            this.numVal = numVal;
            this.symbol = symbol;
        }

        public int getNumVal() {
            return numVal;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    /**
     * Simple wrapper for an individual cell of a TicTacToe board
     */
    public class Cell {
        private PlayerId playerId = null;

        public PlayerId getPlayerId() {
            return playerId;
        }
        public String getSymbol() {
            if (isFree()) return "-";
            else return getPlayerId().getSymbol();
        }

        /**
         * Update the player ID for this cell. This function only updates state if the cell is unoccupied
         *
         * @return True if cell was free and this was a valid move
         * @oaram playerId The ID of the player attempting to claim this cell
         */
        boolean setPlayerId(PlayerId playerId) {
            if (!isFree() || playerId == null) return false;
            this.playerId = playerId;
            return true;
        }

        boolean isFree() {
            return playerId == null;
        }
    }
}
