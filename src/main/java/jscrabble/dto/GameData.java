package jscrabble.dto;

import jscrabble.Player;

public class GameData {
    private Player[] players;
    private int timeForMove;
    private int timeForGame;
    private boolean showComputerPieces;
    
    public int getTimeForGame() {
        return timeForGame;
    }
    public void setTimeForGame(int timeForGame) {
        this.timeForGame = timeForGame;
    }
    public int getTimeForMove() {
        return timeForMove;
    }
    public void setTimeForMove(int timeForMove) {
        this.timeForMove = timeForMove;
    }
    public Player[] getPlayers() {
        return players;
    }
    public void setPlayers(Player[] players) {
        this.players = players;
    }
    
    public boolean isShowComputerPieces() {
        return showComputerPieces;
    }
    public void setShowComputerPieces(boolean showComputerPieces) {
        this.showComputerPieces = showComputerPieces;
    }
}
