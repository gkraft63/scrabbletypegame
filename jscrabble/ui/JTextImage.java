package jscrabble.ui;
import java.awt.*;


public class JTextImage extends JImage {
    private String label;
    private int spotx;
    private int spoty;
    private float alignx = 0.5f;
    private float aligny = 0.5f;
    
    
    public JTextImage(String label, Image image) {
        super(image);
        label.length();
        this.label = label;
    }
    
    public JTextImage(String label, Image image, float alignx, float aligny) {
        this(label, image);
        this.alignx = alignx;
        this.aligny = aligny;
    }
    
    
    public void updateSpot() {
        FontMetrics metrics = getFontMetrics(getFont());
        Dimension size = getSize();
        spotx = (int) (((float) (size.width - metrics.stringWidth(label))) * alignx);
        spoty = (int) (((float) (size.height)) * aligny);
    }
    
    public void setFont(Font f) {
        super.setFont(f);
        updateSpot();
    }
    
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        updateSpot();
    }
    
    
    public void paint(Graphics g) {
        if(spoty <= 0)
            updateSpot();
        super.paint(g);
        g.drawString(label, spotx, spoty);
    }
}
