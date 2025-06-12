package jscrabble.ui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;

import jscrabble.Support;
import jscrabble.GlobalCache;
import jscrabble.Scrabble;
import jscrabble.util.GraphicsBuffer;

public class JUsedPlates extends Dialog {

    private GraphicsBuffer buffer;
    private boolean[] usedPlates = new boolean[100];
    
    public JUsedPlates(Component child) {
        super(Support.getFrame(child), "Mark Used Plates");
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setResizable(false);
        pack();
        Support.centerOnScreen(this, Support.getScrabble(this));
        setVisible(false);
        Support.setHideOnClose(this);
    }
    
    public void setUsedPlates(boolean[] plates) {
        this.usedPlates = plates;
        setVisible(plates != null);
    }
    
    public Dimension getPreferredSize() {
        Insets ins = getInsets();
        return new Dimension(ins.left + 220 + ins.right, ins.top + 220 + ins.bottom);
    }
    
    public void paint(Graphics g) {
        if(buffer == null) {
            buffer = new GraphicsBuffer(this);
            buffer.getGraphics().setColor(Color.red);
        }
        Graphics og = buffer.getGraphics();
        og.drawImage(GlobalCache.ImageCache.USED_PLATES, 0, 0, this);
        for(int i = 0; i < usedPlates.length; i++)
            if(usedPlates[i])
                drawStrike(og, 22*(i%10), 22*(i/10));
        
        Insets ins = getInsets();
        g.drawImage(buffer.getOffscreen(), ins.left, ins.top, this);
    }
    
    public void update(Graphics g) {
        paint(g);
    }
    
    protected void processMouseEvent(MouseEvent e) {
        if(e.getID() == MouseEvent.MOUSE_PRESSED) {
            Insets ins = getInsets();
            int k = (e.getX() - ins.left)/22 + (e.getY() - ins.top)/22*10;
            usedPlates[k] = !usedPlates[k];
            repaint();
        }
    }
    
    private static void drawStrike(Graphics g, int x, int y) {
        g.drawLine(x+3, y+3, x+18, y+18);
        g.drawLine(x+4, y+3, x+19, y+18);
        g.drawLine(x+18, y+3, x+3, y+18);
        g.drawLine(x+19, y+3, x+4, y+18);
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(!visible)
            this.usedPlates = null;
        else
            toFront();
    }
}
