package jscrabble.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JImage extends Component {
    
    public final Image image;
    
    
    public JImage(Image image) {
        if (image == null) {
            // Create a 1x1 transparent image as a fallback
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        this.image = image;
    }
    
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
    
    public void update(Graphics g) {
        paint(g);
    }
}
