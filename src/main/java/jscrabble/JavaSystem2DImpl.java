package jscrabble;

import java.awt.Font;
import java.io.InputStream;

public class JavaSystem2DImpl extends JavaSystem {

    public Font newFont(String name, int style, float size) {
        Font font = null;
        try {
            Class.forName("java.awt.FontFormatException");
            InputStream stream = getClass().getResource("data/" + name + ".ttf").openStream();
            font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(style, size);
            stream.close();
            
        } catch(Exception e) {
        }
        return super.newFont(name, style, size);
    }
}
