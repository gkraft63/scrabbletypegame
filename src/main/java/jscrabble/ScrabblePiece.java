package jscrabble;

import java.awt.Graphics;

public class ScrabblePiece implements java.io.Serializable {
    private static final long serialVersionUID = -9023664482246928152L;

    /** the letter of the piece */
    char letter;
    
    /** the value of the piece */
    int value;
    
    int index;
    

    ScrabblePiece(char letter, int value, int index) {
        this.letter = letter;
        this.value = value;
        this.index = index + 33; // GREEN
    }
    
    /**
     * Returns the scrabble piece's letter.
     * 
     * @return the letter of the piece
     */
    public char getLetter() {
        return letter;
    }
    
    /**
     * Returns the scrabble piece's value.
     * 
     * @return the value of the piece
     */
    public int getValue() {
        return value;
    }
    
    public int getColor() {
        return (index/33) % 3;
    }
    
    public void setColor(int color) {
        index = (index/99*99 + index % 33 + color * 33);
    }
    
    public void paint(Graphics g, int x0, int y0) {
        int x1 = 29 * (index%33), y1 = 29 * ((index/33) % 9);
        g.drawImage(GlobalCache.ImageCache.ALPHABET, x0, y0, x0 + 29, y0 + 29,
                x1, y1, x1 + 29, y1 + 29, null);
    }

    
    public static class BlankPiece extends ScrabblePiece {
        
        BlankPiece() {
            super(' ', 0, 0);
        }
        
        /**
         * Sets the blank piece's letter.
         * 
         * @param letter the letter of the blank piece to set
         */
        public void setIndex(int index) {
            this.letter = GameSystem.ALPHABET[index % 33];
            this.index = 99 + index % 99;
        }
    }
    
    public static final int ORANGE = 0;

    public static final int GREEN = 1;

    public static final int YELLOW = 2;

}
