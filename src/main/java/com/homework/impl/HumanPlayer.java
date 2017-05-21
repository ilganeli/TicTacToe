package com.homework.impl;

import com.homework.Board;
import com.homework.Player;

import java.util.Scanner;

/**
 * Implementation of a human TicTacToe player that gets the next move from the command line.
 */
public class HumanPlayer extends Player {
    public HumanPlayer(Board.PlayerId playerId, String name) {
        super(playerId, name);
    }

    public int getNextMove(Board b)
    {
        if (!b.validMovesRemain()) {
            throw new IllegalStateException("Can't select next move when no moves remain.");
        }

        if (b.isBoardWon()) {
            throw new IllegalStateException("Can't make a move when game has already been won.");
        }

        int move;
        do {
            System.out.println(super.getName() + " - Please enter your next move as a number between 1 and " +
                    b.getBoardSize() * b.getBoardSize());
            Scanner sc = new Scanner(System.in);
            move = sc.nextInt();
            if (!b.isValidMove(move)) {
                System.out.println("Please enter a valid move!");
            }
        } while (!b.isValidMove(move));
        return move;
    }
}
