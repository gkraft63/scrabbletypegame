package jscrabble.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import jscrabble.ScrabbleBoard;
import jscrabble.ScrabblePiece;
import jscrabble.interfaces.Collection;

public class JBoard extends Component implements Collection {

    private ScrabbleBoard model;
    
    public JBoard(ScrabbleBoard model) {
        setBackground(new Color(0xFF009F4F));
        this.model = model;
    }
    
    public ScrabbleBoard getModel() {
        return model;
    }
    
    public void setModel(ScrabbleBoard model) {
        this.model = model;
    }
    
    public void paint(Graphics g) {
        ScrabblePiece piece;
        ScrabbleBoard board = this.model;
        for(int i = 0; i < 15; i++)
            for(int j = 0; j < 15; j++)
                if((piece = board.getPieceAt(i, j)) != null)
                    piece.paint(g, 28*i, 28*j);
        
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(15*28+1, 15*28+1);
    }

    public ScrabblePiece getPieceAt(int x, int y) {
        ScrabblePiece piece = model.getPieceAt(x/28, y/28);
        if(piece != null && piece.getColor() != ScrabblePiece.YELLOW)
            return piece;
        return null;
    }
    
    public boolean containsPieceAt(int x, int y) {
        return model.getPieceAt(x/28, y/28) != null;
    }

    public void removePiece(ScrabblePiece piece) {
        model.removePiece(piece);
        repaint();
    }

    public boolean setPieceAt(int x, int y, ScrabblePiece piece) {
        if(model.getPieceAt(x/28, y/28) == null) {
            model.setPieceAt(x/28, y/28, piece);
            repaint();
            return true;
        }
        return false;
    }
}
