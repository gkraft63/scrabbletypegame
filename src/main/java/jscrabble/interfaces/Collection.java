package jscrabble.interfaces;

import jscrabble.ScrabblePiece;

public interface Collection {

    public ScrabblePiece getPieceAt(int x, int y);
    
    public void removePiece(ScrabblePiece piece);
    
    public boolean setPieceAt(int x, int y, ScrabblePiece piece);
    
}
