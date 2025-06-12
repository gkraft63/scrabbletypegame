package jscrabble;

import jscrabble.exceptions.EmptyBagException;
import jscrabble.exceptions.PieceNotFoundException;

public class GameSystem {
    
    public static final char[] ALPHABET = " abcdefghijklmnopqrstuvwxyz".toCharArray();
    
    /**
     * 
     * @param rack
     * @param bag
     * @throws EmptyBagException
     */
    public static int drawPieces(ScrabbleRack rack, ScrabbleRack swap, ScrabbleBag bag) {
        int numToDraw = 7 - rack.getNumPieces() - swap.getNumPieces();
        int numPieces = bag.getNumPieces();
        if(numToDraw > numPieces)
            numToDraw = numPieces;
        try {
            while(--numToDraw >= 0)
                rack.addPiece(bag.getPiece());
        } catch(EmptyBagException e) {
        }
        return rack.getNumPieces() + swap.getNumPieces();
    }
    
    public static void returnPieces(ScrabbleRack rack, ScrabbleBag bag) {
        for(int i = 0; i < 8; i++) {
            ScrabblePiece piece = rack.getPieceAt(i);
            if(piece != null) {
                rack.removePiece(piece);
                bag.returnPiece(piece);
            }
        }
    }
    
    public static void exchangePieces(ScrabbleRack rack, ScrabbleRack swap, ScrabbleBag bag) {
        int count = 0;
        ScrabblePiece[] returns = new ScrabblePiece[Settings.RACK_SIZE];
        for(int i = 0; i < Settings.RACK_SIZE; i++) {
            ScrabblePiece piece = swap.getPieceAt(i);
            if(piece != null) {
                swap.removePiece(piece);
                returns[count++] = piece;
            }
        }
        for(int i = 0; i < count; i++)
            rack.addPiece(bag.getPiece());
        for(int i = 0; i < count; i++)
            bag.returnPiece(returns[i]);
    }
    
}
