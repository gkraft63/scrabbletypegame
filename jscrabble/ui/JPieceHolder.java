package jscrabble.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import jscrabble.ScrabblePiece;
import jscrabble.interfaces.Collection;

public final class JPieceHolder extends Component implements Runnable {
    private volatile ScrabblePiece piece;
    private volatile int pieceOldColor;
    private volatile Collection source;
    private volatile int sourceX, sourceY;
    private volatile int originX, originY;
    private volatile int hotSpotX, hotSpotY;
    
    public JPieceHolder() {
        setVisible(false);
    }
    
    public ScrabblePiece getPiece() {
        return piece;
    }
    
    public int getHotSpotX() {
        return hotSpotX;
    }

    public int getHotSpotY() {
        return hotSpotY;
    }

    public Dimension getPreferredSize() {
        return new Dimension(30, 30);
    }

    public void paint(Graphics g) {
        ScrabblePiece piece = this.piece;
        if(piece != null)
            piece.paint(g, 0, 0);
    }

    public boolean handle(ScrabblePiece piece, int handleX, int handleY, int originX, int originY, Collection source) {
        if(this.piece != null || piece == null)
            return false;
        
        this.piece = piece;
        this.source = source;
        this.originX = handleX;
        this.originY = handleY;
        this.hotSpotX = handleX%28;
        this.hotSpotY = handleY%28;
        this.sourceX = originX-handleX%28;
        this.sourceY = originY-handleY%28;
        setLocation(sourceX, sourceY);
        pieceOldColor = piece.getColor();
        piece.setColor(ScrabblePiece.GREEN);
        setVisible(true);
        return true;
    }
    
    public void drop() {
        ScrabblePiece piece = this.piece;
        Collection source = this.source;
        if(piece != null && source instanceof JRack) {
            this.piece = null;
            this.source = null;
            piece.setColor(pieceOldColor);
            ((JRack) source).getModel().addPiece(piece);
            ((JRack) source).repaint();
        }
    }
    
    public boolean drop(Collection target, int x, int y) {
        piece.setColor(pieceOldColor);
        if(target != null && piece != null && target.setPieceAt(x, y, piece)) {
            setVisible(false);
            return true;
        }
        if(piece != null)
            new Thread(this).start();
        return false;
    }
    
    public synchronized void run() {
        Point p1 = getLocation();
        int srcX = this.sourceX, srcY = this.sourceY;
        try {
            double dx = (double)(srcX - p1.x)/15, dy = (double)(srcY - p1.y)/15;
            for(int i=0; i<=15; i++) {
                setLocation(p1.x + (int)(dx*i), p1.y + (int)(dy*i));
                Thread.sleep(50);
            }
            Thread.sleep(50);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        ScrabblePiece piece = this.piece;
        Collection source = this.source;
        if(piece != null && source != null)
            source.setPieceAt(originX, originY, piece);
        setVisible(false);
    }
    
    public void setVisible(boolean visible) {
        if(!visible)
            piece = null;
        super.setVisible(visible);
    }
}
