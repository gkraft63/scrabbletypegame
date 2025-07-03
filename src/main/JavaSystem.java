package jscrabble;

import java.awt.Font;
import java.io.InputStream;

public abstract class JavaSystem {

    private static JavaSystem javaSystemImpl;
    
    public static JavaSystem getInstance() {
        try {
            Class.forName("java.awt.Graphics2D");
            javaSystemImpl = new JavaSystem2DImpl();
        } catch(Throwable x) {
            javaSystemImpl = new JavaSystemImpl();
        }
        
        return javaSystemImpl;
    }
    
    public static boolean isJava2D() {
        return getInstance() instanceof JavaSystem2DImpl;
    }
    
    public Font newFont(String name, int style, float size) {
        return new Font(name, style, (int)size);
    }
}
