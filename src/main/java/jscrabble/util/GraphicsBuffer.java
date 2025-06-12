package jscrabble.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class GraphicsBuffer {

    private Graphics graphics;
    private Image offscreen;
    
    public GraphicsBuffer(Component comp) {
        Dimension size = comp.getSize();
        graphics = (offscreen = comp.createImage(size.width, size.height)).getGraphics();
    }

    public Graphics getGraphics() {
        return graphics;
    }
    
    public Image getOffscreen() {
        return offscreen;
    }
    
    public void dispose() {
        if(graphics != null) {
            graphics.dispose();
            graphics = null;
        }
        if(offscreen != null) {
            offscreen.flush();
            offscreen = null;
        }
    }
}
