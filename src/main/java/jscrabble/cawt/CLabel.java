package jscrabble.cawt;

import java.awt.Component;
import java.awt.Label;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public abstract class CLabel implements CComponent {

    public abstract String getText();
    
    public abstract void setText(String text);
    
    public abstract void center();
    
    public static CLabel newInstance() {
        try {
            Class.forName("java.awt.Graphics2D");
            return (CLabel)Class.forName("jscrabble.cawt.SwingLabelImpl").newInstance();
        } catch (Exception e) {
            return new BasicLabelImpl();
        }
    }
}

class BasicLabelImpl extends CLabel {
    private Label comp;
    
    public BasicLabelImpl() {
        comp = new Label("", Label.LEFT);
    }
    
    
    public String getText() {
        return comp.getText();
    }
    
    public void setText(String text) {
        comp.setText(text);
    }
    
    public void center() {
        comp.setAlignment(Label.CENTER);
    }
    
    public Component getComponent() {
        return comp;
    }
}

class SwingLabelImpl extends CLabel {
    private JLabel comp;
    
    public SwingLabelImpl() {
        comp = new JLabel(" ", SwingConstants.LEFT);
    }
    
    
    public String getText() {
        return comp.getText();
    }
    
    public void setText(String text) {
        comp.setText(text);
    }
    
    public void center() {
        comp.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    public Component getComponent() {
        return comp;
    }
}
