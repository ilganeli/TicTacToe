package com.homework.impl;

import com.homework.Board;
import com.homework.Player;

import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of an AI TicTacToe player that selects next move based on a selected algorithm.
 * Currently supports random-choice and a simple greedy algorithm.
 */
public class AIPlayer extends Player {
    public enum Algorithm {
        RANDOM, GREEDY, EVIL
    }

    private Algorithm algo = Algorithm.RANDOM;

    public AIPlayer(Board.PlayerId playerId, String name, Algorithm algo) {
        super(playerId, name);
        this.algo = algo;
    }

    public int getNextMove(Board b) {
        // These checks should hit since the external logic should handle this but check just in case.
        if (!b.validMovesRemain()) {
            throw new IllegalStateException("Can't select next move when no moves remain.");
        }

        if (b.isBoardWon()) {
            throw new IllegalStateException("Can't make a move when game has already been won.");
        }

        // If selecting randomly, start by populating a list of available cells and then pick a random index
        switch(algo) {
            case GREEDY: return getGreedyMove(b);
            case EVIL: return getEvilMove(b);
            default: return getRandomMove(b);
        }
    }

    /**
     * Pick a random square from all available open squares.
     * @param b The current board
     * @return
     */
    private int getRandomMove(Board b) {
        int maxMoves = b.getBoardSize() * b.getBoardSize();
        ArrayList<Integer> moves = new ArrayList<Integer>(maxMoves);

        // TODO Assume square board, if this assumption goes away, update here
        for (int i = 1; i <= maxMoves; i++) {
            if (b.isValidMove(i)) {
                moves.add(i);
            }
        }

        Random r = new Random(System.currentTimeMillis());
        int moveIdx = r.nextInt(moves.size());
        return moves.get(moveIdx);
    }

    /**
     * Attempt to select a move by finding square such that the combined score of that of row, col, and/or diagonal
     * is maximized in favor of the AI.
     *
     * TODO For the moment make the brittle assumption that the AI is player 2 and the numeric value of its score is -1
     * @param b The current board state
     * @return
     */
    private int getGreedyMove(Board b)
    {
        int mostFavorableScore = Integer.MAX_VALUE;
        int bestMove = 1;

        // TODO Assume square board, if this assumption goes away, update here since diagonal checks may be wrong
        for (int row = 0; row < b.getRowTotals().length; row++) {
            for (int col = 0; col < b.getColTotals().length; col++) {
                if (b.isValidMove(b.getMovePosition(row, col))) {
                    // AI score is negative. Assume an X counts as 1, an open counts as 0, and an O counts as -1
                    // We want to find the square which has the highest number of Os in the same row, diag, or col so
                    // take the combined sum of all these scores
                    int cellScore = b.getColTotals()[col] + b.getRowTotals()[row];
                    if (b.onTopToBottomDiag(row, col)) cellScore += b.getTopToBottomDiagTotal();
                    if (b.onBottomToTopDiag(row, col)) cellScore += b.getBottomToTopDiagTotal();

                    if (cellScore < mostFavorableScore) {
                        mostFavorableScore = cellScore;
                        bestMove = b.getMovePosition(row, col);
                    }
                }
            }
        }

        return bestMove;
    }

    /**
     * Attempt to select a move by finding square such that the combined score of that of row, col, and/or diagonal
     * to best block the human player
     *
     * TODO For the moment make the brittle assumption that the AI is player 2 and the numeric value of its score is -1
     * @param b The current board state
     * @return
     */
    private int getEvilMove(Board b)
    {
        int mostFavorableScore = Integer.MIN_VALUE;
        int bestMove = 1;

        // TODO Assume square board, if this assumption goes away, update here since diagonal checks may be wrong
        for (int row = 0; row < b.getRowTotals().length; row++) {
            for (int col = 0; col < b.getColTotals().length; col++) {
                if (b.isValidMove(b.getMovePosition(row, col))) {
                    // AI score is negative. Assume an X counts as 1, an open counts as 0, and an O counts as -1
                    // To best block the human We want to find the square which has the highest number of Xs
                    // in the same row, diag, or col
                    int cellScore = Math.max(b.getColTotals()[col], b.getRowTotals()[row]);
                    if (b.onTopToBottomDiag(row, col)) cellScore = Math.max(cellScore, b.getTopToBottomDiagTotal());
                    if (b.onBottomToTopDiag(row, col)) cellScore = Math.max(cellScore, b.getBottomToTopDiagTotal());

                    if (cellScore > mostFavorableScore) {
                        mostFavorableScore = cellScore;
                        bestMove = b.getMovePosition(row, col);
                    }
                }
            }
        }

        return bestMove;
    }
}
