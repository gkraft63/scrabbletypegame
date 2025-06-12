package jscrabble;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Random;

public class Util {

    private static Util sharedInstance = null;
    
    public static final Random random = new Random();
    
    public static Util getInstance() {
        if(sharedInstance == null) {
            sharedInstance = new Util();
        }
        return sharedInstance;
    }

    public static Class classOrNull(String className) {
        try {
            return Class.forName(className);
        } catch(Exception e) {
        } catch(LinkageError e) {
        }
        // Assume any exception means the class does not exist
        return null;
    }
    
    
    public static Point getRelativeLocation(Component parent, Component child) {
        Point location = new Point();
        while(child != null) {
            if(parent == child)
                return location;
            Point p = child.getLocation();
            location.translate(p.x, p.y);
            child = child.getParent();
        }
        return null;
    }
    
    public static String stackTraceToString(Exception e) {
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        e.printStackTrace(ps);
        ps.flush();
        ps.close();
        return os.toString();
    }
    
    public void paintChildren(Container cont, Graphics g) {
        Rectangle clip = g.getClipBounds();
        if(clip == null)
            (clip = cont.getBounds()).translate(0, 0);
        
        for(int i = cont.getComponentCount(); i-- > 0; ) {
            Component child = cont.getComponent(i);
            if(child.isVisible()) {
                Rectangle rect = child.getBounds();
                if(clip.intersects(rect)) {
                    g.translate(rect.x, rect.y);
                    try {
                        g.setFont(child.getFont());
                        g.setColor(child.getForeground());
                        child.paint(g);
                    }
                    finally {
                        g.translate(-rect.x, -rect.y);
                    }
                }
            }
        }
    }
}
