package jscrabble.util;

import java.awt.*;

import jscrabble.Support;
import jscrabble.GlobalCache;
import jscrabble.ui.JImage;



public class StartupIcon extends JImage {
    
    public StartupIcon() {
        super(GlobalCache.ImageCache.LOGO);
    }
    
    
    public Dimension getPreferredSize() {
        return new Dimension(440, 198);
    }
    
    public void doLayout() {
        setSize(440, 198);
    }
    
    
    public static Window show(Frame frame) {
        try {
            StartupIcon icon = new StartupIcon();
            Window win = new Window(frame);
            win.add(icon);
            win.pack();
            Support.centerOnScreen(win, null);
            win.setVisible(true);
            win.toFront();
            return win;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void hide(Window win) {
        try {
            win.dispose();
        } catch(Exception e) { }
    }
}
