package jscrabble;

import java.util.Random;

import jscrabble.exceptions.PieceNotFoundException;

public class ScrabbleRack implements java.io.Serializable {
    private static final long serialVersionUID = -3971399230151252660L;

    private ScrabblePiece[] pieces = new ScrabblePiece[8];
    private int numPieces;
    
    public ScrabblePiece[] getPieces() {
        return pieces;
    }
    
    public void addPiece(ScrabblePiece piece) {
        if(piece == null)
            return;
        
        ScrabblePiece[] pieces = this.pieces;
        for(int i = 0; i < pieces.length; i++)
            if(pieces[i] == piece)
                throw new IllegalArgumentException("piece already exists in the rack");
        
        for(int i = 0; i < pieces.length; i++)
            if(pieces[i] == null) {
                pieces[i] = piece;
                numPieces++;
                return;
            }
        throw new IllegalArgumentException("rack is full");
    }
    
    public void removePiece(ScrabblePiece piece) {
        ScrabblePiece[] pieces = this.pieces;
        if(piece == null)
            throw new NullPointerException("piece is NULL");
        for(int i = 0; i < pieces.length; i++)
            if(pieces[i] == piece) {
                pieces[i] = null;
                numPieces--;
                return;
            }
        throw new PieceNotFoundException(piece.toString());
    }
    
    public ScrabblePiece removeFirst() {
        ScrabblePiece[] pieces = this.pieces;
        for(int i = 0; i < pieces.length; i++)
            if(pieces[i] != null) {
                ScrabblePiece piece = pieces[i];
                pieces[i] = null;
                numPieces--;
                return piece;
            }
        return null;
    }
    
    public void moveToRack(ScrabbleRack other) {
        ScrabblePiece[] pieces = this.pieces;
        for(int i = 0; i < pieces.length; i++)
            if(pieces[i] != null) {
                other.addPiece(pieces[i]);
                pieces[i] = null;
                numPieces--;
            }
        
    }
    
    public int getNumPieces() {
        return numPieces;
    }
    
    public ScrabblePiece getPieceAt(int index) {
        return pieces[index];
    }
    
    public boolean setPieceAt(int index, ScrabblePiece piece) {
        ScrabblePiece[] pieces = this.pieces;
        if(pieces[index] != null) {
            int i;
            for(i = 0; i < pieces.length; i++)
                if(pieces[i] == null) {
                    pieces[i] = pieces[index];
                    break;
                }
            if(i == pieces.length)
                return false;
        }
        pieces[index] = piece;
        numPieces++;
        return true;
    }
    
    public void scramble() {
        ScrabblePiece[] pieces = new ScrabblePiece[8];
        ScrabblePiece[] origin = this.pieces;
        Random randomizer = new Random();
        
        for(int k = 0; k < numPieces; k++) {
            int i = randomizer.nextInt() & 7;
            int j = i;
            while(origin[j = (j + 1) & 7] == null)
                if(i == j)
                    return;
                
            pieces[k] = origin[j];
            origin[j] = null;
        }
        this.pieces = pieces;
    }
    
    public void collectAllPieces(ScrabbleBag bag) {
        ScrabblePiece[] pieces = this.pieces;
        for(int i = 0; i < pieces.length; i++)
            if(pieces[i] != null) {
                bag.returnPiece(pieces[i]);
                pieces[i] = null;
                numPieces--;
            }
        
    }
    
    public String toString() {
        ScrabblePiece piece;
        ScrabblePiece[] pieces = this.pieces;
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        for(int i = 0; i < pieces.length; i++) {
            if((piece = pieces[i]) instanceof ScrabblePiece.BlankPiece)
                buf.append('_');
            else if(piece != null)
                buf.append(piece.letter);
        }
        return buf.append(']').toString();
    }
}
