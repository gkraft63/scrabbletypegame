package jscrabble.ui;

import java.awt.*;
import java.awt.event.*;

import jscrabble.Support;
import jscrabble.GlobalCache;
import jscrabble.Player;
import jscrabble.Scrabble;
import jscrabble.ScrabbleGame;
import jscrabble.ScrabbleSeat;
import jscrabble.Settings;
import jscrabble.Util;



public class PlayerPanel extends Container {
    
    Scrabble scrabble;
    
    public PlayerPanel(Scrabble scrabble) {
        this.scrabble = scrabble;
        enableEvents(MouseEvent.MOUSE_EVENT_MASK);
    }
    
    
    public Dimension getPreferredSize() {
        return new Dimension(300, 300);
    }
    
    public void doLayout() {
        Dimension size = getSize();
        Component[] cc = getComponents();
        Dimension[] dd = new Dimension[cc.length];
        int dw = 0;
    }
    
    public void paint(Graphics g) {
        Rectangle alloc = getBounds();
        alloc.setLocation(0, 0);
        paint(g, alloc);
        Util.getInstance().paintChildren(this, g);
    }
    
    public void paint(Graphics g, Rectangle alloc) {
        Rectangle childAlloc = null;
        
        ScrabbleSeat currentPlayer = scrabble.getGame().getCurrentPlayer();
        ScrabbleSeat[] seats = scrabble.getGame().seats;
        g.setColor(Color.black);
        
        for(int i = 0; i < seats.length; i++) {
            ScrabbleSeat seat = seats[i];
            if(seat != null)
                paintPlayer(seats[i], g, getChildAllocation(i, alloc, childAlloc), seat == currentPlayer, scrabble.timer);
        }
    }
    
    private Rectangle getChildAllocation(int index, Rectangle alloc, Rectangle cache) {
        if(cache == null)
            cache = new Rectangle();
        cache.setBounds(alloc.x + (index & 1)*150, alloc.y + index/2*150, 150, 150);
        return cache;
    }
    
    public static void paintPlayer(ScrabbleSeat seat, Graphics g, Rectangle alloc, boolean highlighted, Scrabble.ClockTimer timer) {
        Image skin = GlobalCache.ImageCache.THEME;
        int src = (highlighted)? 33: 0;
        Component comp = null;
        g.drawImage(skin, alloc.x, alloc.y, alloc.x + 10, alloc.y + 10,
                src+67, 1, src+77, 11, comp);
        g.drawImage(skin, alloc.x + 10, alloc.y, alloc.x + alloc.width - 10, alloc.y + 10,
                src+78, 1, src+88, 11, comp);
        g.drawImage(skin, alloc.x + alloc.width - 10, alloc.y, alloc.x + alloc.width, alloc.y + 10,
                src+89, 1, src+99, 11, comp);
        g.drawImage(skin, alloc.x, alloc.y + 10, alloc.x + 10, alloc.y + alloc.height - 10,
                src+67, 12, src+77, 22, comp);
        g.drawImage(skin, alloc.x + 10, alloc.y + 10, alloc.x + alloc.width - 10, alloc.y + alloc.height - 10,
                src+78, 12, src+88, 22, comp);
        g.drawImage(skin, alloc.x + alloc.width - 10, alloc.y + 10, alloc.x + alloc.width, alloc.y + alloc.height - 10,
                src+89, 12, src+99, 22, comp);
        g.drawImage(skin, alloc.x, alloc.y + alloc.height - 10, alloc.x + 10, alloc.y + alloc.height,
                src+67, 23, src+77, 33, comp);
        g.drawImage(skin, alloc.x + 10, alloc.y + alloc.height - 10, alloc.x + alloc.width - 10, alloc.y + alloc.height,
                src+78, 23, src+88, 33, comp);
        g.drawImage(skin, alloc.x + alloc.width - 10, alloc.y + alloc.height - 10, alloc.x + alloc.width, alloc.y + alloc.height,
                src+89, 23, src+99, 33, comp);
        g.drawImage(seat.player.getIcon(), alloc.x + alloc.width/2 - 24, alloc.y + 10, comp);
        
        String name = seat.player.getName();
        if(name != null) {
            Color old = g.getColor();
            g.setColor(Color.gray);
            g.drawString(name, alloc.x + 8, alloc.y + 72);
            g.setColor(old);
        }
        
        int value = seat.leftPieces;
        if(value < 7) {
            Font old = g.getFont();
            g.setFont(GlobalCache.FontCache.ARIAL_SMALL);
            g.drawString("(" + String.valueOf(value) + ")", alloc.x + 100, alloc.y + 40);
            g.setFont(old);
        }
        
        value = seat.gameScore;
        if(value >= 0 ) {
            g.drawString(String.valueOf(value), alloc.x + 70, alloc.y + 90);
            if(seat.turnScore >= 0)
                g.drawString("+" + String.valueOf(seat.turnScore), alloc.x + 70, alloc.y + 107);
        }
        if(timer.isRunning() && highlighted) {
            Color old = g.getColor();
            g.setColor(GlobalCache.ColorCache.DEEP_PURPLE);
            g.drawString(Support.formatSimpleTime(timer.moveTime), alloc.x + 15, alloc.y + 32);
            g.drawString(Support.formatSimpleTime(timer.gameTime), alloc.x + 15, alloc.y + 47);
            g.setColor(old);
        } else if(seat.gameTime >= 0) {
            Color old = g.getColor();
            g.setColor(GlobalCache.ColorCache.OLD_GREEN);
            g.drawString(Support.formatSimpleTime(seat.gameTime), alloc.x + 15, alloc.y + 47);
            g.setColor(old);
        }
    }

}
