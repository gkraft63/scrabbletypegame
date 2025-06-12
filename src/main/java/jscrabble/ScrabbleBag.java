package jscrabble;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

import jscrabble.exceptions.EmptyBagException;
import jscrabble.interfaces.Collection;


public class ScrabbleBag implements java.io.Serializable {
    private static final long serialVersionUID = -3032904964632591153L;

    private Random randomizer = new Random();
    private ScrabblePiece[] pieces;
    private int[] indexes = new int[34];
    private int numPieces;

    /**
     * Constructor for ScrabbleBag object. Creates 100 ScrabblePiece objects and
     * sets their letter and value.
     * 
     */
    public ScrabbleBag() {
        ScrabblePiece[] pieces = new ScrabblePiece[100];
        String pieceLetters = " abcdefghijklmnopqrstuvwxyz";
        String pieceCounts =  "29224<232911426821646422121";
        String pieceValues =  "01332142418513113:11114484:";
        int k = 0;
        pieces[k++] = new ScrabblePiece.BlankPiece();
        pieces[k++] = new ScrabblePiece.BlankPiece();
        for(int i = 1; i < pieceCounts.length(); i++) {
            int z = pieceCounts.charAt(i)-'0';
            for(int j = 0; j < z; j++)
                pieces[k++] = new ScrabblePiece(pieceLetters.charAt(i), pieceValues.charAt(i)-'0', i);
        }
        if(k != 100)
            throw new InternalError("Cannot initiate ScrabbleBag object");
        
        int i = 1; k = 1;
        for( ; i < pieceCounts.length(); i++) {
            while(pieces[++k].index % 33 != i)
                ;
            indexes[i] = k;
        }
        indexes[i] = 100;
        this.pieces = pieces;
        numPieces = 100;
    }
    
    /**
     * Removes a ScrabblePiece object from the bag.
     * 
     * @return a ScrabblePiece object from the ScrabbleBag
     */
    public ScrabblePiece getPiece() {
        int i = (randomizer.nextInt() & 0x7FFF) % pieces.length;
        int j = i;
        while(pieces[j = (j + 1)%pieces.length] == null)
            if(i == j)
                throw new EmptyBagException();
        
        numPieces--;
        ScrabblePiece piece = pieces[j];
        piece.setColor(ScrabblePiece.ORANGE);
        pieces[j] = null;
        return piece;
    }
    
    /**
     * Returns a ScrabblePiece object to the bag.
     * 
     * @param piece
     *            ScrabblePiece object to return to the ScrabbleBag
     */
    public void returnPiece(ScrabblePiece piece) {
        ScrabblePiece[] pieces = this.pieces;
        for(int i = 0; i < pieces.length; i++)
            if(pieces[i] == piece)
                throw new IllegalArgumentException("piece already in the bag");
        
        int index = (piece instanceof ScrabblePiece.BlankPiece)? 0 : (piece.index % 33);
        for(int i = indexes[index++]; i < indexes[index]; i++)
            if(pieces[i] == null) {
                pieces[i] = piece;
                numPieces++;
                piece.setColor(ScrabblePiece.GREEN);
                return;
            }
        
        System.err.println("not returned: "+piece);
    }
    
    /**
     * Returns a number of scrabble's pieces left in the bag.
     * 
     * @return
     */
    public int getNumPieces() {
        return numPieces;
    }
    
    public void setSeed(long seed) {
        randomizer.setSeed(seed);
    }
    
    public class BagComponent extends Component implements Collection {

        public BagComponent() {
            setBackground(new Color(0xFFFFFFB0));
        }
        
        public Dimension getPreferredSize() {
            return new Dimension(11*28 + 2, 3*28 + 2);
        }
        
        public void paint(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, 11*28 + 1, 3*28 + 1);
            ScrabblePiece p;
            
            ScrabblePiece[] pieces = ScrabbleBag.this.pieces;
            for(int j=0, z=0; j<3; j++)
                for(int i=0; i<11; i++)
                    for(int a = indexes[z], b = indexes[++z]; a < b; a++)
                        if((p = pieces[a]) != null) {
                            p.paint(g, i*28, j*28);
                            break;
                        }
            
        }
        
        public ScrabblePiece getPieceAt(int x, int y) {
            int i = x/28 + y/28*11;
            ScrabblePiece piece;
            for(int a = indexes[i++], b = indexes[i]; a < b; a++)
                if((piece = pieces[a]) != null) {
                    piece.setColor(ScrabblePiece.GREEN);
                    return piece;
                }
            return null;
        }

        public void removePiece(ScrabblePiece piece) {
            if(piece == null)
                throw new NullPointerException("piece is NULL");
            
            ScrabblePiece[] pieces = ScrabbleBag.this.pieces;
            for(int i = 0; i < pieces.length; i++)
                if(pieces[i] == piece) {
                    pieces[i] = null;
                    numPieces--;
                    return;
                }
            
        }

        public boolean setPieceAt(int x, int y, ScrabblePiece piece) {
            piece.setColor(ScrabblePiece.GREEN);
            ScrabbleBag.this.returnPiece(piece);
            repaint();
            return true;
        }
    }
}
