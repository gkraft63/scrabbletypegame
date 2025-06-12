package jscrabble.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import jscrabble.ScrabblePiece;
import jscrabble.ScrabbleRack;
import jscrabble.interfaces.Collection;

public class JRack extends Component implements Collection {

    private ScrabbleRack model = new ScrabbleRack();
    
    public Dimension getPreferredSize() {
        return new Dimension(8*28 + 1, 29);
    }
    
    public void paint(Graphics g) {
        ScrabblePiece piece;
        ScrabbleRack rack = this.model;
        if (rack == null)
            return;
        for (int i = 0; i < 8; i++)
            if ((piece = rack.getPieceAt(i)) != null)
                piece.paint(g, 28*i, 0);
        
    }

    public ScrabbleRack getModel() {
        return model;
    }

    public void setModel(ScrabbleRack model) {
        this.model = model;
    }

    public ScrabblePiece getPieceAt(int x, int y) {
        try {
            return model.getPieceAt(x/28);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void removePiece(ScrabblePiece piece) {
        model.removePiece(piece);
        repaint();
    }

    public boolean setPieceAt(int x, int y, ScrabblePiece piece) {
        if(model.setPieceAt(x/28, piece)) {
            piece.setColor(ScrabblePiece.ORANGE);
            repaint();
            return true;
        }
        return false;
    }
}
