package jscrabble;
/**
 *
 * @(#) BlankSelector.java      1.1    2004/08/15
 *
 * <p>
 * Okno definiowania etykiety blanka.
 *
 *
 *
 * @author               Mariusz  Bernacki
 * @version              1.1,  19 lipca 2004
 * @character-encoding   iso-8859-2
 * @since                JDK1.1
 *
 */

import java.awt.*;
import java.awt.event.*;

import jscrabble.interfaces.Collection;


public class BlankSelector extends Dialog {
    private ScrabblePiece.BlankPiece blank;
    private Collection target;
    
    public BlankSelector(Component child) {
        super(Support.getFrame(child), "Select blank");
        Support.setHideOnClose(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setResizable(false);
        pack();
        Support.centerOnScreen(this, Support.getScrabble(this));
    }
    
    public Dimension getPreferredSize() {
        Insets ins = getInsets();
        return new Dimension(ins.left + 11*29+1 + ins.right, ins.top + 3*29+1 + ins.bottom);
    }
    
    public void paint(Graphics g) {
        Insets ins = getInsets();
        for(int i = 0; i < 3; i++)
            g.drawImage(GlobalCache.ImageCache.ALPHABET, ins.left, ins.top + i*29, ins.left + 11*29, ins.top + i*29+29,
                    11*29*i, 5*29, 11*29*i+11*29, 5*29+29, this);
    }
    
    protected void processMouseEvent(MouseEvent e) {
        if(e.getID() == MouseEvent.MOUSE_PRESSED) {
            Insets ins = getInsets();
            int i = (e.getX() - ins.left)/29, j = (e.getY() - ins.top)/29;
            ScrabblePiece.BlankPiece blank = this.blank;
            if(blank != null) {
                blank.setIndex(11*j + i);
                Collection target = this.target;
                if(target instanceof Component)
                    ((Component)target).repaint();
            }
        }
    }
    
    
    public void setTarget(ScrabblePiece.BlankPiece blank, Collection target) {
        if(blank != null) {
            this.target = target;
            this.blank = blank;
            if(isVisible())
                toFront();
            else
                setVisible(true);
        } else {
            setVisible(false);
            this.blank = blank;
        }
    }
}
