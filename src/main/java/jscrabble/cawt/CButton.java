package jscrabble.cawt;

import java.awt.Button;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;

public abstract class CButton implements CComponent {
    public abstract void addActionListener(ActionListener l);
    public abstract void setLabel(String label);
    
    public static CButton newInstance() {
        try {
            Class.forName("java.awt.Graphics2D");
            return (CButton)Class.forName("jscrabble.cawt.CButtonSwingImpl").newInstance();
        } catch (Exception e) {
            return new CButtonImpl();
        }
    }
}

class CButtonImpl extends CButton {
    private Button comp;
    
    public CButtonImpl() {
        comp = new Button();
        comp.setBackground(new Color(0xFFEAC9));
        comp.setForeground(Color.BLACK);
    }
    
    public Component getComponent() {
        return comp;
    }
    
    public void addActionListener(ActionListener l) {
        comp.addActionListener(l);
    }
    
    public void setLabel(String label) {
        comp.setLabel(label);
    }
}
    
class CButtonSwingImpl extends CButton {
    private JButton comp;
    
    public CButtonSwingImpl() {
        comp = new JButton();
        comp.setBackground(new Color(0xFFEAC9));
        comp.setForeground(Color.BLACK);
        comp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x8CC6C6)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comp.setFocusPainted(false);
    }
    
    public Component getComponent() {
        return comp;
    }
    
    public void addActionListener(ActionListener l) {
        comp.addActionListener(l);
    }
    
    public void setLabel(String label) {
        comp.setText(label);
    }
}
