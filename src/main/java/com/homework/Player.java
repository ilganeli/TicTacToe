package com.homework;

/**
 * Class to abstract away the implementation of a player so that a human or computer player
 * can be used interchangeably.
 */
public abstract class Player {
    private Board.PlayerId playerId;
    private String name;

    public Board.PlayerId getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public Player(Board.PlayerId playerId, String name) {
        this.playerId = playerId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (playerId != player.playerId) return false;
        return name != null ? name.equals(player.name) : player.name == null;
    }

    /**
     * Given a current board setup, return the next move as an integer.
     * @param b The current state of the tic tac toe board
     * @return The board position of the next move, counting from top top bottom, left to right on a 2-D board,
     * E.g. 1 2 3
     *      4 5 6
     *      7 8 9
     */
    public abstract int getNextMove(Board b);
}
