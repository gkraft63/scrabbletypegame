package jscrabble;

import jscrabble.exceptions.PieceNotFoundException;

public class PlayerMove implements java.io.Serializable {

    private ScrabblePiece[] pieces = new ScrabblePiece[15];
    private int numMoves;
    private int score;
    public int rank;
    private int index; //index >= 0  - row, index < 0 - column
    private int blank1;
    private int blank2;
    
    /**
     * Constructor for the PlayerMove object.
     * 
     * @param name the player's name
     */
    public PlayerMove(ScrabblePiece[] pieces, int offset, int index) {
        ScrabblePiece p;
        while(offset > 0 && pieces[offset-1] != null)
            offset--;
        while(offset < 15 && (p = pieces[offset]) != null) {
            this.pieces[offset] = p;
            numMoves++;
            if(p instanceof ScrabblePiece.BlankPiece) {
                if(blank1 == 0)
                    blank1 = p.index;
                else
                    blank2 = p.index;
            }
        }
        this.index = index;
    }
    
    public PlayerMove(ScrabblePiece[][] board, int i, int j, boolean mirror) {
        ScrabblePiece p;
        blank1 = 0;
        blank2 = 0;
        while(i > 0 && board[i-1][j] != null)
            i--;
        for( ; i < 15 && (p = board[i][j]) != null; i++) {
            pieces[i] = p;
            numMoves++;
            if(p instanceof ScrabblePiece.BlankPiece) {
                if(blank1 == 0)
                    blank1 = p.index;
                else
                    blank2 = p.index;
            }
        }
        index = mirror? ~j : j;
    }
    
    public void play(Scrabble scrabble) {
        ScrabblePiece piece;
        ScrabbleGame game = scrabble.getGame();
        ScrabblePiece[][] board = game.board.pieces;
        ScrabbleSeat seat = game.getCurrentPlayer();
        int index = this.index;
        boolean mirror = index < 0;
        boolean blankPlaced = false;
        for(int i = 0; i < 15; i++)
            if((piece = pieces[i]) != null && piece.getColor() == ScrabblePiece.ORANGE) {
                try {
                    ((seat != null)? seat.playRack : scrabble.jPlayRack.getModel())
                            .removePiece(piece);
                } catch (PieceNotFoundException e) {
                    ((seat != null)? seat.swapRack : scrabble.jSwapRack.getModel())
                            .removePiece(piece);
                }
                
                if(piece instanceof ScrabblePiece.BlankPiece) {
                    ((ScrabblePiece.BlankPiece) piece).setIndex(blankPlaced? blank2 : blank1);
                    blankPlaced = true;
                }
                board[mirror? ~index: i][mirror? i: index] = piece;
            }
        
    }
    
    public ScrabblePiece getPiece(int index) {
        return pieces[index];
    }
    
    public int getNumMoves() {
        return numMoves;
    }
    
    /**
     * Returns true if player's move is pass.
     * 
     * @return boolean pass
     */
    public boolean isPass() {
        return numMoves == 0;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getScore() {
        return score;
    }
    
    public String toString() {
        return toString(ScrabblePiece.ORANGE)+" (+"+score+")";
    }
    
    public String toString(int color) {
        StringBuffer buff = new StringBuffer();
        boolean seenBlank = false;
        ScrabblePiece p;
        
        for(int i = 0; i < 15; i++) {
            if((p = pieces[i]) != null) {
                if(p instanceof ScrabblePiece.BlankPiece) {
                    buff.append(GameSystem.ALPHABET[(seenBlank? blank2: blank1) % 33]);
                    seenBlank = true;
                } else {
                    buff.append(p.letter);
                }
            }
        }
        return buff.toString();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
